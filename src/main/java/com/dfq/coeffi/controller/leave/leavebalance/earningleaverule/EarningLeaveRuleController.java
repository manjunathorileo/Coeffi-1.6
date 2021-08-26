package com.dfq.coeffi.controller.leave.leavebalance.earningleaverule;

import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class EarningLeaveRuleController extends BaseController {

    private EarningLeaveRuleService earningLeaveRuleService;

    @Autowired
    private EarningLeaveRuleController(EarningLeaveRuleService earningLeaveRuleService){
        this.earningLeaveRuleService = earningLeaveRuleService;
    }

    @PostMapping("/earning-leave-rule/create")
    private ResponseEntity<EarningLeaveRule> createEarningLeaveRule(@Valid @RequestBody EarningLeaveRule earningLeaveRule){
        EarningLeaveRule createEarningLeaveRule = earningLeaveRuleService.createEarningLeaveRule(earningLeaveRule);
        return new ResponseEntity<>(createEarningLeaveRule, HttpStatus.OK);
    }

    @GetMapping("/earning-leave-rule/get-all")
    private ResponseEntity<EarningLeaveRule> getAllEarningLeaveRule(){
        List<EarningLeaveRule> getAllEarningLeaveRule = earningLeaveRuleService.getAllEarningLeaveRule();
        if (getAllEarningLeaveRule.isEmpty()){
            throw new EntityNotFoundException("No earning leave rules are present");
        }
        return new ResponseEntity(getAllEarningLeaveRule, HttpStatus.OK);
    }

    @GetMapping("/earning-leave-rule/get-by-id/{id}")
    private ResponseEntity<EarningLeaveRule> getEarningLeaveRuleById(@PathVariable long id){
        EarningLeaveRule getEarningLeaveRuleById = earningLeaveRuleService.getEarningLeaveRulesById(id);
        return new ResponseEntity(getEarningLeaveRuleById, HttpStatus.OK);
    }
}
