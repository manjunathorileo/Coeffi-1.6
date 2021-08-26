package com.dfq.coeffi.servicesimpl.timesheet;

import com.dfq.coeffi.entity.timesheet.ActivityDetails;
import com.dfq.coeffi.repository.timesheet.ActivityDetailsRepository;
import com.dfq.coeffi.service.timesheet.ActivityDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class ActivityDetailServiceImpl implements ActivityDetailService {

    @Autowired
    private ActivityDetailsRepository activityDetailsRepository;

    @Override
    public ActivityDetails createActivityDetails(ActivityDetails activityDetails) {
        return activityDetailsRepository.save(activityDetails);
    }

    @Override
    public List<ActivityDetails> getActivityDetails() {
        return activityDetailsRepository.findAll();
    }

    @Override
    public Optional<ActivityDetails> findOne(long id) {
        return ofNullable(activityDetailsRepository.findOne(id));
    }

    @Override
    public void delete(Long id) {
        activityDetailsRepository.delete(id);
    }
}
