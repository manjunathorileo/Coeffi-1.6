package com.dfq.coeffi.SOPDetails.Event.eventCompletion;

import com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned.EventWiseSopStepsAssigned;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventCompletionReportDto {

    private long eventCompletionId;
    private String sopTypeName;
    private String digitalSopName;
    private String eventTypeName;
    private String triggeredOn;
    private String triggeredBy;
    private String status;
    private String submitedBy;
    private String submitedOn;
    private long totalSopSteps;
    private long totalCompletedSopSteps;
    private long totalPendingSopSteps;
    private float sopStepListstatus;
    private List<EventWiseSopStepsAssigned> allEventSopStepsAssigned;
    private List<EventWiseSopStepsAssigned> completedEventSopSteps;
    private List<EventWiseSopStepsAssigned> pendingEventSopSteps;
}
