package com.dfq.coeffi.servicesimpl.hr;

import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.repository.hr.DepartmentRepository;
import com.dfq.coeffi.service.hr.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public Department save(Department department) {
        department.setStatus(true);
        return departmentRepository.save(department);
    }

    @Override
    public Department merge(Department department) {
        return departmentRepository.saveAndFlush(department);
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.activeDepartments();
    }

    @Override
    public Optional<Department> getDepartment(Long id) {
        return Optional.ofNullable(departmentRepository.findOne(id));
    }

    @Override
    public void delete(Long id) {
    departmentRepository.deactivate(id);
    }

    @Override
    public Optional<Department> isDepartmentExists(String name) {
        return departmentRepository.findDepartmentByName(name);
    }

    @Override
    public Department getByName(String name) {
        return departmentRepository.findByName(name);
    }
}
