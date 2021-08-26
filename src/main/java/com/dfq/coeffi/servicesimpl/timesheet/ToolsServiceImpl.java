package com.dfq.coeffi.servicesimpl.timesheet;

import com.dfq.coeffi.entity.timesheet.Tools;
import com.dfq.coeffi.repository.timesheet.ToolsRepository;
import com.dfq.coeffi.service.timesheet.ToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class ToolsServiceImpl implements ToolsService {

    @Autowired
    private ToolsRepository toolsRepository;

    @Override
    public Tools createTool(Tools tools) {
        return toolsRepository.save(tools);
    }

    @Override
    public List<Tools> getTools() {
        return toolsRepository.findAll();
    }

    @Override
    public Optional<Tools> findOne(long id) {
        return ofNullable(toolsRepository.findOne(id));
    }

    @Override
    public void delete(Long id) {
        toolsRepository.delete(id);
    }
}
