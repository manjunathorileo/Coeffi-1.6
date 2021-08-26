package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.visitor.Entities.AssignSecurity;
import com.dfq.coeffi.visitor.Entities.EmployeeDto;
import com.dfq.coeffi.visitor.Entities.VisitorTimeSlot;
import com.dfq.coeffi.visitor.Services.AssignSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AssignSecurityController extends BaseController {
    @Autowired
    AssignSecurityService assignSecurityService;

    @Autowired
    EmployeeService employeeService;

    @PostMapping("visitor/assignsecurity-save")
    public ResponseEntity<AssignSecurity> saveSecurity(@RequestBody AssignSecurity assignSecurity) {
        AssignSecurity assignSecurity1 = assignSecurityService.saveSecurity(assignSecurity);
        return new ResponseEntity<>(assignSecurity1, HttpStatus.OK);
    }

    @GetMapping("visitor/assignsecurity-view")
    public ResponseEntity<List<AssignSecurity>> getAllSecurity() {
        List<AssignSecurity> assignSecurity2 = assignSecurityService.getAllSecurity();
        return new ResponseEntity<>(assignSecurity2, HttpStatus.OK);
    }

    @DeleteMapping("visitor/assignsecurity-delete/{id}")
    public void deleteTimeByid(@PathVariable long id) {
        assignSecurityService.deleteSecurityById(id);
    }

    @GetMapping("visitor/get-all-security")
    public ResponseEntity<List<EmployeeDto>> getbyEmployee() {
        List<Employee> employees = employeeService.findAll();
        List<EmployeeDto> emp = new ArrayList<>();

        for (Employee e : employees) {
            System.out.println(e.getEmployeeLogin().getRoles().get(0).getName());
            if (e.getEmployeeLogin().getRoles().get(0).getName().equalsIgnoreCase("SECURITY")) {
                EmployeeDto dto = new EmployeeDto();
                dto.setEmployeeId(e.getId());
                dto.setEmployeeName(e.getFirstName() + " " + e.getLastName());
                emp.add(dto);
            }

        }
        return new ResponseEntity<>(emp, HttpStatus.OK);
    }
}
