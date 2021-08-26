package com.dfq.coeffi.service.payroll;

import com.dfq.coeffi.entity.payroll.EmployeeLoan;

import java.util.List;
import java.util.Optional;


public interface EmployeeLoanService 
{
	EmployeeLoan createEmployeeLoan(EmployeeLoan employeeLoan);
	void deleteEmployeeLoan(long id);
	Optional<EmployeeLoan> getEmployeeLoan(long id);
	List<EmployeeLoan> listAllEmployeeLoans();
	Optional<EmployeeLoan> getLoanByEmployeeId(long id);
	boolean isEmployeeLoanTaken(long id);

}
