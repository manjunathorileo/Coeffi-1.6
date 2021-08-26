package com.dfq.coeffi.superadmin.Controllers;

import com.dfq.coeffi.superadmin.Entity.SuperModule;
import com.dfq.coeffi.superadmin.Services.SuperModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SuperModuleController
{
    @Autowired
    SuperModuleService superModuleService;

    @PostMapping("super-module-save")
    public ResponseEntity<SuperModule> create(@RequestBody SuperModule superModule)
    {
        SuperModule superModule1=superModuleService.create(superModule);

        return new ResponseEntity<>(superModule1, HttpStatus.CREATED);
    }

    @GetMapping("super-module-view")
    public ResponseEntity<List<SuperModule>> getAll()
    {
        List<SuperModule> superModuleList=superModuleService.getAllModule();

        return new ResponseEntity<>(superModuleList,HttpStatus.OK);
    }

    @GetMapping("super-module-id")
    public ResponseEntity<SuperModule> getById(@PathVariable long id)
    {
        SuperModule superModule=superModuleService.getModuleById(id);

        return new ResponseEntity<>(superModule,HttpStatus.OK);
    }

}
