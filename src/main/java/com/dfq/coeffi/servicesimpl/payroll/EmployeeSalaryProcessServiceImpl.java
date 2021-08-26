package com.dfq.coeffi.servicesimpl.payroll;

import static java.util.Optional.ofNullable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import com.dfq.coeffi.entity.payroll.SalaryApprovalStatus;
import com.dfq.coeffi.repository.payroll.EmployeeSalaryProcessRepository;
import com.dfq.coeffi.service.payroll.EmployeeSalaryProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmployeeSalaryProcessServiceImpl implements EmployeeSalaryProcessService {

	private final EmployeeSalaryProcessRepository employeeSalaryProcessRepository;
	
	@Autowired
	public EmployeeSalaryProcessServiceImpl(EmployeeSalaryProcessRepository employeeSalaryProcessRepository)
	{
		this.employeeSalaryProcessRepository = employeeSalaryProcessRepository;
	}
	@Override
	public EmployeeSalaryProcess createEmployeeSalaryProcess(EmployeeSalaryProcess employeeSalaryProcess) {
		return employeeSalaryProcessRepository.save(employeeSalaryProcess);
	}

	@Override
	public List<EmployeeSalaryProcess> listAllEmployeeSalaryProcess() {
		return employeeSalaryProcessRepository.findAll();
	}

	@Override
	public Optional<EmployeeSalaryProcess> getEmployeeSalaryProcess(long id) {
		return ofNullable(employeeSalaryProcessRepository.findOne(id));
	}

	@Override
	public Optional<EmployeeSalaryProcess> getEmployeeSalaryProcessByMonth(long employeeId,String inputMonth,String inputYear) {
		return employeeSalaryProcessRepository.getEmployeeSalaryProcessByMonth(employeeId,inputMonth,inputYear);
	}

	@Override
	public List<EmployeeSalaryProcess> getApprovedList(SalaryApprovalStatus salaryApprovalStatus) {
		return employeeSalaryProcessRepository.findBySalaryApprovalStatus(salaryApprovalStatus);
	}

	@Override
	public Optional<EmployeeSalaryProcess> getEmployeeSalaryCreatedByMonth(long employeeId, String inputMonth, String inputYear) {
		return employeeSalaryProcessRepository.getEmployeeSalaryCreatedByMonth(employeeId,inputMonth,inputYear);
	}


	@Override
	public List<EmployeeSalaryProcess> getEmployeeSalaryByMonthAndYear(String inputMonth, String inputYear,SalaryApprovalStatus salaryApprovalStatus) {
		return employeeSalaryProcessRepository.findBySalaryMonthAndSalaryYearAndSalaryApprovalStatus(inputMonth,inputYear,salaryApprovalStatus);
	}

	@Override
	public void delete(long id) {
		employeeSalaryProcessRepository.delete(id);
	}
}
