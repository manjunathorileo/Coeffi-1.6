package com.dfq.coeffi.Exit;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeExitServiceImpl implements EmployeeExitService {

    @Autowired
    private EmployeeExitRepository employeeExitRepository;

    @Override
    public EmployeeExit createExit(EmployeeExit employeeExit) {
        return employeeExitRepository.save(employeeExit);
    }

    @Override
    public Optional<EmployeeExit> getExitById(long id) {
        return employeeExitRepository.findById(id);
    }

    @Override
    public List<EmployeeExit> getAllExit() {
        return employeeExitRepository.findAll();
    }

    @Override
    public Optional<EmployeeExit> getExitByEmpl(Employee employee) {
        return employeeExitRepository.findByEmployee(employee);
    }
}
