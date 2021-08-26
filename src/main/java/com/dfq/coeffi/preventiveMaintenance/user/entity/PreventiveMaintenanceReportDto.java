package com.dfq.coeffi.preventiveMaintenance.user.entity;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PreventiveMaintenanceReportDto {
    private long preventiveMaintenanceId;
    private String sopTypeName;
    private String digitalSopName;
    private String durationType;
    private long durationValue;
    private String triggeredOn;
    private String firstAssignedTo;
    private String secondAssignedTo;
    private String thirdAssignedTo;
    private String status;
    private String submitedBy;
    private String submitedOn;
    private long totalSopSteps;
    private long totalcompletedSopStep;
    private long totalpendingSopStep;
    private float sopSteplistStatus;
    private List<SopStepsAssigned> allSopSteps;
    private List<SopStepsAssigned> CompletedSopSteps;
    private List<SopStepsAssigned> pendingSopSteps;
}
