package com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned;

import com.dfq.coeffi.SOPDetails.Event.eventCompletion.EventCompletion;
import com.dfq.coeffi.SOPDetails.Event.eventCompletion.EventCompletionService;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssignedDocumentUpload;
import com.dfq.coeffi.preventiveMaintenance.user.service.SopStepsAssignedDocumentUploadService;
import com.dfq.coeffi.service.hr.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class EventWiseSopStepsAssignedResource extends BaseController {

    private final EventWiseSopStepsAssignedService eventWiseSopStepsAssignedService;
    private final EmployeeService employeeService;
    private final EventCompletionService eventCompletionService;
    private final SopTypeService sopTypeService;
    private final SopCategoryService SOPCategoryService;
    private final SopStepsAssignedDocumentUploadService sopStepsAssignedDocumentUploadService;

    @Autowired
    public EventWiseSopStepsAssignedResource(EventWiseSopStepsAssignedService eventWiseSopStepsAssignedService, EmployeeService employeeService, EventCompletionService eventCompletionService, SopTypeService sopTypeService, SopCategoryService SOPCategoryService, SopStepsAssignedDocumentUploadService sopStepsAssignedDocumentUploadService) {
        this.eventWiseSopStepsAssignedService = eventWiseSopStepsAssignedService;
        this.employeeService = employeeService;
        this.eventCompletionService = eventCompletionService;
        this.sopTypeService = sopTypeService;
        this.SOPCategoryService = SOPCategoryService;
        this.sopStepsAssignedDocumentUploadService = sopStepsAssignedDocumentUploadService;
    }

    @PostMapping("/event-wise-sop-steps-assigned-update")
    public ResponseEntity<EventWiseSopStepsAssigned> updateEventCheckListAssigned(@Valid @RequestBody EventWiseSopStepsAssigned eventWiseSopStepsAssigned) {
        Date today = new Date();
        Optional<EventWiseSopStepsAssigned> eventCheckListAssignedOptional = eventWiseSopStepsAssignedService.getEventCheckListAssigned(eventWiseSopStepsAssigned.getId());
        SopStepsAssignedDocumentUpload sopStepsAssignedDocumentUpload = sopStepsAssignedDocumentUploadService.getDocumentFileById(eventWiseSopStepsAssigned.getSopStepsAssignedDocumentUpload().getId());
        EventWiseSopStepsAssigned eventWiseSopStepsAssignedObj = eventCheckListAssignedOptional.get();
        eventWiseSopStepsAssignedObj.setCheckPointStatus(eventWiseSopStepsAssigned.getCheckPointStatus());
        eventWiseSopStepsAssignedObj.setRemark(eventWiseSopStepsAssigned.getRemark());
        eventWiseSopStepsAssignedObj.setSubmitedOn(today);
        eventWiseSopStepsAssignedObj.setSubmitedBy(eventWiseSopStepsAssigned.getSubmitedBy());
        eventWiseSopStepsAssignedObj.setSopStepsAssignedDocumentUpload(sopStepsAssignedDocumentUpload);
        EventWiseSopStepsAssigned checkListAssignedUpdate = eventWiseSopStepsAssignedService.createEventCheckListAssigned(eventWiseSopStepsAssignedObj);
        return new ResponseEntity<>(checkListAssignedUpdate, HttpStatus.OK);
    }

    @PostMapping("/event-wise-sop-steps-entry/{sopTypeId}/{digitalSopId}/{eventId}")
    public ResponseEntity<EventWiseSopStepsAssigned> createEventCheckListAssigned(@Valid @RequestBody EventWiseSopStepsAssigned eventWiseSopStepsAssigned, @PathVariable long sopTypeId, @PathVariable long digitalSopId, @PathVariable long eventId){
        Date today = new Date();
        eventWiseSopStepsAssigned.setStatus(true);
        EventWiseSopStepsAssigned checkListAssignedUpdate = eventWiseSopStepsAssignedService.createEventCheckListAssigned(eventWiseSopStepsAssigned);
        EventCompletion eventCompletion = new EventCompletion();
        List<EventCompletion> eventCompletionList = eventCompletionService.getEventCompletionBySopTypeByDigitalSopByEvent(sopTypeId, digitalSopId, eventId);
        for (EventCompletion eventCompletionObj:eventCompletionList) {
            eventCompletion = eventCompletionObj;
        }
        if (eventCompletion == null){
            throw new EntityNotFoundException("There is no event present");
        }
        List<EventWiseSopStepsAssigned> eventWiseSopStepsAssignedOld = eventCompletion.getEventWiseSopStepsAssigneds();
        eventWiseSopStepsAssignedOld.add(checkListAssignedUpdate);
        eventCompletion.setEventWiseSopStepsAssigneds(eventWiseSopStepsAssignedOld);
        EventCompletion eventCompletionObj = eventCompletionService.saveEventCompletion(eventCompletion);
        return new ResponseEntity(eventCompletionObj, HttpStatus.OK);
    }
}