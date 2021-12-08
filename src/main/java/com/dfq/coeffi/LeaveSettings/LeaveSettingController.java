package com.dfq.coeffi.LeaveSettings;

import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class LeaveSettingController extends BaseController {
    @Autowired
    LeaveSettingService leaveSettingService;

    @PostMapping("leave-setting")
    public void saveSetting(@RequestBody LeaveSetting leaveSetting) {
        leaveSettingService.save(leaveSetting);
    }

    @GetMapping("leave-setting/latest")
    public ResponseEntity<LeaveSetting> getLatest() {
        LeaveSetting leaveSetting = leaveSettingService.getLatest();
        return new ResponseEntity<>(leaveSetting, HttpStatus.OK);
    }

    @GetMapping("leave-settings")
    public ResponseEntity<LeaveSetting> getLatestLeave() {
        LeaveSetting leaveSetting = leaveSettingService.getLatest();
        return new ResponseEntity<>(leaveSetting, HttpStatus.OK);
    }

    @PostMapping("leave-setting/financial-year/{from}/{to}")
    public void saveFinancialYear(@PathVariable String from, @PathVariable String to){
        LeaveSetting leaveSetting = leaveSettingService.getLatest();
        leaveSetting.setStartMonth(from);
        leaveSetting.setEndMonth(to);
        leaveSettingService.save(leaveSetting);
    }
    @PostMapping("leave-setting/carry-forward/{type}/{status}/{days}")
    public void carryForward(@PathVariable String type,@PathVariable boolean status,@PathVariable long days){
        LeaveSetting leaveSetting = leaveSettingService.getLatest();
        if (type.equals("EL")) {
            leaveSetting.setEarnLeaveCarried(status);
            leaveSetting.setElmaxCarried(days);
        }
        if (type.equals("CL")) {
            leaveSetting.setCasualLeaveCarried(status);
            leaveSetting.setClmaxCarried(days);
        }
        if (type.equals("SL")) {
            leaveSetting.setSickLeaveCarried(status);
            leaveSetting.setSlmaxCarried(days);
        }
        leaveSettingService.save(leaveSetting);
    }
//    @PostMapping("leave-setting/maximum-leaves-carried/{number}")
//    public void maxLeaves(@PathVariable long number){
//        LeaveSetting leaveSetting = leaveSettingService.getLatest();
//        leaveSetting.setMaxCarried(number);
//        leaveSettingService.save(leaveSetting);
//    }

    @PostMapping("leave-setting/one-earn-leave/{days}")
    public void mearnLeave(@PathVariable long days){
        LeaveSetting leaveSetting = leaveSettingService.getLatest();
        leaveSetting.setNoOfDaysForOneEarnLeave(days);
        leaveSettingService.save(leaveSetting);
    }
}
