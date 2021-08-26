package com.dfq.coeffi.Exit;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeExitDetailsServiceImpl implements EmployeeExitDetailsService {
    @Autowired
    EmployeeExitDetailsRepository employeeExitDetailsRepository;


    @Override
    public EmployeeExitDetails createExitDetail(EmployeeExitDetails employeeExitDetails) {
        return employeeExitDetailsRepository.save(employeeExitDetails);
    }

    @Override
    public List<EmployeeExitDetails> createExitDetails(List<EmployeeExitDetails> employeeExitDetails) {
        return employeeExitDetailsRepository.save(employeeExitDetails);
    }

    @Override
    public Optional<EmployeeExitDetails> getExitDetailsById(long id) {
        return employeeExitDetailsRepository.findById(id);
    }


    @Override
    public List<EmployeeExitDetails> getAllExitDetails() {
        return employeeExitDetailsRepository.findAll();
    }

    @Override
    public List<EmployeeExitDetails> getExitDetailsByEmpl(Employee employee) {
        return employeeExitDetailsRepository.findByEmployeeId(employee);
    }

}
