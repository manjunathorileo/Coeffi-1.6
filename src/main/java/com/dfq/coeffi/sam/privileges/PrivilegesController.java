package com.dfq.coeffi.sam.privileges;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.sam.module.Module;
import com.dfq.coeffi.sam.module.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class PrivilegesController extends BaseController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private PrivilegesService privilegesService;

    @GetMapping("privileges")
    public ResponseEntity<List<Privileges>> getPrivileges() {
        List<Privileges> privileges = privilegesService.getPrivileges();
        if (CollectionUtils.isEmpty(privileges)) {
            throw new EntityNotFoundException("privileges");
        }
        return new ResponseEntity<>(privileges, HttpStatus.OK);
    }

    @GetMapping("privileges-bymodule/{moduleId}")
    public ResponseEntity<List<Privileges>> getPrivilegesByModules(@PathVariable long moduleId) {
        Module module = moduleService.getModule(moduleId);
        List<Privileges> privileges = privilegesService.getPrivilegesByModule(module);
        if (CollectionUtils.isEmpty(privileges)) {
            throw new EntityNotFoundException("privileges-for-module");
        }
        return new ResponseEntity<>(privileges, HttpStatus.OK);
    }

    @PostMapping("/privileges/{moduleId}")
    public ResponseEntity<Privileges> createPrivileges(@RequestBody Privileges privileges, @PathVariable("moduleId") long moduleId)  {
        Module module = moduleService.getModule(moduleId);
        privileges.setModule(module);
        Privileges persistedObject = privilegesService.create(privileges);
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }
}