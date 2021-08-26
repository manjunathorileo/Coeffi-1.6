package com.dfq.coeffi.entity.hr;

import com.dfq.coeffi.employeePermanentContract.entities.ContractCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface DepartmentTrackerRepository extends JpaRepository<DepartmentTracker,Long> {
}
