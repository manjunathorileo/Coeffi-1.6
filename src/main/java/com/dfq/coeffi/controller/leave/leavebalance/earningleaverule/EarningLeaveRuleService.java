package com.dfq.coeffi.controller.leave.leavebalance.earningleaverule;

import com.dfq.coeffi.entity.leave.LeaveType;

import java.util.List;
import java.util.Optional;

public interface EarningLeaveRuleService {

    EarningLeaveRule createEarningLeaveRule(EarningLeaveRule earningLeaveRule);
    List<EarningLeaveRule> getAllEarningLeaveRule();
    EarningLeaveRule getEarningLeaveRulesById(long id);
    Optional<EarningLeaveRule> getEarningLeaveRulesByLeaveType(LeaveType leaveType);
}
