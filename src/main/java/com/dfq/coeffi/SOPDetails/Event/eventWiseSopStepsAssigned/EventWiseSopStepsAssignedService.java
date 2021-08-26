package com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned;

import java.util.List;
import java.util.Optional;

public interface EventWiseSopStepsAssignedService {

    EventWiseSopStepsAssigned createEventCheckListAssigned(EventWiseSopStepsAssigned eventWiseSopStepsAssigned);
    List<EventWiseSopStepsAssigned> getAllEventCheckListAssigned();
    Optional<EventWiseSopStepsAssigned> getEventCheckListAssigned(long id);
    EventWiseSopStepsAssigned deleteEventCheckListAssigned(long id);
}
