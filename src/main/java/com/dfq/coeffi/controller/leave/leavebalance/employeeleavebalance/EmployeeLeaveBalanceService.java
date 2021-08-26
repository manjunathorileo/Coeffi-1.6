package com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeLeaveBalanceService {

    EmployeeLeaveBalance createEmployeeLeaveBalance(EmployeeLeaveBalance employeeLeaveBalance);
    List<EmployeeLeaveBalance> getAllEmployeeLeaveBalance();
    EmployeeLeaveBalance getEmployeeLeaveBalanceById(long id);
    Optional<EmployeeLeaveBalance> getEmployeeLeaveBalanceByEmployeeId(long employeeId);
    List<EmployeeLeaveBalance> getEmployeeLeaveBalanceByFinancialYearId(long financialYearId);
    EmployeeLeaveBalance getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(long employeeId, long academicYearId);
    EmployeeLeaveBalance getEmployeeLeaveBalanceByEmployeeIdByFinancialYearIdByCurrentMonth(long employeeId, long academicYearId, long currentMonth);
}
