package com.dfq.coeffi.controller.timesheet;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.timesheet.ActivityDetails;
import com.dfq.coeffi.service.timesheet.ActivityDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ActivityDetailController extends BaseController {

    private final ActivityDetailService activityDetailService;

    @Autowired
    public ActivityDetailController(ActivityDetailService activityDetailService){
        this.activityDetailService = activityDetailService;
    }

    @GetMapping("activity-details")
    public ResponseEntity<List<ActivityDetails>> getActivityDetails()
    {
        List<ActivityDetails> activityDetails= activityDetailService.getActivityDetails();
        if (CollectionUtils.isEmpty(activityDetails)) {
            throw new EntityNotFoundException("ActivityDetails");
        }
        return new ResponseEntity<>(activityDetails , HttpStatus.OK);
    }

    @GetMapping("activity-details/{id}")
    public ResponseEntity<ActivityDetails> getActivityDetail(@PathVariable Long id) {
        Optional<ActivityDetails> persistenceObject = activityDetailService.findOne(id);
        if(!persistenceObject.isPresent())
        {
            throw new EntityNotFoundException(ActivityDetails.class.getName());
        }
        return new ResponseEntity<ActivityDetails>(persistenceObject.get(),HttpStatus.OK);
    }

    @PutMapping("activity-details/{id}")
    public ResponseEntity<ActivityDetails> updateActivityDetails(@PathVariable long id, @Valid @RequestBody ActivityDetails activityDetails) {
        Optional<ActivityDetails> persistedActivityDeatil = activityDetailService.findOne(id);
        if (!persistedActivityDeatil.isPresent()) {
            log.warn("Activity Details with ID {} not found", id);
            throw new EntityNotFoundException(ActivityDetails.class.getSimpleName());
        }
        activityDetails.setId(id);
        activityDetailService.createActivityDetails(activityDetails);
        return new ResponseEntity<>(activityDetails, HttpStatus.OK);
    }
}