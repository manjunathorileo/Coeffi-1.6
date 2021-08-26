package com.dfq.coeffi.employeePerformanceManagement.serviceImpl;

import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagement;
import com.dfq.coeffi.employeePerformanceManagement.entity.GoalStatusEnum;
import com.dfq.coeffi.employeePerformanceManagement.repository.EmployeePerformanceManagementRepository;
import com.dfq.coeffi.employeePerformanceManagement.service.EmployeePerformanceManagementService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeePerformanceManagementServiceImpl implements EmployeePerformanceManagementService {

    @Autowired
    private EmployeePerformanceManagementRepository employeePerformanceManagementRepository;

    @Override
    public EmployeePerformanceManagement createEmployeePerformanceManagement(EmployeePerformanceManagement employeePerformanceManagement) {
        return employeePerformanceManagementRepository.save(employeePerformanceManagement);
    }

    @Override
    public Optional<EmployeePerformanceManagement> getEmployeePerformanceManagement(long employeePerformanceManagementId) {
        return employeePerformanceManagementRepository.findById(employeePerformanceManagementId);
    }

    @Override
    public List<EmployeePerformanceManagement> getAllEmployeePerformanceManagement() {
        return employeePerformanceManagementRepository.findAll();
    }

    @Override
    public List<EmployeePerformanceManagement> getEmployeePerformanceManagementNyEmplId(Employee employee) {
        return employeePerformanceManagementRepository.findByEmployee(employee);
    }

    @Override
    public List<EmployeePerformanceManagement> getEmployeePerformanceManagementByGoalStatus(String goalStatus) {
        return employeePerformanceManagementRepository.findByGoalStatus(goalStatus);
    }

    @Override
    public List<EmployeePerformanceManagement> getEmployeePerformanceManagement(Employee employee, GoalStatusEnum goalStatus) {
        return null;
    }

    @Override
    public List<EmployeePerformanceManagement> getEmployeePerformanceManagementByGoalStatus(GoalStatusEnum goalStatus, boolean status, Employee firstManager) {
        return employeePerformanceManagementRepository.findByGoalStatusAndStatus(goalStatus,status,firstManager);
    }

    @Override
    public List<EmployeePerformanceManagement> getEmployeePerformanceManagementByGoalStatusByEmployee(GoalStatusEnum goalStatus, boolean status, Employee employee) {
        return employeePerformanceManagementRepository.findByGoalStatusAndStatusAnd2Employee(goalStatus,status,employee);
    }

    @Override
    public List<EmployeePerformanceManagement> getEmployeePerformanceManagementByGoalStatusBy2ndMgr(GoalStatusEnum goalStatus, boolean status, Employee secondManager) {
        return employeePerformanceManagementRepository.findByGoalStatusAndStatusAnd2ndMgr(goalStatus,status,secondManager);
    }

}
