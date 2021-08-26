package com.dfq.coeffi.repository.payroll;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.dfq.coeffi.entity.payroll.EmployeeAppraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;


@Transactional
@EnableJpaRepositories
public interface EmployeeAppraisalRepository extends JpaRepository<EmployeeAppraisal, Long>
{
	@Query("update EmployeeAppraisal e set e.status=false where e.id=:id") 
	@Modifying
	public void delete(@Param("id") long id);
	
	@Query("select ea from EmployeeAppraisal ea where ea.status=true")
	List<EmployeeAppraisal> listAllEmployeeAppraisal();

	@Query("select e from  EmployeeAppraisal e where e.employee.id = :id")
	Optional<EmployeeAppraisal> getAppraisalByEmployee(@Param("id") long id);

}
