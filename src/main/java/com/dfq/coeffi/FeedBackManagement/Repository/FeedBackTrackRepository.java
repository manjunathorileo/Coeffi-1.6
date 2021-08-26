package com.dfq.coeffi.FeedBackManagement.Repository;

import com.dfq.coeffi.CanteenManagement.Entity.DailyFoodMenu;
import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackParameter;
import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface FeedBackTrackRepository extends JpaRepository<FeedBackTrack,Long> {

    @Query("SELECT e FROM FeedBackTrack e WHERE e.employee.id = :employeeId AND e.createdDate=:today")
    List<FeedBackTrack> findFeedBacktrack(@Param("employeeId") long employeeId, @Param("today")Date today);


}
