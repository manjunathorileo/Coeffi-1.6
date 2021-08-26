package com.dfq.coeffi.employeePerformanceManagement.repository;


import com.dfq.coeffi.employeePerformanceManagement.entity.GoalApprovalTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoalApprovalTrackRepository extends JpaRepository<GoalApprovalTrack, Long> {

    Optional<GoalApprovalTrack> findById(long id);

    @Query("SELECT goalApprovalTrack FROM GoalApprovalTrack goalApprovalTrack WHERE " +
            " goalApprovalTrack.employeePerformanceManagement.employee.id = :emplId")
    List<GoalApprovalTrack> findByEmplId(@Param("emplId") long emplId);
}