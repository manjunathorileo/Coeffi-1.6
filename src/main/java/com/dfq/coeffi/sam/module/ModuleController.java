package com.dfq.coeffi.sam.module;

import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class ModuleController extends BaseController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping("module")
    public ResponseEntity<List<Module>> getModules() {
        List<Module> modules = moduleService.getModules();
        if (CollectionUtils.isEmpty(modules)) {
            throw new EntityNotFoundException("Module");
        }
        return new ResponseEntity<>(modules, HttpStatus.OK);
    }

    @PostMapping("/module")
    public ResponseEntity<Module> createModule(@RequestBody Module module)  {
        Module persistedObject = moduleService.create(module);
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }


    @GetMapping("module/{id}")
    private ResponseEntity<Module> getModule(@PathVariable long id) {
        Module module = moduleService.getModule(id);
        return new ResponseEntity<>(module, HttpStatus.OK);
    }
}