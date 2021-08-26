package com.dfq.coeffi.SOPDetails.Event.eventMaster;

import com.dfq.coeffi.SOPDetails.Event.eventSopBinding.EventSopBind;
import com.dfq.coeffi.SOPDetails.Event.eventSopBinding.EventSopBindRepository;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.dto.Completiondto;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.bridj.cpp.std.list;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@RestController
public class EventMasterController extends BaseController {

    private final EventMasterService eventMasterService;
    private final EmployeeService employeeService;

    @Autowired
    public EventMasterController(EventMasterService eventMasterService, EmployeeService employeeService) {
        this.eventMasterService = eventMasterService;
        this.employeeService = employeeService;
    }

    @Autowired
    EventSopBindRepository sopBindRepository;

    @PostMapping("save-event")
    public ResponseEntity<EventMaster> saveEvent(@RequestBody EventMaster eventMaster) {
        Date toDay = new Date();
        eventMaster.setCreatedOn(toDay);
        eventMaster.setStatus(true);
        EventMaster eventMaster1 = eventMasterService.saveEvent(eventMaster);
        return new ResponseEntity<>(eventMaster1, HttpStatus.CREATED);
    }

    @GetMapping("get-all-events")
    public ResponseEntity<List<EventMaster>> getEvents() {
        List<EventMaster> eventMasterList = eventMasterService.getAllEvents();
        if (eventMasterList.isEmpty()) {
            throw new EntityNotFoundException("There is No event");
        }
        return new ResponseEntity<>(eventMasterList, HttpStatus.OK);
    }

    @GetMapping("get-event/{id}")
    public ResponseEntity<EventMaster> getEvent(@PathVariable("id") long eventId) {
        EventMaster eventMaster = eventMasterService.getEventById(eventId);
        return new ResponseEntity<>(eventMaster, HttpStatus.OK);
    }

    @DeleteMapping("/event-delete/{id}")
    public ResponseEntity<EventMaster> deleteEventMaster(@PathVariable long id) {
        EventMaster eventMaster = eventMasterService.deleteEvent(id);
        return new ResponseEntity<>(eventMaster, HttpStatus.OK);
    }

    @GetMapping("get-employee-events/{emplId}")
    public ResponseEntity<List<EventMaster>> getEmployeeEvents(@PathVariable long emplId) {
        Optional<Employee> employee = employeeService.getEmployee(emplId);
        List<EventMaster> eventMasterList = eventMasterService.getEventMasterByEmployee(employee.get());
        if (eventMasterList.isEmpty()){
            throw new EntityNotFoundException("There is No event assigned to You.");
        }
        return new ResponseEntity<>(eventMasterList, HttpStatus.OK);
    }

    @GetMapping("get-user-completion-status")
    public ResponseEntity<list<Completiondto>> completion(){
        List<EventSopBind> eventMasterList=sopBindRepository.findAll();
        List<Completiondto> completiondtoList=new ArrayList<>();
        for (EventSopBind e:eventMasterList) {
            Completiondto completiondto=new Completiondto();
            EventMaster eventMaster=e.getEventMaster();
            completiondto.setEventNumber(eventMaster.getId());
            completiondto.setEventDescription(eventMaster.getDescription());
            completiondto.setEventDate(eventMaster.getCreatedOn());
            completiondto.setTriggeredBy(eventMaster.getAttachType());
            SopType sopType=e.getSopType();
            completiondto.setSopType(sopType.getSopTypeName());
            SopCategory SOPCategory =e.getSopCategory();
            completiondto.setSopCategory(SOPCategory.getName());
            Employee employee=e.getCreatedBy();
            completiondto.setExecutedBy(employee.getFirstName());
            completiondto.setExecutedOn(e.getCreatedOn());
            completiondtoList.add(completiondto);
        }
        return new ResponseEntity(completiondtoList,HttpStatus.OK);
    }

    @GetMapping("/attach-type-list")
    public ResponseEntity<List<AttachType>> getAttachTypeList(){
        List<AttachType> attachTypes = Arrays.asList(AttachType.values());
        return new ResponseEntity<>(attachTypes, HttpStatus.OK);
    }
}