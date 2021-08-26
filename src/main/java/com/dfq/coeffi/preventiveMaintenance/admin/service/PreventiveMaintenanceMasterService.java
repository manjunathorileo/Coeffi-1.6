package com.dfq.coeffi.preventiveMaintenance.admin.service;

import com.dfq.coeffi.SOPDetails.Event.eventSopBinding.EventSopBind;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.PreventiveMaintenanceMaster;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;

import java.util.List;
import java.util.Optional;

public interface PreventiveMaintenanceMasterService {

    PreventiveMaintenanceMaster createPreventiveMaintenanceMaster(PreventiveMaintenanceMaster preventiveMaintenanceMaster);

    List<PreventiveMaintenanceMaster> getAllPreventiveMaintenanceMaster();

    Optional<PreventiveMaintenanceMaster> getPreventiveMaintenanceMaster(long id);

    PreventiveMaintenanceMaster getPreventiveMaintenanceMasterByAssemblyLineByStages(SopType sopType, SopCategory SOPCategory, DurationType durationType, long durationValue);

    List<PreventiveMaintenanceMaster> getLatestPreventiveMaintenanceMasterBySopTypeByDigitalSop(SopType sopType, SopCategory SOPCategory);


}
