package com.dfq.coeffi.entity.leave;

import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.OpeningLeave;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRule;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRuleService;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.repository.leave.AvailLeaveRepo;
import com.dfq.coeffi.repository.leave.ClosingLeaveRepo;
import com.dfq.coeffi.repository.leave.OpeningLeaveRepo;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;


@EnableScheduling
@RestController
public class LeaveBalanceSchedular {
    @Autowired
    AcademicYearService academicYearService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeLeaveBalanceService employeeLeaveBalanceService;
    @Autowired
    AvailLeaveRepo availLeaveRepo;
    @Autowired
    ClosingLeaveRepo closingLeaveRepo;
    @Autowired
    OpeningLeaveRepo openingLeaveRepo;
    @Autowired
    EarningLeaveRuleService earningLeaveRuleService;

    //    @Scheduled(cron = "0 0 10 1 1/1 *")
    @GetMapping("employee-leave-balance/refresh-leaves")
    public void refreshEmployeeLeaveBalances() {

        Optional<AcademicYear> academicYearOptional = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear=null;

        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employee.getId(),academicYearOptional.get().getId());

            OpeningLeave openingLeave = employeeLeaveBalance.getOpeningLeave();
            ClosingLeave closingLeave = employeeLeaveBalance.getClosingLeave();
            AvailLeave availLeave = employeeLeaveBalance.getAvailLeave();

            openingLeave.setMedicalLeave(closingLeave.getMedicalLeave());
            openingLeave.setEarnLeave(closingLeave.getEarnLeave());
            openingLeave.setClearanceLeave(closingLeave.getClearanceLeave());
            openingLeave.setTotalLeave(closingLeave.getTotalLeave());

            availLeave.setTotalLeave(BigDecimal.ZERO);
            availLeave.setClearanceLeave(BigDecimal.ZERO);
            availLeave.setEarnLeave(BigDecimal.ZERO);
            availLeave.setMedicalLeave(BigDecimal.ZERO);

            closingLeave.setMedicalLeave(BigDecimal.ZERO);
            closingLeave.setEarnLeave(BigDecimal.ZERO);
            closingLeave.setClearanceLeave(BigDecimal.ZERO);
            closingLeave.setTotalLeave(BigDecimal.ZERO);

            employeeLeaveBalance.setOpeningLeave(openingLeave);
            employeeLeaveBalance.setClosingLeave(closingLeave);
            employeeLeaveBalance.setAvailLeave(availLeave);
            EmployeeLeaveBalance balance = employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalance);

        }
    }


    @GetMapping("allocate-meadical-leaves")
    @Scheduled(cron = "0 0 0 1 1 *")
    public void allocateLeave() {
        List<Employee> employeeList = employeeService.findAll();
        for (Employee employee : employeeList) {

            System.out.println("FRESH YEAR ENTRY");
            long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            EmployeeLeaveBalance employeeLeaveBalances = new EmployeeLeaveBalance();
            Optional<AcademicYear> academicYearOptional = academicYearService.getActiveAcademicYear();
            employeeLeaveBalances.setEmployee(employee);
            employeeLeaveBalances.setAcademicYear(academicYearOptional.get());
            employeeLeaveBalances.setStatus(true);
            OpeningLeave openingLeave = new OpeningLeave();
            ClosingLeave closingLeave = new ClosingLeave();
            AvailLeave availLeave = new AvailLeave();

            List<EarningLeaveRule> earningLeaveRuleList = earningLeaveRuleService.getAllEarningLeaveRule();
            for (EarningLeaveRule earningLeaveRule : earningLeaveRuleList) {
                EmployeeLeaveBalance employeeLeaveBalanceOld = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employee.getId(), academicYearOptional.get().getId());
                if (earningLeaveRule.getLeaveType().equals(LeaveType.EARN_LEAVE)) {
                    openingLeave.setEarnLeave(employeeLeaveBalanceOld.getOpeningLeave().getEarnLeave());
                    closingLeave.setEarnLeave(employeeLeaveBalanceOld.getClosingLeave().getEarnLeave());
                } else {
                    openingLeave.setEarnLeave(employeeLeaveBalanceOld.getOpeningLeave().getEarnLeave());
                    closingLeave.setEarnLeave(employeeLeaveBalanceOld.getClosingLeave().getEarnLeave());
                }

                if (earningLeaveRule.getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {
                    if (employee.getEsiNumber().compareTo("0") > 0) {
                        openingLeave.setMedicalLeave(new BigDecimal(0));
                        closingLeave.setMedicalLeave(new BigDecimal(0));
                    } else {
                        openingLeave.setMedicalLeave(new BigDecimal(10));
                        closingLeave.setMedicalLeave(new BigDecimal(10));
                    }
                } else {
                    if (employee.getEsiNumber().compareTo("0") > 0) {
                        openingLeave.setMedicalLeave(new BigDecimal(0));
                        closingLeave.setMedicalLeave(new BigDecimal(0));
                    } else {
                        openingLeave.setMedicalLeave(new BigDecimal(10));
                        closingLeave.setMedicalLeave(new BigDecimal(10));

                    }
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
