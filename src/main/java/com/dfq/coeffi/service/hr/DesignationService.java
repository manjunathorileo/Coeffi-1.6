package com.dfq.coeffi.service.hr;

import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.Designation;

import java.util.List;
import java.util.Optional;

public interface DesignationService {

    Designation save(Designation designation);

    List<Designation> findAll();

    Optional<Designation> getDesignation(Long id);

    void delete(Long id);

    List<Designation> getDepartmentDetail(Department department);
}
