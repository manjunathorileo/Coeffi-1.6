package com.dfq.coeffi.resource.timesheet;

import com.dfq.coeffi.entity.timesheet.Activities;
import com.dfq.coeffi.entity.timesheet.ActivityDetails;
import com.dfq.coeffi.entity.timesheet.AttachResource;
import com.dfq.coeffi.entity.timesheet.Timesheet;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.timesheet.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TimesheetResourceConverter {

    @Autowired
    private ProjectsService projectsService;

    @Autowired
    private EmployeeService employeeService;

    private void setEmployee(Timesheet timesheet, TimesheetResource timesheetResource){
        timesheet.setEmployee(employeeService.getEmployee(timesheetResource.getEmployeeId()).get());
    }

    private void setProject(Timesheet timesheet, TimesheetResource timesheetResource){
        timesheet.setProjects(projectsService.findOne(timesheetResource.getProjectId()).get());
    }

//    private void setActivityDetails(Timesheet timesheet, TimesheetResource timesheetResource){
//        List<ActivityDetails> activityDetails = new ArrayList<ActivityDetails>();
//        if(timesheetResource != null && timesheetResource.getActivityDetailIds() != null){
//            for (Integer activityDetailId: timesheetResource.getActivityDetailIds()) {
//                Optional<ActivityDetails> activityDetailsObj = activitiesService.findOne(activityId);
//                Activities activities = activitiesObj.get();
//                activitiesList.add(activities);
//            }
//            attachResource.setActivities(activitiesList);
//        }
//    }
}
