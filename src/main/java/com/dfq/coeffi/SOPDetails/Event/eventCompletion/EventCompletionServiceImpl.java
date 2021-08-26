package com.dfq.coeffi.SOPDetails.Event.eventCompletion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventCompletionServiceImpl implements EventCompletionService {

    @Autowired
    EventCompletionRepository eventCompletionRepository;

    @Override
    public EventCompletion saveEventCompletion(EventCompletion eventCompletion) {
        return eventCompletionRepository.save(eventCompletion);
    }

    @Override
    public List<EventCompletion> getAllEventCompletion() {
        return eventCompletionRepository.findAll();
    }

    @Override
    public Optional<EventCompletion> getEventCompletion(long id) {
        return eventCompletionRepository.findById(id);
    }

    @Override
    public List<EventCompletion> getEventCompletionBySopTypeByDigitalSopByEvent(long sopTypeId, long digitalSopId, long eventId) {
        List<EventCompletion> eventCompletions = new ArrayList<>();
        List<EventCompletion> eventCompletionList = eventCompletionRepository.findBySopTypeBydigitalSopByEvent(sopTypeId, digitalSopId, eventId);
        for (EventCompletion eventCompletionObj:eventCompletionList) {
            if (eventCompletionObj.getStatus().equals(true) && eventCompletionObj.getSopType().getStatus().equals(true) && eventCompletionObj.getSopCategory().getStatus().equals(true)){
                eventCompletions.add(eventCompletionObj);
            }
        }
        return eventCompletions;
    }

    @Override
    public List<EventCompletion> getEventCompletionBySopTypeBySopCategory(long sopTypeId, long sopCategoryId) {
        List<EventCompletion> eventCompletions = new ArrayList<>();
        List<EventCompletion> eventCompletionList = eventCompletionRepository.findBySopTypeBySopCategory(sopTypeId, sopCategoryId);
        for (EventCompletion eventCompletionObj:eventCompletionList) {
            if (eventCompletionObj.getStatus().equals(true) && eventCompletionObj.getSopType().getStatus().equals(true) && eventCompletionObj.getSopCategory().getStatus().equals(true)){
                eventCompletions.add(eventCompletionObj);
            }
        }
        return eventCompletions;
    }
}