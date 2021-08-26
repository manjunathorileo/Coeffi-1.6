package com.dfq.coeffi.repository.payroll;

import com.dfq.coeffi.entity.payroll.EmployeeSalaryDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface EmployeeSalaryDeductionRepository extends JpaRepository<EmployeeSalaryDeduction,Long> {

    @Query("SELECT esd FROM EmployeeSalaryDeduction esd WHERE (DATE(esd.recordedOn) BETWEEN :startDate AND :endDate) AND esd.employee.id=:employeeId")
    List<EmployeeSalaryDeduction> getEmployeeSalaryDeductionsByEmployeeId(@Param("employeeId") long employeeId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
