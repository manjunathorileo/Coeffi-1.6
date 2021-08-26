package com.dfq.coeffi.servicesimpl.communication;
/**
 * @Auther H Kapil Kumar on 7/3/18.
 * @Company Orileo Technologies
 */

import com.dfq.coeffi.entity.communication.Event;
import com.dfq.coeffi.repository.communication.EventRepository;
import com.dfq.coeffi.service.communication.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.*;

import static java.util.Optional.ofNullable;

@Service
public class EventServiceImpl implements EventService,Serializable
{
	private static final long serialVersionUID = -3777190476073638464L;
	@Autowired
    private EventRepository eventRepository;


    @Transactional
    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    @Override
    public void closeEvent(long id) {

    }

    @Override
    public Optional<Event> getEvent(long id) {
		return ofNullable(eventRepository.findOne(id));
    }

    @Override
    public List<Event> getEventByEmployee(long employeeId) {
        return eventRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public void delete(long id) {
        eventRepository.delete(id);
    }
}