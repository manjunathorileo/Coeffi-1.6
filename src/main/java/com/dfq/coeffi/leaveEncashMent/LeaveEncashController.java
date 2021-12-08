package com.dfq.coeffi.leaveEncashMent;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class LeaveEncashController extends BaseController {
    @Autowired
    LeaveEncashService leaveEncashService;
    @Autowired
    EmployeeLeaveBalanceService employeeLeaveBalanceService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    AcademicYearService academicYearService;

    @PostMapping("leave-encash")
    public void createLeaveEncash(@RequestBody LeaveEncash leaveEncash) {
        Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(leaveEncash.getEmployeeCode());
        if (employee.isPresent()) {
            leaveEncash.setEmployeeName(employee.get().getFirstName() + " " + employee.get().getLastName());
        }
        updateEmployeeAvailLeaveBalance(leaveEncash.getNoOfLeaves(), leaveEncash.getLeaveType(), employee.get().getId());
        leaveEncashService.save(leaveEncash);
    }

    @GetMapping("leave-encash")
    public ResponseEntity<List<LeaveEncash>> getAll() {
        List<LeaveEncash> leaveEncashes = leaveEncashService.getAll();
        return new ResponseEntity<>(leaveEncashes, HttpStatus.OK);
    }

    @PostMapping("leave-encash/{empId}")
    public ResponseEntity<EmployeeLeaveBalance> getBalance(@PathVariable long empId) {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
        AcademicYear academicYearObj = academicYear.get();
        EmployeeLeaveBalance getEmployeeLeaveBalanceByFinancialYearIdAndEmp = employeeLeaveBalanceService.
                getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(empId, academicYearObj.getId());
        EmployeeLeaveBalance elb = new EmployeeLeaveBalance();
        if (getEmployeeLeaveBalanceByFinancialYearIdAndEmp != null) {
            Optional<Employee> emp = employeeService.getEmployee(empId);
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
        }
        return new ResponseEntity<>(elb, HttpStatus.OK);
    }

    private ResponseEntity<EmployeeLeaveBalance> updateEmployeeAvailLeaveBalance(double noOfLeavesEnchashed, String type, long empId) {
        Optional<AcademicYear> currentYear = academicYearService.getActiveAcademicYear();

        EmployeeLeaveBalance currentYearEmployeeLeaveBalanceByEmployeeId = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(empId, currentYear.get().getId());
        AvailLeave availLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave();
        ClosingLeave closingLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getClosingLeave();
        EmployeeLeaveBalance employeeLeaveBalance = new EmployeeLeaveBalance();
        BigDecimal earnLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getEarnLeave();
        BigDecimal clearanceLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getClearanceLeave();
        BigDecimal medicalLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getMedicalLeave();
        if (type.equals("EL")) {
            earnLeave = earnLeave.add(BigDecimal.valueOf(noOfLeavesEnchashed));
            availLeave.setEarnLeave(earnLeave);
            closingLeave.setEarnLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getEarnLeave().subtract(earnLeave));
        } else if (type.equals("CL")) {
            clearanceLeave = clearanceLeave.add(BigDecimal.valueOf(noOfLeavesEnchashed));
            availLeave.setClearanceLeave(clearanceLeave);
            closingLeave.setClearanceLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getClearanceLeave().subtract(clearanceLeave));
        } else if (type.equals("SL")) {
            medicalLeave = medicalLeave.add(BigDecimal.valueOf(noOfLeavesEnchashed));
            availLeave.setMedicalLeave(medicalLeave);
            closingLeave.setMedicalLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getMedicalLeave().subtract(medicalLeave));
        }
        availLeave.setTotalLeave(earnLeave.add(clearanceLeave).add(medicalLeave));
        closingLeave.setTotalLeave(closingLeave.getEarnLeave().add(closingLeave.getClearanceLeave().add(closingLeave.getMedicalLeave())));
        currentYearEmployeeLeaveBalanceByEmployeeId.setAvailLeave(availLeave);
        currentYearEmployeeLeaveBalanceByEmployeeId.setClosingLeave(closingLeave);
        employeeLeaveBalance = employeeLeaveBalanceService.createEmployeeLeaveBalance(currentYearEmployeeLeaveBalanceByEmployeeId);
        return new ResponseEntity(employeeLeaveBalance, HttpStatus.OK);
    }
}
