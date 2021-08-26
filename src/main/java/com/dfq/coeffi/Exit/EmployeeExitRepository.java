package com.dfq.coeffi.Exit;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeExitRepository extends JpaRepository<EmployeeExit, Long> {

    Optional<EmployeeExit> findById(long id);

    Optional<EmployeeExit> findByEmployee(Employee employee);
}