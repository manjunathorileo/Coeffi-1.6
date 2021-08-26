package com.dfq.coeffi.service.timesheet;

import com.dfq.coeffi.entity.timesheet.Projects;

import java.util.List;
import java.util.Optional;

public interface ProjectsService
{
    public Projects createProjects(Projects projects);
    public List<Projects> getAllProjects();
    Optional<Projects> findOne(long id);
    void delete(Long id);
}