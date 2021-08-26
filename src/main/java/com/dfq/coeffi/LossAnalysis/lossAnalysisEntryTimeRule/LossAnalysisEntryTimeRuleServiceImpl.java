package com.dfq.coeffi.LossAnalysis.lossAnalysisEntryTimeRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LossAnalysisEntryTimeRuleServiceImpl implements LossAnalysisEntryTimeRuleService {

    @Autowired
    private LossAnalysisEntryTimeRuleRepository lossAnalysisEntryTimeRuleRepository;

    @Override
    public LossAnalysisEntryTimeRule createLossAnalysisEntryTimeRule(LossAnalysisEntryTimeRule lossAnalysisEntryTimeRule) {
        return lossAnalysisEntryTimeRuleRepository.save(lossAnalysisEntryTimeRule);
    }

    @Override
    public List<LossAnalysisEntryTimeRule> getAllLossAnalysisEntryTimeRule() {
        List<LossAnalysisEntryTimeRule> lossAnalysisEntryTimeRules = new ArrayList<>();
        List<LossAnalysisEntryTimeRule> lossAnalysisEntryTimeRuleList = lossAnalysisEntryTimeRuleRepository.findAll();
        for (LossAnalysisEntryTimeRule lossAnalysisEntryTimeRule:lossAnalysisEntryTimeRuleList) {
            if (lossAnalysisEntryTimeRule.getStatus() == true){
                lossAnalysisEntryTimeRules.add(lossAnalysisEntryTimeRule);
            }
        }
        return lossAnalysisEntryTimeRules;
    }

    @Override
    public Optional<LossAnalysisEntryTimeRule> getLossAnalysisEntryTimeRule(long id) {
        return lossAnalysisEntryTimeRuleRepository.findById(id);
    }
}
