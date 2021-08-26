package com.dfq.coeffi.repository.payroll;

import java.util.Optional;

import javax.transaction.Transactional;

import com.dfq.coeffi.entity.payroll.EmployeeLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

@Transactional
@EnableJpaRepositories
public interface EmployeeLoanRepository extends JpaRepository<EmployeeLoan, Long>
{
	@Query("select e from  EmployeeLoan e where e.employee.id = :id")
	Optional<EmployeeLoan> getLoanByEmployeeId(@Param("id") long id);

}
