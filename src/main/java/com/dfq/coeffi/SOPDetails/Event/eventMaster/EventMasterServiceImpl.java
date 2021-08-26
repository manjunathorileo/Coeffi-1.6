package com.dfq.coeffi.SOPDetails.Event.eventMaster;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventMasterServiceImpl implements EventMasterService {

    @Autowired
    EventMasterRepository eventMasterRepository;

    @Override
    public EventMaster saveEvent(EventMaster eventMaster) {
        return eventMasterRepository.save(eventMaster);
    }

    @Override
    public List<EventMaster> getAllEvents() {
        List<EventMaster> eventMasters = new ArrayList<>();
        List<EventMaster> eventMasterList = eventMasterRepository.findAll();
        for (EventMaster eventMasterObj:eventMasterList) {
            if (eventMasterObj.getStatus().equals(true)){
                eventMasters.add(eventMasterObj);
            }
        }
        return eventMasters;
    }

    @Override
    public EventMaster getEventById(long id) {
        return eventMasterRepository.findOne(id);
    }

    @Override
    public EventMaster deleteEvent(long id) {
        EventMaster eventMaster = eventMasterRepository.findOne(id);
        eventMaster.setStatus(false);
        EventMaster deletedEventMaster = eventMasterRepository.save(eventMaster);
        return deletedEventMaster;
    }

    @Override
    public List<EventMaster> getEventMasterByEmployee(Employee employee) {
        List<EventMaster> eventMasters = new ArrayList<>();
        List<EventMaster> eventMasterList = eventMasterRepository.findByAttachedTo(employee);
        for (EventMaster eventMasterObj:eventMasterList) {
            if (eventMasterObj.getStatus().equals(true)){
                eventMasters.add(eventMasterObj);
            }
        }
        return eventMasters;
    }
}
