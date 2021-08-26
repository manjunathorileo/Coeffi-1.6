package com.dfq.coeffi.controller.leave.leavebalance;

import com.dfq.coeffi.controller.leave.AccureLeave;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRule;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRuleService;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.repository.leave.AvailLeaveRepo;
import com.dfq.coeffi.repository.leave.ClosingLeaveRepo;
import com.dfq.coeffi.repository.leave.OpeningLeaveRepo;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Configuration
@EnableScheduling
@Service
public class LeaveSchedular {

    private EarningLeaveRuleService earningLeaveRuleService;
    private EmployeeService employeeService;
    private EmployeeLeaveBalanceService employeeLeaveBalanceService;
    private EmployeeAttendanceService employeeAttendanceService;
    private AcademicYearService academicYearService;

    @Autowired
    AvailLeaveRepo availLeaveRepo;
    @Autowired
    ClosingLeaveRepo closingLeaveRepo;
    @Autowired
    OpeningLeaveRepo openingLeaveRepo;

    @Autowired
    public LeaveSchedular(EarningLeaveRuleService earningLeaveRuleService, EmployeeLeaveBalanceService employeeLeaveBalanceService, EmployeeAttendanceService employeeAttendanceService, EmployeeService employeeService, AcademicYearService academicYearService) {
        this.earningLeaveRuleService = earningLeaveRuleService;
        this.employeeLeaveBalanceService = employeeLeaveBalanceService;
        this.employeeAttendanceService = employeeAttendanceService;
        this.employeeService = employeeService;
        this.academicYearService = academicYearService;
    }

    @Scheduled(cron = "0 0 0 28-31 * *")
//  @Scheduled(fixedDelay = 180000, initialDelay = 100)
    public void checkForLeaveRules() {
        System.out.println("********************");
        LocalDate today = LocalDate.now();
        long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        System.out.println("Current month is: " + currentMonth);
        System.out.println("Previous month is: " + (currentMonth - 1));
        Date monthBeginDate = java.sql.Date.valueOf(today.withDayOfMonth(1));
        Date monthEndDate = java.sql.Date.valueOf(today.plusMonths(1).withDayOfMonth(1).minusDays(1));
        List<EarningLeaveRule> earningLeaveRuleList = earningLeaveRuleService.getAllEarningLeaveRule();
        List<Employee> getAllEmployeeList = employeeService.findAll();
        for (Employee employee : getAllEmployeeList) {
            EmployeeLeaveBalance newEmployeeLeaveBalance = new EmployeeLeaveBalance();
            AccureLeave accureLeave = new AccureLeave();
            OpeningLeave openingLeave = null;
            AvailLeave availLeave = null;
            ClosingLeave closingLeave = null;
            Optional<AcademicYear> academicYearOptional = academicYearService.getActiveAcademicYear();
            AcademicYear academicYear = academicYearOptional.get();
            EmployeeLeaveBalance employeeLeaveBalances = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employee.getId(), academicYear.getId());
            if (employeeLeaveBalances != null) {
                openingLeave = employeeLeaveBalances.getOpeningLeave();
                availLeave = employeeLeaveBalances.getAvailLeave();
                closingLeave = employeeLeaveBalances.getClosingLeave();
                List<EmployeeAttendance> getPresentEmployeesList = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(monthBeginDate, monthEndDate, employee.getId());
                ArrayList<EmployeeAttendance> presentEmployeeList = new ArrayList<>();
                if (getPresentEmployeesList != null && getPresentEmployeesList.size() > 0) {
                    for (EmployeeAttendance employeeAttendance : getPresentEmployeesList) {
                        if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                            presentEmployeeList.add(employeeAttendance);
                            System.out.println("name & size: " + employee.getFirstName() + "  " + presentEmployeeList.size());
                        }
                    }
                    System.out.println("****************** UPDATE ENTRY **************************");
                    for (EarningLeaveRule earningLeaveRule : earningLeaveRuleList) {
                        if (earningLeaveRule.getLeaveType().toString().equalsIgnoreCase("EARN_LEAVE")) {
                            openingLeave.setEarnLeave(earningLeaveRule.getValue().add(employeeLeaveBalances.getOpeningLeave().getEarnLeave()));
                            closingLeave.setEarnLeave(openingLeave.getEarnLeave().subtract(availLeave.getEarnLeave()));
                            accureLeave.setEarnLeave(BigDecimal.valueOf(presentEmployeeList.size()).divide(earningLeaveRule.getRepeated(), 2, RoundingMode.HALF_UP));
                            System.out.println("Accured Leave:" + accureLeave.getEarnLeave());
                        } else if (earningLeaveRule.getLeaveType().toString().equalsIgnoreCase("CASUAL_LEAVE")) {
                            openingLeave.setClearanceLeave((earningLeaveRule.getValue().add(employeeLeaveBalances.getClosingLeave().getClearanceLeave())));
                            closingLeave.setClearanceLeave(openingLeave.getClearanceLeave().subtract(availLeave.getClearanceLeave()));
                            accureLeave.setCasualLeave(BigDecimal.valueOf(presentEmployeeList.size()).divide(earningLeaveRule.getRepeated(), 2, RoundingMode.HALF_UP));
                            System.out.println("Casual Leave:" + accureLeave.getCasualLeave());
                        }
                    }
                }
                newEmployeeLeaveBalance.setAccureLeave(accureLeave);
                newEmployeeLeaveBalance.setOpeningLeave(openingLeave);
                newEmployeeLeaveBalance.setClosingLeave(closingLeave);
                newEmployeeLeaveBalance.setAvailLeave(availLeave);
                newEmployeeLeaveBalance.setEmployee(employee);
                newEmployeeLeaveBalance.setStatus(true);
                newEmployeeLeaveBalance.setAcademicYear(academicYearOptional.get());
                newEmployeeLeaveBalance.setCurrentMonth(currentMonth);
                employeeLeaveBalanceService.createEmployeeLeaveBalance(newEmployeeLeaveBalance);
            } else {
                System.out.println("FRESH YEAR ENTRY");
                employeeLeaveBalances = new EmployeeLeaveBalance();
                employeeLeaveBalances.setEmployee(employee);
                employeeLeaveBalances.setAcademicYear(academicYear);
                employeeLeaveBalances.setStatus(true);
                openingLeave = new OpeningLeave();
                closingLeave = new ClosingLeave();
                availLeave = new AvailLeave();
                for (EarningLeaveRule earningLeaveRule : earningLeaveRuleList) {
                    if (earningLeaveRule.getLeaveType().equals(LeaveType.EARN_LEAVE)) {
                        openingLeave.setEarnLeave(new BigDecimal(0));
                        closingLeave.setEarnLeave(new BigDecimal(0));
                    } else {
                        openingLeave.setEarnLeave(new BigDecimal(0));
                        closingLeave.setEarnLeave(new BigDecimal(0));
                    }

                    if (earningLeaveRule.getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {
                        if (employee.getEsiNumber().compareTo("0") > 0) {
                            openingLeave.setMedicalLeave(new BigDecimal(0));
                            closingLeave.setMedicalLeave(new BigDecimal(0));
                        } else {
                            openingLeave.setMedicalLeave(earningLeaveRule.getValue());
                            closingLeave.setMedicalLeave(earningLeaveRule.getValue());

                        }
                    } else {
                        openingLeave.setMedicalLeave(new BigDecimal(0));
                        closingLeave.setMedicalLeave(new BigDecimal(0));
                    }

                    openingLeave.setClearanceLeave(new BigDecimal(0));
                    closingLeave.setClearanceLeave(new BigDecimal(0));
                    availLeave.setEarnLeave(new BigDecimal(0));
                    availLeave.setMedicalLeave(new BigDecimal(0));
                    availLeave.setClearanceLeave(new BigDecimal(0));
                    availLeave.setTotalLeave(new BigDecimal(0));

                    openingLeave.setTotalLeave(openingLeave.getEarnLeave().add(openingLeave.getClearanceLeave().add(openingLeave.getMedicalLeave())));
                    closingLeave.setTotalLeave(openingLeave.getEarnLeave().add(openingLeave.getClearanceLeave().add(openingLeave.getMedicalLeave())));
                    availLeaveRepo.save(availLeave);
                    openingLeaveRepo.save(openingLeave);
                    closingLeaveRepo.save(closingLeave);
                    employeeLeaveBalances.setAvailLeave(availLeave);
                    employeeLeaveBalances.setOpeningLeave(openingLeave);
                    employeeLeaveBalances.setClosingLeave(closingLeave);
                    employeeLeaveBalances.setCurrentMonth(currentMonth);
                    employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalances);
                }
            }
        }
    }


//    @Scheduled(cron = "0 0 10 1 1/1 *")
//    public void refreshEmployeeLeaveBalances() {
//        Optional<AcademicYear> academicYearOptional = academicYearService.getActiveAcademicYear();
//        List<Employee> employees = employeeService.findAll();
//        for (Employee employee : employees) {
//            EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employee.getId(), academicYearOptional.get().getId());
//            System.out.println("Ename: "+employeeLeaveBalance.getEmployee().getFirstName());
//
//            OpeningLeave openingLeave = employeeLeaveBalance.getOpeningLeave();
//            ClosingLeave closingLeave = employeeLeaveBalance.getClosingLeave();
//            AvailLeave availLeave = employeeLeaveBalance.getAvailLeave();
//            AccureLeave accureLeave = employeeLeaveBalance.getAccureLeave();
//
//            openingLeave.setMedicalLeave(closingLeave.getMedicalLeave());
//            openingLeave.setEarnLeave(closingLeave.getEarnLeave());
//            openingLeave.setClearanceLeave(closingLeave.getClearanceLeave());
//            openingLeave.setTotalLeave(closingLeave.getTotalLeave());
//
//            availLeave.setTotalLeave(BigDecimal.ZERO);
//            availLeave.setClearanceLeave(BigDecimal.ZERO);
//            availLeave.setEarnLeave(BigDecimal.ZERO);
//            availLeave.setMedicalLeave(BigDecimal.ZERO);
//
//            closingLeave.setMedicalLeave(BigDecimal.ZERO);
//            closingLeave.setEarnLeave(BigDecimal.ZERO);
//            closingLeave.setClearanceLeave(BigDecimal.ZERO);
//            closingLeave.setTotalLeave(BigDecimal.ZERO);
//            employeeLeaveBalance.setOpeningLeave(openingLeave);
//            employeeLeaveBalance.setClosingLeave(closingLeave);
//            employeeLeaveBalance.setAvailLeave(availLeave);
//            EmployeeLeaveBalance balance= employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalance);
//            System.out.println("OL EL: "+balance.getOpeningLeave().getEarnLeave()+" & id: "+balance.getOpeningLeave().getId());
//
//        }
//    }
}