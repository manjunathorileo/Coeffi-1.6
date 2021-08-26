package com.dfq.coeffi.service.payroll;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import com.dfq.coeffi.entity.payroll.SalaryApprovalStatus;

public interface EmployeeSalaryProcessService {
    EmployeeSalaryProcess createEmployeeSalaryProcess(EmployeeSalaryProcess employeeSalaryProcess);

    List<EmployeeSalaryProcess> listAllEmployeeSalaryProcess();

    Optional<EmployeeSalaryProcess> getEmployeeSalaryProcess(long id);

    Optional<EmployeeSalaryProcess> getEmployeeSalaryProcessByMonth(long employeeId, String inputMonth, String inputYear);

    List<EmployeeSalaryProcess> getApprovedList(SalaryApprovalStatus salaryApprovalStatus);

    Optional<EmployeeSalaryProcess> getEmployeeSalaryCreatedByMonth(long employeeId, String inputMonth, String inputYear);

    List<EmployeeSalaryProcess> getEmployeeSalaryByMonthAndYear(String inputMonth, String inputYear, SalaryApprovalStatus salaryApprovalStatus);

    void delete(long id);

}
