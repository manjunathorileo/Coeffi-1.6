package com.dfq.coeffi.servicesimpl.hr;

import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.Designation;
import com.dfq.coeffi.repository.hr.DesignationRepository;
import com.dfq.coeffi.service.hr.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DesignationServiceImpl implements DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    @Override
    public Designation save(Designation designation) {
        designation.setStatus(true);
        return designationRepository.save(designation);
    }

    @Override
    public List<Designation> findAll() {
        return designationRepository.findByStatus(true);
    }

    @Override
    public Optional<Designation> getDesignation(Long id) {
        return Optional.ofNullable(designationRepository.findOne(id));
    }

    @Override
    public void delete(Long id) {
        designationRepository.deactivate(id);
    }

    @Override
    public List<Designation> getDepartmentDetail(Department department) {
        return designationRepository.findByDepartment(department);
    }

    @Override
    public Designation getByName(String role) {
        return designationRepository.findByName(role);
    }
}