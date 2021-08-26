package com.dfq.coeffi.service.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.entity.timesheet.Timesheet;
import com.dfq.coeffi.entity.timesheet.WeeklySchedule;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface WeeklyScheduleService {

    WeeklySchedule createWeeklySchedule(WeeklySchedule weeklySchedule);
    List<WeeklySchedule> getWeeklySchedule();
    Optional<WeeklySchedule> findOne(long id);
    void delete(Long id);

    List<WeeklySchedule> getWeeklyScheduleByStartAndEnd(long projectId, long employeeId, Date startDate, Date endDate);
}
