package com.dfq.coeffi.SOPDetails.Event.eventSopBinding;

import java.util.List;
import java.util.Optional;

public interface EventSopBindService {

    EventSopBind createEventSopBind(EventSopBind eventSopBind);
    List<EventSopBind> getEventSopBind();
    Optional<EventSopBind> getEventSopBind(long id);
    EventSopBind deleteEventSopBind(long id);
    List<EventSopBind> getEventSopBindBySopTypeByDigitalSop(long sopTypeId, long digitalSopId);
}
