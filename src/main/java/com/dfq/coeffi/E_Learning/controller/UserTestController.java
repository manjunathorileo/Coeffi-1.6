package com.dfq.coeffi.E_Learning.controller;

import com.dfq.coeffi.E_Learning.modules.UserTest;
import com.dfq.coeffi.E_Learning.service.UserTestService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class UserTestController extends BaseController {
    @Autowired
    UserTestService userTestService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    CompanyConfigureService companyConfigureService;

    @PostMapping("/user-test")
    public ResponseEntity<UserTest> saveUpdateUserTest(@RequestBody UserTest userTest) {
        UserTest persistedObject = userTestService.saveUpdateUserTest(userTest);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);

    }

    @GetMapping("/user-test")
    public ResponseEntity<List<UserTest>> getUserTest(boolean status) {
        List<UserTest> userTests = userTestService.getUserTest(true);
        if (CollectionUtils.isEmpty(userTests)) {
            //not found
        }
        return new ResponseEntity<>(userTests, HttpStatus.OK);
    }

    @GetMapping("/user-test/{id}")
    public ResponseEntity<UserTest> getUserTestById(@PathVariable long id) {
        UserTest userTest2 = userTestService.getUserTestById(id);
        return new ResponseEntity<>(userTest2, HttpStatus.OK);
    }

    @GetMapping("/usertest/{userid}")
    public ResponseEntity<List<UserTest>> getUserTestByUsreId(@PathVariable("userid") long userid) {
        Optional<Employee> e = employeeService.getEmployee(userid);
        List<UserTest> userTestList = userTestService.getUserTestByUserId(e.get());
        return new ResponseEntity<>(userTestList, HttpStatus.OK);
    }

    @DeleteMapping("/user-test/{id}")
    public ResponseEntity<UserTest> deActivateById(@PathVariable long id) {
        userTestService.deActivateById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/user-test/user/{userid}")
    public UserTest getUserTestByUserId(long userid) {
        Optional<Employee> u = employeeService.getEmployee(userid);
        return (UserTest) userTestService.getUserTestByUserId(u.get());
    }

    @GetMapping("/user-test/{userId}/{pId}/{level}")
    public ResponseEntity<List<UserTest>> userValidateForMoreThanThrice(@PathVariable long userId, @PathVariable long pId, @PathVariable long level) throws Exception {
        Optional<Employee> employee = employeeService.getEmployee(userId);
        List<UserTest> userTestList = userTestService.getUserTestByUserId(employee.get());
        List<UserTest> userTestsProductAndLevelWise = new ArrayList<>();

        if (userTestList == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        for (UserTest userTest : userTestList) {
            if (userTest.getLevel() == level && userTest.getProductId() == pId) {
                userTestsProductAndLevelWise.add(userTest);
            }
        }

        List<CompanyConfigure> companyConfigure = companyConfigureService.getCompany();
        if (companyConfigure.get(0).isLevelByLevelOrRandom()) {
            CheckPreviousTest(userId, pId, level);
        }

        if (userTestsProductAndLevelWise.size() >= 3) {
            throw new Exception("Test limit exceeded,U can take test only thrice");
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    public void CheckPreviousTest(long userId, long pId, long level) {
        Optional<Employee> employee = employeeService.getEmployee(userId);
        List<UserTest> userTests = userTestService.getUserTestByUserId(employee.get());
        List<UserTest> userTestProductCheck = new ArrayList<>();
        List<UserTest> userTestsProductWise = new ArrayList<>();

        for (UserTest userTest : userTests) {
            if (userTest.getProductId() == pId && (userTest.getLevel() == (level - 1) || userTest.getLevel() == (level))) {
                if (userTest.getLevel() == (level) || userTest.getFinalResult().equalsIgnoreCase("PASS")) {
                    userTestsProductWise.add(userTest);
                }
            }
        }

        if (!userTests.isEmpty()) {
            for (UserTest userTest : userTests) {
                if (userTest.getProductId() == pId) {
                    userTestProductCheck.add(userTest);
                }
            }
        }
        if (userTestProductCheck.isEmpty()) {
            if (level != 1) {
                throw new EntityNotFoundException("Take level 1 test");
            }
        }
        if (!userTestProductCheck.isEmpty() && userTestsProductWise.isEmpty()) {
            throw new EntityNotFoundException("Not allowed");
        }

    }

    @GetMapping("/user-test-noOfTimes/{userId}/{pId}/{level}")
    public ResponseEntity<Long> userValidateForMoreThanThriceNo(@PathVariable long userId, @PathVariable long pId, @PathVariable long level) throws Exception {
        Optional<Employee> employee = employeeService.getEmployee(userId);
        List<UserTest> userTestList = userTestService.getUserTestByUserId(employee.get());
        List<UserTest> userTestsProductAndLevelWise = new ArrayList<>();
        if (userTestList == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        for (UserTest userTest : userTestList) {
            if (userTest.getLevel() == level && userTest.getProductId() == pId) {
                userTestsProductAndLevelWise.add(userTest);
            }
        }
        return new ResponseEntity(userTestsProductAndLevelWise.size(), HttpStatus.OK);
    }


}
