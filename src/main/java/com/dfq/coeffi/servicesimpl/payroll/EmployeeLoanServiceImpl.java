package com.dfq.coeffi.servicesimpl.payroll;

import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

import com.dfq.coeffi.entity.payroll.EmployeeLoan;
import com.dfq.coeffi.repository.payroll.EmployeeLoanRepository;
import com.dfq.coeffi.service.payroll.EmployeeLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmployeeLoanServiceImpl implements EmployeeLoanService {

	private final EmployeeLoanRepository employeeLoanRepository;
	
	@Autowired
	public EmployeeLoanServiceImpl(EmployeeLoanRepository employeeLoanRepository)
	{
		this.employeeLoanRepository=employeeLoanRepository;
	}
	
	@Override
	public EmployeeLoan createEmployeeLoan(EmployeeLoan employeeLoan) {
		return employeeLoanRepository.save(employeeLoan);
	}

	@Override
	public void deleteEmployeeLoan(long id) {
		employeeLoanRepository.delete(id);
	}

	@Override
	public Optional<EmployeeLoan> getEmployeeLoan(long id) {
		return ofNullable(employeeLoanRepository.findOne(id));
	}

	@Override
	public List<EmployeeLoan> listAllEmployeeLoans() {
 		return employeeLoanRepository.findAll();
	}

	@Override
	public Optional<EmployeeLoan> getLoanByEmployeeId(long id) {
		return employeeLoanRepository.getLoanByEmployeeId(id);
	}

	@Override
	public boolean isEmployeeLoanTaken(long id) {
		return employeeLoanRepository.exists(id);
	}

}
