package com.dfq.coeffi.E_Learning.controller;

import com.dfq.coeffi.E_Learning.service.RoleService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
public class RoleEController extends BaseController {
    @Autowired
    RoleService roleService;

    @PostMapping(value = "/role")
    public Role create(@RequestBody Role role) {
        roleService.saveUpdateRole(role);
        return role;
    }

    @GetMapping(value = "/role")
    public List<Role> getRole() {
        return roleService.getRole();
    }

    @GetMapping(value = "/role/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable long id) {
        Optional<Role> roleOptional = roleService.getRoleById(id);
        if (!roleOptional.isPresent()) {
            throw new EntityNotFoundException();
        }
        Role role = roleOptional.get();
        return new ResponseEntity<>(role, HttpStatus.OK);


    }

    @DeleteMapping(value = "/status/{id}")
    public String getRole(@PathVariable long id) {
        roleService.deActiveStatus(id);
        return "deleted row with id=" + id;
    }

    @GetMapping(value = "/role/{status}")
    public List<Role> getRoleByStatus(@PathVariable boolean status) {
        return roleService.getActiveRoles(status);
    }
}
