package com.dfq.coeffi.repository.payroll;

import com.dfq.coeffi.entity.payroll.EmployeeCTCData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface EmployeeCtcDataRepo extends JpaRepository<EmployeeCTCData,Long> {

    List<EmployeeCTCData> findByEmpId(long empId);

}
