package com.dfq.coeffi.servicesimpl.timesheet;

import com.dfq.coeffi.entity.timesheet.WeeklyEstimation;
import com.dfq.coeffi.repository.timesheet.ActivitiesRepository;
import com.dfq.coeffi.repository.timesheet.WeeklyEstimationRepository;
import com.dfq.coeffi.service.timesheet.WeeklyEstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WeeklyEstimationServiceImpl implements WeeklyEstimationService {

    @Autowired
    private WeeklyEstimationRepository weeklyEstimationRepository;

    @Override
    public List<WeeklyEstimation> getWeeklyEstimations() {
        return weeklyEstimationRepository.findAll();
    }
}
