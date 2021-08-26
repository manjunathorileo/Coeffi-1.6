package com.dfq.coeffi.controller.payroll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.EmployeeLoan;
import com.dfq.coeffi.entity.payroll.LoanPlanner;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeLoanService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeLoanController extends BaseController {

    @Autowired
    private EmployeeLoanService employeeLoanService;
    private EmployeeService employeeService;
    private AcademicYearService academicYearService;

    @Autowired
    public EmployeeLoanController(EmployeeLoanService employeeLoanService, EmployeeService employeeService, AcademicYearService academicYearService) {
        this.employeeLoanService = employeeLoanService;
        this.employeeService = employeeService;
        this.academicYearService = academicYearService;

    }

    /**
     * @return all the Employees Loan List with details in the database
     */
    @GetMapping("employeeLoan")
    public ResponseEntity<List<EmployeeLoan>> listAllEmployeeLoans() {
        List<EmployeeLoan> employeeLoans = employeeLoanService.listAllEmployeeLoans();
        if (CollectionUtils.isEmpty(employeeLoans)) {
            throw new EntityNotFoundException("employeeLoans");
        }
        return new ResponseEntity<>(employeeLoans, HttpStatus.OK);
    }

    /**
     * @param employeeLoan : save object to database and return the saved object
     * @return
     */

    @PostMapping("employeeLoan")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EmployeeLoan> createEmployeeLoan(@Valid @RequestBody final EmployeeLoan employeeLoan) {
        if (employeeLoan.getEmployee() != null) {
            Optional<Employee> employee = employeeService.getEmployee(employeeLoan.getEmployee().getId());
            employeeLoan.setEmployee(employee.get());
        }
        if (employeeLoan.getAcademicYear() != null) {
            Optional<AcademicYear> academicYear = academicYearService.getAcademicYear(employeeLoan.getAcademicYear().getId());
            employeeLoan.setAcademicYear(academicYear.get());
        }
        List<LoanPlanner> loanPlannerList = new ArrayList<>();
        if (employeeLoan.getLoanDuration() > 0) {
            for (int i = 0; i < employeeLoan.getLoanDuration(); i++) {
                LoanPlanner loanPlanner = new LoanPlanner();
                loanPlanner.setStatus(true);
                loanPlanner.setEmiPay(employeeLoan.getEmiPay());
                Calendar myCal = Calendar.getInstance();
                //	myCal.set(Calendar.MONTH,+1);
                myCal.set(Calendar.DATE, myCal.getActualMaximum(Calendar.DATE));
                myCal.add(Calendar.MONTH, +i);
                loanPlanner.setEmiDate(myCal.getTime());
                loanPlanner.setEmployeeLoan(employeeLoan);
                loanPlannerList.add(loanPlanner);
            }
            employeeLoan.setLoanPlanners(loanPlannerList);
        }

        //EmployeeLoan employeeLoanDate = new EmployeeLoan();
        employeeLoan.setDueDate(DateUtil.addMonthsToDate(employeeLoan.getLoanDuration()));

        /****
         * 	Calendar myCal = Calendar.getInstance();
         myCal.add(Calendar.MONTH, employeeLoan.getLoanDuration());
         employeeLoan.setDueDate(myCal.getTime());
         System.out.println("Value is "+employeeLoan.getDueDate());
         *
         * 		employeeLoan.setDueDate(DateUtil.addMonthsToDate(employeeLoan.getLoanDuration()));

         */

        EmployeeLoan persistedObject = employeeLoanService.createEmployeeLoan(employeeLoan);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);

    }

    /**
     * @param id
     * @param employeeLoan
     * @return to update the employeeLoan object
     */

    @PutMapping("employeeLoan/{id}")
    public ResponseEntity<EmployeeLoan> update(@PathVariable long id, @Valid @RequestBody EmployeeLoan employeeLoan) {
        Optional<EmployeeLoan> persistedEmployeeLoan = employeeLoanService.getEmployeeLoan(id);
        if (!persistedEmployeeLoan.isPresent()) {
            System.out.println("EmployeeLoan with ID not found " + id);
            throw new EntityNotFoundException(EmployeeLoan.class.getSimpleName());
        }
        employeeLoan.setId(id);
        employeeLoanService.createEmployeeLoan(employeeLoan);
        return new ResponseEntity<>(employeeLoan, HttpStatus.OK);
    }

}
