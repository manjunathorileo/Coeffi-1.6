package com.dfq.coeffi.controller.leave.leavebalance.earningleaverule;

import com.dfq.coeffi.entity.leave.LeaveType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EarningLeaveRuleServiceImpl implements EarningLeaveRuleService {

    private EarningLeaveRuleRepository earningLeaveRuleRepository;

    @Autowired
    private EarningLeaveRuleServiceImpl(EarningLeaveRuleRepository earningLeaveRuleRepository){
        this.earningLeaveRuleRepository = earningLeaveRuleRepository;
    }

    @Override
    public EarningLeaveRule createEarningLeaveRule(EarningLeaveRule earningLeaveRule) {
        return earningLeaveRuleRepository.save(earningLeaveRule);
    }

    @Override
    public List<EarningLeaveRule> getAllEarningLeaveRule() {
        return earningLeaveRuleRepository.findAll();
    }

    @Override
    public EarningLeaveRule getEarningLeaveRulesById(long id) {
        return earningLeaveRuleRepository.findOne(id);
    }

    @Override
    public Optional<EarningLeaveRule> getEarningLeaveRulesByLeaveType(LeaveType leaveType) {
        return earningLeaveRuleRepository.findByLeaveType(leaveType);
    }
}
