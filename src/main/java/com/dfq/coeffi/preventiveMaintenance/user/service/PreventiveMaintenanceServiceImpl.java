package com.dfq.coeffi.preventiveMaintenance.user.service;

import com.dfq.coeffi.preventiveMaintenance.user.entity.PreventiveMaintenance;
import com.dfq.coeffi.preventiveMaintenance.user.repository.PreventiveMaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreventiveMaintenanceServiceImpl implements PreventiveMaintenanceService {

    @Autowired
    private PreventiveMaintenanceRepository preventiveMaintenanceRepository;

    @Override
    public PreventiveMaintenance createPreventiveMaintenance(PreventiveMaintenance preventiveMaintenance) {
        return preventiveMaintenanceRepository.save(preventiveMaintenance);
    }

    @Override
    public List<PreventiveMaintenance> getAllPreventiveMaintenance() {
        return preventiveMaintenanceRepository.findAll();
    }

    @Override
    public Optional<PreventiveMaintenance> getPreventiveMaintenance(long id) {
        return preventiveMaintenanceRepository.findById(id);
    }

    @Override
    public List<PreventiveMaintenance> getPreventiveMaintenanceByAssemblyLineByStagesByDurationTypeByDurationValue(long assemblyLineId, long stagesId, long durationTypeId, long durationValue) {
        return preventiveMaintenanceRepository.findByAssemblyLineByStagesByDurationTypeByDurationValue(assemblyLineId, stagesId, durationTypeId, durationValue);
    }

    @Override
    public List<PreventiveMaintenance> getPreventiveMaintenanceByDurationTypeByDurationValue(long durationTypeId, long durationValue) {
        return preventiveMaintenanceRepository.findByDurationTypeByDurationValue(durationTypeId, durationValue);
    }

    @Override
    public List<PreventiveMaintenance> getPreventiveMaintenanceBySopTypeBySopCategory(long sopTypeId, long SopCategoryId) {
        return preventiveMaintenanceRepository.findBySopTypeBySopCategory(sopTypeId, SopCategoryId);
    }
}
