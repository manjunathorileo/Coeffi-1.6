package com.dfq.coeffi.repository.payroll;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;


@Transactional
@EnableJpaRepositories
public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, Long> {

	
	@Query("select es from EmployeeSalary es where es.employee.employeeCode=:employeeCode")
	Optional<EmployeeSalary> getEmployeeByEmployeeCode(@Param("employeeCode") String employeeCode);
	
	@Query("select es from EmployeeSalary es where es.employee.id=:id")
	Optional<EmployeeSalary> getEmployeeSalaryByEmployeeId(@Param("id") long id);

	/*@Query("select es from EmployeeSalary es where es.employee.id=:id")
	Optional<EmployeeSalary> getEmployeeByEmployeeCode(@Param("id") String employeeCode);*/
	
	@Query("select es.basicSalary from EmployeeSalary es ")
	List<EmployeeSalary> employeeDetails();

	@Query("select es from EmployeeSalary es where es.approve=true")
	List<EmployeeSalary> getActiveEmployee();
}
