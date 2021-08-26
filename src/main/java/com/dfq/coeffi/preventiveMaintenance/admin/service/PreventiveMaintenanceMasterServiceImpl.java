package com.dfq.coeffi.preventiveMaintenance.admin.service;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.PreventiveMaintenanceMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.repository.PreventiveMaintenanceMasterRepository;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class PreventiveMaintenanceMasterServiceImpl implements PreventiveMaintenanceMasterService {

    @Autowired
    private PreventiveMaintenanceMasterRepository preventiveMaintenanceMasterRepository;

    @Override
    public PreventiveMaintenanceMaster createPreventiveMaintenanceMaster(PreventiveMaintenanceMaster preventiveMaintenanceMaster) {
        return preventiveMaintenanceMasterRepository.save(preventiveMaintenanceMaster);
    }

    @Override
    public List<PreventiveMaintenanceMaster> getAllPreventiveMaintenanceMaster() {
        return preventiveMaintenanceMasterRepository.findAll();
    }

    @Override
    public Optional<PreventiveMaintenanceMaster> getPreventiveMaintenanceMaster(long id) {
        return preventiveMaintenanceMasterRepository.findById(id);
    }

    @Override
    public PreventiveMaintenanceMaster getPreventiveMaintenanceMasterByAssemblyLineByStages(SopType sopType, SopCategory SOPCategory, DurationType durationType, long durationValue) {
        return preventiveMaintenanceMasterRepository.findByAssemblyLineByStagesByDurationTypeByDurationValue(sopType.getId(), SOPCategory.getId(), durationType.getId(), durationValue);
    }

    @Override
    public List<PreventiveMaintenanceMaster> getLatestPreventiveMaintenanceMasterBySopTypeByDigitalSop(SopType sopType, SopCategory SOPCategory) {
        return preventiveMaintenanceMasterRepository.findBySopTypeByDigitalSop(sopType.getId(), SOPCategory.getId());
    }
}
