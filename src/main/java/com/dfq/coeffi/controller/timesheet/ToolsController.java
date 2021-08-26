package com.dfq.coeffi.controller.timesheet;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.timesheet.Tools;
import com.dfq.coeffi.service.timesheet.ToolsService;
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
public class ToolsController extends BaseController {

    private final ToolsService toolsService;

    @Autowired
    public ToolsController(ToolsService toolsService){
        this.toolsService = toolsService;
    }

    @GetMapping("tool")
    public ResponseEntity<List<Tools>> getTools() {
        List<Tools> tools = toolsService.getTools();
        if (CollectionUtils.isEmpty(tools)) {
            throw new EntityNotFoundException("Tools");
        }
        return new ResponseEntity<>(tools, HttpStatus.OK);
    }

    @PostMapping("tool")
    public ResponseEntity<Tools> createTool(@RequestBody Tools tool)  {
        Tools persistedTool = toolsService.createTool(tool);
        return new ResponseEntity<>(persistedTool, HttpStatus.OK);
    }

    @GetMapping("tool/{toolId}")
    private ResponseEntity<Tools> getTool(@PathVariable long toolId) {
        Optional<Tools> tool = toolsService.findOne(toolId);
        if (!tool.isPresent()) {
            throw new EntityNotFoundException(Tools.class.getSimpleName());
        }
        return new ResponseEntity<>(tool.get(), HttpStatus.OK);
    }

    @DeleteMapping("tool/{toolId}")
    public ResponseEntity<Tools> deleteTool(@PathVariable Long toolId) {
        Optional<Tools> tool = toolsService.findOne(toolId);
        if (!tool.isPresent()) {
            throw new EntityNotFoundException(Tools.class.getSimpleName());
        }
        toolsService.delete(toolId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}