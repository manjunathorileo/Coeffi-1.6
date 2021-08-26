package com.dfq.coeffi.preventiveMaintenance.admin.service;

import com.dfq.coeffi.preventiveMaintenance.admin.entity.SopStepsMaster;

import java.util.List;
import java.util.Optional;

public interface SopStepsMasterService {

    SopStepsMaster createCheckListMaster(SopStepsMaster sopStepsMaster);
    List<SopStepsMaster> getAllCheckListMaster();
    Optional<SopStepsMaster> getCheckListMaster(long id);
}
