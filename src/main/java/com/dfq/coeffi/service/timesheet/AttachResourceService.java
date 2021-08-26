package com.dfq.coeffi.service.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.Activities;
import com.dfq.coeffi.entity.timesheet.AttachResource;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.entity.timesheet.Timesheet;

import java.util.List;
import java.util.Optional;

public interface AttachResourceService {

    AttachResource create(AttachResource attachResource);
    List<AttachResource> getAttachResources();
    Optional<AttachResource> findOne(long id);
    void delete(Long id);

    AttachResource getAttachResourceByProjectAndEmployee(Projects projects, Employee employee);

    List<AttachResource> getAttachResourcesByProjectAndEmployee(Projects projects, Employee employee);
}