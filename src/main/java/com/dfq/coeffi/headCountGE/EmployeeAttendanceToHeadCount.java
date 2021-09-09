package com.dfq.coeffi.headCountGE;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@EnableScheduling
@Slf4j
@RestController
@Configuration
public class EmployeeAttendanceToHeadCount extends BaseController {

    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    PermanentContractService permanentContractService;


    @Scheduled(initialDelay = 10020, fixedRate = 60000)
    public void syncCurrentDayAttendanceToHeadcount() {
        System.out.println("-----------------------Pushing attendance data to head count-----------------------------");
        List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceService.getTodayMarkedEmployeeAttendance(new Date());
        for (EmployeeAttendance employeeAttendance : employeeAttendanceList) {
            if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                EmpPermanentContract empPermanentContract = permanentContractService.get(employeeAttendance.getEmployee().getEmployeeCode());
                if (empPermanentContract != null) {
                    PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeId(DateUtil.getTodayDate(), empPermanentContract.getId());
                    if (permanentContractAttendance == null) {

                        PermanentContractAttendance newEmployeeAttendance = new PermanentContractAttendance();
                        newEmployeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                        newEmployeeAttendance.setEmpId(empPermanentContract.getId());
                        newEmployeeAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                        if (employeeAttendance.getInTime() != null) {
                            newEmployeeAttendance.setInTime(employeeAttendance.getInTime());
                        }
                        if (employeeAttendance.getOutTime() != null) {
                            newEmployeeAttendance.setOutTime(employeeAttendance.getOutTime());
                        }
                        newEmployeeAttendance.setMaskWearing(false);
                        newEmployeeAttendance.setEntryBodyTemperature(0);
                        newEmployeeAttendance.setEntryGateNumber("01");
                        newEmployeeAttendance.setMarkedOn(employeeAttendance.getMarkedOn());
                        newEmployeeAttendance.setRecordedTime(new Date());
                        newEmployeeAttendance.setEmployeeName(empPermanentContract.getFirstName());
                        permanentContractAttendanceRepo.save(newEmployeeAttendance);
                    } else {
                        PermanentContractAttendance newEmployeeAttendance = permanentContractAttendance;
                        newEmployeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                        newEmployeeAttendance.setEmpId(empPermanentContract.getId());
                        newEmployeeAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                        if (employeeAttendance.getInTime() != null) {
                            newEmployeeAttendance.setInTime(employeeAttendance.getInTime());
                        }
                        if (employeeAttendance.getOutTime() != null) {
                            newEmployeeAttendance.setOutTime(employeeAttendance.getOutTime());
                        }
                        newEmployeeAttendance.setMaskWearing(false);
                        newEmployeeAttendance.setEntryBodyTemperature(0);
                        newEmployeeAttendance.setEntryGateNumber("01");
                        newEmployeeAttendance.setMarkedOn(employeeAttendance.getMarkedOn());
                        newEmployeeAttendance.setRecordedTime(new Date());
                        newEmployeeAttendance.setEmployeeName(empPermanentContract.getFirstName());
                        permanentContractAttendanceRepo.save(newEmployeeAttendance);
                    }

                }
            }
        }
    }


}
