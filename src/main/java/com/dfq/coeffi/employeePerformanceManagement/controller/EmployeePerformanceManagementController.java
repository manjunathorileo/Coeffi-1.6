package com.dfq.coeffi.employeePerformanceManagement.controller;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagement;
import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagementDto;
import com.dfq.coeffi.employeePerformanceManagement.entity.GoalStatusEnum;
import com.dfq.coeffi.employeePerformanceManagement.service.EmployeePerformanceManagementService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class EmployeePerformanceManagementController extends BaseController {
    @Autowired
    private EmployeePerformanceManagementService employeePerformanceManagementService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private MailService mailService;


    @PostMapping("/employee-performance-management")
    public ResponseEntity<EmployeePerformanceManagement> createEmployeePerformanceManagement(@Valid @RequestBody EmployeePerformanceManagement employeePerformanceManagement) {
        Date todaysDate = new Date();
        employeePerformanceManagement.setStatus(Boolean.TRUE);
        employeePerformanceManagement.setApprovalDate(null);
        EmployeePerformanceManagement employeePerformanceManagementObj = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/employee-performance-management/{id}")
    public ResponseEntity<EmployeePerformanceManagement> updateEmployeePerformanceManagement(@Valid @RequestBody EmployeePerformanceManagement employeePerformanceManagement, @PathVariable long id) {
        Date todaysDate = new Date();
        Optional<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagement(id);
        if (!employeePerformanceManagement1.isPresent()) {
            throw new EntityNotFoundException("No data ");
        }
        EmployeePerformanceManagement employeePerformanceManagement11 = employeePerformanceManagement1.get();
        employeePerformanceManagement11.setGoalName(employeePerformanceManagement.getGoalName());
        employeePerformanceManagement11.setGoalDiscription(employeePerformanceManagement.getGoalDiscription());
        EmployeePerformanceManagement employeePerformanceManagementObj = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement11);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/employee-performance-management")
    public ResponseEntity<EmployeePerformanceManagement> getAllEmployeePerformanceManagement() {
        List<EmployeePerformanceManagement> employeePerformanceManagements = new ArrayList<>();
        List<EmployeePerformanceManagement> employeePerformanceManagementList = employeePerformanceManagementService.getAllEmployeePerformanceManagement();
        if(employeePerformanceManagementList.isEmpty()){
            throw new EntityNotFoundException("**");
        }
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagementList) {
            if (employeePerformanceManagementObj.getStatus().equals(true)) {
                employeePerformanceManagements.add(employeePerformanceManagementObj);
            }
        }

        return new ResponseEntity(employeePerformanceManagements, HttpStatus.OK);
    }

    @GetMapping("/employee-performance-management/{id}")
    public ResponseEntity<EmployeePerformanceManagement> getEmployeePerformanceManagementById(@PathVariable long id) {
        Optional<EmployeePerformanceManagement> employeePerformanceManagementOptional = employeePerformanceManagementService.getEmployeePerformanceManagement(id);
        if(!employeePerformanceManagementOptional.isPresent()){
            throw new EntityNotFoundException("**");
        }
        return new ResponseEntity(employeePerformanceManagementOptional, HttpStatus.OK);
    }

    @PostMapping("/employee-performance-management/save-goal")
    public ResponseEntity<EmployeePerformanceManagement> saveEmployeePerformanceGoal(@Valid @RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto) {
        Date todaysDate = new Date();
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        if (!employeePerformanceManagementDto.getEmployeePerformanceManagements().isEmpty()) {
            for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagementDto.getEmployeePerformanceManagements()) {
                Optional<EmployeePerformanceManagement> employeePerformanceManagementOptional = employeePerformanceManagementService.getEmployeePerformanceManagement(employeePerformanceManagementObj.getId());
                EmployeePerformanceManagement employeePerformanceManagement = employeePerformanceManagementOptional.get();
                employeePerformanceManagement.setManagerGoalRemarks(employeePerformanceManagementObj.getManagerGoalRemarks());
                employeePerformanceManagement.setOverAllGoalRemarks(employeePerformanceManagementDto.getFinalRemarks());
                employeePerformanceManagement.setApprovalDate(todaysDate);
                EmployeePerformanceManagement employeePerformanceManagementUpdate = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagementUpdate);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/employee-performance-management/save-appraisal")
    public ResponseEntity<EmployeePerformanceManagement> saveEmployeePerformanceAppraisal(@Valid @RequestBody List<EmployeePerformanceManagement> employeePerformanceManagementDto) {
        Date todaysDate = new Date();
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        if (!employeePerformanceManagementDto.isEmpty()) {
            for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagementDto) {
                Optional<EmployeePerformanceManagement> employeePerformanceManagementOptional = employeePerformanceManagementService.getEmployeePerformanceManagement(employeePerformanceManagementObj.getId());
                EmployeePerformanceManagement employeePerformanceManagement = employeePerformanceManagementOptional.get();
                employeePerformanceManagement.setSelfAppraisal(employeePerformanceManagementObj.getSelfAppraisal());
                employeePerformanceManagement.setSelfRating(employeePerformanceManagementObj.getSelfRating());
                employeePerformanceManagement.setApprovalDate(todaysDate);
                EmployeePerformanceManagement employeePerformanceManagementUpdate = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagementUpdate);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/employee-performance-management/add-appraisal")
    public ResponseEntity<EmployeePerformanceManagement> saveEmployeePerformanceAppraisal(@Valid @RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto) {
        Date todaysDate = new Date();
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        if (!employeePerformanceManagementDto.getEmployeePerformanceManagements().isEmpty()) {
            for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagementDto.getEmployeePerformanceManagements()) {
                Optional<EmployeePerformanceManagement> employeePerformanceManagementOptional = employeePerformanceManagementService.getEmployeePerformanceManagement(employeePerformanceManagementObj.getId());
                EmployeePerformanceManagement employeePerformanceManagement = employeePerformanceManagementOptional.get();
                employeePerformanceManagement.setSelfAppraisal(employeePerformanceManagementObj.getSelfAppraisal());
                employeePerformanceManagement.setManagerRemark(employeePerformanceManagementObj.getManagerRemark());
                employeePerformanceManagement.setOverAllAppraisalRemarks(employeePerformanceManagementDto.getFinalRemarks());
                employeePerformanceManagement.setOverAllAppraisalRating(employeePerformanceManagementDto.getFinalRatings());
                employeePerformanceManagement.setSelfRating(employeePerformanceManagementObj.getSelfRating());
                employeePerformanceManagement.setAppraisalDate(todaysDate);
                EmployeePerformanceManagement employeePerformanceManagementUpdate = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagementUpdate);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("/submit-goal")
    public ResponseEntity<List<EmployeePerformanceManagement>> submitGoal(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto, Principal user) {
        EmployeePerformanceManagement employeePerformanceManagement = null;
        Optional<Employee> employee = employeeService.getEmployee(employeePerformanceManagementDto.getEmployeeId());
        List<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagement1) {
            if (employeePerformanceManagementObj != null) {
                employeePerformanceManagement = employeePerformanceManagementObj;

                if (employeePerformanceManagement.getGoalStatus().equals(GoalStatusEnum.YET_TO_SUBMIT_GOAL)) {
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.GOAL_COMPLETED);
                }
                EmployeePerformanceManagement employeePerformanceManagement2 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagement2);
            } else {
                throw new EntityNotFoundException();
            }

        }
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("/approve-goal")
    public ResponseEntity<List<EmployeePerformanceManagement>> approveGoal(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto, Principal user) {
        EmployeePerformanceManagement employeePerformanceManagement = null;
        Optional<Employee> employee = employeeService.getEmployee(employeePerformanceManagementDto.getEmployeeId());
        List<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagement1) {
            if (employeePerformanceManagementObj != null) {
                employeePerformanceManagement = employeePerformanceManagementObj;
                if (employeePerformanceManagement.getGoalStatus().equals(GoalStatusEnum.GOAL_COMPLETED)) {
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.GOAL_APPROVED);

                }
                Principal loggedUser = user;
                employeePerformanceManagement.setGoalApprovedBy(loggedUser.getName());
                EmployeePerformanceManagement employeePerformanceManagement2 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagement2);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }
        String message = "Your " + GoalStatusEnum.GOAL_APPROVED;
        sendEmailGoalApproval(employee.get().getEmployeeLogin().getEmail(), employee.get().getFirstName(), message);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("forward-goal")
    public ResponseEntity<List<EmployeePerformanceManagement>> forwardGoal(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto, Principal user) {
        EmployeePerformanceManagement employeePerformanceManagement = null;
        Optional<Employee> employee = employeeService.getEmployee(employeePerformanceManagementDto.getEmployeeId());
        List<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagement1) {
            if (employeePerformanceManagementObj != null) {
                employeePerformanceManagement = employeePerformanceManagementObj;
                if (employeePerformanceManagement.getGoalStatus().equals(GoalStatusEnum.GOAL_COMPLETED)) {
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.GOAL_FORWARDED);
                }
                EmployeePerformanceManagement employeePerformanceManagement2 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagement2);
            } else {
                throw new EntityNotFoundException();
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("sendback-goal")
    public ResponseEntity<List<EmployeePerformanceManagement>> sendBackGoal(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto, Principal user) {
        EmployeePerformanceManagement employeePerformanceManagement = null;
        Optional<Employee> employee = employeeService.getEmployee(employeePerformanceManagementDto.getEmployeeId());
        List<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagement1) {
            if (employeePerformanceManagementObj != null) {
                employeePerformanceManagement = employeePerformanceManagementObj;
                if (employeePerformanceManagement.getGoalStatus().equals(GoalStatusEnum.GOAL_COMPLETED)) {
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.YET_TO_SUBMIT_GOAL);
                }
                EmployeePerformanceManagement employeePerformanceManagement2 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagement2);
            } else {
                throw new EntityNotFoundException();
            }

        }
        String message = "Your " + GoalStatusEnum.GOAL_APPROVED;
        sendEmailGoalApproval(employee.get().getEmployeeLogin().getEmail(), employee.get().getFirstName(), message);
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("/submit-appraisal")
    public ResponseEntity<List<EmployeePerformanceManagement>> submitAppraisal(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto, Principal user) {
        EmployeePerformanceManagement employeePerformanceManagement = null;
        Optional<Employee> employee = employeeService.getEmployee(employeePerformanceManagementDto.getEmployeeId());
        List<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagement1) {
            if (employeePerformanceManagementObj != null) {
                employeePerformanceManagement = employeePerformanceManagementObj;

                if (employeePerformanceManagement.getGoalStatus().equals(GoalStatusEnum.GOAL_APPROVED)) {
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.SELF_APPRAISAL_COMPLETED);
                }
                Principal loggedUser = user;
                employeePerformanceManagement.setGoalApprovedBy(loggedUser.getName());
                EmployeePerformanceManagement employeePerformanceManagement2 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagement2);
            } else {
                throw new EntityNotFoundException();
            }

        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping("/sendback-appraisal")
    public ResponseEntity<List<EmployeePerformanceManagement>> sendBackAppraisal(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto, Principal user) {
        EmployeePerformanceManagement employeePerformanceManagement = null;
        Optional<Employee> employee = employeeService.getEmployee(employeePerformanceManagementDto.getEmployeeId());
        List<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagement1) {
            if (employeePerformanceManagementObj != null) {
                employeePerformanceManagement = employeePerformanceManagementObj;
                if (employeePerformanceManagement.getGoalStatus().equals(GoalStatusEnum.SELF_APPRAISAL_COMPLETED)) {
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.GOAL_APPROVED);
                }
                EmployeePerformanceManagement employeePerformanceManagement2 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagement2);
            } else {
                throw new EntityNotFoundException();
            }

        }
        String message = "Your " + GoalStatusEnum.GOAL_REJECTED;
        sendEmailGoalApproval(employee.get().getEmployeeLogin().getEmail(), employee.get().getFirstName(), message);
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("/approve-appraisal")
    public ResponseEntity<List<EmployeePerformanceManagement>> approveAppraisal(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto, Principal user) {
        EmployeePerformanceManagement employeePerformanceManagement = null;
        Optional<Employee> employee = employeeService.getEmployee(employeePerformanceManagementDto.getEmployeeId());
        List<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagement1) {
            if (employeePerformanceManagementObj != null) {
                employeePerformanceManagement = employeePerformanceManagementObj;
                if (employeePerformanceManagement.getGoalStatus().equals(GoalStatusEnum.SELF_APPRAISAL_COMPLETED)) {
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.MANAGER_REVIEW_COMPLETED);
                }
                Principal loggedUser = user;
                employeePerformanceManagement.setGoalApprovedBy(loggedUser.getName());
                EmployeePerformanceManagement employeePerformanceManagement2 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagement2);
                Date date = new Date();
                employee.get().setLastAppraisalDate(date);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        sendEmailGoalApproval(employee.get().getEmployeeLogin().getEmail(), employee.get().getFirstName(), "MANAGER_REVIEW_COMPLETED");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("forward-appraisal")
    public ResponseEntity<List<EmployeePerformanceManagement>> forwardAppraisal(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto, Principal user) {
        EmployeePerformanceManagement employeePerformanceManagement = null;
        Optional<Employee> employee = employeeService.getEmployee(employeePerformanceManagementDto.getEmployeeId());
        List<EmployeePerformanceManagement> employeePerformanceManagement1 = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagementObj : employeePerformanceManagement1) {
            if (employeePerformanceManagementObj != null) {
                employeePerformanceManagement = employeePerformanceManagementObj;
                if (employeePerformanceManagement.getGoalStatus().equals(GoalStatusEnum.SELF_APPRAISAL_COMPLETED)) {
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.SELF_APPRAISAL_FORWARDED);
                }
                EmployeePerformanceManagement employeePerformanceManagement2 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                employeePerformanceManagementList.add(employeePerformanceManagement2);
            } else {
                throw new EntityNotFoundException();
            }

        }
        return new ResponseEntity<>(HttpStatus.OK);

    }


    public void sendEmailGoalApproval(String email, String firstName, String content) {
        Date todayDate = new Date();
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = "GoalStatus" + " on " + todayDate;
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, null);
        mailService.sendEmail(mailnew, "****");
    }


    @PostMapping("/employee-performance-filter")
    public ResponseEntity<List<EmployeePerformanceManagement>> filterByEmployeeOrStatus(@RequestBody EmployeePerformanceManagementDto employeePerformanceManagementDto) {
        List<EmployeePerformanceManagement> employeePerformanceManagements = employeePerformanceManagementService.getAllEmployeePerformanceManagement();
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        if(employeePerformanceManagements.isEmpty()){
            throw new EntityNotFoundException("**");
        }
        for (EmployeePerformanceManagement employeePerformanceManagement : employeePerformanceManagements) {
            if (employeePerformanceManagement.getEmployee().getId().equals(employeePerformanceManagementDto.getEmployeeId())) {
                employeePerformanceManagementList.add(employeePerformanceManagement);
            }
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @GetMapping("/all-employee-performance-by-employee/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesByEmployeeId(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatusByEmployee(GoalStatusEnum.YET_TO_SUBMIT_GOAL, true, employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setEmployee(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }


    @GetMapping("/all-employee-performance-appraisal/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getAppraisalByEmployeeId(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatusByEmployee(GoalStatusEnum.GOAL_APPROVED, true, employee.get());
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {

            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setEmployee(employee1);
            //----------------

            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @DeleteMapping("employee-performance-management/{id}")
    public ResponseEntity<EmployeePerformanceManagement> deleteEmployeeperformanceManagement(@PathVariable long id) {
        Optional<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagement(id);
        EmployeePerformanceManagement employeePerformanceManagement1 = employeePerformanceManagement.get();
        if (employeePerformanceManagement1 != null) {
            employeePerformanceManagement1.setStatus(false);
            employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagement1, HttpStatus.OK);
    }


    @GetMapping("/employee-performance-1st-mgr/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesBy1stmgr(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatus(GoalStatusEnum.GOAL_COMPLETED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setFirstManager(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }


    @GetMapping("/employee-performance-2nd-mgr/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesBy2ndMgr(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatusBy2ndMgr(GoalStatusEnum.GOAL_FORWARDED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setSecondManager(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @GetMapping("/employee-performance-1st-mgr-status-list/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesBy1stmgrStatus(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatus(GoalStatusEnum.GOAL_APPROVED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setEmployee(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @GetMapping("/employee-performance-2nd-mgr-status-list/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesBy2ndmgrStatus(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatusBy2ndMgr(GoalStatusEnum.GOAL_APPROVED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setEmployee(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @GetMapping("/view-appraisal/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> viewAppraisal(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatusByEmployee(GoalStatusEnum.SELF_APPRAISAL_COMPLETED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setEmployee(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @GetMapping("/employee-performance-appraisal-1st-mgr/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesAppraisalBy1stmgr(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatus(GoalStatusEnum.SELF_APPRAISAL_COMPLETED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setFirstManager(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @GetMapping("/employee-performance-appraisal-2nd-mgr/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesAppraisalBy2ndmgr(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatusBy2ndMgr(GoalStatusEnum.SELF_APPRAISAL_FORWARDED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setSecondManager(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @GetMapping("/employee-performance-appraisal-1st-mgr-status-list/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesAppraisalBy1stmgrStatus(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatus(GoalStatusEnum.MANAGER_REVIEW_COMPLETED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setEmployee(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    @GetMapping("/employee-performance-appraisal-2nd-mgr-status-list/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> getEmployeePerformancesAppraisalBy2ndmgrStatus(@PathVariable long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagement = employeePerformanceManagementService.getEmployeePerformanceManagementByGoalStatusBy2ndMgr(GoalStatusEnum.MANAGER_REVIEW_COMPLETED, true, employee.get());
        if (employeePerformanceManagement.isEmpty()) {
            throw new EntityNotFoundException("No data for " + employee.get().getFirstName());
        }
        List<EmployeePerformanceManagement> employeePerformanceManagementList = new ArrayList<>();
        for (EmployeePerformanceManagement employeePerformanceManagement1 : employeePerformanceManagement) {
            //----------------------
            Employee employee1=new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeePerformanceManagement1.setEmployee(employee1);
            //----------------
            employeePerformanceManagementList.add(employeePerformanceManagement1);
        }
        return new ResponseEntity<>(employeePerformanceManagementList, HttpStatus.OK);
    }

    public ResponseEntity<EmployeePerformanceManagement> hrView() {
        return null;
    }


    @PostMapping("employee-performance-management/check-last-appraisal-date/{empid}")
    public ResponseEntity<Boolean> checkLastAppraisalDate(@PathVariable("empid") long empid) {
        Date d = new Date();
        Optional<Employee> employee = employeeService.getEmployee(empid);
        Date lastAppraisalDate = employee.get().getLastAppraisalDate();
        if(lastAppraisalDate==null){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        long period = DateUtil.monthsBetween(d,lastAppraisalDate);
        if (period>6) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("You cannot apply for appraisal more than once in 6 Months");
        }
    }

}
