package com.dfq.coeffi.employeePerformanceManagement.serviceImpl;

import com.dfq.coeffi.employeePerformanceManagement.entity.GoalApprovalTrack;
import com.dfq.coeffi.employeePerformanceManagement.repository.GoalApprovalTrackRepository;
import com.dfq.coeffi.employeePerformanceManagement.service.GoalApprovalTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GoalApprovalTrackServiceImpl implements GoalApprovalTrackService {

    @Autowired
    private GoalApprovalTrackRepository GoalApprovalTrackRepository;

    @Override
    public GoalApprovalTrack createGoalApprovalTrack(GoalApprovalTrack goalApprovalTrack) {
        return GoalApprovalTrackRepository.save(goalApprovalTrack);
    }

    @Override
    public Optional<GoalApprovalTrack> getGoalApprovalTrackById(long Id) {
        return GoalApprovalTrackRepository.findById(Id);
    }

    @Override
    public List<GoalApprovalTrack> getAllGoalApprovalTrack() {
        return GoalApprovalTrackRepository.findAll();
    }

    @Override
    public List<GoalApprovalTrack> getGoalApprovalTrackByEmplId(long emplId) {
        return GoalApprovalTrackRepository.findByEmplId(emplId);
    }
}
