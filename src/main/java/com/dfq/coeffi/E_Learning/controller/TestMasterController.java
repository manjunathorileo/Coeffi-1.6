package com.dfq.coeffi.E_Learning.controller;

import com.dfq.coeffi.E_Learning.modules.TestMaster;
import com.dfq.coeffi.E_Learning.modules.UserTest;
import com.dfq.coeffi.E_Learning.service.TestMasterService;
import com.dfq.coeffi.E_Learning.service.UserTestService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class TestMasterController extends BaseController {
    @Autowired
    TestMasterService testMasterService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    UserTestService userTestService;

    @PostMapping("/test-master/{pid}")
    public ResponseEntity<TestMaster> saveUpdateTestMaster(@RequestBody TestMaster testMaster, @PathVariable("pid") long pid) {
        TestMaster persistedobj = testMasterService.saveUpdateTestMaster(testMaster, pid);
        return new ResponseEntity<>(persistedobj, HttpStatus.CREATED);
    }

    @GetMapping("/test-master")
    public ResponseEntity<List<TestMaster>> getTestMaster() {
        List<TestMaster> testMasterlist = testMasterService.getTestMaster();
        Collections.sort(testMasterlist, (o1, o2) -> Math.toIntExact(o1.getTestLevel() - o2.getTestLevel()));
        return new ResponseEntity<>(testMasterlist, HttpStatus.OK);
    }

    public long setNoOfTime(@PathVariable long userId, @PathVariable long pId, @PathVariable long level) {
        Optional<Employee> employee = employeeService.getEmployee(userId);
        List<UserTest> userTestList = userTestService.getUserTestByUserId(employee.get());
        List<UserTest> userTestsProductAndLevelWise = new ArrayList<>();
        for (UserTest userTest : userTestList) {
            if (userTest.getLevel() == level && userTest.getProductId() == pId) {
                userTestsProductAndLevelWise.add(userTest);
            }
        }
        return userTestsProductAndLevelWise.size();
    }

    @GetMapping("/test-master/{id}")
    public ResponseEntity<TestMaster> getTestMasterById(@PathVariable long id) {
        TestMaster testMasterById = testMasterService.getTestMasterById(id);
        return new ResponseEntity<>(testMasterById, HttpStatus.OK);
    }

    @GetMapping("/test-master/product/{productId}")
    public ResponseEntity<List<TestMaster>> getTestMasterByProductId(@PathVariable long productId) {
        List<TestMaster> testMasterByProductId = testMasterService.getTestMasterByProductId(productId);
        return new ResponseEntity<>(testMasterByProductId, HttpStatus.OK);
    }


    @DeleteMapping("/test-master/{id}")
    public ResponseEntity<TestMaster> deActiveStatus(@PathVariable long id) {
        testMasterService.deActiveStatus(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }


}
