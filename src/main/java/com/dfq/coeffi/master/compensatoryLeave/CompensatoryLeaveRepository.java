package com.dfq.coeffi.master.compensatoryLeave;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface CompensatoryLeaveRepository extends JpaRepository<CompensatoryLeave,Long> {

    List<CompensatoryLeave> findByEmployeeId(long empolyeeId);
}
