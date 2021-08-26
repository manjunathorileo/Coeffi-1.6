package com.dfq.coeffi.service.hr;

import com.dfq.coeffi.entity.hr.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    Department save(Department department);

    Department merge(Department department);

    List<Department> findAll();

    Optional<Department> getDepartment(Long id);

    void delete(Long id);

    Optional<Department> isDepartmentExists(String name);

    Department getByName(String department);
}
