package com.dfq.coeffi.preventiveMaintenance.user.service;

import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssigned;

import java.util.List;
import java.util.Optional;

public interface SopStepsAssignedService {

    SopStepsAssigned createCheckListAssigned(SopStepsAssigned sopStepsAssigned);
    List<SopStepsAssigned> getAllCheckListAssigned();
    Optional<SopStepsAssigned> getCheckListAssigned(long id);
}
