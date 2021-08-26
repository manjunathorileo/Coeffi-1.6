package com.dfq.coeffi.LossAnalysis.lossAnalysisEntryTimeRule;

import java.util.List;
import java.util.Optional;

public interface LossAnalysisEntryTimeRuleService {

    LossAnalysisEntryTimeRule createLossAnalysisEntryTimeRule(LossAnalysisEntryTimeRule lossAnalysisEntryTimeRule);
    List<LossAnalysisEntryTimeRule> getAllLossAnalysisEntryTimeRule();
    Optional<LossAnalysisEntryTimeRule> getLossAnalysisEntryTimeRule(long id);
}
