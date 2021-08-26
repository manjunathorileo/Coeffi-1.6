package com.dfq.coeffi.repository.leave;

import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface ClosingLeaveRepo extends JpaRepository<ClosingLeave,Long> {

}
