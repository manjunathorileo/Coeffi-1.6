package com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface EmployeeLeaveBalanceRepository extends JpaRepository<EmployeeLeaveBalance,Long> {

    @Query("select e from EmployeeLeaveBalance e where e.employee.id = :employeeId AND e.status = true")
    Optional<EmployeeLeaveBalance> findByEmployee(@Param("employeeId") long employeeId);

    @Query("select e from EmployeeLeaveBalance e where e.academicYear.id = :financialYearId AND e.status = true")
    List<EmployeeLeaveBalance> findByAcademicYear(@Param("financialYearId") long financialYearId);

    @Query("select e from EmployeeLeaveBalance e where e.employee.id = :employeeId AND e.academicYear.id = :academicYearId AND e.status = true")
    List<EmployeeLeaveBalance> findByEmployeeIdByAcademicYearId(@Param("employeeId") long employeeId, @Param("academicYearId") long academicYearId);

    @Query("select e from EmployeeLeaveBalance e where e.employee.id = :employeeId AND e.academicYear.id = :academicYearId AND e.currentMonth= :currentMonth AND e.status = true")
    List<EmployeeLeaveBalance> findByEmployeeIdByAcademicYearIdByCurrentMonth(@Param("employeeId") long employeeId, @Param("academicYearId") long academicYearId, @Param("currentMonth") long currentMonth);
}
