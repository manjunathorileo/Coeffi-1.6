package com.dfq.coeffi.service.payroll;

import com.dfq.coeffi.entity.payroll.EmployeeAppraisal;

import java.util.List;
import java.util.Optional;


public interface EmployeeAppraisalService 
{
	public EmployeeAppraisal createEmployeeAppraisal(EmployeeAppraisal employeeAppraisal);
	List<EmployeeAppraisal> listAllEmployeeAppraisal();
	Optional<EmployeeAppraisal> getEmployeeAppraisal(long id);
	void deleteEmployeeAppraisal(long id);
	Optional<EmployeeAppraisal> getAppraisalByEmployee(long id);
	
}
