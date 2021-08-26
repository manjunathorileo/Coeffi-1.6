package com.dfq.coeffi.master.assignShifts;

import com.dfq.coeffi.master.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface EmployeeShiftAssignmentRepository extends JpaRepository<EmployeeShiftAssignment,Long> {

    Optional<EmployeeShiftAssignment> findByShift(Shift shift);

    @Query("SELECT shift FROM EmployeeShiftAssignment shift WHERE (:todayDate BETWEEN shift.fromDate AND shift.toDate) AND shift.employee.id =:employeeId")
    EmployeeShiftAssignment getCurrentShiftByEmployeeId(@Param("employeeId") long employeeId, @Param("todayDate") Date todayDate);

    @Query("SELECT shift FROM EmployeeShiftAssignment shift WHERE (:todayDate BETWEEN shift.fromDate AND shift.toDate) AND shift.shift=:shift")
    List<EmployeeShiftAssignment> getEmployeeListByShiftAndDate(@Param("todayDate") Date todayDate, @Param("shift") Shift shift);

    @Query("SELECT shift FROM EmployeeShiftAssignment shift WHERE shift.employee.id =:employeeId")
    List<EmployeeShiftAssignment> findByEmployeeId(@Param("employeeId") long employeeId);
}
