package com.dfq.coeffi.service.timesheet;

import com.dfq.coeffi.entity.timesheet.Tools;

import java.util.List;
import java.util.Optional;

public interface ToolsService {

    Tools createTool(Tools tools);
    List<Tools> getTools();
    Optional<Tools> findOne(long id);
    void delete(Long id);
}
