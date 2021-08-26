package com.dfq.coeffi.sam;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RestController
public class SamController extends BaseController {

    @Autowired
    private EmployeeService employeeService;

    // Assign modules and privileges to the employee

    @PostMapping("/employee-modules-privileges")
    public ResponseEntity<Employee> assignModuleAndPrivileges(@RequestBody SamDto samDto) {

        Optional<Employee> employeeObj = employeeService.getEmployee(samDto.getEmpId());
        if(!employeeObj.isPresent()){
            throw new EntityNotFoundException("employee not found");
        }

        Employee employee = employeeObj.get();
        employee.setModules(samDto.getModules());
        employee.setPrivileges(samDto.getPrivileges());
        Employee persistedEmployee = employeeService.save(employee);

        return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
    }
}