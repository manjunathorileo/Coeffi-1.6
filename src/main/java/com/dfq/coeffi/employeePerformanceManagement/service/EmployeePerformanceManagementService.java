package com.dfq.coeffi.employeePerformanceManagement.service;

import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagement;
import com.dfq.coeffi.employeePerformanceManagement.entity.GoalStatusEnum;
import com.dfq.coeffi.entity.hr.employee.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeePerformanceManagementService {

    EmployeePerformanceManagement createEmployeePerformanceManagement(EmployeePerformanceManagement employeePerformanceManagement);
    Optional<EmployeePerformanceManagement> getEmployeePerformanceManagement(long employeePerformanceManagementId);
    List<EmployeePerformanceManagement> getAllEmployeePerformanceManagement();
    List<EmployeePerformanceManagement> getEmployeePerformanceManagementNyEmplId(Employee employee);
    List<EmployeePerformanceManagement> getEmployeePerformanceManagementByGoalStatus(String goalStatus);
    List<EmployeePerformanceManagement> getEmployeePerformanceManagement(Employee empId, GoalStatusEnum goalStatus);
    List<EmployeePerformanceManagement> getEmployeePerformanceManagementByGoalStatus(GoalStatusEnum goalStatus, boolean status, Employee firstManager);
    List<EmployeePerformanceManagement> getEmployeePerformanceManagementByGoalStatusByEmployee(GoalStatusEnum goalStatus, boolean status, Employee employee);
    List<EmployeePerformanceManagement> getEmployeePerformanceManagementByGoalStatusBy2ndMgr(GoalStatusEnum goalStatus, boolean status, Employee secondManager);
}
