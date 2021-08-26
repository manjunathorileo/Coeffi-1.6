package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.entity.hr.employee.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role saveUpdateRole(Role role);

    List<Role> getRole();

    List<Role> getActiveRoles(boolean roles);

    void deActiveStatus(long id);

    Optional<Role> getRoleById(long id);
}
