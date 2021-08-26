package com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class EmployeeLeaveBalanceServiceImpl implements EmployeeLeaveBalanceService {

    private EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;

    @Autowired
    public EmployeeLeaveBalanceServiceImpl(EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository) {
        this.employeeLeaveBalanceRepository = employeeLeaveBalanceRepository;
    }

    @Override
    public EmployeeLeaveBalance createEmployeeLeaveBalance(EmployeeLeaveBalance employeeLeaveBalance) {
        return employeeLeaveBalanceRepository.save(employeeLeaveBalance);
    }

    @Override
    public List<EmployeeLeaveBalance> getAllEmployeeLeaveBalance() {
        return employeeLeaveBalanceRepository.findAll();
    }

    @Override
    public EmployeeLeaveBalance getEmployeeLeaveBalanceById(long id) {
        return employeeLeaveBalanceRepository.findOne(id);
    }

    @Override
    public Optional<EmployeeLeaveBalance> getEmployeeLeaveBalanceByEmployeeId(long employeeId) {
        return employeeLeaveBalanceRepository.findByEmployee(employeeId);
    }

    @Override
    public List<EmployeeLeaveBalance> getEmployeeLeaveBalanceByFinancialYearId(long financialYearId) {
        List<EmployeeLeaveBalance> employeeLeaveBalanceList=employeeLeaveBalanceRepository.findByAcademicYear(financialYearId);
        return employeeLeaveBalanceList;
    }

    @Override
    public EmployeeLeaveBalance getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(long employeeId, long academicYearId) {
        List<EmployeeLeaveBalance> employeeLeaveBalances = employeeLeaveBalanceRepository.findByEmployeeIdByAcademicYearId(employeeId, academicYearId);
        if (employeeLeaveBalances.size() > 0) {
            Collections.reverse(employeeLeaveBalances);
            EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalances.get(0);
            return employeeLeaveBalance;
        } else
            return null;
    }

    @Override
    public EmployeeLeaveBalance getEmployeeLeaveBalanceByEmployeeIdByFinancialYearIdByCurrentMonth(long employeeId, long academicYearId, long currentMonth) {
        List<EmployeeLeaveBalance> employeeLeaveBalances = employeeLeaveBalanceRepository.findByEmployeeIdByAcademicYearIdByCurrentMonth(employeeId, academicYearId, currentMonth);
//        Collections.reverse(employeeLeaveBalances);
//        EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalances.get(0);
        if (employeeLeaveBalances.size() > 0) {
            Collections.reverse(employeeLeaveBalances);
            EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalances.get(0);
            return employeeLeaveBalance;
        } else
            return null;
    }
}
