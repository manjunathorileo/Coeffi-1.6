package com.dfq.coeffi.Exit;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeExitDetailsRepository extends JpaRepository<EmployeeExitDetails, Long> {

    Optional<EmployeeExitDetails> findById(long id);

    @Query("select exd from EmployeeExitDetails exd where exd.employee = :employee")
    List<EmployeeExitDetails> findByEmployeeId(@Param("employee") Employee employee);
}
