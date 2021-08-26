package com.dfq.coeffi.servicesimpl.payroll;

import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import com.dfq.coeffi.repository.payroll.EmployeeSalaryRepository;
import com.dfq.coeffi.service.payroll.EmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeSalaryServiceImpl implements EmployeeSalaryService {

	private final EmployeeSalaryRepository employeeSalaryRepository;
	
	@Autowired
	public EmployeeSalaryServiceImpl(EmployeeSalaryRepository employeeSalaryRepository)
	{
		this.employeeSalaryRepository = employeeSalaryRepository;
	}
	
	@Override
	public EmployeeSalary createEmployeeSalary(EmployeeSalary employeeSalary) {
		return employeeSalaryRepository.save(employeeSalary);
	}

	@Override
	public List<EmployeeSalary> listAllEmployeeSalary() {
		return employeeSalaryRepository.findAll();
	}

	@Override
	public Optional<EmployeeSalary> getEmployeeSalary(long id) {
		return ofNullable(employeeSalaryRepository.findOne(id));
	}

	@Override
	public void deleteEmployeeSalary(long id) {
		employeeSalaryRepository.delete(id);
	}


	@Override
	public Optional<EmployeeSalary> getEmployeeByEmployeeCode(String employeeCode) {
		return employeeSalaryRepository.getEmployeeByEmployeeCode(employeeCode);
	}

	@Override
	public Optional<EmployeeSalary> getEmployeeSalaryByEmployeeId(long id) {
		return employeeSalaryRepository.getEmployeeSalaryByEmployeeId(id);
	}

	@Override
	public List<EmployeeSalary> employeeDetails() {
		
		return employeeSalaryRepository.employeeDetails();
	}

	@Override
	public List<EmployeeSalary> getActiveEmployee() {
		return employeeSalaryRepository.getActiveEmployee();
	}

	
}
