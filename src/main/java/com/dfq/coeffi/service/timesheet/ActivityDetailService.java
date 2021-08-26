package com.dfq.coeffi.service.timesheet;

import com.dfq.coeffi.entity.timesheet.ActivityDetails;
import java.util.List;
import java.util.Optional;

public interface ActivityDetailService {

    ActivityDetails createActivityDetails(ActivityDetails activityDetails);
    List<ActivityDetails> getActivityDetails();
    Optional<ActivityDetails> findOne(long id);
    void delete(Long id);
}
