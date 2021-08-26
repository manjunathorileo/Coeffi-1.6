package com.dfq.coeffi.employeePermanentContract.services;

import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


public interface PermanentContractService {
    EmpPermanentContract save(EmpPermanentContract empPermanentContract);

    EmpPermanentContract get(long id);

    EmpPermanentContract get(String employeeCode);

    List<EmpPermanentContract> getAll(boolean status);

    List<PermanentContractAttendance> getTodayMarkedEmployeeAttendance(Date todayDate);

    List<PermanentContractAttendance> getEmployeeAttendanceByDepartment(Date startDate, long departmentId);

    List<EmpPermanentContract> getEmployeesByDepartment(long departmentId);


}
