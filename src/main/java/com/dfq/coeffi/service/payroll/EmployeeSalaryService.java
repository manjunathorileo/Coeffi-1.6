package com.dfq.coeffi.service.payroll;

import com.dfq.coeffi.entity.payroll.EmployeeSalary;

import java.util.List;
import java.util.Optional;


public interface EmployeeSalaryService 
{
	public EmployeeSalary createEmployeeSalary(EmployeeSalary employeeSalary);
	List<EmployeeSalary> listAllEmployeeSalary();
	Optional<EmployeeSalary> getEmployeeSalary(long id);
	void deleteEmployeeSalary(long id);
	Optional<EmployeeSalary> getEmployeeByEmployeeCode(String employeeCode);
	Optional<EmployeeSalary> getEmployeeSalaryByEmployeeId(long id);
	List<EmployeeSalary> employeeDetails();
	List<EmployeeSalary>getActiveEmployee();

}
