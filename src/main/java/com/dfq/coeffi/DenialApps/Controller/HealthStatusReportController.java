package com.dfq.coeffi.DenialApps.Controller;

import com.dfq.coeffi.DenialApps.Entities.HealthStatusDto;
import com.dfq.coeffi.DenialApps.Entities.HealthStatusTrack;
import com.dfq.coeffi.DenialApps.Repository.HealthStatusTrackRepository;
import com.dfq.coeffi.EvacuationDashboard.EvacuationDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import jxl.format.Colour;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class HealthStatusReportController {
    @Autowired
    HealthStatusTrackRepository healthStatusTrackRepository;
    @Autowired
    EmployeeService employeeService;

    @GetMapping("health-status-view")
    public ResponseEntity<List<HealthStatusDto>> getHealthStatusView(){
        Date date=new Date();
        List<HealthStatusTrack> healthStatusTrackList=healthStatusTrackRepository.findBySubmittedOn(date);
        List<HealthStatusDto> healthStatusDtoList=new ArrayList<>();
        for (HealthStatusTrack  u:healthStatusTrackList) {
            HealthStatusDto  healthStatusDto=new HealthStatusDto();
            Optional<Employee> employee=employeeService.getEmployee(u.getEmployeeId());
            healthStatusDto.setEmployeeId(u.getEmployeeId());
            healthStatusDto.setEmployeeName(employee.get().getFirstName());
            healthStatusDto.setDesignation(employee.get().getDesignation().getName());
            healthStatusDto.setDepartment(employee.get().getDepartmentName());
            healthStatusDto.setMobileNumber(employee.get().getPhoneNumber());
            healthStatusDto.setHealthStatus(u.getHealthStatus());
            healthStatusDto.setSubmittedBy(u.getSubmittedOn()) ;
            healthStatusDtoList.add(healthStatusDto);
        }
        return new ResponseEntity<>(healthStatusDtoList, HttpStatus.OK);
    }

    @GetMapping("health-status-report")
    public ResponseEntity<List<EvacuationDto>> getPermanentReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<HealthStatusDto> monthlyEmployeeAttendanceDtos = getHealthStatusReport();
        OutputStream out = null;
        String fileName = "Health_Status" ;
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
    private WritableWorkbook attendanceEntry1(WritableWorkbook workbook, List<HealthStatusDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index) throws IOException, WriteException {
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
        cellFormat.setBackground(jxl.format.Colour.GRAY_25);
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
        for (HealthStatusDto   employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
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
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getDepartment() ));
            s.addCell(new Label(6, 0, "Health Status", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getHealthStatus()));
            s.addCell(new Label(7, 0, "Date", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeAttendanceDto.getSubmittedBy()));
            rowNum = rowNum + 1;
        }
        return workbook;
    }

    public List<HealthStatusDto> getHealthStatusReport(){
        Date date=new Date();
        List<HealthStatusTrack> healthStatusTrackList=healthStatusTrackRepository.findBySubmittedOn(date);
        List<HealthStatusDto> healthStatusDtoList=new ArrayList<>();
        for (HealthStatusTrack  u:healthStatusTrackList) {
            HealthStatusDto  healthStatusDto=new HealthStatusDto();
            Optional<Employee> employee=employeeService.getEmployee(u.getEmployeeId());
            healthStatusDto.setEmployeeId(u.getEmployeeId());
            healthStatusDto.setEmployeeName(employee.get().getFirstName());
            healthStatusDto.setDesignation(employee.get().getDesignation().getName());
            healthStatusDto.setDepartment(employee.get().getDepartmentName());
            healthStatusDto.setMobileNumber(employee.get().getPhoneNumber());
            healthStatusDto.setHealthStatus(u.getHealthStatus());
            healthStatusDto.setSubmittedBy(u.getSubmittedOn()) ;
            healthStatusDtoList.add(healthStatusDto);
        }
        return healthStatusDtoList;
    }
}
