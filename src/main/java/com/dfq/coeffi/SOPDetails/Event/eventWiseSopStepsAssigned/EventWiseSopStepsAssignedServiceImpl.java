package com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventWiseSopStepsAssignedServiceImpl implements EventWiseSopStepsAssignedService {

    @Autowired
    private EventWiseSopStepsAssignedRepository eventWiseSopStepsAssignedRepository;

    @Override
    public EventWiseSopStepsAssigned createEventCheckListAssigned(EventWiseSopStepsAssigned eventWiseSopStepsAssigned) {
        return eventWiseSopStepsAssignedRepository.save(eventWiseSopStepsAssigned);
    }

    @Override
    public List<EventWiseSopStepsAssigned> getAllEventCheckListAssigned() {
        List<EventWiseSopStepsAssigned> eventWiseSopStepsAssigneds = new ArrayList<>();
        List<EventWiseSopStepsAssigned> eventWiseSopSteps = eventWiseSopStepsAssignedRepository.findAll();
        for (EventWiseSopStepsAssigned eventWiseSopStepsAssignedObj : eventWiseSopSteps) {
            if (eventWiseSopStepsAssignedObj.getStatus().equals(true)){
                eventWiseSopStepsAssigneds.add(eventWiseSopStepsAssignedObj);
            }
        }
        return eventWiseSopStepsAssigneds;
    }

    @Override
    public Optional<EventWiseSopStepsAssigned> getEventCheckListAssigned(long id) {
        return eventWiseSopStepsAssignedRepository.findById(id);
    }

    @Override
    public EventWiseSopStepsAssigned deleteEventCheckListAssigned(long id) {
        Optional<EventWiseSopStepsAssigned> eventCheckListAssigned = eventWiseSopStepsAssignedRepository.findById(id);
        EventWiseSopStepsAssigned eventWiseSopStepsAssignedobj = eventCheckListAssigned.get();
        eventWiseSopStepsAssignedobj.setStatus(false);
        EventWiseSopStepsAssigned deletedEventWiseSopStepsAssigned = eventWiseSopStepsAssignedRepository.save(eventWiseSopStepsAssignedobj);
        return deletedEventWiseSopStepsAssigned;
    }
}
