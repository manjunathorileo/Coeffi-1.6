package com.dfq.coeffi.preventiveMaintenance.user.service;

import com.dfq.coeffi.preventiveMaintenance.user.entity.PreventiveMaintenance;

import java.util.List;
import java.util.Optional;


public interface PreventiveMaintenanceService {

    PreventiveMaintenance createPreventiveMaintenance(PreventiveMaintenance preventiveMaintenance);

    List<PreventiveMaintenance> getAllPreventiveMaintenance();

    Optional<PreventiveMaintenance> getPreventiveMaintenance(long id);

    List<PreventiveMaintenance> getPreventiveMaintenanceByAssemblyLineByStagesByDurationTypeByDurationValue(long assemblyLineId, long stagesId, long durationTypeId, long durationValue);

    List<PreventiveMaintenance> getPreventiveMaintenanceByDurationTypeByDurationValue(long durationType, long durationValue);

    List<PreventiveMaintenance> getPreventiveMaintenanceBySopTypeBySopCategory(long sopTypeId, long SopCategoryId);
}