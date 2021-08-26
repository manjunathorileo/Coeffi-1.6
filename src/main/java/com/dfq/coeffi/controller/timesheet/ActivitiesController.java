package com.dfq.coeffi.controller.timesheet;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.timesheet.Activities;
import com.dfq.coeffi.entity.timesheet.Tools;
import com.dfq.coeffi.service.timesheet.ActivitiesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class ActivitiesController extends BaseController {

    private final ActivitiesService activitiesService;

    @Autowired
    public ActivitiesController(ActivitiesService activitiesService)
    {
        this.activitiesService=activitiesService;
    }

    @GetMapping("activity")
    public ResponseEntity<List<Activities>> getAllActivities()
    {
        List<Activities> activities= activitiesService.getAllActivities();
        if (CollectionUtils.isEmpty(activities)) {
            throw new EntityNotFoundException("Activities");
        }
        return new ResponseEntity<>(activities , HttpStatus.OK);
    }

    @PostMapping("activity")
    public ResponseEntity<Activities> createActivity(@RequestBody Activities activities)  {
        Activities persistedActivity = activitiesService.createActivities(activities);
        return new ResponseEntity<>(persistedActivity, HttpStatus.OK);
    }

    @GetMapping("activity/{activityId}")
    private ResponseEntity<Activities> getActivity(@PathVariable long activityId) {
        Optional<Activities> activities = activitiesService.findOne(activityId);
        if (!activities.isPresent()) {
            throw new EntityNotFoundException(Activities.class.getSimpleName());
        }
        return new ResponseEntity<>(activities.get(), HttpStatus.OK);
    }

    @DeleteMapping("activity/{activityId}")
    public ResponseEntity<Activities> deleteActivity(@PathVariable Long activityId) {
        Optional<Activities> activities = activitiesService.findOne(activityId);
        if (!activities.isPresent()) {
            throw new EntityNotFoundException(Activities.class.getSimpleName());
        }
        activitiesService.delete(activityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}