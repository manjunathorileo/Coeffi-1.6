package com.dfq.coeffi.LossAnalysis.lossAnalysisEntryTimeRule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface LossAnalysisEntryTimeRuleRepository extends JpaRepository<LossAnalysisEntryTimeRule,Long> {
    Optional<LossAnalysisEntryTimeRule> findById(long id);
}