package com.dfq.coeffi.servicesimpl.payroll;

import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

import com.dfq.coeffi.entity.payroll.EmployeeAppraisal;
import com.dfq.coeffi.repository.payroll.EmployeeAppraisalRepository;
import com.dfq.coeffi.service.payroll.EmployeeAppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeAppraisalServiceImpl implements EmployeeAppraisalService
{

	private final EmployeeAppraisalRepository employeeAppraisalRepository;
	
	@Autowired
	public EmployeeAppraisalServiceImpl(EmployeeAppraisalRepository employeeAppraisalRepository)
	{
		this.employeeAppraisalRepository = employeeAppraisalRepository;
	}
	
	@Override
	public EmployeeAppraisal createEmployeeAppraisal(EmployeeAppraisal employeeAppraisal)
	{
		employeeAppraisal.setStatus(true);
		return employeeAppraisalRepository.save(employeeAppraisal);
	}

	@Override
	public List<EmployeeAppraisal> listAllEmployeeAppraisal() {
		return employeeAppraisalRepository.listAllEmployeeAppraisal();
	}

	@Override
	public Optional<EmployeeAppraisal> getEmployeeAppraisal(long id) {
		return ofNullable(employeeAppraisalRepository.findOne(id));
	}

	@Override
	public void deleteEmployeeAppraisal(long id) {
		employeeAppraisalRepository.delete(id);
	}

	@Override
	public Optional<EmployeeAppraisal> getAppraisalByEmployee(long id) {
		return employeeAppraisalRepository.getAppraisalByEmployee(id);
	}

}
