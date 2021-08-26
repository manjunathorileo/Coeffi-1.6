package com.dfq.coeffi.controller.timesheet;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.timesheet.Activities;
import com.dfq.coeffi.entity.timesheet.WeeklyEstimation;
import com.dfq.coeffi.service.timesheet.ActivitiesService;
import com.dfq.coeffi.service.timesheet.WeeklyEstimationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RestController
public class WeeklyEstimationController extends BaseController {

    private final WeeklyEstimationService weeklyEstimationService;

    @Autowired
    public WeeklyEstimationController(WeeklyEstimationService weeklyEstimationService)
    {
        this.weeklyEstimationService=weeklyEstimationService;
    }

    @GetMapping("weekly-estimation")
    public ResponseEntity<List<WeeklyEstimation>> getWeeklyEstimations()
    {
        List<WeeklyEstimation> weeklyEstimations = weeklyEstimationService.getWeeklyEstimations();
        if (CollectionUtils.isEmpty(weeklyEstimations)) {
            throw new EntityNotFoundException("WeeklyEstimation");
        }
        return new ResponseEntity<>(weeklyEstimations , HttpStatus.OK);
    }
}