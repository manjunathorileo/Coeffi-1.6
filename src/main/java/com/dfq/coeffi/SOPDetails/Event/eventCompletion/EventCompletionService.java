package com.dfq.coeffi.SOPDetails.Event.eventCompletion;

import java.util.List;
import java.util.Optional;

public interface EventCompletionService
{
    EventCompletion saveEventCompletion(EventCompletion eventCompletion);
    List<EventCompletion> getAllEventCompletion();
    Optional<EventCompletion> getEventCompletion(long id);
    List<EventCompletion> getEventCompletionBySopTypeByDigitalSopByEvent(long sopTypeId, long digitalSopId, long eventId);
    List<EventCompletion> getEventCompletionBySopTypeBySopCategory(long sopTypeId, long sopCategoryId);
}
