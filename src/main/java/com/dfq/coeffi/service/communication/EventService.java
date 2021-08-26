package com.dfq.coeffi.service.communication;

/**
 * @Auther H Kapil Kumar on 7/3/18.
 * @Company Orileo Technologies
 */

import com.dfq.coeffi.entity.communication.Event;

import java.util.List;
import java.util.Optional;

public interface EventService
{
    public Event createEvent(Event event);
    public void closeEvent(long id);
    Optional<Event> getEvent(long id);
    public List<Event> getAllEvents();
    void delete(long id);

    List<Event> getEventByEmployee(long employeeId);
}