package com.dfq.coeffi.controller.timesheet;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.entity.timesheet.Timesheet;
import com.dfq.coeffi.entity.timesheet.WeeklySchedule;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.timesheet.ProjectsService;
import com.dfq.coeffi.service.timesheet.WeeklyScheduleService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class WeeklyScheduleController extends BaseController {

    private final WeeklyScheduleService weeklyScheduleService;
    private final ProjectsService projectService;
    private final EmployeeService employeeService;

    @Autowired
    public WeeklyScheduleController(WeeklyScheduleService weeklyScheduleService,ProjectsService projectService,
                                    EmployeeService employeeService){
        this.weeklyScheduleService = weeklyScheduleService;
        this.projectService = projectService;
        this.employeeService = employeeService;
    }

    @GetMapping("weekly-schedule")
    public ResponseEntity<List<WeeklySchedule>> getWeeklySchedules()
    {
        List<WeeklySchedule> weeklySchedules= weeklyScheduleService.getWeeklySchedule();
        if (CollectionUtils.isEmpty(weeklySchedules)) {
            throw new EntityNotFoundException("WeeklySchedule");
        }
        return new ResponseEntity<>(weeklySchedules , HttpStatus.OK);
    }

    @GetMapping("weekly-schedule/{id}")
    public ResponseEntity<WeeklySchedule> getWeeklySchedule(@PathVariable Long id) {
        Optional<WeeklySchedule> weeklySchedule = weeklyScheduleService.findOne(id);
        if(!weeklySchedule.isPresent())
        {
            throw new EntityNotFoundException(WeeklySchedule.class.getName());
        }
        return new ResponseEntity<WeeklySchedule>(weeklySchedule.get(),HttpStatus.OK);
    }

    @PutMapping("weekly-schedule/{id}")
    public ResponseEntity<WeeklySchedule> updateWeeklySchedule(@PathVariable long id, @Valid @RequestBody WeeklySchedule weeklySchedule) {
        Optional<WeeklySchedule> persistedSchedule = weeklyScheduleService.findOne(id);
        if (!persistedSchedule.isPresent()) {
            log.warn("WeeklySchedule with ID {} not found", id);
            throw new EntityNotFoundException(WeeklySchedule.class.getSimpleName());
        }
        weeklySchedule.setEmployeeId(persistedSchedule.get().getEmployeeId());
        weeklySchedule.setProjectId(persistedSchedule.get().getProjectId());
        weeklySchedule.setActivities(persistedSchedule.get().getActivities());
        weeklySchedule.setId(id);

        weeklyScheduleService.createWeeklySchedule(weeklySchedule);
        return new ResponseEntity<>(weeklySchedule, HttpStatus.OK);
    }

    @PostMapping("weekly-schedule/{id}")
    public ResponseEntity<WeeklySchedule> createWeeklySchedule(WeeklySchedule weeklySchedule){

        WeeklySchedule persistedWeeklySchedule = weeklyScheduleService.createWeeklySchedule(weeklySchedule);
        return new ResponseEntity<>(persistedWeeklySchedule,HttpStatus.OK);
    }

    @DeleteMapping("weekly-schedule/{id}")
    public ResponseEntity<WeeklySchedule> deleteWeeklySchedule(@PathVariable Long weeklyScheduleId){

        Optional<WeeklySchedule> weeklySchedule = weeklyScheduleService.findOne(weeklyScheduleId);
        if(!weeklySchedule.isPresent()){
            log.warn("Weekly Schedule with ID {} not found",weeklyScheduleId);
            throw new EntityNotFoundException(weeklySchedule.getClass().getSimpleName());
        }
        weeklyScheduleService.delete(weeklyScheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("weekly-schedule/{projectId}/{employeeId}")
    public ResponseEntity<List<WeeklySchedule>> getWeeklyTimesheet(@PathVariable long projectId, @PathVariable long employeeId, @RequestBody DateDto dateDto) {
        Optional<Projects> projects = projectService.findOne(projectId);
        if (!projects.isPresent()) {
            log.warn("Project with ID {} not found", projectId);
            throw new EntityNotFoundException(Projects.class.getSimpleName());
        }
        Projects projects1 = projects.get();
        Optional<Employee> employee = employeeService.getEmployee(employeeId);
        if (!employee.isPresent()) {
            log.warn("Employee with ID {} not found", employeeId);
            throw new EntityNotFoundException(Employee.class.getSimpleName());
        }
        Employee employee1 = employee.get();
        List<WeeklySchedule> weeklySchedules = weeklyScheduleService.getWeeklyScheduleByStartAndEnd(projects1.getId(),employee1.getId(),DateUtil.convertDateToFormat(dateDto.getStartDate()),DateUtil.convertDateToFormat(dateDto.getEndDate()));
        return new ResponseEntity<>(weeklySchedules, HttpStatus.OK);
    }
}