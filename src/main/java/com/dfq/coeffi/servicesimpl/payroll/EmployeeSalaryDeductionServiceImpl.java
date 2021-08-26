package com.dfq.coeffi.servicesimpl.payroll;

import com.dfq.coeffi.entity.payroll.EmployeeSalaryDeduction;
import com.dfq.coeffi.master.assignShifts.EmployeeShiftAssignmentService;
import com.dfq.coeffi.repository.payroll.EmployeeSalaryDeductionRepository;
import com.dfq.coeffi.service.payroll.EmployeeSalaryDeductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeSalaryDeductionServiceImpl implements EmployeeSalaryDeductionService {

    private EmployeeSalaryDeductionRepository employeeSalaryDeductionRepository;

    @Autowired
    public EmployeeSalaryDeductionServiceImpl(EmployeeSalaryDeductionRepository employeeSalaryDeductionRepository){
        this.employeeSalaryDeductionRepository = employeeSalaryDeductionRepository;
    }

    @Override
    public EmployeeSalaryDeduction saveEmployeeSalaryDeduction(EmployeeSalaryDeduction employeeSalaryDeduction) {
        return employeeSalaryDeductionRepository.save(employeeSalaryDeduction);
    }

    @Override
    public List<EmployeeSalaryDeduction> getAllEmployeeSalaryDeductions() {
        return employeeSalaryDeductionRepository.findAll();
    }

    @Override
    public Optional<EmployeeSalaryDeduction> getEmployeeSalaryDeductionById(long id) {
        return Optional.ofNullable(employeeSalaryDeductionRepository.findOne(id));
    }

    @Override
    public List<EmployeeSalaryDeduction> getEmployeeSalaryDeductionsByEmployeeId(long employeeId,Date startDate,Date endDate) {
        System.out.println(employeeSalaryDeductionRepository.getEmployeeSalaryDeductionsByEmployeeId(employeeId,startDate,endDate));
        return employeeSalaryDeductionRepository.getEmployeeSalaryDeductionsByEmployeeId(employeeId,startDate,endDate);
    }
}
