package com.dfq.coeffi.controller.timesheet;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.timesheet.AttachResource;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.resource.timesheet.AttachResourceConverter;
import com.dfq.coeffi.resource.timesheet.ProjectResource;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.timesheet.AttachResourceService;
import com.dfq.coeffi.service.timesheet.ProjectsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class AttachResourceController extends BaseController {

    private final AttachResourceService attachResourceService;
    private final AttachResourceConverter attachResourceConverter;
    private final ProjectsService projectsService;
    private final EmployeeService employeeService;

    @Autowired
    public AttachResourceController(AttachResourceService attachResourceService,AttachResourceConverter attachResourceConverter,
                                    ProjectsService projectsService,EmployeeService employeeService){
        this.attachResourceService = attachResourceService;
        this.attachResourceConverter = attachResourceConverter;
        this.projectsService = projectsService;
        this.employeeService = employeeService;
    }

    @GetMapping("attach-resource")
    public ResponseEntity<List<AttachResource>> getAttachResource() {
        List<AttachResource> attachResources = attachResourceService.getAttachResources();
        if (CollectionUtils.isEmpty(attachResources)) {
            throw new EntityNotFoundException("AttachResource");
        }
        return new ResponseEntity<>(attachResources, HttpStatus.OK);
    }

    @PostMapping("attach-resource/{projectId}")
    public ResponseEntity<AttachResource> attachResourceToProject(@RequestBody List<ProjectResource> resource, @PathVariable long projectId) {
        AttachResource  attachResourceObj = null;
        Optional<Projects> projectsObj = projectsService.findOne(projectId);
        if (!projectsObj.isPresent()) {
            log.warn("Project with ID {} not found", projectId);
            throw new EntityNotFoundException(Projects.class.getSimpleName());
        }
        Projects projects = projectsObj.get();
        if(resource != null){
            for(ProjectResource res : resource) {
                res.setProjectId(projects.getId());
                AttachResource attachResource1 = attachResourceConverter.toEntity(res);
                attachResourceObj = attachResourceService.create(attachResource1);
            }
        }
        return new ResponseEntity<>(attachResourceObj,HttpStatus.OK);
    }

    @GetMapping("attach-resource/{projectId}/{employeeId}")
    public ResponseEntity<List<AttachResource>> getAttachResourceByProjectAndEmployee(@PathVariable long projectId, @PathVariable long employeeId) {

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
        List<AttachResource> attachResources = attachResourceService.getAttachResourcesByProjectAndEmployee(projects,employee);
        if (CollectionUtils.isEmpty(attachResources)) {
            throw new EntityNotFoundException("AttachResource");
        }
        return new ResponseEntity<>(attachResources, HttpStatus.OK);
    }
}