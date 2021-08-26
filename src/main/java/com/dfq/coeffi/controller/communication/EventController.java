package com.dfq.coeffi.controller.communication;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.EventDto;
import com.dfq.coeffi.entity.communication.Event;
import com.dfq.coeffi.service.communication.EventService;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.communication.EmailLogService;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.notification.messages.TextMessageService;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.*;

@RestController
public class EventController extends BaseController {
    private final EventService eventService;
    private final EmployeeService employeeService;
    private final MailService mailService;
    private final EmailLogService emaillogservice;


    @Autowired
    public EventController(EventService eventService, EmployeeService employeeService, MailService mailService,
                           EmailLogService emaillogservice) {
        this.eventService = eventService;
        this.employeeService = employeeService;
        this.mailService = mailService;
        this.emaillogservice = emaillogservice;
    }

    @Autowired
    TextMessageService textMessageService;

    @GetMapping("event")
    public ResponseEntity<List<Event>> listOfEvents() {
        List<Event> events = eventService.getAllEvents();
        List<Event> eventList = new ArrayList<>();
        for (Event event : events) {
            List<Employee> employees = event.getEmployeeForEvent();
            List<Employee> employeeList = new ArrayList<>();
            for (Employee employeeOfEvent : employees) {
                Employee employee = new Employee();
                employee.setId(employeeOfEvent.getId());
                employee.setFirstName(employeeOfEvent.getFirstName());
                employee.setLastName(employeeOfEvent.getLastName());
                employee.setEmployeeCode(employeeOfEvent.getEmployeeCode());
                employeeList.add(employee);
            }
            event.setEmployeeForEvent(employeeList);
            eventList.add(event);
        }
        if (CollectionUtils.isEmpty(events)) {
            throw new EntityNotFoundException("events");
        }
        return new ResponseEntity<>(eventList, HttpStatus.OK);
    }


    @PutMapping("event/{id}")
    public ResponseEntity<Event> update(@PathVariable long id, @Valid @RequestBody Event event) {
        Optional<Event> persidtedEvent = eventService.getEvent(id);
        if (!persidtedEvent.isPresent()) {
            throw new EntityNotFoundException(Event.class.getSimpleName());
        }
        event.getClass();
        eventService.createEvent(event);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @DeleteMapping("event/{id}")
    public ResponseEntity<Event> delete(@PathVariable Long id) {
        Optional<Event> event = eventService.getEvent(id);
        if (!event.isPresent()) {
            throw new EntityNotFoundException(Event.class.getName());
        }
        eventService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param eventDto
     * @return
     * @throws MessagingException
     */
    @PostMapping("event/email")
    public ResponseEntity sendMail(@RequestBody EventDto eventDto) throws MessagingException {
        ArrayList employees = new ArrayList();
        for (Long employeeIds : eventDto.empIds) {
            Optional<Employee> employeeId = employeeService.getEmployee(employeeIds);
            if (!employeeId.isPresent()) {
                throw new EntityNotFoundException("employee");
            }
            Employee employee = employeeId.get();
            employees.add(employee);

        }
        Event event = saveEvent(eventDto, employees);
        for (Employee employee : event.getEmployeeForEvent()) {
            if (eventDto.isEmail) {
                if (employee.getEmployeeLogin() == null) {
                    throw new NullPointerException("No email set for " + employee.getFirstName());
                } else {
                    sendEmailAndSaveLog(eventDto, employee.getEmployeeLogin().getEmail(), employee.getFirstName());
                    event.setEmail(eventDto.isEmail);
                }
            } else if (eventDto.isSMS) {
                textMessageService.sendTextMessage(employee.getPhoneNumber(), eventDto.message);
                event.setSMS(eventDto.isSMS);
            }
        }
        Optional<Employee> employee1 = employeeService.getEmployee(Long.valueOf(eventDto.getEventIncharge()));
        event.setEventIncharge(employee1.get().getFirstName() + " " + employee1.get().getLastName());
        event.setEmployeeForEvent(employees);
        eventService.createEvent(event);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    public Event saveEvent(EventDto eventDto, List<Employee> employees) {
        Event event = new Event();
        event.setMessage(eventDto.message);
        event.setMessage1(eventDto.getMessage1());
        event.setMessage2(eventDto.getMessage2());
        event.setMessage3(eventDto.getMessage3());
        event.setMessage4(eventDto.getMessage4());
        event.setVenue(eventDto.venue);
        event.setEventName(eventDto.eventName);
        event.setEventDate(eventDto.eventDate);
        event.setEmployeeForEvent(employees);
        return event;
    }

    public void sendEmailAndSaveLog(EventDto eventDto, String email, String firstName) {
        ArrayList<EmailLog> emaillogs = new ArrayList<EmailLog>();
        Map<String, Object> model = new HashMap<>();
        model.put("Event-Incharge", eventDto.eventIncharge);
        model.put("Message1", eventDto.getMessage1());
        model.put("Message2", eventDto.getMessage2());
        model.put("Message3", eventDto.getMessage3());
        model.put("Message4", eventDto.getMessage4());
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = eventDto.eventName + " on " + eventDto.eventDate;
        String content = model.toString();
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, model);
        mailService.sendEmail(mailnew, "****");
        EmailLog emails = new EmailLog();
        emails.setRecipient(email);
        emails.setStudentName(firstName);
        emails.setSubject("Event Title");
        emails.setMessage(eventDto.message);
        emails.setDate(eventDto.eventDate);
        emaillogs.add(emails);
        emaillogservice.saveAllEmailLogs(emaillogs);
    }


    public void sendEmailAndSaveLogforShare(Event event, String email, String firstName) {
        ArrayList<EmailLog> emaillogs = new ArrayList<EmailLog>();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", event.getMessage());
        model.put("Incharge", event.getEventIncharge());
        model.put("Message1", event.getMessage1());
        model.put("Message2", event.getMessage2());
        model.put("Message3", event.getMessage3());
        model.put("Message4", event.getMessage4());
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = event.getEventName() + " on " + event.getEventDate();
        String content = model.toString();
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, model);
        mailService.sendEmail(mailnew, "Sample.txt");
        EmailLog emails = new EmailLog();
        emails.setRecipient(email);
        emails.setStudentName(firstName);
        emails.setSubject("Event Title");
        emails.setMessage(event.getMessage());
        emails.setMessage(event.getMessage1());
        emails.setMessage(event.getMessage2());
        emails.setMessage(event.getMessage3());
        emails.setMessage(event.getMessage4());
        emails.setDate(event.getEventDate());
        emaillogs.add(emails);
        emaillogservice.saveAllEmailLogs(emaillogs);
    }

    @PostMapping("event/share/{id}")
    public ResponseEntity<Mail> shareEvent(@PathVariable long id, @RequestBody EventDto eventDto) throws MessagingException {
        Optional<Event> event = eventService.getEvent(id);
        ArrayList bl = new ArrayList();
        for (Long refid1 : eventDto.empIds) {
            Optional<Employee> employeeId = employeeService.getEmployee(refid1);
            Employee employee = employeeId.get();
            if (!employeeId.isPresent()) {
                throw new EntityNotFoundException("employee");
            }
            if (event.get().isEmail()) {
                sendEmailAndSaveLogforShare(event.get(), employee.getEmployeeLogin().getEmail(), employee.getFirstName());
                event.get().setEmail(event.get().isEmail());
            } else if (event.get().isSMS()) {
                textMessageService.sendTextMessage(employee.getPhoneNumber(), event.get().getMessage());
                event.get().setSMS(event.get().isSMS());
            }
            bl.add(refid1);
            System.out.println(bl);
            eventService.createEvent(event.get());
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("event/by-employee/{employeeId}")
    public ResponseEntity<List<Event>> listOfEventsByEmployeeId(@PathVariable("employeeId") long employeeId) {
//        List<Event> events = eventService.getEventByEmployee(employeeId);
//        if (CollectionUtils.isEmpty(events)) {
//            throw new EntityNotFoundException("events not found for employee : " + employeeId);
//        }
//        return new ResponseEntity<>(events, HttpStatus.OK);
//    }
        ArrayList events1 = new ArrayList();
        List<Event> events = eventService.getAllEvents();
        for (Event event : events) {
            List<Employee> employees = event.getEmployeeForEvent();
            for (Employee employee : employees) {
                if (employee.getId() == employeeId) {
                    events1.add(event);
                }
            }
        }
        if (CollectionUtils.isEmpty(events)) {
            throw new EntityNotFoundException("events not found for employee : " + employeeId);
        }
        return new ResponseEntity<>(events1, HttpStatus.OK);
    }

}