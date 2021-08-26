package com.dfq.coeffi.employeePermanentContract.services;

import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PermenantContractServiceImpl implements PermanentContractService {
    @Autowired
    PermanentContractRepo permanentContractRepo;
    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;

    @Override
    public EmpPermanentContract save(EmpPermanentContract empPermanentContract) {
        return permanentContractRepo.save(empPermanentContract);
    }

    @Override
    public EmpPermanentContract get(long id) {
        return permanentContractRepo.findOne(id);
    }

    @Override
    public EmpPermanentContract get(String employeeCode) {
        return permanentContractRepo.findByEmployeeCode(employeeCode);
    }

    @Override
    public List<EmpPermanentContract> getAll(boolean status) {
        return permanentContractRepo.findAll();
    }

    @Override
    public List<PermanentContractAttendance> getTodayMarkedEmployeeAttendance(Date todayDate) {
        return permanentContractAttendanceRepo.findByMarkedOnAsc(todayDate);
    }

    @Override
    public List<PermanentContractAttendance> getEmployeeAttendanceByDepartment(Date startDate, long departmentId) {
        return permanentContractAttendanceRepo.getEmployeeAttendanceByDepartment(startDate, departmentId);
    }

    @Override
    public List<EmpPermanentContract> getEmployeesByDepartment(long departmentId) {
        return permanentContractRepo.findByDepartment(departmentId);
    }

}
