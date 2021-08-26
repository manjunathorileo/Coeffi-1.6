package com.dfq.coeffi.controller.timesheet;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.MonthlyStatusDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.*;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.timesheet.AttachResourceService;
import com.dfq.coeffi.service.timesheet.ProjectsService;
import com.dfq.coeffi.service.timesheet.TimesheetService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class TimesheetController extends BaseController {

    private final TimesheetService timesheetService;
    private final ProjectsService projectsService;
    private final EmployeeService employeeService;
    private final AttachResourceService attachResourceService;

    @Autowired
    public TimesheetController(TimesheetService timesheetService,ProjectsService projectsService,EmployeeService employeeService,
                               AttachResourceService attachResourceService){
        this.timesheetService = timesheetService;
        this.projectsService = projectsService;
        this.employeeService = employeeService;
        this.attachResourceService = attachResourceService;
    }

    @GetMapping("timesheet")
    public ResponseEntity<List<Timesheet>> getTimesheets() {
        List<Timesheet> timesheets = timesheetService.getTimesheets();
        if (CollectionUtils.isEmpty(timesheets)) {
            throw new EntityNotFoundException("Timesheet");
        }
        return new ResponseEntity<>(timesheets, HttpStatus.OK);
    }

    @PostMapping("timesheet/{projectId}/{employeeId}")
    public ResponseEntity<Timesheet> createTimesheet(@PathVariable long projectId, @PathVariable long employeeId) {

        Optional<Projects> projectsObj = projectsService.findOne(projectId);
        if (!projectsObj.isPresent()) {
            log.warn("Project with ID {} not found", projectId);
            throw new EntityNotFoundException(Projects.class.getSimpleName());
        }
        Projects projects = projectsObj.get();

        Optional<Employee> employeeObj = employeeService.getEmployee(employeeId);
        if (!employeeObj.isPresent()) {
            log.warn("Employee with ID {} not found", employeeId);
            throw new EntityNotFoundException(Employee.class.getSimpleName());
        }
        Employee employee = employeeObj.get();
        AttachResource attachResources = attachResourceService.getAttachResourceByProjectAndEmployee(projects,employee);
        Timesheet timesheet = new Timesheet();
        List<ActivityDetails> activityDetails = new ArrayList<ActivityDetails>();

        if(attachResources != null && attachResources.getActivities() != null){
            for (Activities activities :attachResources.getActivities()) {

                WeeklyEstimation weeklyEstimation = new WeeklyEstimation();
                weeklyEstimation.setActivities(activities);

               // weeklyEstimation.setWeeklySchedules(buildWeeklySchedule(activities,projects.getId(),employee.getId()));

                ActivityDetails activityDetailsObj = new ActivityDetails();
                activityDetailsObj.setWeeklyEstimation(weeklyEstimation);
                activityDetails.add(activityDetailsObj);

                timesheet.setActivityDetails(activityDetails);
                timesheet.setEmployee(attachResources.getEmployee());
                timesheet.setProjects(attachResources.getProjects());
            }
        }
        timesheetService.createTimesheet(timesheet);
        return new ResponseEntity<>(timesheet,HttpStatus.OK);
    }

    /*private List<WeeklySchedule> buildWeeklySchedule(Activities activities,long projectId,long employeeId){

        Optional<Projects> projectsObj = projectsService.findOne(projectId);
        Projects projects = projectsObj.get();

        Optional<Employee> employeeObj = employeeService.getEmployee(employeeId);
        Employee employee = employeeObj.get();

        List<WeeklySchedule> weeklySchedules = new ArrayList<WeeklySchedule>();

        List<Date> dates = DateUtil.getDaysBetweenTwoDates(activities.getStartDate(),activities.getExpectedEndDate());

            if(dates != null){
                for (Date date:dates) {
                    WeeklySchedule weeklySchedule = new WeeklySchedule();

                    if(DateUtil.getDay(date).equalsIgnoreCase("Sun")){
                        weeklySchedules.remove(weeklySchedule);
                    }
                    else{
                        weeklySchedule.setDate(date);
                        weeklySchedule.setDay(DateUtil.getDay(date));
                        weeklySchedule.setProjectId(projects.getId());
                        weeklySchedule.setEmployeeId(employee.getId());
                        weeklySchedule.setActivities(activities);
                        weeklySchedules.add(weeklySchedule);
                    }
                }
            }
        return weeklySchedules;
    }
*/
    @GetMapping("timesheet/{projectId}/{employeeId}")
    public ResponseEntity<Timesheet> getTimesheetByProjectAndEmployee(@PathVariable long projectId, @PathVariable long employeeId) {

        Optional<Projects> projects = projectsService.findOne(projectId);
        if (!projects.isPresent()) {
            log.warn("Project with ID {} not found", projectId);
            throw new EntityNotFoundException(Projects.class.getSimpleName());
        }
        Optional<Employee> employee = employeeService.getEmployee(employeeId);
        if (!employee.isPresent()) {
            log.warn("Employee with ID {} not found", employeeId);
            throw new EntityNotFoundException(Employee.class.getSimpleName());
        }
        Timesheet timesheet = timesheetService.getTimesheetByProjectAndEmployee(projects.get(),employee.get());
        return new ResponseEntity<>(timesheet, HttpStatus.OK);
    }
}