package com.dfq.coeffi.DenialApps.Controller;

import com.dfq.coeffi.DenialApps.Entities.*;
import com.dfq.coeffi.DenialApps.Repository.UninstalledMustAppsTrackRepository;
import com.dfq.coeffi.DenialApps.Services.MustAppService;
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
public class MustAppController {
    @Autowired
    MustAppService mustAppService;
    @Autowired
    UninstalledMustAppsTrackRepository uninstalledMustAppsTrackRepository;
    @Autowired
    EmployeeService employeeService;

    @PostMapping("save-must-app")
    public ResponseEntity<MustApps> saveMustApp(@RequestBody MustApps mustApps) {
        MustApps mustApps1 = mustApps;
        Date date = new Date();
        mustApps1.setCreatedOn(date);
        mustApps1.setStatus(true);
        mustAppService.saveMustApp(mustApps1);

        return new ResponseEntity<>(mustApps1, HttpStatus.CREATED);
    }

    @GetMapping("get-must-apps")
    public ResponseEntity<List<MustApps>> getMustApps() {
        List<MustApps> mustAppsList = mustAppService.getMustApps();
        List<MustApps> mustAppsList1 = new ArrayList<>();
        for (MustApps m : mustAppsList) {
            if (m.isStatus() == true) {
                mustAppsList1.add(m);
            }

        }
        return new ResponseEntity<>(mustAppsList1, HttpStatus.OK);
    }

    @DeleteMapping("delete-must-app/{id}")
    public void deleteMustApp(@PathVariable("id") long id) {
        MustApps mustApps = mustAppService.getMustApp(id);
        mustApps.setStatus(false);
        mustAppService.saveMustApp(mustApps);
    }

    @GetMapping("mustapp-uninstalled-view")
    public ResponseEntity<List<UninstalledAppsDto>> getMustAppsView() {
        Date date = new Date();
        List<UninstalledMustAppsTrack> uninstalledMustAppsTrackList = uninstalledMustAppsTrackRepository.findByUninstalledOn(date);
        List<UninstalledAppsDto> uninstalledAppsDtoList = new ArrayList<>();
        for (UninstalledMustAppsTrack u : uninstalledMustAppsTrackList) {
            UninstalledAppsDto uninstalledAppsDto = new UninstalledAppsDto();
            Optional<Employee> employee = employeeService.getEmployee(u.getEmployeeId());
            uninstalledAppsDto.setEmployeeId(u.getEmployeeId());
            uninstalledAppsDto.setEmployeeName(employee.get().getFirstName());
            uninstalledAppsDto.setDesignation(employee.get().getDesignation().getName());
            uninstalledAppsDto.setDepartment(employee.get().getDepartmentName());
            uninstalledAppsDto.setMobileNumber(employee.get().getPhoneNumber());
            uninstalledAppsDto.setUninstalledAPP(u.getUninstalledApp());
            uninstalledAppsDto.setUninstalledOn(u.getUninstalledOn());
            uninstalledAppsDto.setUninstalledTime(u.getUninstalledTime());
            uninstalledAppsDtoList.add(uninstalledAppsDto);
        }
        return new ResponseEntity<>(uninstalledAppsDtoList, HttpStatus.OK);
    }

    @GetMapping("mustapp-uninstalled-report")
    public ResponseEntity<List<EvacuationDto>> getPermanentReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<UninstalledAppsDto> monthlyEmployeeAttendanceDtos = getMustUninstalledAppsView();
        OutputStream out = null;
        String fileName = "Uninstalled_Must_Apps" ;
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
    private WritableWorkbook attendanceEntry1(WritableWorkbook workbook, List<UninstalledAppsDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Uninstalled-Apps", index);
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
        for (UninstalledAppsDto   employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
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
            s.addCell(new Label(6, 0, "Uninstalled App", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getUninstalledAPP()));
            s.addCell(new Label(7, 0, "Uninstalled On", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeAttendanceDto.getUninstalledOn()));
            s.addCell(new Label(8, 0, "Uninstalled Time", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeAttendanceDto.getUninstalledTime()));
            rowNum = rowNum + 1;

        }
        return workbook;
    }


    public List<UninstalledAppsDto> getMustUninstalledAppsView() {
        Date date = new Date();
        List<UninstalledMustAppsTrack> uninstalledMustAppsTrackList = uninstalledMustAppsTrackRepository.findByUninstalledOn(date);
        List<UninstalledAppsDto> uninstalledAppsDtoList = new ArrayList<>();
        for (UninstalledMustAppsTrack u : uninstalledMustAppsTrackList) {
            UninstalledAppsDto uninstalledAppsDto = new UninstalledAppsDto();
            Optional<Employee> employee = employeeService.getEmployee(u.getEmployeeId());
            uninstalledAppsDto.setEmployeeId(u.getEmployeeId());
            uninstalledAppsDto.setEmployeeName(employee.get().getFirstName());
            uninstalledAppsDto.setDesignation(employee.get().getDesignation().getName());
            uninstalledAppsDto.setDepartment(employee.get().getDepartmentName());
            uninstalledAppsDto.setMobileNumber(employee.get().getPhoneNumber());
            uninstalledAppsDto.setUninstalledAPP(u.getUninstalledApp());
            uninstalledAppsDto.setUninstalledOn(u.getUninstalledOn());
            uninstalledAppsDto.setUninstalledTime(u.getUninstalledTime());
            uninstalledAppsDtoList.add(uninstalledAppsDto);
        }
        return uninstalledAppsDtoList;
    }

}
