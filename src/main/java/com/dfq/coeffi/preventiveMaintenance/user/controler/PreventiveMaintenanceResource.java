package com.dfq.coeffi.preventiveMaintenance.user.controler;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.preventiveMaintenance.admin.service.SopStepsMasterService;
import com.dfq.coeffi.preventiveMaintenance.user.entity.PreventiveMaintenance;
import com.dfq.coeffi.preventiveMaintenance.user.entity.PreventiveMaintenanceReportDto;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssigned;
import com.dfq.coeffi.preventiveMaintenance.user.service.PreventiveMaintenanceService;
import com.dfq.coeffi.service.hr.EmployeeService;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
@Slf4j
public class PreventiveMaintenanceResource extends BaseController {

    private final SopTypeService sopTypeService;
    private final SopCategoryService SOPCategoryService;
    private final PreventiveMaintenanceService preventiveMaintenanceService;
    private final SopStepsMasterService sopStepsMasterService;
    private final EmployeeService employeeService;

    @Autowired
    public PreventiveMaintenanceResource(SopTypeService sopTypeService, SopCategoryService SOPCategoryService, PreventiveMaintenanceService preventiveMaintenanceService, SopStepsMasterService sopStepsMasterService, EmployeeService employeeService) {
        this.sopTypeService = sopTypeService;
        this.SOPCategoryService = SOPCategoryService;
        this.preventiveMaintenanceService = preventiveMaintenanceService;
        this.sopStepsMasterService = sopStepsMasterService;
        this.employeeService = employeeService;
    }

    @PostMapping("/preventive-maintenance-by-sopType-SOPDetails/{loggerId}")
    public ResponseEntity<PreventiveMaintenance> submitPreventiveMaintenance(@Valid @RequestBody PreventiveMaintenance preventiveMaintenance, @PathVariable long loggerId) {
        Optional<Employee> logger = employeeService.getEmployee(loggerId);
        Date toDay = new Date();
        Optional<PreventiveMaintenance> preventiveMaintenanceOptional = preventiveMaintenanceService.getPreventiveMaintenance(preventiveMaintenance.getId());
        PreventiveMaintenance preventiveMaintenanceObj = preventiveMaintenanceOptional.get();
        PreventiveMaintenance preventiveMaintenanceSubmit = new PreventiveMaintenance();
        if (preventiveMaintenanceObj.getSubmitedBy() != null) {
            throw new EntityNotFoundException("This is Already Submited.");
        } else {
            preventiveMaintenanceObj.setAllCheckListCompleted(true);
            preventiveMaintenanceObj.setSubmitedBy(logger.get().getFirstName());
            preventiveMaintenanceObj.setSubmitedOn(toDay);
            preventiveMaintenanceObj.setIsAutoSubmit(false);
            preventiveMaintenanceSubmit = preventiveMaintenanceService.createPreventiveMaintenance(preventiveMaintenanceObj);
        }
        return new ResponseEntity<>(preventiveMaintenanceSubmit, HttpStatus.OK);
    }

    @GetMapping("/preventive-maintenance-by-sopType-SOPDetails-duration/{sopTypeId}/{digitalSopId}/{durationTypeId}/{durationValue}/{loggerId}")
    public ResponseEntity<PreventiveMaintenance> getPreventiveMaintenanceByAssetCategoryByAsset(@PathVariable long sopTypeId, @PathVariable long digitalSopId, @PathVariable long durationTypeId, @PathVariable long durationValue, @PathVariable long loggerId) {
        List<PreventiveMaintenance> preventiveMaintenanceList = preventiveMaintenanceService.getPreventiveMaintenanceByAssemblyLineByStagesByDurationTypeByDurationValue(sopTypeId, digitalSopId, durationTypeId, durationValue);
        if (preventiveMaintenanceList.isEmpty()) {
            throw new EntityNotFoundException("There is no data for this SOP");
        }
        PreventiveMaintenance preventiveMaintenance = new PreventiveMaintenance();
        for (PreventiveMaintenance preventiveMaintenanceObj : preventiveMaintenanceList) {
            List<Employee> employeeList = preventiveMaintenanceObj.getEmployee();
            for (Employee employeeObj :employeeList) {
                if (employeeObj.getId() == loggerId){
                    preventiveMaintenance = preventiveMaintenanceObj;
                }
            }
        }
        if (preventiveMaintenance == null || preventiveMaintenance.getId() == 0){
            throw new EntityNotFoundException("There is no activity present for this logger.");
        }
        return new ResponseEntity<>(preventiveMaintenance, HttpStatus.OK);
    }

    @GetMapping("/all-preventive-maintenance-by-sopType-sopCategory/{sopTypeId}/{sopCategoryId}")
    public ResponseEntity<List<PreventiveMaintenanceReportDto>> getPreventiveMaintenanceByAssetCategoryByAsset(@PathVariable long sopTypeId, @PathVariable long sopCategoryId) {
        List<PreventiveMaintenanceReportDto> preventiveMaintenanceReportDtos = getPreventiveMaintenanceReport(sopTypeId, sopCategoryId, 0, 0);
        if (preventiveMaintenanceReportDtos.isEmpty()) {
            throw new EntityNotFoundException("There is no data for this SOP");
        }
        return new ResponseEntity<>(preventiveMaintenanceReportDtos, HttpStatus.OK);
    }

    public List<PreventiveMaintenanceReportDto> getPreventiveMaintenanceReport(long sopTypeId, long digitalSopId, long durationTypeId, long durationValue) {
        List<PreventiveMaintenance> preventiveMaintenanceList = new ArrayList<>();
        List<PreventiveMaintenance> preventiveMaintenances = preventiveMaintenanceService.getAllPreventiveMaintenance();

        if (sopTypeId == 0 && digitalSopId == 0 && durationTypeId == 0 && durationValue == 0) {
            preventiveMaintenanceList = preventiveMaintenances;
        } else if (sopTypeId > 0 && digitalSopId > 0 && durationTypeId > 0 && durationValue > 0) {
            preventiveMaintenanceList = preventiveMaintenanceService.getPreventiveMaintenanceByAssemblyLineByStagesByDurationTypeByDurationValue(sopTypeId, digitalSopId, durationTypeId, durationValue);
        } else if (sopTypeId > 0 && digitalSopId == 0 && durationTypeId == 0 && durationValue == 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getSopType().getId() == sopTypeId) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        } else if (sopTypeId > 0 && digitalSopId > 0 && durationTypeId == 0 && durationValue == 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getSopType().getId() == sopTypeId && preventiveMaintenance.getSopCategory().getId() == digitalSopId) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        } else if (sopTypeId > 0 && digitalSopId > 0 && durationTypeId > 0 && durationValue == 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getSopType().getId() == sopTypeId && preventiveMaintenance.getSopCategory().getId() == digitalSopId && preventiveMaintenance.getDurationType().getId() == durationTypeId) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId > 0 && durationTypeId == 0 && durationValue == 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getSopCategory().getId() == digitalSopId) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId > 0 && durationTypeId > 0 && durationValue == 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getSopCategory().getId() == digitalSopId && preventiveMaintenance.getDurationType().getId() == durationTypeId) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId > 0 && durationTypeId > 0 && durationValue > 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getSopCategory().getId() == digitalSopId && preventiveMaintenance.getDurationType().getId() == durationTypeId && preventiveMaintenance.getDurationValue() == durationValue) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId == 0 && durationTypeId > 0 && durationValue == 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getDurationType().getId() == durationTypeId) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId == 0 && durationTypeId > 0 && durationValue > 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getDurationType().getId() == durationTypeId && preventiveMaintenance.getDurationValue() == durationValue) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        } else if (sopTypeId == 0 && digitalSopId == 0 && durationTypeId == 0 && durationValue > 0) {
            for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenances) {
                if (preventiveMaintenance.getDurationValue() == durationValue) {
                    preventiveMaintenanceList.add(preventiveMaintenance);
                }
            }
        }

        List<PreventiveMaintenanceReportDto> preventiveMaintenanceReportDtos = new ArrayList<>();
        for (PreventiveMaintenance preventiveMaintenance : preventiveMaintenanceList) {
            PreventiveMaintenanceReportDto preventiveMaintenanceReportDto = new PreventiveMaintenanceReportDto();

            List<SopStepsAssigned> sopStepsAssignedList = preventiveMaintenance.getSopStepsAssigned();
            List<SopStepsAssigned> allSopSteps = new ArrayList<>();
            List<SopStepsAssigned> completedSopSteps = new ArrayList<>();
            List<SopStepsAssigned> pendingSopSteps = new ArrayList<>();
            long totalSopSteps = sopStepsAssignedList.size();
            long totalCompletedSopSteps = 0;
            long totalPendingSopSteps = 0;
            float sopSteplistStatus = 0;
            String status = "";
            for (SopStepsAssigned sopStepsAssigned : sopStepsAssignedList) {
                allSopSteps.add(sopStepsAssigned);
                if (sopStepsAssigned.getSubmitedBy() != null) {
                    completedSopSteps.add(sopStepsAssigned);
                    totalCompletedSopSteps++;
                } else {
                    pendingSopSteps.add(sopStepsAssigned);
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
            String triggeredOn = dateFormat.format(preventiveMaintenance.getAssignedOn());
            preventiveMaintenanceReportDto.setPreventiveMaintenanceId(preventiveMaintenance.getId());
            preventiveMaintenanceReportDto.setSopTypeName(preventiveMaintenance.getSopType().getSopTypeName());
            preventiveMaintenanceReportDto.setDigitalSopName(preventiveMaintenance.getSopCategory().getName());
            preventiveMaintenanceReportDto.setDurationType(preventiveMaintenance.getDurationType().getDurationType());
            preventiveMaintenanceReportDto.setDurationValue(preventiveMaintenance.getDurationValue());
            preventiveMaintenanceReportDto.setTriggeredOn(triggeredOn);
            int i=1;
            List<Employee> employeeList = preventiveMaintenance.getEmployee();
            for (Employee employee :employeeList) {
                if (i==1){
                    preventiveMaintenanceReportDto.setFirstAssignedTo(employee.getFirstName());
                } else if (i==2){
                    preventiveMaintenanceReportDto.setSecondAssignedTo(employee.getFirstName());
                } else if (i==3){
                    preventiveMaintenanceReportDto.setThirdAssignedTo(employee.getFirstName());
                }
                i = i+1;
            }
            preventiveMaintenanceReportDto.setStatus(status);
            if (preventiveMaintenance.getSubmitedBy() != null ){
                preventiveMaintenanceReportDto.setSubmitedBy(preventiveMaintenance.getSubmitedBy());
            } else {
                preventiveMaintenanceReportDto.setSubmitedBy(" ");
            }
            if (preventiveMaintenance.getSubmitedOn() != null){
                String submitedOn = dateFormat.format(preventiveMaintenance.getSubmitedOn());
                preventiveMaintenanceReportDto.setSubmitedOn(submitedOn);
            } else {
                preventiveMaintenanceReportDto.setSubmitedOn(" ");
            }
            preventiveMaintenanceReportDto.setTotalSopSteps(totalSopSteps);
            preventiveMaintenanceReportDto.setTotalcompletedSopStep(totalCompletedSopSteps);
            preventiveMaintenanceReportDto.setTotalpendingSopStep(totalPendingSopSteps);
            preventiveMaintenanceReportDto.setSopSteplistStatus(sopSteplistStatus);
            preventiveMaintenanceReportDto.setAllSopSteps(allSopSteps);
            preventiveMaintenanceReportDto.setCompletedSopSteps(completedSopSteps);
            preventiveMaintenanceReportDto.setPendingSopSteps(pendingSopSteps);
            preventiveMaintenanceReportDtos.add(preventiveMaintenanceReportDto);
        }
        return preventiveMaintenanceReportDtos;
    }

    @GetMapping("/preventive-maintenance-report/{sopTypeId}/{digitalSopId}/{durationTypeId}/{durationValue}")
    public ResponseEntity<PreventiveMaintenanceReportDto> getPreventiveMaintenanceReportForUI(@PathVariable long sopTypeId, @PathVariable long digitalSopId, @PathVariable long durationTypeId, @PathVariable long durationValue) {
        List<PreventiveMaintenanceReportDto> preventiveMaintenanceReportDtos = getPreventiveMaintenanceReport(sopTypeId, digitalSopId, durationTypeId, durationValue);
        if (preventiveMaintenanceReportDtos.isEmpty()){
            throw new EntityNotFoundException("There is no record for this duration.");
        }
        return new ResponseEntity(preventiveMaintenanceReportDtos, HttpStatus.OK);
    }

    @GetMapping("/preventive-maintenance-report-download/{sopTypeId}/{digitalSopId}/{durationTypeId}/{durationValue}")
    public void jobSubCategoryCycleTimeTrackExcelReport(HttpServletRequest request, HttpServletResponse response, @PathVariable long sopTypeId, @PathVariable long digitalSopId, @PathVariable long durationTypeId, @PathVariable long durationValue) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Execution_Duration_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        List<PreventiveMaintenanceReportDto> preventiveMaintenances = getPreventiveMaintenanceReport(sopTypeId, digitalSopId, durationTypeId, durationValue);
        try {
            writeToSheetPreventiveMaintenamce(workbook, preventiveMaintenances, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Something Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetPreventiveMaintenamce(WritableWorkbook workbook, List<PreventiveMaintenanceReportDto> preventiveMaintenances, int index) throws IOException, WriteException {

        WritableSheet s = workbook.createSheet("Execution Duration", index);
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

        s.addCell(new Label(0, 0, "EXECUTION DURATION REPORT", headerFormat));
        s.addCell(new Label(0, 1, "SlNo.", headerFormat1));
        s.addCell(new Label(1, 1, "SOP Type", headerFormat1));
        s.addCell(new Label(2, 1, "Digital SOP", headerFormat1));
        s.addCell(new Label(3, 1, "Duration Type", headerFormat1));
        s.addCell(new Label(4, 1, "Duration Value", headerFormat1));
        s.addCell(new Label(5, 1, "Triggered On", headerFormat1));
        s.addCell(new Label(6, 1, "First Persion Assigned", headerFormat1));
        s.addCell(new Label(7, 1, "Second Persion Assigned", headerFormat1));
        s.addCell(new Label(8, 1, "Third Persion Assigned", headerFormat1));
        s.addCell(new Label(9, 1, "Status", headerFormat1));
        s.addCell(new Label(10, 1, "Total SOP Steps", headerFormat1));
        s.addCell(new Label(11, 1, "Completed SOP Steps", headerFormat1));
        s.addCell(new Label(12, 1, "Pending SOP Steps", headerFormat1));
        s.addCell(new Label(13, 1, "SOP List Status", headerFormat1));

        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 15);
        s.setColumnView(4, 15);
        s.setColumnView(5, 15);
        s.setColumnView(6, 20);
        s.setColumnView(7, 20);
        s.setColumnView(8, 20);
        s.setColumnView(9, 20);
        s.setColumnView(10, 20);
        s.setColumnView(11, 20);
        s.setColumnView(12, 20);
        s.setColumnView(13, 15);

        s.setRowView(0, 550);
        s.setRowView(1, 350);

        s.mergeCells(0, 0, 13, 0);

        int i = 1;
        int row = 2;
        for (PreventiveMaintenanceReportDto preventiveMaintenanceObj : preventiveMaintenances) {
            s.addCell(new Label(0, row, "" + i, dataFormat));
            s.addCell(new Label(1, row, "" + preventiveMaintenanceObj.getSopTypeName(), dataFormat));
            s.addCell(new Label(2, row, "" + preventiveMaintenanceObj.getDigitalSopName(), dataFormat));
            s.addCell(new Label(3, row, "" + preventiveMaintenanceObj.getDurationType(), dataFormat));
            s.addCell(new Label(4, row, "" + preventiveMaintenanceObj.getDurationValue(), dataFormat));
            s.addCell(new Label(5, row, "" + preventiveMaintenanceObj.getTriggeredOn(), dataFormat));
            s.addCell(new Label(6, row, "" + preventiveMaintenanceObj.getFirstAssignedTo(), dataFormat));
            s.addCell(new Label(7, row, "" + preventiveMaintenanceObj.getSecondAssignedTo(), dataFormat));
            s.addCell(new Label(8, row, "" + preventiveMaintenanceObj.getThirdAssignedTo(), dataFormat));
            s.addCell(new Label(9, row, "" + preventiveMaintenanceObj.getStatus(), dataFormat));
            s.addCell(new Label(10, row, "" + preventiveMaintenanceObj.getTotalSopSteps(), dataFormat));
            s.addCell(new Label(11, row, "" + preventiveMaintenanceObj.getTotalcompletedSopStep(), dataFormat));
            s.addCell(new Label(12, row, "" + preventiveMaintenanceObj.getTotalpendingSopStep(), dataFormat));
            s.addCell(new Label(13, row, "" + preventiveMaintenanceObj.getSopSteplistStatus(), dataFormat));

            i++;
            row++;
        }
        return workbook;
    }

    @GetMapping("/preventive-maintenance-sop-stepsreport-download/{preventiveMaintenanceId}")
    public void jobSubCategoryCycleTimeTrackExcelReport(HttpServletRequest request, HttpServletResponse response, @PathVariable long preventiveMaintenanceId) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Sop_Step_List_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        Optional<PreventiveMaintenance> preventiveMaintenances = preventiveMaintenanceService.getPreventiveMaintenance(preventiveMaintenanceId);
        try {
            writeToSheetTotalSopSteps(workbook, preventiveMaintenances.get(), 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Something Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetTotalSopSteps(WritableWorkbook workbook, PreventiveMaintenance preventiveMaintenances, int index) throws IOException, WriteException {

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

        List<SopStepsAssigned> allSopStepsAssigned = preventiveMaintenances.getSopStepsAssigned();

        for (SopStepsAssigned sopStepsAssigned : allSopStepsAssigned) {
            String checkPointStatus = "Not Submited";
            if (sopStepsAssigned.getCheckPointStatus() != null) {
                if (sopStepsAssigned.getCheckPointStatus().equals(true)) {
                    checkPointStatus = "Submited";
                }
            }
            s.addCell(new Label(0, row, "" + i, dataFormat));
            s.addCell(new Label(1, row, "" + sopStepsAssigned.getCheckPart(), dataFormat));
            s.addCell(new Label(2, row, "" + sopStepsAssigned.getCheckPoint(), dataFormat));
            if (sopStepsAssigned.getDescription() == null) {
                s.addCell(new Label(3, row, " ", dataFormat));
            } else {
                s.addCell(new Label(3, row, "" + sopStepsAssigned.getDescription(), dataFormat));
            }
            s.addCell(new Label(4, row, "" + sopStepsAssigned.getStandardValue(), dataFormat));
            s.addCell(new Label(5, row, "" + checkPointStatus, dataFormat));
            if (sopStepsAssigned.getRemark() == null){
                s.addCell(new Label(6, row, "", dataFormat));
            } else {
                s.addCell(new Label(6, row, "" + sopStepsAssigned.getRemark(), dataFormat));
            }
            if (sopStepsAssigned.getSubmitedOn() == null){
                s.addCell(new Label(7, row, "", dataFormat));
            } else {
                s.addCell(new Label(7, row, "" + sopStepsAssigned.getSubmitedOn(), dataFormat));
            }
            if (sopStepsAssigned.getSubmitedBy() == null){
                s.addCell(new Label(8, row, "", dataFormat));
            } else {
                s.addCell(new Label(8, row, "" + sopStepsAssigned.getSubmitedBy(), dataFormat));
            }
            i++;
            row++;
        }
        return workbook;
    }
}