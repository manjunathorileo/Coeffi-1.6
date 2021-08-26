package com.dfq.coeffi.Exit;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class EmployeeExitController extends BaseController {

    private final EmployeeExitService employeeExitService;
    private final EmployeeService employeeService;
    private final EmployeeLeaveBalanceService employeeLeaveBalanceService;

    public EmployeeExitController(EmployeeExitService employeeExitService, EmployeeService employeeService, EmployeeLeaveBalanceService employeeLeaveBalanceService) {
        this.employeeExitService = employeeExitService;
        this.employeeService = employeeService;
        this.employeeLeaveBalanceService = employeeLeaveBalanceService;
    }

    @PostMapping("employeeExit")
    public ResponseEntity<EmployeeExit> createExit(@Valid @RequestBody EmployeeExit employeeExit) {
        Optional<Employee> employeeOptional = employeeService.getEmployee(employeeExit.getEmployee().getId());
        Employee employee = employeeOptional.get();
        Optional<EmployeeLeaveBalance> employeeLeaveBalanceOptional = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeId(employeeExit.getEmployee().getId());
        BigDecimal totalNoticePeriod = employee.getTotalNoticePeriod();
        BigDecimal leaveBalance = employeeLeaveBalanceOptional.get().getClosingLeave().getTotalLeave();
        employeeExit.setTotalNoticePeriod(totalNoticePeriod);
        employeeExit.setLeaveBalance(leaveBalance);
        employeeExit.setStatus(true);
        EmployeeExit employeeExitObj = employeeExitService.createExit(employeeExit);
        return new ResponseEntity<>(employeeExitObj, HttpStatus.OK);
    }

    @GetMapping("exit")
    public ResponseEntity<EmployeeExit> getAllExit() {
        List<EmployeeExit> employeeExits = new ArrayList<>();
        List<EmployeeExit> employeeExitList = employeeExitService.getAllExit();
        for (EmployeeExit employeeExitObj : employeeExitList) {
            if (employeeExitObj.getStatus().equals(true)) {
                employeeExits.add(employeeExitObj);
            }
        }
        return new ResponseEntity(employeeExits, HttpStatus.OK);
    }

    @GetMapping("exit/{id}")
    public ResponseEntity<EmployeeExit> getExitById(@PathVariable long id) {
        Optional<EmployeeExit> exitOptional = employeeExitService.getExitById(id);
        return new ResponseEntity(exitOptional, HttpStatus.OK);
    }

    @GetMapping("exit-by-empl/{emplId}")
    public ResponseEntity<EmployeeExit> getExitByEmpl(@PathVariable long emplId) {
        Optional<Employee> employeeOptional = employeeService.getEmployee(emplId);
        Optional<EmployeeExit> exitOptional = employeeExitService.getExitByEmpl(employeeOptional.get());
        EmployeeExit employeeExit = new EmployeeExit();
        if (exitOptional.get().getStatus().equals(true)) {
            employeeExit = exitOptional.get();
        }
        return new ResponseEntity<>(employeeExit, HttpStatus.OK);
    }

}
