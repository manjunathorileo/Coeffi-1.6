package com.dfq.coeffi.resource.timesheet;

import com.dfq.coeffi.entity.timesheet.Activities;
import com.dfq.coeffi.entity.timesheet.AttachResource;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.entity.timesheet.Tools;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.timesheet.ActivitiesService;
import com.dfq.coeffi.service.timesheet.ProjectsService;
import com.dfq.coeffi.service.timesheet.ToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AttachResourceConverter {

    @Autowired
    private ToolsService toolsService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private ProjectsService projectsService;

    public AttachResource toEntity(ProjectResource projectResource){

        AttachResource attachResource = new AttachResource();

        if(projectResource.getEmployeeId() > 0){
            setEmployee(attachResource,projectResource);
        }

        if(projectResource.getProjectId() > 0){
            setProject(attachResource,projectResource);
        }

        if(projectResource.getActivityIds() != null){
            setActivities(attachResource,projectResource);
        }

        if(projectResource.getToolIds() != null){
            setTools(attachResource,projectResource);
        }
        return attachResource;
    }

    private void setEmployee(AttachResource attachResource, ProjectResource projectResource){
        attachResource.setEmployee(employeeService.getEmployee(projectResource.getEmployeeId()).get());
    }

    private void setProject(AttachResource attachResource, ProjectResource projectResource){
        attachResource.setProjects(projectsService.findOne(projectResource.getProjectId()).get());
    }


    private void setActivities(AttachResource attachResource, ProjectResource projectResource){

        List<Activities> activitiesList = new ArrayList<Activities>();
        if(projectResource != null && projectResource.getActivityIds() != null){
            for (Integer activityId: projectResource.getActivityIds()) {
                Optional<Activities> activitiesObj = activitiesService.findOne(activityId);
                Activities activities = activitiesObj.get();
                activitiesList.add(activities);
            }
            attachResource.setActivities(activitiesList);
        }
    }

    private void setTools(AttachResource attachResource, ProjectResource projectResource){

        List<Tools> toolsList = new ArrayList<Tools>();
        if(projectResource != null && projectResource.getToolIds() != null){
            for (Integer toolId: projectResource.getToolIds()) {
                Optional<Tools> toolsObj = toolsService.findOne(toolId);
                Tools tools = toolsObj.get();
                toolsList.add(tools);
            }
            attachResource.setTools(toolsList);
        }
    }

}
