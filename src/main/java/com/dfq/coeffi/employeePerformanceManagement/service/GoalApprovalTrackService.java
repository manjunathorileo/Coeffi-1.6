package com.dfq.coeffi.employeePerformanceManagement.service;


import com.dfq.coeffi.employeePerformanceManagement.entity.GoalApprovalTrack;

import java.util.List;
import java.util.Optional;

public interface GoalApprovalTrackService {

    GoalApprovalTrack createGoalApprovalTrack(GoalApprovalTrack goalApprovalTrack);
    Optional<GoalApprovalTrack> getGoalApprovalTrackById(long Id);
    List<GoalApprovalTrack> getAllGoalApprovalTrack();
    List<GoalApprovalTrack> getGoalApprovalTrackByEmplId(long emplId);

}