package com.dfq.coeffi.LossAnalysis.lossAnalysisEntryTimeRule;

import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class LossAnalysisEntryTimeRuleResource extends BaseController {

    private final LossAnalysisEntryTimeRuleService lossAnalysisEntryTimeRuleService;

    @Autowired
    public LossAnalysisEntryTimeRuleResource(LossAnalysisEntryTimeRuleService lossAnalysisEntryTimeRuleService) {
        this.lossAnalysisEntryTimeRuleService = lossAnalysisEntryTimeRuleService;
    }

    @PostMapping("/loss-analysis-entry-time")
    private ResponseEntity<LossAnalysisEntryTimeRule> createLossAnalysisEntryTimeRule(@Valid @RequestBody LossAnalysisEntryTimeRule lossAnalysisEntryTimeRule) {
        Date today = new Date();
        lossAnalysisEntryTimeRule.setCreatedOn(today);
        lossAnalysisEntryTimeRule.setStatus(true);
        List<LossAnalysisEntryTimeRule> lossAnalysisEntryTimeRuleList = lossAnalysisEntryTimeRuleService.getAllLossAnalysisEntryTimeRule();
        LossAnalysisEntryTimeRule lossAnalysisEntryTimeRuleObj = new LossAnalysisEntryTimeRule();
        if (lossAnalysisEntryTimeRuleList.isEmpty()){
            lossAnalysisEntryTimeRuleObj = lossAnalysisEntryTimeRuleService.createLossAnalysisEntryTimeRule(lossAnalysisEntryTimeRule);
        } else {
            for (LossAnalysisEntryTimeRule lossAnalysisEntryTimeRule1:lossAnalysisEntryTimeRuleList) {
                lossAnalysisEntryTimeRule1.setCreatedOn(today);
                lossAnalysisEntryTimeRule1.setStatus(lossAnalysisEntryTimeRule.getStatus());
                lossAnalysisEntryTimeRule1.setCreatedBy(lossAnalysisEntryTimeRule.getCreatedBy());
                lossAnalysisEntryTimeRule1.setMinValue(lossAnalysisEntryTimeRule.getMinValue());
                lossAnalysisEntryTimeRuleObj = lossAnalysisEntryTimeRuleService.createLossAnalysisEntryTimeRule(lossAnalysisEntryTimeRule1);
            }
        }
        return new ResponseEntity<>(lossAnalysisEntryTimeRuleObj, HttpStatus.OK);
    }

    @GetMapping("/loss-analysis-entry-time")
    private ResponseEntity<LossAnalysisEntryTimeRule> getAllLossAnalysisEntryTimeRule() {
        List<LossAnalysisEntryTimeRule> lossAnalysisEntryTimeRules = lossAnalysisEntryTimeRuleService.getAllLossAnalysisEntryTimeRule();
        if (lossAnalysisEntryTimeRules.isEmpty()){
            throw new EntityNotFoundException("There is no time for Production Rate entry.");
        }
        return new ResponseEntity(lossAnalysisEntryTimeRules, HttpStatus.OK);
    }
}