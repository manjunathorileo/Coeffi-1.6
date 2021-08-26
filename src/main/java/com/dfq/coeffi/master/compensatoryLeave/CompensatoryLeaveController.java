package com.dfq.coeffi.master.compensatoryLeave;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
public class CompensatoryLeaveController extends BaseController {


    private CompensatoryLeaveService compensatoryLeaveService;
    private EmployeeAttendanceService employeeAttendanceService;
    private EmployeeService employeeService;
    NumberFormat formatter = new DecimalFormat("#0.00");

    @Autowired
    public CompensatoryLeaveController(CompensatoryLeaveService compensatoryLeaveService,
                                       EmployeeAttendanceService employeeAttendanceService,
                                       EmployeeService employeeService){
        this.compensatoryLeaveService = compensatoryLeaveService;
        this.employeeAttendanceService = employeeAttendanceService;
        this.employeeService = employeeService;
    }


    //@Scheduled(fixedRate = 1000)
    @GetMapping("compensatory-leave/calculation")
    public ResponseEntity<CompensatoryLeave> createCompensatoryLeave(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = 1;
        calendar.set(year, month, day);
        int numOfDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        Date endDate = calendar.getTime();
        List<Employee> employeeList = employeeService.findAll();
        if(employeeList != null && employeeList.size() > 0 ){
            for (Employee employee:employeeList) {
                double effectiveHourPerMonth = 0;
                double openingLeaveBalance;
                double closingLeaveBalance ;
                double earnLeaves;
                List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employee.getId(), AttendanceStatus.PRESENT, startDate, endDate);
                List<CompensatoryLeave> compensatoryLeaves = compensatoryLeaveService.getCompensatoryLeavesByEmployee(employee.getId());
                if(compensatoryLeaves != null && compensatoryLeaves.size() > 0){
                    Collections.reverse(compensatoryLeaves);
                    CompensatoryLeave compensatoryLeave = compensatoryLeaves.get(0);
                    openingLeaveBalance =compensatoryLeave.getClosedBalance();
                }else
                {
                    openingLeaveBalance =0;
                }
                if(employeeAttendances != null && employeeAttendances.size() > 0 ){
                    for (EmployeeAttendance employeeAttendance:employeeAttendances) {
                        if(employeeAttendance.getEffectiveOverTime() != null && Double.valueOf(employeeAttendance.getEffectiveOverTime()) > 0 ){
                            effectiveHourPerMonth = effectiveHourPerMonth + Double.valueOf(employeeAttendance.getEffectiveOverTime());
                        }
                    }
                    double earnLeave = effectiveHourPerMonth/10;
                    CompensatoryLeave compensatoryLeave = new CompensatoryLeave();
                    compensatoryLeave.setMonthName(DateUtil.getCurrentMonth());
                    compensatoryLeave.setOpeningBalance(openingLeaveBalance);
                    compensatoryLeave.setEmployee(employee);
                    compensatoryLeave.setEarnedLeave(Double.parseDouble(formatter.format(earnLeave)));
                    compensatoryLeave.setClosedBalance(Double.parseDouble(formatter.format(openingLeaveBalance + earnLeave)));
                    compensatoryLeaveService.saveCompensatoryLeave(compensatoryLeave);
                }

            }

        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
