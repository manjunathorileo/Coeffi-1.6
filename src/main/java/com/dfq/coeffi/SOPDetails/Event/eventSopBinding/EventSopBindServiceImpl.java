package com.dfq.coeffi.SOPDetails.Event.eventSopBinding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventSopBindServiceImpl implements EventSopBindService{

    @Autowired
    private EventSopBindRepository eventSopBindRepository;

    @Override
    public EventSopBind createEventSopBind(EventSopBind eventSopBind) {
        return eventSopBindRepository.save(eventSopBind);
    }

    @Override
    public List<EventSopBind> getEventSopBind() {
        List<EventSopBind> eventSopBinds = new ArrayList<>();
        List<EventSopBind> eventSopBindList = eventSopBindRepository.findAll();
        for (EventSopBind eventSopBindObj:eventSopBindList) {
            if (eventSopBindObj.getStatus().equals(true) && eventSopBindObj.getSopType().getStatus().equals(true) && eventSopBindObj.getSopCategory().getStatus().equals(true)){
                eventSopBinds.add(eventSopBindObj);
            }
        }
        return null;
    }

    @Override
    public Optional<EventSopBind> getEventSopBind(long id) {
        return eventSopBindRepository.findById(id);
    }

    @Override
    public EventSopBind deleteEventSopBind(long id) {
        Optional<EventSopBind> eventSopBindOptional = eventSopBindRepository.findById(id);
        EventSopBind eventSopBind = eventSopBindOptional.get();
        eventSopBind.setStatus(false);
        EventSopBind deleteEventSopBind = eventSopBindRepository.save(eventSopBind);
        return deleteEventSopBind;
    }

    @Override
    public List<EventSopBind> getEventSopBindBySopTypeByDigitalSop(long sopTypeId, long digitalSopId) {
        return eventSopBindRepository.findBySopTypeBydigitalSOP(sopTypeId, digitalSopId);
    }
}
