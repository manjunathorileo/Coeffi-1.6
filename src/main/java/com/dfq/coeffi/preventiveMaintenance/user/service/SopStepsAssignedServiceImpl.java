package com.dfq.coeffi.preventiveMaintenance.user.service;

import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssigned;
import com.dfq.coeffi.preventiveMaintenance.user.repository.SopStepsAssignedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SopStepsAssignedServiceImpl implements SopStepsAssignedService {

    @Autowired
    private SopStepsAssignedRepository sopStepsAssignedRepository;

    @Override
    public SopStepsAssigned createCheckListAssigned(SopStepsAssigned sopStepsAssigned) {
        return sopStepsAssignedRepository.save(sopStepsAssigned);
    }

    @Override
    public List<SopStepsAssigned> getAllCheckListAssigned() {
        return sopStepsAssignedRepository.findAll();
    }

    @Override
    public Optional<SopStepsAssigned> getCheckListAssigned(long id) {
        return sopStepsAssignedRepository.findById(id);
    }
}
