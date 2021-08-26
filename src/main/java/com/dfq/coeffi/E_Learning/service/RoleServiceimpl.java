package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.entity.hr.employee.Role;
import com.dfq.coeffi.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceimpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;


    @Override
    public Role saveUpdateRole(Role role) {
        return null;
    }

    @Override
    public List<Role> getRole() {
        return null;
    }

    @Override
    public List<Role> getActiveRoles(boolean roles) {
        return null;
    }

    @Override
    public void deActiveStatus(long id) {

    }

    @Override
    public Optional<Role> getRoleById(long id) {
        return Optional.empty();
    }
}

