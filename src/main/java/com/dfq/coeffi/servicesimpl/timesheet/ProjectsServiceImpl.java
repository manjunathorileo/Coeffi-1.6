package com.dfq.coeffi.servicesimpl.timesheet;

import com.dfq.coeffi.entity.timesheet.Projects;
import com.dfq.coeffi.repository.timesheet.ProjectsRepository;
import com.dfq.coeffi.service.timesheet.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class ProjectsServiceImpl implements ProjectsService
{
    @Autowired
    private ProjectsRepository projectsRepository;

    @Override
    public Projects createProjects(Projects projects) { return projectsRepository.save(projects); }

    @Override
    public List<Projects> getAllProjects()
    {
        return projectsRepository.findAll();
    }

    @Override
    public Optional<Projects> findOne(long id)
    {
        return ofNullable(projectsRepository.findOne(id));
    }

    @Override
    public void delete(Long id) {
        projectsRepository.delete(id);
    }

}