package com.dfq.coeffi.CanteenManagement.employeeBalance;

import com.dfq.coeffi.entity.hr.employee.Employee;

public interface EmployeeBalanceService {

    EmployeeBalance saveEmployeeBalance(EmployeeBalance employeeBalance);
    EmployeeBalance getByEmpId(Employee employee);
}
