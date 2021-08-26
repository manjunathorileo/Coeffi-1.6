package com.dfq.coeffi.service.payroll;

import com.dfq.coeffi.entity.payroll.EmployeeSalaryDeduction;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeSalaryDeductionService {

    EmployeeSalaryDeduction saveEmployeeSalaryDeduction(EmployeeSalaryDeduction employeeSalaryDeduction);
    List<EmployeeSalaryDeduction> getAllEmployeeSalaryDeductions();
    Optional<EmployeeSalaryDeduction> getEmployeeSalaryDeductionById(long id);
    List<EmployeeSalaryDeduction> getEmployeeSalaryDeductionsByEmployeeId(long employeeId, Date startDate, Date endDate);

}
