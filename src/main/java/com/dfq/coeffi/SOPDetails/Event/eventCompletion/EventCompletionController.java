package com.dfq.coeffi.SOPDetails.Event.eventCompletion;

import com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned.EventWiseSopStepsAssigned;
import com.dfq.coeffi.controller.BaseController;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Boolean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
public class EventCompletionController extends BaseController {

    @Autowired
    private EventCompletionService eventCompletionService;

    @PostMapping("event-completion")
    public ResponseEntity<EventCompletion> saveEventCompletion(@RequestBody EventCompletion eventCompletion) {
        Date toDay = new Date();
        Optional<EventCompletion> eventCompletionOptional = eventCompletionService.getEventCompletion(eventCompletion.getId());
        EventCompletion eventCompletionOld = eventCompletionOptional.get();
        EventCompletion eventCompletionUpdated = new EventCompletion();
        if (eventCompletionOld.getSubmitedBy() != null) {
            throw new EntityNotFoundException("This is Already Submited.");
        } else {
            eventCompletionOld.setSubmitedBy(eventCompletion.getSubmitedBy());
            eventCompletionOld.setSubmitedOn(toDay);
            eventCompletionUpdated = eventCompletionService.saveEventCompletion(eventCompletionOld);
        }
        return new ResponseEntity<>(eventCompletionUpdated, HttpStatus.CREATED);
    }

    @GetMapping("event-completion-by-sopType-SOPDetails-event/{sopTypeId}/{digitalSopId}/{eventId}")
    public ResponseEntity<EventCompletion> getEventCompletionBySopTypeByDigitalSopByEvent(@PathVariable long sopTypeId, @PathVariable long digitalSopId, @PathVariable long eventId) {
        List<EventCompletion> eventCompletionList = eventCompletionService.getEventCompletionBySopTypeByDigitalSopByEvent(sopTypeId, digitalSopId, eventId);
        if (eventCompletionList.isEmpty()) {
            throw new EntityNotFoundException("There is no data for this SOP");
        }
        EventCompletion eventCompletion = new EventCompletion();
        for (EventCompletion eventCompletionObj : eventCompletionList) {
            if (eventCompletionObj.getStatus().equals(true)) {
                eventCompletion = eventCompletionObj;
            }
        }
        return new ResponseEntity<>(eventCompletion, HttpStatus.OK);
    }

    @GetMapping("all-event-completion-by-sopType-SOPDetails-event/{sopTypeId}/{sopCategoryId}")
    public ResponseEntity<List<EventCompletionReportDto>> getAllEventCompletionBySopTypeByDigitalSopByEvent(@PathVariable long sopTypeId, @PathVariable long sopCategoryId) {
        List<EventCompletionReportDto> eventCompletionReportDtos = getEventCompletionReport(sopTypeId, sopCategoryId, 0);
        if (eventCompletionReportDtos.isEmpty()) {
            throw new EntityNotFoundException("There is no data for this SOP");
        }
        return new ResponseEntity<>(eventCompletionReportDtos, HttpStatus.OK);
    }

    public List<EventCompletionReportDto> getEventCompletionReport(long sopTypeId, long digitalSopId, long eventId) {
        List<EventCompletion> eventCompletions = new ArrayList<>();
        List<EventCompletion> eventCompletionList = eventCompletionService.getAllEventCompletion();
        if (sopTypeId == 0 && digitalSopId == 0 && eventId == 0) {
            eventCompletions = eventCompletionList;
        } else if (sopTypeId > 0 && digitalSopId > 0 && eventId > 0) {
            eventCompletionList = eventCompletionService.getEventCompletionBySopTypeByDigitalSopByEvent(sopTypeId, digitalSopId, eventId);
            eventCompletions = eventCompletionList;
        } else if (sopTypeId > 0 && digitalSopId == 0 && eventId == 0) {
            for (EventCompletion eventCompletion : eventCompletionList) {
                if (eventCompletion.getSopType().getId() == sopTypeId) {
                    eventCompletions.add(eventCompletion);
                }
            }
        } else if (sopTypeId > 0 && digitalSopId > 0 && eventId == 0) {
            for (EventCompletion eventCompletion : eventCompletionList) {
                if (eventCompletion.getSopType().getId() == sopTypeId && eventCompletion.getSopCategory().getId() == digitalSopId) {
                    eventCompletions.add(eventCompletion);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId > 0 && eventId == 0) {
            for (EventCompletion eventCompletion : eventCompletionList) {
                if (eventCompletion.getSopCategory().getId() == digitalSopId) {
                    eventCompletions.add(eventCompletion);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId > 0 && eventId > 0) {
            for (EventCompletion eventCompletion : eventCompletionList) {
                if (eventCompletion.getSopCategory().getId() == digitalSopId && eventCompletion.getEventMaster().getId() == eventId) {
                    eventCompletions.add(eventCompletion);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId == 0 && eventId > 0) {
            for (EventCompletion eventCompletion : eventCompletionList) {
                if (eventCompletion.getEventMaster().getId() == eventId) {
                    eventCompletions.add(eventCompletion);
                }
            }
        }
        List<EventCompletionReportDto> eventCompletionReportDtos = new ArrayList<>();
        for (EventCompletion eventCompletion : eventCompletions) {
            EventCompletionReportDto eventCompletionReportDto = new EventCompletionReportDto();
            List<EventWiseSopStepsAssigned> eventWiseSopStepsAssigneds = eventCompletion.getEventWiseSopStepsAssigneds();
            List<EventWiseSopStepsAssigned> allEventSopStepsAssigned = new ArrayList<>();
            for (EventWiseSopStepsAssigned eventWiseSopStepsAssigned:eventWiseSopStepsAssigneds) {
                EventWiseSopStepsAssigned eventWiseSopStepsAssignedObj = new EventWiseSopStepsAssigned();
                eventWiseSopStepsAssignedObj.setCheckPart(eventWiseSopStepsAssigned.getCheckPart());
                eventWiseSopStepsAssignedObj.setCheckPoint(eventWiseSopStepsAssigned.getCheckPoint());
                eventWiseSopStepsAssignedObj.setDescription(eventWiseSopStepsAssigned.getDescription());
                eventWiseSopStepsAssignedObj.setStandardValue(eventWiseSopStepsAssigned.getStandardValue());
                if (eventWiseSopStepsAssigned.getSubmitedBy() != null){
                    eventWiseSopStepsAssignedObj.setCheckPointStatus(eventWiseSopStepsAssigned.getCheckPointStatus());
                    eventWiseSopStepsAssignedObj.setRemark(eventWiseSopStepsAssigned.getRemark());
                    eventWiseSopStepsAssignedObj.setSubmitedOn(eventWiseSopStepsAssigned.getSubmitedOn());
                    eventWiseSopStepsAssignedObj.setSubmitedBy(eventWiseSopStepsAssigned.getSubmitedBy());
                    eventWiseSopStepsAssignedObj.setSopStepsAssignedDocumentUpload(eventWiseSopStepsAssigned.getSopStepsAssignedDocumentUpload());
                } else {
                    eventWiseSopStepsAssignedObj.setCheckPointStatus(Boolean.FALSE);
                    eventWiseSopStepsAssignedObj.setRemark("");
                }
                allEventSopStepsAssigned.add(eventWiseSopStepsAssignedObj);
            }
            List<EventWiseSopStepsAssigned> completedEventSopSteps = new ArrayList<>();
            List<EventWiseSopStepsAssigned> pendingEventSopSteps = new ArrayList<>();
            long totalSopSteps = allEventSopStepsAssigned.size();
            long totalCompletedSopSteps = 0;
            long totalPendingSopSteps = 0;
            float sopSteplistStatus = 0;
            String status = "";
            for (EventWiseSopStepsAssigned eventWiseSopStepsAssigned : allEventSopStepsAssigned) {
                if (eventWiseSopStepsAssigned.getSubmitedBy() != null) {
                    completedEventSopSteps.add(eventWiseSopStepsAssigned);
                    totalCompletedSopSteps++;
                } else {
                    pendingEventSopSteps.add(eventWiseSopStepsAssigned);
                    totalPendingSopSteps++;
                }
            }
            if (totalCompletedSopSteps == 0) {
                status = "Not Started";
            } else if (totalCompletedSopSteps > 0 && totalCompletedSopSteps != totalSopSteps) {
                status = "WIP";
            } else if (totalCompletedSopSteps > 0 && totalCompletedSopSteps == totalSopSteps) {
                status = "Completed";
            }
            sopSteplistStatus = (Float.valueOf(totalCompletedSopSteps) / Float.valueOf(totalSopSteps)) * 100;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String triggeredOn = dateFormat.format(eventCompletion.getTriggeredOn());

            eventCompletionReportDto.setEventCompletionId(eventCompletion.getId());
            eventCompletionReportDto.setSopTypeName(eventCompletion.getSopType().getSopTypeName());
            eventCompletionReportDto.setDigitalSopName(eventCompletion.getSopCategory().getName());
            eventCompletionReportDto.setEventTypeName(eventCompletion.getEventMaster().getEventName());
            eventCompletionReportDto.setTriggeredOn(triggeredOn);
            eventCompletionReportDto.setTriggeredBy(eventCompletion.getEventMaster().getCreatedBy().getFirstName());
            eventCompletionReportDto.setStatus(status);
            if (eventCompletion.getSubmitedBy() != null ){
                eventCompletionReportDto.setSubmitedBy(eventCompletion.getSubmitedBy().getFirstName());
            } else {
                eventCompletionReportDto.setSubmitedBy(" ");
            }
            if (eventCompletion.getSubmitedOn() != null){
                String submitedOn = dateFormat.format(eventCompletion.getSubmitedOn());
                eventCompletionReportDto.setSubmitedOn(submitedOn);
            } else {
                eventCompletionReportDto.setSubmitedOn(" ");
            }
            eventCompletionReportDto.setTotalSopSteps(totalSopSteps);
            eventCompletionReportDto.setTotalCompletedSopSteps(totalCompletedSopSteps);
            eventCompletionReportDto.setTotalPendingSopSteps(totalPendingSopSteps);
            eventCompletionReportDto.setSopStepListstatus(sopSteplistStatus);
            eventCompletionReportDto.setAllEventSopStepsAssigned(allEventSopStepsAssigned);
            eventCompletionReportDto.setCompletedEventSopSteps(completedEventSopSteps);
            eventCompletionReportDto.setPendingEventSopSteps(pendingEventSopSteps);
            eventCompletionReportDtos.add(eventCompletionReportDto);
        }
        return eventCompletionReportDtos;
    }

    @GetMapping("/event-completion-report/{sopTypeId}/{digitalSopId}/{eventId}")
    public ResponseEntity<EventCompletionReportDto> getEventCompletionReportForUI(@PathVariable long sopTypeId, @PathVariable long digitalSopId, @PathVariable long eventId) {
        List<EventCompletionReportDto> eventCompletionReportDtos = getEventCompletionReport(sopTypeId, digitalSopId, eventId);
        return new ResponseEntity(eventCompletionReportDtos, HttpStatus.OK);
    }

    @GetMapping("/event-completion-report-download/{sopTypeId}/{digitalSopId}/{eventId}")
    public void jobSubCategoryCycleTimeTrackExcelReport(HttpServletRequest request, HttpServletResponse response, @PathVariable long sopTypeId, @PathVariable long digitalSopId, @PathVariable long eventId) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Execution_Event_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        List<EventCompletionReportDto> eventCompletionReportDtos = getEventCompletionReport(sopTypeId, digitalSopId, eventId);
        try {
            writeToSheetEventCompletion(workbook, eventCompletionReportDtos, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Something Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetEventCompletion(WritableWorkbook workbook, List<EventCompletionReportDto> eventCompletionReportDtos, int index) throws IOException, WriteException {

        WritableSheet s = workbook.createSheet("Execution Event", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 12);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setWrap(true);

        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 10);
        headerFont1.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setWrap(true);

        WritableFont dataFont = new WritableFont(WritableFont.TIMES, 10);
        WritableCellFormat dataFormat = new WritableCellFormat(dataFont);
        dataFormat.setAlignment(CENTRE);
        dataFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        dataFormat.setWrap(true);

        s.addCell(new Label(0, 0, "EXECUTION EVENT REPORT", headerFormat));
        s.addCell(new Label(0, 1, "SlNo.", headerFormat1));
        s.addCell(new Label(1, 1, "SOP Type", headerFormat1));
        s.addCell(new Label(2, 1, "Digital SOP", headerFormat1));
        s.addCell(new Label(3, 1, "Event Type", headerFormat1));
        s.addCell(new Label(4, 1, "Triggered On", headerFormat1));
        s.addCell(new Label(5, 1, "Triggered By", headerFormat1));
        s.addCell(new Label(6, 1, "Status", headerFormat1));
        s.addCell(new Label(7, 1, "Total SOP Steps", headerFormat1));
        s.addCell(new Label(8, 1, "Completed SOP Steps", headerFormat1));
        s.addCell(new Label(9, 1, "Pending SOP Steps", headerFormat1));
        s.addCell(new Label(10, 1, "SOP List Status", headerFormat1));

        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 15);
        s.setColumnView(4, 15);
        s.setColumnView(5, 15);
        s.setColumnView(6, 15);
        s.setColumnView(7, 15);
        s.setColumnView(8, 20);
        s.setColumnView(9, 20);
        s.setColumnView(10, 15);

        s.setRowView(0, 550);
        s.setRowView(1, 350);

        s.mergeCells(0, 0, 10, 0);

        int i = 0;
        int row = 2;
        for (EventCompletionReportDto eventCompletionReportDto:eventCompletionReportDtos){
            s.addCell(new Label(0, row, "" + i, dataFormat));
            s.addCell(new Label(1, row, "" + eventCompletionReportDto.getSopTypeName(), dataFormat));
            s.addCell(new Label(2, row, "" + eventCompletionReportDto.getDigitalSopName(), dataFormat));
            s.addCell(new Label(3, row, "" + eventCompletionReportDto.getEventTypeName(), dataFormat));
            s.addCell(new Label(4, row, "" + eventCompletionReportDto.getTriggeredOn(), dataFormat));
            s.addCell(new Label(5, row, "" + eventCompletionReportDto.getTriggeredBy(), dataFormat));
            s.addCell(new Label(6, row, "" + eventCompletionReportDto.getStatus(), dataFormat));
            s.addCell(new Label(7, row, "" + eventCompletionReportDto.getTotalSopSteps(), dataFormat));
            s.addCell(new Label(8, row, "" + eventCompletionReportDto.getTotalCompletedSopSteps(), dataFormat));
            s.addCell(new Label(9, row, "" + eventCompletionReportDto.getTotalPendingSopSteps(), dataFormat));
            s.addCell(new Label(10, row, "" + eventCompletionReportDto.getSopStepListstatus(), dataFormat));

            i++;
            row++;
        }
        return workbook;
    }

    @GetMapping("/Event-sop-stepsreport-download/{eventId}")
    public void jobSubCategoryCycleTimeTrackExcelReport(HttpServletRequest request, HttpServletResponse response, @PathVariable long eventId) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Sop_Step_List_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        Optional<EventCompletion> eventCompletion = eventCompletionService.getEventCompletion(eventId);
        try {
            writeToSheetTotalSopSteps(workbook, eventCompletion.get(), 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Something Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetTotalSopSteps(WritableWorkbook workbook, EventCompletion eventCompletion, int index) throws IOException, WriteException {

        WritableSheet s = workbook.createSheet("SOP Step List", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 12);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setWrap(true);

        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 10);
        headerFont1.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setWrap(true);

        WritableFont dataFont = new WritableFont(WritableFont.TIMES, 10);
        WritableCellFormat dataFormat = new WritableCellFormat(dataFont);
        dataFormat.setAlignment(CENTRE);
        dataFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        dataFormat.setWrap(true);

        s.addCell(new Label(0, 0, "SOP STEPS", headerFormat));
        s.addCell(new Label(0, 1, "SlNo.", headerFormat1));
        s.addCell(new Label(1, 1, "Check Part", headerFormat1));
        s.addCell(new Label(2, 1, "Check Point", headerFormat1));
        s.addCell(new Label(3, 1, "Description", headerFormat1));
        s.addCell(new Label(4, 1, "Standard Value", headerFormat1));
        s.addCell(new Label(5, 1, "Check Point Status", headerFormat1));
        s.addCell(new Label(6, 1, "Remark", headerFormat1));
        s.addCell(new Label(7, 1, "Submited On", headerFormat1));
        s.addCell(new Label(8, 1, "Submited By", headerFormat1));

        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 15);
        s.setColumnView(4, 15);
        s.setColumnView(5, 15);
        s.setColumnView(6, 15);
        s.setColumnView(7, 15);
        s.setColumnView(8, 20);

        s.setRowView(0, 550);
        s.setRowView(1, 350);

        s.mergeCells(0, 0, 8, 0);

        int i = 1;
        int row = 2;

        List<EventWiseSopStepsAssigned> allSopStepsAssigned = eventCompletion.getEventWiseSopStepsAssigneds();

        for (EventWiseSopStepsAssigned eventWiseSopStepsAssigned:allSopStepsAssigned) {
            String checkPointStatus = "Not Submited";
            if (eventWiseSopStepsAssigned.getCheckPointStatus() != null) {
                if (eventWiseSopStepsAssigned.getCheckPointStatus().equals(true)) {
                    checkPointStatus = "Submited";
                }
            }
            s.addCell(new Label(0, row, "" + i, dataFormat));
            s.addCell(new Label(1, row, "" + eventWiseSopStepsAssigned.getCheckPart(), dataFormat));
            s.addCell(new Label(2, row, "" + eventWiseSopStepsAssigned.getCheckPoint(), dataFormat));
            if (eventWiseSopStepsAssigned.getDescription() == null) {
                s.addCell(new Label(3, row, "", dataFormat));
            } else {
                s.addCell(new Label(3, row, "" + eventWiseSopStepsAssigned.getDescription(), dataFormat));
            }
            s.addCell(new Label(4, row, "" + eventWiseSopStepsAssigned.getStandardValue(), dataFormat));
            s.addCell(new Label(5, row, "" + checkPointStatus, dataFormat));
            if (eventWiseSopStepsAssigned.getRemark() == null){
                s.addCell(new Label(6, row, "", dataFormat));
            } else {
                s.addCell(new Label(6, row, "" + eventWiseSopStepsAssigned.getRemark(), dataFormat));
            }
            if (eventWiseSopStepsAssigned.getSubmitedOn() == null){
                s.addCell(new Label(7, row, "", dataFormat));
            } else {
                s.addCell(new Label(7, row, "" + eventWiseSopStepsAssigned.getSubmitedOn(), dataFormat));
            }
            if (eventWiseSopStepsAssigned.getSubmitedBy() == null){
                s.addCell(new Label(8, row, "", dataFormat));
            } else {
                s.addCell(new Label(8, row, "" + eventWiseSopStepsAssigned.getSubmitedBy(), dataFormat));
            }
            i++;
            row++;
        }
        return workbook;
    }
}