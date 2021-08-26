package com.dfq.coeffi.servicesimpl.timesheet;

import com.dfq.coeffi.entity.timesheet.Activities;
import com.dfq.coeffi.repository.timesheet.ActivitiesRepository;
import com.dfq.coeffi.service.timesheet.ActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class ActivitiesServiceImpl implements ActivitiesService
{
    @Autowired
    private ActivitiesRepository activitiesRepository;

    @Override
    public Activities createActivities(Activities activities ) {
        return activitiesRepository.save(activities);
    };

    @Override
    public List<Activities> getAllActivities() {
        return activitiesRepository.findAll();
    };

    @Override
    public Optional<Activities> findOne(long id) {
        return ofNullable(activitiesRepository.findOne(id));
    };

    @Override
    public void delete(Long id) {
        activitiesRepository.delete(id);
    }

}