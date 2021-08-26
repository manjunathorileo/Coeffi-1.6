package com.dfq.coeffi.controller.payroll;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.EmployeeSalaryDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeAppraisal;
import com.dfq.coeffi.entity.payroll.EmployeeLoan;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import com.dfq.coeffi.entity.payroll.payrollmaster.PayHead;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.*;
import com.dfq.coeffi.util.SalaryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class EmployeeSalaryController extends BaseController {
    @Autowired
    private EmployeeSalaryService employeeSalaryService;
    private EmployeeService employeeService;
    private PayHeadService payHeadService;
    private EmployeeLoanService employeeLoanService;
    private EmployeeAppraisalService employeeAppraisalService;
    private EmployeeSalaryProcessService employeeSalaryProcessService;

    @Autowired
    public EmployeeSalaryController(EmployeeSalaryService employeeSalaryService, PayHeadService payHeadService, EmployeeService employeeService,
                                    EmployeeLoanService employeeLoanService, EmployeeAppraisalService employeeAppraisalService) {
        this.employeeSalaryService = employeeSalaryService;
        this.payHeadService = payHeadService;
        this.employeeService = employeeService;
        this.employeeLoanService = employeeLoanService;
        this.employeeAppraisalService = employeeAppraisalService;
        this.employeeSalaryProcessService = employeeSalaryProcessService;
    }

    /**
     * @return all the Employees List with Salary in the database
     */

    @GetMapping("employee-salary")
    public ResponseEntity<List<EmployeeSalary>> listAllEmployeeSalary() {
        List<EmployeeSalary> employeeSalaryLists = employeeSalaryService.listAllEmployeeSalary();
        if (CollectionUtils.isEmpty(employeeSalaryLists)) {
            throw new EntityNotFoundException("employeeSalaryLists");
        }
        return new ResponseEntity<>(employeeSalaryLists, HttpStatus.OK);
    }

    /**
     * API to get salary from employee code
     *
     * @param employeeCode
     * @return
     */

    @GetMapping("employee-salary/breakup/{employeeCode}")
    public ResponseEntity<EmployeeSalary> getEmployeeByEmployeeCode(@PathVariable("employeeCode") String employeeCode) {
        Optional<EmployeeSalary> employeeSalary = employeeSalaryService.getEmployeeByEmployeeCode(employeeCode);
        if (!employeeSalary.isPresent()) {
            throw new EntityNotFoundException("employeeSalary");
        }
        return new ResponseEntity<>(employeeSalary.get(), HttpStatus.OK);
    }

    @GetMapping("employee-salary/salary-approval/{id}")
    public ResponseEntity<EmployeeSalary> getSalaryApprovalById(@PathVariable("id") long id) {
        Optional<EmployeeSalary> employeeSalaryObj = employeeSalaryService.getEmployeeSalary(id);
        if (!employeeSalaryObj.isPresent()) {
            throw new EntityNotFoundException("employeeSalary");
        }

        EmployeeSalary employeeSalary = employeeSalaryObj.get();
        //employeeSalary.setApprove(true);

        employeeSalaryService.createEmployeeSalary(employeeSalary);
        return new ResponseEntity<>(employeeSalary, HttpStatus.OK);
    }

    /**
     * @param id : save object to database and return the saved object
     * @return
     */

    @GetMapping("employee-salary/{id}")
    public ResponseEntity<EmployeeSalary> createEmployeeSalary(@PathVariable("id") Long id) {
        Optional<EmployeeAppraisal> employeeAppraisal = employeeAppraisalService.getAppraisalByEmployee(id);
        List<PayHead> payHeadList = payHeadService.getAllPayHead();
        Optional<Employee> employee = employeeService.getEmployee(id);
        if (employee.get().getStatus().booleanValue()) {
            if (employeeAppraisal.isPresent() && employeeAppraisal.get().isStatus()) {
                BigDecimal offeredSalary = new BigDecimal(employee.get().getOfferedSalary());
                BigDecimal percentage = new BigDecimal(100);
                offeredSalary = offeredSalary.add(offeredSalary.multiply(employeeAppraisal.get().getSalaryIncrement().divide(percentage)));
                Double revisedOfferedSalary = offeredSalary.doubleValue();
                employee.get().setOfferedSalary(revisedOfferedSalary);
                employeeAppraisal.get().setStatus(false);
            }
            EmployeeSalary employeeSalary = SalaryUtil.calculateBasicSalary(payHeadList, employee.get());

            /****
             * To check Salary generation date is lies between EMI start date and end date
             */

            BigDecimal netPay = new BigDecimal(0);
            Date date = new Date();
            EmployeeSalary persistedEmployeeSalary = employeeSalaryService.createEmployeeSalary(employeeSalary);
            return new ResponseEntity<>(persistedEmployeeSalary, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("employee-salary/get-employee-salary")
    public ResponseEntity<List<EmployeeSalary>> getEmployeeSalary() {
        List<EmployeeSalary> employeesalary = employeeSalaryService.employeeDetails();
        if (CollectionUtils.isEmpty(employeesalary)) {
            throw new EntityNotFoundException("employeesalary");
        }
        return new ResponseEntity<>(employeesalary, HttpStatus.OK);
    }

    @GetMapping("employee-salary/get-active-salary")
    public ResponseEntity<List<EmployeeSalary>> getActiveSalary() {
        List<EmployeeSalary> employeesalary = employeeSalaryService.getActiveEmployee();
        if (CollectionUtils.isEmpty(employeesalary)) {
            throw new EntityNotFoundException("employeesalary");
        }
        return new ResponseEntity<>(employeesalary, HttpStatus.OK);
    }

    @PostMapping("employee-salary/update-employee-salary")
    public ResponseEntity<EmployeeSalary> approvedsalaryProcess(@RequestBody EmployeeSalary employeeSalary) {
        EmployeeSalary updatedEmployeeSalary = employeeSalaryService.createEmployeeSalary(employeeSalary);
        return new ResponseEntity<EmployeeSalary>(updatedEmployeeSalary, HttpStatus.OK);
    }

    /**
     * @param id
     * @param employeeSalaryDto
     * @return
     */

    @PostMapping("employee-salary/{id}")
    public ResponseEntity<EmployeeSalary> update(@PathVariable long id, @Valid @RequestBody EmployeeSalaryDto employeeSalaryDto) {
        Optional<EmployeeSalary> persistedEmployeeSalary = employeeSalaryService.getEmployeeSalary(id);
        if (!persistedEmployeeSalary.isPresent()) {
            throw new EntityNotFoundException(EmployeeSalary.class.getSimpleName());
        }
        EmployeeSalary employeeSalary1 = persistedEmployeeSalary.get();
        employeeSalary1.setId(id);
        employeeSalary1.setNetSalaryPayble(employeeSalaryDto.netSalary);
        employeeSalaryService.createEmployeeSalary(employeeSalary1);
        return new ResponseEntity<>(employeeSalary1, HttpStatus.OK);
    }
}