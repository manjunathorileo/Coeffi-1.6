package com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.AccureLeave;
import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.LeaveSchedular;
import com.dfq.coeffi.controller.leave.leavebalance.OpeningLeave;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRuleService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.repository.hr.EmployeeRepository;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;


@RestController
public class EmployeeLeaveBalanceController extends BaseController {

    private EmployeeLeaveBalanceService employeeLeaveBalanceService;
    private AcademicYearService academicYearService;
    private LeaveService leaveService;
    private EarningLeaveRuleService earningLeaveRuleService;
    private LeaveSchedular leaveSchedular;

    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeLeaveBalanceController(EmployeeLeaveBalanceService employeeLeaveBalanceService, AcademicYearService academicYearService, LeaveService leaveService, EarningLeaveRuleService earningLeaveRuleService, LeaveSchedular leaveSchedular) {
        this.employeeLeaveBalanceService = employeeLeaveBalanceService;
        this.academicYearService = academicYearService;
        this.leaveService = leaveService;
        this.earningLeaveRuleService = earningLeaveRuleService;
        this.leaveSchedular = leaveSchedular;
    }

    @GetMapping("/employee-leave-balance/get-all")
    private ResponseEntity<EmployeeLeaveBalance> getAllEmployeeLeaveBalance() {
        List<EmployeeLeaveBalance> getAllEmployeeLeaveBalance = employeeLeaveBalanceService.getAllEmployeeLeaveBalance();
        if (getAllEmployeeLeaveBalance.isEmpty()) {
            throw new EntityNotFoundException("No employee leave balance found");
        }
        return new ResponseEntity(getAllEmployeeLeaveBalance, HttpStatus.OK);
    }

    @GetMapping("/employee-leave-balance/get-by-id/{id}")
    private ResponseEntity<EmployeeLeaveBalance> getEmployeeLeaveBalanceById(@PathVariable long id) {
        EmployeeLeaveBalance getemployeeLeaveBalanceById = employeeLeaveBalanceService.getEmployeeLeaveBalanceById(id);
        return new ResponseEntity(getemployeeLeaveBalanceById, HttpStatus.OK);
    }

    @GetMapping("/employee-leave-balance/get-by-employee/{employeeId}")
    private ResponseEntity<EmployeeLeaveBalance> getEmployeeLeaveBalanceByEmployeeId(@PathVariable long employeeId) {
        Optional<EmployeeLeaveBalance> getEmployeeLeaveBalanceByEmployeeId = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeId(employeeId);
        if (!getEmployeeLeaveBalanceByEmployeeId.isPresent()) {
            throw new EntityNotFoundException("There is no leave balance for employee : " + employeeId);
        }
        return new ResponseEntity(getEmployeeLeaveBalanceByEmployeeId, HttpStatus.OK);
    }

    @DeleteMapping("/employee-leave-balance/delete/{id}")
    private ResponseEntity<EmployeeLeaveBalance> deleteEmployeeLeaveBalance(@PathVariable long id) {
        EmployeeLeaveBalance getemployeeLeaveBalanceById = employeeLeaveBalanceService.getEmployeeLeaveBalanceById(id);
        getemployeeLeaveBalanceById.setId(id);
        getemployeeLeaveBalanceById.setStatus(false);
        EmployeeLeaveBalance createmployeeLeaveBalance = employeeLeaveBalanceService.createEmployeeLeaveBalance(getemployeeLeaveBalanceById);
        return new ResponseEntity(createmployeeLeaveBalance, HttpStatus.OK);
    }

    @GetMapping("/employee-leave-balance/get-current-year-leave-balance")
    private ResponseEntity<EmployeeLeaveBalance> getEmployeeLeaveBalanceByFinancialYearId() {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
        AcademicYear academicYearObj = academicYear.get();
//        long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
//        long previousMonth = Calendar.getInstance().get(Calendar.MONTH);
//        List<EmployeeLeaveBalance> getEmployeeLeaveBalanceByFinancialYearId = employeeLeaveBalanceService.getEmployeeLeaveBalanceByFinancialYearId(academicYearObj.getId());
//
//        if (getEmployeeLeaveBalanceByFinancialYearId.isEmpty()) {
//            throw new EntityNotFoundException("No leave balance for this year");
//        }
//        List<EmployeeLeaveBalance> currentMonthLeaveBalance = new ArrayList<>();
//        for (EmployeeLeaveBalance employeeLeaveBalance : getEmployeeLeaveBalanceByFinancialYearId) {
//            if (employeeLeaveBalance.getCurrentMonth() == currentMonth) {
//                currentMonthLeaveBalance.add(employeeLeaveBalance);
//            }
//        }
        List<EmployeeLeaveBalance> finalEB = new ArrayList<>();
        List<Employee> employeeList = employeeService.findAll();
        for (Employee e : employeeList) {
            EmployeeLeaveBalance getEmployeeLeaveBalanceByFinancialYearIdAndEmp = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(e.getId(), academicYearObj.getId());
            if (getEmployeeLeaveBalanceByFinancialYearIdAndEmp!=null) {
                EmployeeLeaveBalance elb = new EmployeeLeaveBalance();
                Optional<Employee> emp = employeeService.getEmployee(e.getId());
                Employee empnew = new Employee();
                empnew.setId(emp.get().getId());
                empnew.setFirstName(emp.get().getFirstName());
                empnew.setLastName(emp.get().getLastName());
                empnew.setEmployeeCode(emp.get().getEmployeeCode());
                elb.setId(getEmployeeLeaveBalanceByFinancialYearIdAndEmp.getId());
                elb.setAvailLeave(getEmployeeLeaveBalanceByFinancialYearIdAndEmp.getAvailLeave());
                elb.setOpeningLeave(getEmployeeLeaveBalanceByFinancialYearIdAndEmp.getOpeningLeave());
                elb.setClosingLeave(getEmployeeLeaveBalanceByFinancialYearIdAndEmp.getClosingLeave());
                elb.setEmployee(empnew);
                finalEB.add(elb);
            }

        }
        return new ResponseEntity(finalEB, HttpStatus.OK);
    }

    @GetMapping("/employee-leave-balance/get-leave-balance-employee-wise/{employeeId}")
    private ResponseEntity<EmployeeLeaveBalance> getCurrentYearEmployeeLeaveBalanceByEmployeeId(@PathVariable long employeeId) {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
        AcademicYear academicYearObj = academicYear.get();
        EmployeeLeaveBalance getCurrentYearEmployeeLeaveBalanceEmployeeWise = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employeeId, academicYearObj.getId());
        Employee employee = new Employee();
        employee.setId(getCurrentYearEmployeeLeaveBalanceEmployeeWise.getEmployee().getId());
        employee.setFirstName(getCurrentYearEmployeeLeaveBalanceEmployeeWise.getEmployee().getFirstName());
        employee.setLastName(getCurrentYearEmployeeLeaveBalanceEmployeeWise.getEmployee().getLastName());
        employee.setEmployeeCode(getCurrentYearEmployeeLeaveBalanceEmployeeWise.getEmployee().getEmployeeCode());
        getCurrentYearEmployeeLeaveBalanceEmployeeWise.setEmployee(employee);
        return new ResponseEntity(getCurrentYearEmployeeLeaveBalanceEmployeeWise, HttpStatus.OK);
    }

    @GetMapping("/employee-leave-balance/update-avail-leave/{id}")
    private ResponseEntity<EmployeeLeaveBalance> updateEmployeeAvailLeaveBalance(@PathVariable long id) {
        Optional<AcademicYear> currentYear = academicYearService.getActiveAcademicYear();
        Optional<Leave> leaveList = leaveService.getLeave(id);
        Leave leaveObjt = leaveList.get();
        EmployeeLeaveBalance currentYearEmployeeLeaveBalanceByEmployeeId = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(leaveObjt.getRefId(), currentYear.get().getId());
        AvailLeave availLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave();
        ClosingLeave closingLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getClosingLeave();
        EmployeeLeaveBalance employeeLeaveBalance = new EmployeeLeaveBalance();
        BigDecimal earnLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getEarnLeave();
        BigDecimal clearanceLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getClearanceLeave();
        BigDecimal medicalLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getMedicalLeave();
        if (leaveObjt.getLeaveStatus().equals(LeaveStatus.APPROVED)) {
            if (leaveObjt.getLeaveType().equals(LeaveType.EARN_LEAVE)) {
                earnLeave = earnLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setEarnLeave(earnLeave);
                closingLeave.setEarnLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getEarnLeave().subtract(earnLeave));
            } else if (leaveObjt.getLeaveType().equals(LeaveType.CASUAL_LEAVE)) {
                clearanceLeave = clearanceLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setClearanceLeave(clearanceLeave);
                closingLeave.setClearanceLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getClearanceLeave().subtract(clearanceLeave));
            } else if (leaveObjt.getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {
                medicalLeave = medicalLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setMedicalLeave(medicalLeave);
                closingLeave.setMedicalLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getMedicalLeave().subtract(medicalLeave));
            }
            availLeave.setTotalLeave(earnLeave.add(clearanceLeave).add(medicalLeave));
            closingLeave.setTotalLeave(closingLeave.getEarnLeave().add(closingLeave.getClearanceLeave().add(closingLeave.getMedicalLeave())));
            currentYearEmployeeLeaveBalanceByEmployeeId.setAvailLeave(availLeave);
            currentYearEmployeeLeaveBalanceByEmployeeId.setClosingLeave(closingLeave);
            employeeLeaveBalance = employeeLeaveBalanceService.createEmployeeLeaveBalance(currentYearEmployeeLeaveBalanceByEmployeeId);
        }
        return new ResponseEntity(employeeLeaveBalance, HttpStatus.OK);
    }

}
