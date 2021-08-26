package com.dfq.coeffi.SOPDetails.Event.eventSopBinding;

import com.dfq.coeffi.SOPDetails.Event.eventCompletion.EventCompletion;
import com.dfq.coeffi.SOPDetails.Event.eventCompletion.EventCompletionService;
import com.dfq.coeffi.SOPDetails.Event.eventMaster.EventMasterService;
import com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned.EventWiseSopStepsAssigned;
import com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned.EventWiseSopStepsAssignedService;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.PreventiveMaintenanceMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.SopStepsMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.service.PreventiveMaintenanceMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class EventSopBindController extends BaseController {

    private final EventSopBindService eventSopBindService;
    private final SopTypeService sopTypeService;
    private final SopCategoryService SOPCategoryService;
    private final EventCompletionService eventCompletionService;
    private final PreventiveMaintenanceMasterService preventiveMaintenanceMasterService;
    private final EventWiseSopStepsAssignedService eventWiseSopStepsAssignedService;
    private final EventMasterService eventMasterService;

    @Autowired
    public EventSopBindController(EventSopBindService eventSopBindService, SopTypeService sopTypeService, SopCategoryService SOPCategoryService, EventCompletionService eventCompletionService, PreventiveMaintenanceMasterService preventiveMaintenanceMasterService, EventWiseSopStepsAssignedService eventWiseSopStepsAssignedService, EventMasterService eventMasterService) {
        this.eventSopBindService = eventSopBindService;
        this.sopTypeService = sopTypeService;
        this.SOPCategoryService = SOPCategoryService;
        this.eventCompletionService = eventCompletionService;
        this.preventiveMaintenanceMasterService = preventiveMaintenanceMasterService;
        this.eventWiseSopStepsAssignedService = eventWiseSopStepsAssignedService;
        this.eventMasterService = eventMasterService;
    }

    @PostMapping("event-sop-bind")
    public ResponseEntity<EventSopBind> saveEventSopBind(@RequestBody EventSopBind eventSopBind){
        Boolean status = checkOldEvent(eventSopBind);
        if (status.equals(true)){
            throw new EntityNotFoundException("This Event is already binded, Please create new Event.");
        }
        Date today = new Date();
        eventSopBind.setStatus(true);
        eventSopBind.setCreatedOn(today);
        EventSopBind eventSopBindObj = eventSopBindService.createEventSopBind(eventSopBind);

        PreventiveMaintenanceMaster preventiveMaintenanceMaster = new PreventiveMaintenanceMaster();
        List<PreventiveMaintenanceMaster> preventiveMaintenanceMasterList = preventiveMaintenanceMasterService.getLatestPreventiveMaintenanceMasterBySopTypeByDigitalSop(eventSopBindObj.getSopType(), eventSopBindObj.getSopCategory());
        List<EventWiseSopStepsAssigned> eventWiseSopStepsAssigneds = new ArrayList<>();
        if (!preventiveMaintenanceMasterList.isEmpty()) {
            for (PreventiveMaintenanceMaster preventiveMaintenanceMasterObj : preventiveMaintenanceMasterList) {
                preventiveMaintenanceMaster = preventiveMaintenanceMasterObj;
            }
            List<SopStepsMaster> sopStepsMaster = preventiveMaintenanceMaster.getSopStepsMasters();
            for (SopStepsMaster sopStepsMasterObj : sopStepsMaster) {
                EventWiseSopStepsAssigned eventWiseSopStepsAssigned = new EventWiseSopStepsAssigned();
                eventWiseSopStepsAssigned.setCheckPart(sopStepsMasterObj.getCheckPart());
                eventWiseSopStepsAssigned.setCheckPoint(sopStepsMasterObj.getCheckPoint());
                eventWiseSopStepsAssigned.setDescription(sopStepsMasterObj.getDescription());
                eventWiseSopStepsAssigned.setStandardValue(sopStepsMasterObj.getStandardValue());
                eventWiseSopStepsAssigned.setStatus(true);
                EventWiseSopStepsAssigned eventWiseSopStepsAssignedObj = eventWiseSopStepsAssignedService.createEventCheckListAssigned(eventWiseSopStepsAssigned);
                eventWiseSopStepsAssigneds.add(eventWiseSopStepsAssigned);
            }
        }
        EventCompletion eventCompletion = new EventCompletion();
        eventCompletion.setSopType(eventSopBindObj.getSopType());
        eventCompletion.setSopCategory(eventSopBindObj.getSopCategory());
        eventCompletion.setEventMaster(eventSopBindObj.getEventMaster());
        eventCompletion.setStatus(true);
        if (!eventWiseSopStepsAssigneds.isEmpty()) {
            eventCompletion.setEventWiseSopStepsAssigneds(eventWiseSopStepsAssigneds);
        }
        EventCompletion eventCompletionObj = eventCompletionService.saveEventCompletion(eventCompletion);
        return new ResponseEntity<>(eventSopBindObj, HttpStatus.CREATED);
    }

    @GetMapping("event-sop-bind")
    public ResponseEntity<List<EventSopBind>> getAllEventSopBind(){
        List<EventSopBind> eventSopBinds = eventSopBindService.getEventSopBind();
        if (eventSopBinds.isEmpty()){
            throw new EntityNotFoundException("There is no event binded");
        }
        return new ResponseEntity<>(eventSopBinds, HttpStatus.CREATED);
    }

    @GetMapping("event-sop-bind/{id}")
    public ResponseEntity<EventSopBind> getEventSopBind(@PathVariable long id){
        Optional<EventSopBind> eventSopBindOptional = eventSopBindService.getEventSopBind(id);
        return new ResponseEntity(eventSopBindOptional, HttpStatus.OK);
    }

    @DeleteMapping("/event-sop-bind/{id}")
    public ResponseEntity<EventSopBind> deleteEventSopBind(@PathVariable long id){
        EventSopBind eventSopBind = eventSopBindService.deleteEventSopBind(id);
        return new ResponseEntity<>(eventSopBind, HttpStatus.OK);
    }

    @GetMapping("event-sop-bind-by-sopType-SOPDetails/{sopTypeId}/{digitalSopId}")
    public ResponseEntity<List<EventSopBind>> getEventBySopTypeByDigitalSop(@PathVariable long sopTypeId, @PathVariable long digitalSopId){
        List<EventSopBind> eventSopBinds = eventSopBindService.getEventSopBindBySopTypeByDigitalSop(sopTypeId, digitalSopId);
        if (eventSopBinds.isEmpty()){
            throw new EntityNotFoundException("There is No event for this SOP");
        }
        return new ResponseEntity<>(eventSopBinds, HttpStatus.OK);
    }

    private Boolean checkOldEvent(EventSopBind eventSopBind){
        Boolean status = false;
        List<EventCompletion> eventCompletions = eventCompletionService.getEventCompletionBySopTypeByDigitalSopByEvent(eventSopBind.getSopType().getId(), eventSopBind.getSopCategory().getId(), eventSopBind.getEventMaster().getId());
        for (EventCompletion eventCompletion:eventCompletions) {
            if (eventCompletion.getSubmitedBy().equals(null) || eventCompletion.getSubmitedBy().equals(null)){
                status = true;
            }
        }
        return status;
    }
}
