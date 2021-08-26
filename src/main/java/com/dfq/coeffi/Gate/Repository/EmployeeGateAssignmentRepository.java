package com.dfq.coeffi.Gate.Repository;

import com.dfq.coeffi.Gate.Entity.EmployeeGateAssignment;
import com.dfq.coeffi.master.assignShifts.EmployeeShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaAuditing
@Transactional
public interface EmployeeGateAssignmentRepository extends JpaRepository<EmployeeGateAssignment,Long> {
    @Query("SELECT gate FROM EmployeeGateAssignment gate WHERE gate.employee.id =:employeeId")
    EmployeeGateAssignment findByEmployeeId(@Param("employeeId") long employeeId);

}
