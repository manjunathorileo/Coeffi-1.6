package com.dfq.coeffi.controller.payroll;


import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeSalaryDeduction;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryDeductionService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.*;
import java.util.*;

@RestController
public class EmployeeSalaryDeductionController extends BaseController {
    private final EmployeeSalaryDeductionService employeeSalaryDeductionService;
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeSalaryDeductionController(EmployeeSalaryDeductionService employeeSalaryDeductionService,
                                             EmployeeService employeeService) {
        this.employeeSalaryDeductionService = employeeSalaryDeductionService;
        this.employeeService = employeeService;
    }

    @GetMapping("employee-salary-deduction")
    public ResponseEntity<List<EmployeeSalaryDeduction>> getAllEmployeeSalaryDeduction() {
        long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        long previousMonth = Calendar.getInstance().get(Calendar.MONTH);
        List<EmployeeSalaryDeduction> salaryDeductions = employeeSalaryDeductionService.getAllEmployeeSalaryDeductions();
        List<EmployeeSalaryDeduction> deductionListCurrentMonth = new ArrayList<>();
        for (EmployeeSalaryDeduction employeeSalaryDeduction : salaryDeductions) {
            int m = DateUtil.getMonthNumber(employeeSalaryDeduction.getRecordedOn());
            if (m == currentMonth || m == previousMonth) {
                if (employeeSalaryDeduction.isStatus() == true) {
                    Employee employee = employeeSalaryDeduction.getEmployee();
                    Employee employee1 = new Employee();
                    employee1.setId(employee.getId());
                    employee1.setFirstName(employee.getFirstName());
                    employee1.setLastName(employee.getLastName());
                    employee1.setEmployeeCode(employee.getEmployeeCode());
                    employeeSalaryDeduction.setEmployee(employee1);
                    deductionListCurrentMonth.add(employeeSalaryDeduction);
                }
            }
        }

        if (salaryDeductions == null && salaryDeductions.size() == 0) {
            throw new EntityNotFoundException("Employee Salary Deduction List not found");
        }
        return new ResponseEntity(deductionListCurrentMonth, HttpStatus.OK);
    }

    @PostMapping("employee-salary-deduction")
    public ResponseEntity<List<EmployeeSalaryDeduction>> createEmployeeSalaryDeduction(@Valid @RequestBody EmployeeSalaryDeduction employeeSalaryDeduction) {
        Employee employee = null;
        if (employeeSalaryDeduction.getEmployee().getId() != null) {
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeSalaryDeduction.getEmployee().getId());
            employee = employeeObj.get();
        }
        employeeSalaryDeduction.setEmployee(employee);
        employeeSalaryDeduction.setStatus(true);
        employeeSalaryDeduction.setRecordedOn(employeeSalaryDeduction.getRecordedOn());
        EmployeeSalaryDeduction deduction = employeeSalaryDeductionService.saveEmployeeSalaryDeduction(employeeSalaryDeduction);
        return new ResponseEntity(deduction, HttpStatus.CREATED);
    }

    @DeleteMapping("employee-salary-deduction/{id}")
    public void deleteEmployeeSalaryDeduction(@PathVariable("id") long id) {
        Employee employee = null;
        Optional<EmployeeSalaryDeduction> employeeSalaryDeduction = employeeSalaryDeductionService.getEmployeeSalaryDeductionById(id);
        EmployeeSalaryDeduction deductionObj = employeeSalaryDeduction.get();
        deductionObj.setStatus(false);
        EmployeeSalaryDeduction deduction = employeeSalaryDeductionService.saveEmployeeSalaryDeduction(deductionObj);
    }


    @GetMapping("total-employee-salary-deduction")
    public ArrayList<BigDecimal> TotalDeduction() {
        ArrayList<BigDecimal> arr = new ArrayList<>();
        BigDecimal totalTds = BigDecimal.valueOf(0);
        BigDecimal totalAdvance = BigDecimal.valueOf(0);
        BigDecimal totalMeal = BigDecimal.valueOf(0);
        BigDecimal otherTotal = BigDecimal.valueOf(0);
        BigDecimal otherDeductionTotal = BigDecimal.valueOf(0);
        List<EmployeeSalaryDeduction> deductionList = employeeSalaryDeductionService.getAllEmployeeSalaryDeductions();
        long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        long previousMonth = Calendar.getInstance().get(Calendar.MONTH);
        for (EmployeeSalaryDeduction ded : deductionList) {
            int m = DateUtil.getMonthNumber(ded.getRecordedOn());
            if (ded.isStatus() == true && (m == currentMonth || m == previousMonth)) {
                totalTds = totalTds.add(ded.getTdsIncometax());
                totalAdvance = totalAdvance.add(ded.getAdvance());
                totalMeal = totalMeal.add(ded.getMeal());
                otherTotal = otherTotal.add(ded.getOther());
                otherDeductionTotal = otherDeductionTotal.add(ded.getOtherDeduction());
            }

        }
        arr.add(totalTds);
        arr.add(totalAdvance);
        arr.add(totalMeal);
        arr.add(otherTotal);
        arr.add(otherDeductionTotal);

        return arr;
    }
}
