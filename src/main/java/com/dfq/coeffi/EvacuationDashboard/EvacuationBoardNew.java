package com.dfq.coeffi.EvacuationDashboard;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.EmployeePass;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.EmployeePassRepo;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.evacuationApi.InsideCsvRaw;
import com.dfq.coeffi.evacuationApi.InsideCsvRawRepository;
import com.dfq.coeffi.repository.payroll.EmployeeAttendanceRepository;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.visitor.Repositories.VisitorRepository;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import com.dfq.coeffi.vivo.entity.VivoInfo;
import com.dfq.coeffi.vivo.service.VivoInfoService;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static jxl.format.Alignment.*;

@RestController
@Slf4j
public class EvacuationBoardNew extends BaseController {

    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    EmployeePassRepo employeePassRepo;
    @Autowired
    EmployeeAttendanceRepository employeeAttendanceRepository;
    @Autowired
    VivoInfoService vivoInfoService;
    @Autowired
    VisitorRepository visitorRepository;
    @Autowired
    PermanentContractRepo permanentContractRepo;
    @Autowired
    InsideCsvRawRepository insideCsvRawRepository;
    @Autowired
    VisitorPassService visitorPassService;
    @Autowired
    CompanyConfigureService companyConfigureService;
    @Autowired
    CompanyNameService companyNameService;


    @GetMapping("evacuation/permanent-contract")
    public ResponseEntity<List<EvacuationDto>> getContractEmployeeAttendanceReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getReportByType("EMPLOYEE");
        OutputStream out = null;
        String fileName = "Permanent-Contract-Report_";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry(workbook, monthlyEmployeeAttendanceDtos, response, 0);
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("No entries today");
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

    private WritableWorkbook attendanceEntry(WritableWorkbook workbook, List<EvacuationDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Employee", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
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
        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
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

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 4, 0);
        Label lable = new Label(0, 0, "Permanent Employees Live Head Count Report - On: " + DateUtil.mySqlFormatDate(), headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 1, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment(), cLeft));
            s.addCell(new Label(4, 1, "Employee_type", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getEmployeeType(), cLeft));
            s.addCell(new Label(5, 1, "Company", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getCompanyName(), cLeft));
            rowNum = rowNum + 1;
        }
        s.addCell(new Label(5, rowNum, "Total Count = " + Integer.valueOf(rowNum - 2), cellFormat));
        return workbook;
    }

    public List<EvacuationDto> getPermanentContract() {
        Date date = new Date();
        List<EvacuationDto> evacuationDtoList = new ArrayList<>();
        List<PermanentContractAttendance> empPermanentContractList = permanentContractAttendanceRepo.findByMarkedOn(date);
        for (PermanentContractAttendance e : empPermanentContractList) {
            EvacuationDto evacuationDto = new EvacuationDto();
            EmployeePass employeePass = employeePassRepo.findByEmpId(e.getEmpId());
            EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(e.getEmployeeCode());
            if (empPermanentContract != null) {

                if (empPermanentContract.getDepartmentName() != null) {
                    evacuationDto.setDepartment(empPermanentContract.getDepartmentName());
                } else {
                    evacuationDto.setDepartment("");
                }

                evacuationDto.setEmployeeId(e.getEmpId());
                evacuationDto.setEmployeeCode(e.getEmployeeCode());
                evacuationDto.setEmployeeName(empPermanentContract.getFirstName());
                evacuationDto.setDate(DateUtil.mySqlFormatDate());
                if (e.getInTime() != null) {
                    evacuationDto.setInTime(e.getInTime());
                }
                if (e.getOutTime() != null) {
                    evacuationDto.setOutTime(e.getOutTime());
                }
                if (e.getWorkedHours() != null) {
                    evacuationDto.setTotalHours(e.getWorkedHours());
                }
                if (e.getInTime() != null && e.getOutTime() != null) {
                    evacuationDto.setTotalHours(setTotalHrs(e.getInTime(), e.getOutTime()));
                }
                if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                    evacuationDtoList.add(evacuationDto);
                }
            }
        }
        return evacuationDtoList;

    }

    @GetMapping("evacuation/permanent")
    public ResponseEntity<List<EvacuationDto>> getPermanentReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getReportByType("Employee");
        OutputStream out = null;
        String fileName = "Permanent__Daily_Report_";
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

    private WritableWorkbook attendanceEntry1(WritableWorkbook workbook, List<EvacuationDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Employee", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
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
        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
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

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 4, 0);
        Label lable = new Label(0, 0, "Permanent Employees Live Head Count Report - On: " + DateUtil.mySqlFormatDate(), headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 1, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment(), cLeft));
            s.addCell(new Label(4, 1, "Employee_type", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getEmployeeType(), cLeft));
            rowNum = rowNum + 1;

        }
        s.addCell(new Label(5, rowNum, "Total Count = " + Integer.valueOf(rowNum - 2), cellFormat));
        return workbook;
    }

    public List<EvacuationDto> getPermanent() {
        Date date = new Date();
        List<EvacuationDto> evacuationDtoList = new ArrayList<>();
        List<EmployeeAttendance> empPermanentContractList = employeeAttendanceRepository.findByMarkedOn(date);
        for (EmployeeAttendance e : empPermanentContractList) {
            EvacuationDto evacuationDto = new EvacuationDto();
            evacuationDto.setEmployeeId(e.getId());
            Employee employee = e.getEmployee();
            evacuationDto.setEmployeeName(employee.getFirstName());
            evacuationDto.setDepartment(employee.getDepartmentName());
            evacuationDto.setDate(DateUtil.mySqlFormatDate());
            evacuationDto.setInTime(e.getInTime());
            evacuationDto.setOutTime(e.getOutTime());
            evacuationDto.setTotalHours(e.getWorkedHours());
            if (e.getInTime() != null && e.getOutTime() != null) {
                evacuationDto.setTotalHours(setTotalHrs(e.getInTime(), e.getOutTime()));
            }
            if (e.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                evacuationDtoList.add(evacuationDto);
            }
        }
        return evacuationDtoList;

    }


    @GetMapping("evacuation/contract")
    public ResponseEntity<List<EvacuationDto>> getContractEmployeeAttendanceReports(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getReportByType("Contractor");
        OutputStream out = null;
        String fileName = "Permanent-Contract_Daily_Report_";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry2(workbook, monthlyEmployeeAttendanceDtos, response, 0);
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("No entries today");
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

    private WritableWorkbook attendanceEntry2(WritableWorkbook workbook, List<EvacuationDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Contract", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
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
        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
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

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 4, 0);
        Label lable = new Label(0, 0, "Contract Employees Live Head Count Report - On: " + DateUtil.mySqlFormatDate(), headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 1, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment(), cLeft));
            s.addCell(new Label(4, 1, "Employee_type", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getEmployeeType(), cLeft));
            s.addCell(new Label(5, 1, "Company", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getCompanyName(), cLeft));
            rowNum = rowNum + 1;

        }
        s.addCell(new Label(5, rowNum, "Total Count = " + Integer.valueOf(rowNum - 2), cellFormat));
        return workbook;
    }

    public List<EvacuationDto> getcontract() {
        Date date = new Date();
        List<EvacuationDto> evacuationDtoList = new ArrayList<>();
        List<PermanentContractAttendance> empPermanentContractList = permanentContractAttendanceRepo.findByMarkedOn(date);
        for (PermanentContractAttendance e : empPermanentContractList) {
            EvacuationDto evacuationDto = new EvacuationDto();
            EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(e.getEmployeeCode());
            if (empPermanentContract != null) {
                if (empPermanentContract.getDepartmentName() != null) {
                    evacuationDto.setDepartment(empPermanentContract.getDepartmentName());
                } else {
                    evacuationDto.setDepartment("");
                }

                evacuationDto.setEmployeeId(e.getEmpId());
                evacuationDto.setEmployeeCode(e.getEmployeeCode());
                evacuationDto.setEmployeeName(empPermanentContract.getFirstName());
                evacuationDto.setDate(DateUtil.mySqlFormatDate());
                if (e.getInTime() != null) {
                    evacuationDto.setInTime(e.getInTime());
                }
                if (e.getOutTime() != null) {
                    evacuationDto.setOutTime(e.getOutTime());
                }
                if (e.getWorkedHours() != null) {
                    evacuationDto.setTotalHours(e.getWorkedHours());
                }
                if (e.getInTime() != null && e.getOutTime() != null) {
                    evacuationDto.setTotalHours(setTotalHrs(e.getInTime(), e.getOutTime()));
                }

                if (empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                    evacuationDtoList.add(evacuationDto);
                }
            }
        }
        return evacuationDtoList;

    }

    @GetMapping("evacuation/vehicle")
    public void evacuationDownload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<VivoInfo> vivoInfoList = viewVivoInfo();
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Vivo.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, vivoInfoList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }

    }


    public List<VivoInfo> viewVivoInfo() {
        List<VivoInfo> vivoInfoList = vivoInfoService.getByMarkedOn(new Date());
        return vivoInfoList;
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, HttpServletResponse response, List<VivoInfo> vivoInfos, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Vehicle", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
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
        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
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

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);
        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.addCell(new Label(0, 1, "#", cellFormat));
        s.addCell(new Label(1, 1, "Employee Name", cellFormat));
        s.addCell(new Label(2, 1, "Department Name", cellFormat));
        s.addCell(new Label(3, 1, "Cardid", cellFormat));
        s.addCell(new Label(4, 1, "VehicleType", cellFormat));
        s.addCell(new Label(5, 1, "VehicleNumber", cellFormat));

        s.mergeCells(0, 0, 5, 0);
        Label lable = new Label(0, 0, "Vehicle Live Count Report - On: " + DateUtil.mySqlFormatDate(), headerFormat);
        s.addCell(lable);

        int rownum = 2;
        for (VivoInfo vivoInfo : vivoInfos) {
            if (vivoInfo.getCheckedOut() == null) {
                s.addCell(new Label(0, rownum, "" + Integer.valueOf(rownum - 1), cLeft));

                if (vivoInfo.getEmpname() != null) {
                    s.addCell(new Label(1, rownum, "" + vivoInfo.getEmpname(), cLeft));
                } else {
                    s.addCell(new Label(1, rownum, "" + " ", cLeft));
                }

                s.addCell(new Label(2, rownum, "-" + " ", cLeft));

                if (vivoInfo.getCardId() > 0) {
                    s.addCell(new Label(3, rownum, "" + vivoInfo.getCardId(), cLeft));
                } else {
                    s.addCell(new Label(3, rownum, "" + " ", cLeft));
                }

                s.addCell(new Label(1, rownum, "" + vivoInfo.getVehicleType().getTypeOfVehicle(), cLeft));

                if (vivoInfo.getVehicleNumber() != null) {
                    s.addCell(new Label(4, rownum, "" + vivoInfo.getVehicleNumber(), cLeft));
                } else {
                    s.addCell(new Label(4, rownum, "" + " ", cLeft));
                }

                s.addCell(new Label(5, rownum, "" + "", cLeft));

                rownum++;
            }
        }
        s.addCell(new Label(6, rownum, "Total Count = " + Integer.valueOf(rownum - 2), cellFormat));
        return workbook;
    }


    @GetMapping("evacuation/visitor")
    public void evacuationVisitorDownload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Visitor> visitorList = viewVisitorInfo();
        List<EvacuationDto> visitorList = getReportByType("Visitor");
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Visitor.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            visitorDialyReport(workbook, response, visitorList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }

    }

    @GetMapping("evacuation/temp")
    public void evacuationTempDownload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Visitor> visitorList = viewVisitorInfo();
        List<EvacuationDto> visitorList = getReportByType("Temp");
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Visitor.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            tempDialyReport(workbook, response, visitorList, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }

    }


    private WritableWorkbook visitorDialyReport(WritableWorkbook workbook, HttpServletResponse response, List<EvacuationDto> monthlyEmployeeAttendanceDto, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Visitor/Vendor", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
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
        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
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

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 4, 0);
        Label lable = new Label(0, 0, "Visitor Live Head Count Report - On: " + DateUtil.mySqlFormatDate(), headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Visitor/Vendor Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 1, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment(), cLeft));
            s.addCell(new Label(4, 1, "Type", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getEmployeeType()+"/Vendor", cLeft));
            s.addCell(new Label(5, 1, "Company", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getCompanyName(), cLeft));
            rowNum = rowNum + 1;

        }
        s.addCell(new Label(5, rowNum, "Total Count = " + Integer.valueOf(rowNum - 2), cellFormat));
        return workbook;
    }

    private WritableWorkbook tempDialyReport(WritableWorkbook workbook, HttpServletResponse response, List<EvacuationDto> monthlyEmployeeAttendanceDto, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Temporary", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
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
        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
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

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 4, 0);
        Label lable = new Label(0, 0, "Temporary Live Head Count Report - On: " + DateUtil.mySqlFormatDate(), headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 1, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment(), cLeft));
            s.addCell(new Label(4, 1, "Type", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getEmployeeType(), cLeft));
            s.addCell(new Label(5, 1, "Company", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getCompanyName(), cLeft));
            rowNum = rowNum + 1;

        }
        s.addCell(new Label(5, rowNum, "Total Count = " + Integer.valueOf(rowNum - 2), cellFormat));
        return workbook;
    }

    public List<Visitor> viewVisitorInfo() {
        List<Visitor> visitorList = visitorRepository.findByLoggedOn(new Date());
        return visitorList;
    }

    private String setTotalHrs(Date startDate, Date endDate) {
        long workedMillis = endDate.getTime() - startDate.getTime();
        double workedMinutes = TimeUnit.MILLISECONDS.toMinutes(workedMillis);
        double workedHrs = workedMinutes / 60;

        String hrs = String.format("%.2f", workedHrs);
        return convertOt(hrs);
    }

    public String convertOt(String d) {
        String hrs = String.valueOf((long) (Double.parseDouble(d)));
        String min;
        String numberD = String.valueOf(d);
        numberD = numberD.substring(numberD.indexOf(".")).substring(1);
        double mind = Double.parseDouble(numberD);
        mind = mind / (1.67);
        long minl = (long) mind;
        min = String.valueOf(minl);
        if (min.length() == 1) {
            min = "0" + min;
        }
        String ot = hrs + "." + min;
        return ot;
    }


    @GetMapping("evacuation/total")
    public ResponseEntity<List<EvacuationDto>> getTotalReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {


        List<EvacuationDto> permanentList = getReportByType("Employee");
        List<EvacuationDto> permanentContractList = getReportByType("Employee");
        List<EvacuationDto> contractList = getReportByType("Contractor");
        List<VivoInfo> vivoInfoList = viewVivoInfo();
        List<EvacuationDto> visitorList = getReportByType("Visitor");
        List<EvacuationDto> tempList = getReportByType("Temp");

        OutputStream out = null;
        String fileName = "Total_Daily_Report";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());


        try {
//            if (permanentList != null) {
//                attendanceEntry1(workbook, permanentList, response, 0);
//            }
            if (permanentContractList != null) {
                attendanceEntry(workbook, permanentContractList, response, 0);
            }
            if (contractList != null) {
                attendanceEntry2(workbook, contractList, response, 1);
            }

            if (visitorList != null) {
                visitorDialyReport(workbook, response, visitorList, 2);
            }
            if (tempList != null) {
                tempDialyReport(workbook, response, tempList, 3);
            }
            if (vivoInfoList != null) {
                writeToSheet(workbook, response, vivoInfoList, 4);
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(workbook, HttpStatus.OK);
    }




    public List<EvacuationDto> getReportByType(String type) {

        String companyName = "";
        List<CompanyName> companyNames = companyNameService.getCompany();
        if (!companyNames.isEmpty()){
            Collections.reverse(companyNames);
            companyName = companyNames.get(0).getCompanyName();
        }
        System.out.println("Company here"+ companyName);

        List<InsideCsvRaw> insideCsvRaws = insideCsvRawRepository.findAll();
        List<InsideCsvRaw> insideCsvRawList = new ArrayList<>();
        List<EvacuationDto> evacuationDtos = new ArrayList<>();
        for (InsideCsvRaw insideCsvRaw : insideCsvRaws) {
            if (insideCsvRaw.getType().equalsIgnoreCase(type)) {
                EvacuationDto evacuationDto = new EvacuationDto();
                if (insideCsvRaw.getName() != null) {
                    evacuationDto.setEmployeeName(insideCsvRaw.getName());
                } else {
                    evacuationDto.setEmployeeName("");
                }
                if (insideCsvRaw.getSsoId() != null) {
                    evacuationDto.setEmployeeCode(insideCsvRaw.getSsoId());
                } else {
                    evacuationDto.setEmployeeCode("");
                }
                if (insideCsvRaw.getType() != null) {
                    evacuationDto.setEmployeeType(insideCsvRaw.getType());
                }
                EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(insideCsvRaw.getSsoId());
                if (empPermanentContract != null) {
                    evacuationDto.setDepartment(empPermanentContract.getDepartmentName());

                    if (empPermanentContract.getContractCompany() != null ) {
                        System.out.println("Im  here 111111111: " +empPermanentContract.getContractCompany());
                        if(!empPermanentContract.getContractCompany() .equalsIgnoreCase("")) {
                            System.out.println("Im  here 2222222");
                            evacuationDto.setCompanyName(empPermanentContract.getContractCompany());
                        }else {
                            System.out.println("Im  here 333333333");
                            evacuationDto.setCompanyName(companyName);
                        }
                        if (empPermanentContract.getContractCompany().equalsIgnoreCase("")){
                            System.out.println("Im  here 44444444");
                            evacuationDto.setCompanyName(companyName);
                        }
                    }


                }
                VisitorPass visitorPass = visitorPassService.getByMobileNumber(insideCsvRaw.getSsoId());
                if (visitorPass != null) {
                    evacuationDto.setDepartment(visitorPass.getDepartmentName());

                    if (visitorPass.getCompanyName() != null) {
                        evacuationDto.setCompanyName(visitorPass.getCompanyName());
                    } else {
                        evacuationDto.setCompanyName("");
                    }
                }
                evacuationDtos.add(evacuationDto);
            }
        }
        return evacuationDtos;
    }


}
