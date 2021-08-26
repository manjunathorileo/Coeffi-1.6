package com.dfq.coeffi.CanteenManagement.employeeBalance;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class EmployeeBalanceController extends BaseController {

    private final EmployeeBalanceService employeeBalanceService;
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeBalanceController(EmployeeBalanceService employeeBalanceService, EmployeeService employeeService) {
        this.employeeBalanceService = employeeBalanceService;
        this.employeeService = employeeService;
    }


}
