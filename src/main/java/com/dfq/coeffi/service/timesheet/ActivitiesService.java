package com.dfq.coeffi.service.timesheet;

import com.dfq.coeffi.entity.timesheet.Activities;

import java.util.List;
import java.util.Optional;

public interface ActivitiesService
{
    public Activities createActivities(Activities activities);
    public List<Activities> getAllActivities();
    Optional<Activities> findOne(long id);
    void delete(Long id);
}

