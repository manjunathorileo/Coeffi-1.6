package com.dfq.coeffi.DenialApps.Controller;

import com.dfq.coeffi.DenialApps.Entities.DenialApps;
import com.dfq.coeffi.DenialApps.Entities.DenialAppsDto;
import com.dfq.coeffi.DenialApps.Entities.UsedDenialAppsTrack;
import com.dfq.coeffi.DenialApps.Repository.UsedDenialAppsTrackRepository;
import com.dfq.coeffi.DenialApps.Services.DenialAppService;
import com.dfq.coeffi.EvacuationDashboard.EvacuationDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import jxl.format.Colour;
import jxl.write.*;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.*;

@RestController
public class DenialAppController {
    @Autowired
    DenialAppService denialAppService;

    @Autowired
    UsedDenialAppsTrackRepository usedDenialAppsTrackRepository;

    @Autowired
    EmployeeService employeeService;

    @PostMapping("save-denial-app")
    public ResponseEntity<DenialApps> saveMustApp(@RequestBody DenialApps denialApps) {
        DenialApps denialApps1 = denialApps;
        Date date = new Date();
        denialApps1.setCreatedOn(date);
        denialApps1.setStatus(true);
        denialAppService.saveDenialApp(denialApps1);

        return new ResponseEntity<>(denialApps1, HttpStatus.CREATED);
    }

    @GetMapping("get-denial-apps")
    public ResponseEntity<List<DenialApps>> getMustApps() {
        List<DenialApps> denialAppsList = denialAppService.getDenialApps();
        List<DenialApps> denialAppsList1 = new ArrayList<>();
        for (DenialApps d : denialAppsList) {
            if (d.isStatus() == true) {
                denialAppsList1.add(d);
            }

        }
        return new ResponseEntity<>(denialAppsList1, HttpStatus.OK);
    }

    @DeleteMapping("delete-denial-app/{id}")
    public void deleteMustApp(@PathVariable("id") long id) {
        DenialApps denialApps = denialAppService.getDenialApp(id);
        denialApps.setStatus(false);
        denialAppService.saveDenialApp(denialApps);
    }

    @GetMapping("used-employee-denialapps-view")
    public ResponseEntity<List<DenialAppsDto>> getUsedAppsView() {
        Date date = new Date();
        List<UsedDenialAppsTrack> denialAppsTrackList = usedDenialAppsTrackRepository.findByUsedOn(date);
        List<DenialAppsDto> denialAppsDtoList = new ArrayList<>();
        for (UsedDenialAppsTrack u : denialAppsTrackList) {
            DenialAppsDto denialAppsDto = new DenialAppsDto();
            Optional<Employee> employee = employeeService.getEmployee(u.getEmployeeId());
            denialAppsDto.setEmployeeId(u.getEmployeeId());
            denialAppsDto.setEmployeeName(employee.get().getFirstName());
            denialAppsDto.setDesignation(employee.get().getDesignation().getName());
            denialAppsDto.setDepartment(employee.get().getDepartmentName());
            denialAppsDto.setMobileNumber(employee.get().getPhoneNumber());
            denialAppsDto.setUsedApp(u.getUsedApp());
            denialAppsDto.setUsedOn(u.getUsedOn());
            denialAppsDto.setUsedTime(u.getUsedOn());
            denialAppsDtoList.add(denialAppsDto);
        }
        return new ResponseEntity<>(denialAppsDtoList, HttpStatus.OK);
    }

    @GetMapping("get-used-employee-denialapps-report")
    public ResponseEntity<List<EvacuationDto>> getPermanentReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<DenialAppsDto> monthlyEmployeeAttendanceDtos = getEmployeeUsedAppsReport();
        OutputStream out = null;
        String fileName = "Used_Employee_DenialApps";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry1(workbook, monthlyEmployeeAttendanceDtos, response, 0);
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    private WritableWorkbook attendanceEntry1(WritableWorkbook workbook, List<DenialAppsDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Used-Employee-DenialApps", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);
        WritableFont cellFont = new WritableFont(WritableFont.TIMES);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(Colour.GRAY_25);
        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
        cellFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
        cellFormatRight.setAlignment(RIGHT);
        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
        cellFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
        cellFormatLeft.setAlignment(LEFT);
        WritableFont cellFontSimpleRight = new WritableFont(WritableFont.TIMES);
        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleRight);
        cellFormatSimpleRight.setAlignment(RIGHT);
        int rowNum = 1;
        for (DenialAppsDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeId()));
            s.addCell(new Label(2, 0, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName()));
            s.addCell(new Label(3, 0, "Designation", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDesignation()));
            s.addCell(new Label(4, 0, "Mobile Number", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getMobileNumber()));
            s.addCell(new Label(5, 0, "Department", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getDepartment()));
            s.addCell(new Label(6, 0, "Used App", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getUsedApp()));
            s.addCell(new Label(7, 0, "Used On", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeAttendanceDto.getUsedOn()));
            s.addCell(new Label(8, 0, "Used Time", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeAttendanceDto.getUsedTime()));
            rowNum = rowNum + 1;

        }
        return workbook;
    }


    public List<DenialAppsDto> getEmployeeUsedAppsReport() {
        Date date = new Date();
        List<UsedDenialAppsTrack> denialAppsTrackList = usedDenialAppsTrackRepository.findByUsedOn(date);
        List<DenialAppsDto> denialAppsDtoList = new ArrayList<>();
        for (UsedDenialAppsTrack u : denialAppsTrackList) {
            DenialAppsDto denialAppsDto = new DenialAppsDto();
            Optional<Employee> employee = employeeService.getEmployee(u.getEmployeeId());
            denialAppsDto.setEmployeeId(u.getEmployeeId());
            denialAppsDto.setEmployeeName(employee.get().getFirstName());
            denialAppsDto.setDesignation(employee.get().getDesignation().getName());
            denialAppsDto.setDepartment(employee.get().getDepartmentName());
            denialAppsDto.setMobileNumber(employee.get().getPhoneNumber());
            denialAppsDto.setUsedApp(u.getUsedApp());
            denialAppsDto.setUsedOn(u.getUsedOn());
            denialAppsDto.setUsedTime(u.getUsedOn());
            denialAppsDtoList.add(denialAppsDto);
        }
        return denialAppsDtoList;
    }

}
