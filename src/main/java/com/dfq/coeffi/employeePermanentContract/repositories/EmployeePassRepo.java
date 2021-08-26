package com.dfq.coeffi.employeePermanentContract.repositories;

import com.dfq.coeffi.employeePermanentContract.entities.EmployeePass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface EmployeePassRepo extends JpaRepository<EmployeePass, Long> {

    EmployeePass findByEmpId(long empId);
}
