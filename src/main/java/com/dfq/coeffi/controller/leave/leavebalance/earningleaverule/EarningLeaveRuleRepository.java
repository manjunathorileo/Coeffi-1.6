package com.dfq.coeffi.controller.leave.leavebalance.earningleaverule;

import com.dfq.coeffi.entity.leave.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;
import java.util.Optional;

@EnableJpaRepositories
@Transactional
public interface EarningLeaveRuleRepository extends JpaRepository<EarningLeaveRule,Long> {

    Optional<EarningLeaveRule> findByLeaveType(LeaveType leaveType);
}
