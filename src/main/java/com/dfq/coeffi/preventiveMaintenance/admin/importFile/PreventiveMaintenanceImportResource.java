
package com.dfq.coeffi.preventiveMaintenance.admin.importFile;


import com.dfq.coeffi.SOPDetails.Exceptions.FileStorageException;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.PreventiveMaintenanceMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.entity.SopStepsMaster;
import com.dfq.coeffi.preventiveMaintenance.admin.service.PreventiveMaintenanceMasterService;
import com.dfq.coeffi.preventiveMaintenance.admin.service.SopStepsMasterService;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationTypeService;
import com.dfq.coeffi.preventiveMaintenance.user.SopStepsDto;
import com.dfq.coeffi.preventiveMaintenance.user.entity.PreventiveMaintenance;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssigned;
import com.dfq.coeffi.preventiveMaintenance.user.service.PreventiveMaintenanceService;
import com.dfq.coeffi.preventiveMaintenance.user.service.SopStepsAssignedService;
import com.dfq.coeffi.service.hr.DepartmentService;
import com.dfq.coeffi.service.hr.EmployeeService;
import jxl.format.Colour;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
@Slf4j
public class PreventiveMaintenanceImportResource extends BaseController {

    /*@Value("${checkListMaster.uploadDir}")
    private String uploadDir;*/

    //long checkListSlNo = 00;

    private final SopTypeService sopTypeService;
    private final SopCategoryService SOPCategoryService;
    private final PreventiveMaintenanceMasterService preventiveMaintenanceMasterService;
    private final SopStepsMasterService checkListService;
    private final DurationTypeService durationTypeService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PreventiveMaintenanceService preventiveMaintenanceService;
    private final SopStepsAssignedService sopStepsAssignedService;

    @Autowired
    public PreventiveMaintenanceImportResource(SopTypeService sopTypeService, SopCategoryService SOPCategoryService, PreventiveMaintenanceMasterService preventiveMaintenanceMasterService, SopStepsMasterService checkListService, DurationTypeService durationTypeService, EmployeeService employeeService, DepartmentService departmentService, PreventiveMaintenanceService preventiveMaintenanceService, SopStepsAssignedService sopStepsAssignedService) {
        this.sopTypeService = sopTypeService;
        this.SOPCategoryService = SOPCategoryService;
        this.preventiveMaintenanceMasterService = preventiveMaintenanceMasterService;
        this.checkListService = checkListService;
        this.durationTypeService = durationTypeService;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.preventiveMaintenanceService = preventiveMaintenanceService;
        this.sopStepsAssignedService = sopStepsAssignedService;
    }

    @PostMapping("/preventive-maintenance-import/{sopTypeId}/{digitalSOPId}/{durationTypeId}/{durationValue}/{loggerId}/{firstEmployeeId}/{secondEmployeeId}/{thirdEmployeeId}")
    public ResponseEntity<PreventiveMaintenanceImportDto> importPreventiveMaintenanceData(@RequestParam("file") MultipartFile file, @PathVariable long sopTypeId, @PathVariable long digitalSOPId, @PathVariable long durationTypeId, @PathVariable long durationValue, @PathVariable long loggerId, @PathVariable long firstEmployeeId, @PathVariable long secondEmployeeId, @PathVariable long thirdEmployeeId) throws IOException {
        Optional<Employee> employeeOptional = employeeService.getEmployee(loggerId);
        Date toDay = new Date();
        Optional<SopType> sopTypeOptional = sopTypeService.getSopTypeById(sopTypeId);
        Optional<SopCategory> SOPCategoryOptional = SOPCategoryService.getSopCategory(digitalSOPId);
        SopCategory SOPCategory = SOPCategoryOptional.get();
        Optional<DurationType> durationType = durationTypeService.getDurationById(durationTypeId);
        List<Employee> employeeList = new ArrayList<>();
        if (firstEmployeeId>0) {
            Optional<Employee> firstEmployee = employeeService.getEmployee(firstEmployeeId);
            employeeList.add(firstEmployee.get());
        }
        if (secondEmployeeId>0){
            Optional<Employee> secondEmployee = employeeService.getEmployee(secondEmployeeId);
            employeeList.add(secondEmployee.get());
        }
        if (thirdEmployeeId>0){
            Optional<Employee> thirdEmployee = employeeService.getEmployee(thirdEmployeeId);
            employeeList.add(thirdEmployee.get());
        }

        PreventiveMaintenanceImportDto dto = PreventiveMaintenanceImport.importPreventiveMaintenanceSheet(file, sopTypeOptional.get(), SOPCategory, durationType.get(), durationValue, employeeList);
        PreventiveMaintenanceMaster preventiveMaintenance = toPreventiveMaintenance(file, sopTypeOptional.get(), SOPCategory, dto);
        if (preventiveMaintenance != null) {
            List<SopStepsMaster> sopStepsMasters = preventiveMaintenance.getSopStepsMasters();
            preventiveMaintenance.setCreatedOn(toDay);
            preventiveMaintenance.setUploadedBy(employeeOptional.get());
            PreventiveMaintenanceMaster preventiveMaintenanceMaster = preventiveMaintenanceMasterService.createPreventiveMaintenanceMaster(preventiveMaintenance);
            List<PreventiveMaintenance> preventiveMaintenanceOld = preventiveMaintenanceService.getPreventiveMaintenanceByAssemblyLineByStagesByDurationTypeByDurationValue(preventiveMaintenanceMaster.getSopType().getId(), preventiveMaintenanceMaster.getSopCategory().getId(), preventiveMaintenanceMaster.getDurationType().getId(), preventiveMaintenanceMaster.getDurationValue());
            if (preventiveMaintenanceOld.isEmpty()) {
                PreventiveMaintenance preventiveMaintenanceObj = savePreventiveMaintenance(preventiveMaintenanceMaster);
            }
        }
        return new ResponseEntity(dto.getSopStepsImportDtos(), HttpStatus.OK);
    }

    private PreventiveMaintenanceMaster toPreventiveMaintenance(MultipartFile file, SopType sopType, SopCategory SOPCategory, PreventiveMaintenanceImportDto dto) throws IOException {
        Date today = new Date();
        PreventiveMaintenanceMaster preventiveMaintenance = preventiveMaintenanceMasterService.getPreventiveMaintenanceMasterByAssemblyLineByStages(dto.getSopType(), dto.getSopCategory(), dto.getDurationType(), dto.getDurationValue());
        if (preventiveMaintenance == null) {
            preventiveMaintenance = new PreventiveMaintenanceMaster();
        }
        List<SopStepsMaster> sopStepsMasters = new ArrayList<>();
        List<SopStepsImportDto> sopStepsImportDto = dto.getSopStepsImportDtos();
        for (SopStepsImportDto sopStepsImportDtoObj : sopStepsImportDto) {
            SopStepsMaster sopStepsMaster = new SopStepsMaster();
            sopStepsMaster.setCheckPart(sopStepsImportDtoObj.getCheckPart());
            sopStepsMaster.setCheckPoint(sopStepsImportDtoObj.getCheckPoint());
            sopStepsMaster.setDescription(sopStepsImportDtoObj.getDescription());
            sopStepsMaster.setStandardValue(sopStepsImportDtoObj.getStandardValue());
            sopStepsMaster.setStatus(true);
            sopStepsMaster.setCreatedOn(today);
            SopStepsMaster sopStepsMasterObj = checkListService.createCheckListMaster(sopStepsMaster);
            sopStepsMasters.add(sopStepsMasterObj);
        }
        if (dto != null) {
            preventiveMaintenance.setSopType(dto.getSopType());
            preventiveMaintenance.setSopCategory(dto.getSopCategory());
            preventiveMaintenance.setSopStepsMasters(sopStepsMasters);
            preventiveMaintenance.setDurationType(dto.getDurationType());
            preventiveMaintenance.setDurationValue(dto.getDurationValue());
            preventiveMaintenance.setEmployee(dto.getEmployee());
        }
        return preventiveMaintenance;
    }

    public SopStepsDto saveNewCheckListSheet(MultipartFile file, PreventiveMaintenanceImportDto dto) throws IOException {
        long slNo = 1;
        PreventiveMaintenanceMaster preventiveMaintenance = preventiveMaintenanceMasterService.getPreventiveMaintenanceMasterByAssemblyLineByStages(dto.getSopType(), dto.getSopCategory(), dto.getDurationType(), dto.getDurationValue());
        if (preventiveMaintenance == null || preventiveMaintenance.equals("")) {
            slNo = 1;
        } else {
            slNo = preventiveMaintenance.getCheckListSlNo() + 1;
        }

        SopStepsDto sopStepsDto = new SopStepsDto();
        String fileName = "SopStepsMaster" + dto.getSopCategory().getId() + "_" + slNo + ".xlsx"; //StringUtils.cleanPath(file.getOriginalFilename());
        byte[] bytes = file.getBytes();
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }
        String dir = "E:/";
        Path path = Paths.get(dir + fileName);
        Files.write(path, bytes);
        sopStepsDto.setCheckListFileName(fileName);
        sopStepsDto.setCheckListUrl(String.valueOf(path));
        sopStepsDto.setFileSize(file.getSize());
        sopStepsDto.setSlNo(slNo);
        return sopStepsDto;
    }

    @GetMapping("/sop-steps-format-export")
    private void createcheckList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        String fileName = "SOPStep.xlsx";
        response.setHeader("Content-Disposition", "attachment; filename= " + fileName);
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheetforCheckList(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetforCheckList(WritableWorkbook workbook, HttpServletResponse response, int i) throws IOException, WriteException {

        WritableSheet s = workbook.createSheet("Data Input", i);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);

        s.setColumnView(0, 30);
        s.setColumnView(1, 30);
        s.setColumnView(2, 30);
        s.setColumnView(3, 20);

        s.addCell(new Label(0, 0, "Check Part", headerFormat));
        s.addCell(new Label(1, 0, "Check Point", headerFormat));
        s.addCell(new Label(2, 0, "Description", headerFormat));
        s.addCell(new Label(3, 0, "Standard Value", headerFormat));
        return workbook;
    }

    private PreventiveMaintenance savePreventiveMaintenance(PreventiveMaintenanceMaster preventiveMaintenanceMaster) {
        Date today = new Date();
        String yearFormat = "yyyy";
        SimpleDateFormat currentYearFormat = new SimpleDateFormat(yearFormat);
        long currentYear = Long.parseLong(currentYearFormat.format(today));
        PreventiveMaintenance preventiveMaintenance = new PreventiveMaintenance();
        List<SopStepsAssigned> sopStepsAssigneds = new ArrayList<>();
        List<SopStepsMaster> sopStepsMasters = preventiveMaintenanceMaster.getSopStepsMasters();
        for (SopStepsMaster sopStepsMaster : sopStepsMasters) {
            SopStepsAssigned sopStepsAssigned = new SopStepsAssigned();
            sopStepsAssigned.setCheckPart(sopStepsMaster.getCheckPart());
            sopStepsAssigned.setCheckPoint(sopStepsMaster.getCheckPoint());
            sopStepsAssigned.setDescription(sopStepsMaster.getDescription());
            sopStepsAssigned.setStandardValue(sopStepsMaster.getStandardValue());
            sopStepsAssigned.setStatus(true);
            SopStepsAssigned sopStepsAssignedObj = sopStepsAssignedService.createCheckListAssigned(sopStepsAssigned);
            sopStepsAssigneds.add(sopStepsAssignedObj);
        }
        List<Employee> employeeList = preventiveMaintenanceMaster.getEmployee();
        List<Employee> employee = new ArrayList<>();
        for (Employee employeeObj:employeeList) {
            employee.add(employeeObj);
        }
        preventiveMaintenance.setSopType(preventiveMaintenanceMaster.getSopType());
        preventiveMaintenance.setSopCategory(preventiveMaintenanceMaster.getSopCategory());
        preventiveMaintenance.setDurationType(preventiveMaintenanceMaster.getDurationType());
        preventiveMaintenance.setDurationValue(preventiveMaintenanceMaster.getDurationValue());
        preventiveMaintenance.setEmployee(employee);
        preventiveMaintenance.setSubmitedYear(currentYear);
        preventiveMaintenance.setSopStepsAssigned(sopStepsAssigneds);
        PreventiveMaintenance preventiveMaintenanceSave = preventiveMaintenanceService.createPreventiveMaintenance(preventiveMaintenance);
        return preventiveMaintenanceSave;
    }
}