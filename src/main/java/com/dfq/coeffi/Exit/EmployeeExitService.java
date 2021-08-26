package com.dfq.coeffi.Exit;

import com.dfq.coeffi.entity.hr.employee.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeExitService {

    EmployeeExit createExit(EmployeeExit employeeExit);
    Optional<EmployeeExit> getExitById(long id);
    List<EmployeeExit> getAllExit();
    Optional<EmployeeExit> getExitByEmpl(Employee employee);
}
