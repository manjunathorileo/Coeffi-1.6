package com.dfq.coeffi.servicesimpl.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.entity.timesheet.Timesheet;
import com.dfq.coeffi.repository.timesheet.TimesheetRepository;
import com.dfq.coeffi.service.timesheet.TimesheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class TimesheetServiceImpl implements TimesheetService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Override
    public Timesheet createTimesheet(Timesheet timesheet) {
        return timesheetRepository.save(timesheet);
    }

    @Override
    public List<Timesheet> getTimesheets() {
        return timesheetRepository.findAll();
    }
    @Override
    public Optional<Timesheet> findOne(long id) {
        return ofNullable(timesheetRepository.findOne(id));
    }

    @Override
    public void delete(Long id) {
        timesheetRepository.delete(id);
    }

    @Override
    public Timesheet getTimesheetByProjectAndEmployee(Projects projects, Employee employee) {
        return timesheetRepository.getTimesheet(projects,employee);
    }

    @Override
    public List<Timesheet> getTimesheetByStartDateAndEndDate(Projects projects, Employee employee, Date startDate, Date endDate) {
        return timesheetRepository.getTimesheetByProject(projects,employee,startDate,endDate);
    }
}
