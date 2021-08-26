package com.dfq.coeffi.preventiveMaintenance.admin.service;

import com.dfq.coeffi.preventiveMaintenance.admin.entity.SopStepsMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.repository.SopStepsMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SopStepsMasterServiceImpl implements SopStepsMasterService {


    @Autowired
    private SopStepsMasterRepository sopStepsMasterRepository;

    @Override
    public SopStepsMaster createCheckListMaster(SopStepsMaster sopStepsMaster) {
        return sopStepsMasterRepository.save(sopStepsMaster);
    }

    @Override
    public List<SopStepsMaster> getAllCheckListMaster() {
        return sopStepsMasterRepository.findAll();
    }

    @Override
    public Optional<SopStepsMaster> getCheckListMaster(long id) {
        return sopStepsMasterRepository.findById(id);
    }
}
