package com.dfq.coeffi.employeePerformanceManagement.controller;


import com.dfq.coeffi.employeePerformanceManagement.entity.GoalApprovalTrack;
import com.dfq.coeffi.employeePerformanceManagement.service.GoalApprovalTrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class GoalApprovalTrackController {

    private final GoalApprovalTrackService goalApprovalTrackService;

    public GoalApprovalTrackController(GoalApprovalTrackService goalApprovalTrackService) {
        this.goalApprovalTrackService = goalApprovalTrackService;
    }

    @PostMapping("goalApprovalTrack")
    public ResponseEntity<GoalApprovalTrack> createGoalApprovalTrack(@Valid @RequestBody GoalApprovalTrack goalApprovalTrack){
        goalApprovalTrack.setStatus(true);
        GoalApprovalTrack goalApprovalTrackObj = goalApprovalTrackService.createGoalApprovalTrack(goalApprovalTrack);
        return new ResponseEntity<>(goalApprovalTrackObj, HttpStatus.OK);
    }

    @GetMapping("goalAllApprovalTrack")
    public ResponseEntity<GoalApprovalTrack> getAllGoalApprovalTrack(){
        List<GoalApprovalTrack> goalApprovalTrackList = new ArrayList<>();
        List<GoalApprovalTrack> goalApprovalTracks = goalApprovalTrackService.getAllGoalApprovalTrack();
        for (GoalApprovalTrack goalApprovalTrackObj:goalApprovalTracks) {
            if (goalApprovalTrackObj.getStatus().equals(true)){
                goalApprovalTrackList.add(goalApprovalTrackObj);
            }
        }
        return new ResponseEntity(goalApprovalTrackList, HttpStatus.OK);
    }

    @GetMapping("/goalApprovalTrack/{id}")
    public ResponseEntity<GoalApprovalTrack> getGoalApprovalTrackByEmplId(@PathVariable long emplId){
        List<GoalApprovalTrack> goalApprovalTrackList = new ArrayList<>();
        List<GoalApprovalTrack> goalApprovalTracks = goalApprovalTrackService.getGoalApprovalTrackByEmplId(emplId);
        for (GoalApprovalTrack goalApprovalTrackObj:goalApprovalTracks) {
            if (goalApprovalTrackObj.getStatus().equals(true)){
                goalApprovalTrackList.add(goalApprovalTrackObj);
            }
        }
        return new ResponseEntity(goalApprovalTrackList, HttpStatus.OK);
    }

}
