package com.dfq.coeffi.Exit;

import com.dfq.coeffi.entity.hr.employee.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeExitDetailsService {
    EmployeeExitDetails createExitDetail(EmployeeExitDetails employeeExitDetails);
    List<EmployeeExitDetails> createExitDetails(List<EmployeeExitDetails> employeeExitDetails);
    Optional<EmployeeExitDetails> getExitDetailsById(long id);
    List<EmployeeExitDetails> getAllExitDetails();
    List<EmployeeExitDetails> getExitDetailsByEmpl(Employee employee);

}
