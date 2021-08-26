package com.dfq.coeffi.controller.timesheet;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.resource.timesheet.AttachResourceConverter;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.timesheet.ProjectsService;
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

@Slf4j
@RestController
public class ProjectsController extends BaseController{

    private final ProjectsService projectsService;
    private final EmployeeService employeeService;

    @Autowired
    public ProjectsController(ProjectsService projectsService,EmployeeService employeeService,AttachResourceConverter projectResourceConverter){
        this.projectsService = projectsService;
        this.employeeService = employeeService;
    }

    @GetMapping("/project")
    public ResponseEntity<List<Projects>> getAllProjects() {

        List<Projects> projects = projectsService.getAllProjects();
        if (CollectionUtils.isEmpty(projects)) {
            throw new EntityNotFoundException("projects");
        }
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @PostMapping("/project/{employeeId}")
    public ResponseEntity<Projects> createProject(@RequestBody final Projects projects, @PathVariable long employeeId)
    {
        Optional<Employee> employeePersisted = employeeService.getEmployee(employeeId);
        if(!employeePersisted.isPresent()){
            log.warn("Employee with ID {} not found", employeeId);
            throw new EntityNotFoundException(Employee.class.getSimpleName());
        }
        Employee employee = employeePersisted.get();
        projects.setEmployee(employee);
        Projects persistedObject = projectsService.createProjects(projects);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<Projects> update(@PathVariable long id, @Valid @RequestBody Projects projects) {
        Optional<Projects> persistedProjects = projectsService.findOne(id);
        if (!persistedProjects.isPresent())
        {
            throw new EntityNotFoundException(Projects.class.getSimpleName());
        }
        projects.setId(id);
        projectsService.createProjects(projects);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Projects> deleteProject(@PathVariable Long projectId) {
        Optional<Projects> projects = projectsService.findOne(projectId);
        if (!projects.isPresent()) {
            throw new EntityNotFoundException(Projects.class.getSimpleName());
        }
        projectsService.delete(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}