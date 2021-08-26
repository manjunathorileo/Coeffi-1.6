package com.dfq.coeffi.SOPDetails.Event.eventMaster;

import com.dfq.coeffi.entity.hr.employee.Employee;

import java.util.List;

public interface EventMasterService {

    EventMaster saveEvent(EventMaster eventMaster);

    List<EventMaster> getAllEvents();

    EventMaster getEventById(long id);

    EventMaster deleteEvent(long id);

    List<EventMaster> getEventMasterByEmployee(Employee employee);
}
