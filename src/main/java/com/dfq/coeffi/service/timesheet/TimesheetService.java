package com.dfq.coeffi.service.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.entity.timesheet.Timesheet;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TimesheetService {

    Timesheet createTimesheet(Timesheet timesheet);
    List<Timesheet> getTimesheets();
    Optional<Timesheet> findOne(long id);
    void delete(Long id);

    Timesheet getTimesheetByProjectAndEmployee(Projects projects, Employee employee);

    List<Timesheet> getTimesheetByStartDateAndEndDate(Projects projects, Employee employee, Date startDate, Date endDate);
}
