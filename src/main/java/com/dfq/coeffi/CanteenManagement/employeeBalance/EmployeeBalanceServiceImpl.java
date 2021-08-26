package com.dfq.coeffi.CanteenManagement.employeeBalance;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeBalanceServiceImpl implements EmployeeBalanceService {

    @Autowired
    private EmployeeBalanceRepository employeeBalanceRepository;

    @Override
    public EmployeeBalance saveEmployeeBalance(EmployeeBalance employeeBalance) {
        return employeeBalanceRepository.save(employeeBalance);
    }

    @Override
    public EmployeeBalance getByEmpId(Employee employee) {
        return employeeBalanceRepository.findByEmployee(employee);
    }
}
