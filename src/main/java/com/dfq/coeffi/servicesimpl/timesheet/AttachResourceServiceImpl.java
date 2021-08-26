package com.dfq.coeffi.servicesimpl.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.timesheet.AttachResource;
import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.repository.timesheet.ActivitiesRepository;
import com.dfq.coeffi.repository.timesheet.AttachResourceRepository;
import com.dfq.coeffi.service.timesheet.ActivitiesService;
import com.dfq.coeffi.service.timesheet.AttachResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class AttachResourceServiceImpl implements AttachResourceService {

    @Autowired
    private AttachResourceRepository attachResourceRepository;

    @Override
    public AttachResource create(AttachResource attachResource) {
        return attachResourceRepository.save(attachResource);
    }

    @Override
    public List<AttachResource> getAttachResources() {
        return attachResourceRepository.findAll();
    }

    @Override
    public Optional<AttachResource> findOne(long id) {
        return ofNullable(attachResourceRepository.findOne(id));
    }

    @Override
    public void delete(Long id) {
        attachResourceRepository.delete(id);
    }

    @Override
    public AttachResource getAttachResourceByProjectAndEmployee(Projects projects, Employee employee) {
        return attachResourceRepository.getAttachResourceByProjectAndEmployee(projects,employee);
    }

    @Override
    public List<AttachResource> getAttachResourcesByProjectAndEmployee(Projects projects, Employee employee) {
        return attachResourceRepository.getAttachResourcesByProjectAndEmployee(projects,employee);
    }
}
