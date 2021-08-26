package com.dfq.coeffi.servicesimpl.timesheet;

import com.dfq.coeffi.entity.timesheet.Timesheet;
import com.dfq.coeffi.entity.timesheet.WeeklySchedule;
import com.dfq.coeffi.repository.timesheet.WeeklyScheduleRepository;
import com.dfq.coeffi.service.timesheet.WeeklyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class WeeklyScheduleServiceImpl implements WeeklyScheduleService {

    @Autowired
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @Override
    public WeeklySchedule createWeeklySchedule(WeeklySchedule weeklySchedule) {
        return weeklyScheduleRepository.save(weeklySchedule);
    }

    @Override
    public List<WeeklySchedule> getWeeklySchedule() {
        return weeklyScheduleRepository.findAll();
    }

    @Override
    public Optional<WeeklySchedule> findOne(long id) {
        return ofNullable(weeklyScheduleRepository.findOne(id));
    }

    @Override
    public void delete(Long id) {
        weeklyScheduleRepository.delete(id);
    }

    @Override
    public List<WeeklySchedule> getWeeklyScheduleByStartAndEnd(long projectId, long employeeId, Date startDate, Date endDate) {
        return weeklyScheduleRepository.getWeeklySchedule(projectId,employeeId,startDate,endDate);
    }
}
