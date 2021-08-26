package com.dfq.coeffi.evacuationApi;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.EvacuationDashboard.EvacuationDto;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
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
import com.dfq.coeffi.repository.payroll.EmployeeAttendanceRepository;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.visitor.Repositories.VisitorRepository;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import com.dfq.coeffi.vivo.entity.VivoInfo;
import com.dfq.coeffi.vivo.service.VivoInfoService;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class EvacuationBoardFilter extends BaseController {

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
    CompanyNameService companyNameService;


    @PostMapping("evacuation-filter/permanent-contract")
    public ResponseEntity<List<EvacuationDto>> getContractEmployeeAttendanceReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getPermanentContract(dateDto);
        OutputStream out = null;
        String fileName = "Permanent-Contract_Daily_Report_";
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
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 8, 0);
        Label lable = new Label(0, 0, "Permanent Employees Report", headerFormat);
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

            s.addCell(new Label(4, 1, "Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDate(), cLeft));

            s.addCell(new Label(5, 1, "In Time", cellFormat));
            if (employeeAttendanceDto.getInTime() != null) {
                s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getInTime(), cLeft));
            } else {
                s.addCell(new Label(5, rowNum, "", cLeft));
            }

            s.addCell(new Label(6, 1, "In Gate", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getEntryGate(), cLeft));

            s.addCell(new Label(7, 1, "Out Time", cellFormat));
            if (employeeAttendanceDto.getOutTime() != null) {
                s.addCell(new Label(7, rowNum, "" + employeeAttendanceDto.getOutTime(), cLeft));
            } else {
                s.addCell(new Label(7, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(8, 1, "Out Gate", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeAttendanceDto.getExitGate(), cLeft));

            s.addCell(new Label(9, 1, "Total Time(HH.MM)", cellFormat));
            if (employeeAttendanceDto.getTotalHours() != null) {
                s.addCell(new Label(9, rowNum, "" + employeeAttendanceDto.getTotalHours(), cLeft));
            } else {
                s.addCell(new Label(9, rowNum, "", cLeft));
            }

            s.addCell(new Label(10, 1, "Company", cellFormat));
            s.addCell(new Label(10, rowNum, "" + employeeAttendanceDto.getCompanyName(), cLeft));
            rowNum = rowNum + 1;

        }
        return workbook;
    }


    public List<EvacuationDto> getPermanentContract(DateDto dateDto) {

        String companyName = "";
        List<CompanyName> companyNames = companyNameService.getCompany();
        if (!companyNames.isEmpty()) {
            Collections.reverse(companyNames);
            companyName = companyNames.get(0).getCompanyName();
        }
        List<EvacuationDto> evacuationDtoList = new ArrayList<>();
        List<PermanentContractAttendance> empPermanentContractList = permanentContractAttendanceRepo.getEmployeeAttendanceBetweenDate(dateDto.startDate, dateDto.endDate);
        for (PermanentContractAttendance e : empPermanentContractList) {
            EvacuationDto evacuationDto = new EvacuationDto();
            EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(e.getEmployeeCode());
            if (empPermanentContract != null) {

                if (empPermanentContract.getDepartmentName() != null) {
                    evacuationDto.setDepartment(empPermanentContract.getDepartmentName());
                } else {
                    evacuationDto.setDepartment("");
                }

                if (empPermanentContract.getContractCompany() != null) {
                    if (empPermanentContract.getContractCompany() != "") {
                        evacuationDto.setCompanyName(empPermanentContract.getContractCompany());
                    } else {
                        evacuationDto.setCompanyName(companyName);
                    }
                }
                if (empPermanentContract.getContractCompany() == "") {
                    evacuationDto.setCompanyName(companyName);
                }
                evacuationDto.setCompanyName(companyName);
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
                if (e.getEntryGateNumber() != null) {
                    evacuationDto.setEntryGate(e.getEntryGateNumber());
                } else {
                    evacuationDto.setEntryGate("");
                }
                if (e.getExitGateNumber() != null) {
                    evacuationDto.setExitGate(e.getExitGateNumber());
                } else {
                    evacuationDto.setExitGate("");
                }
                if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                    evacuationDtoList.add(evacuationDto);
                }
            }
        }
        return evacuationDtoList;

    }

    @PostMapping("evacuation-filter/permanent")
    public ResponseEntity<List<EvacuationDto>> getPermanentReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getPermanentContract(dateDto);
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
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 8, 0);
        Label lable = new Label(0, 0, "Permanent Employees Report", headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));

            s.addCell(new Label(1, 1, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));

            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));

            s.addCell(new Label(3, 1, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment(), cLeft));

            s.addCell(new Label(4, 1, "Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDate(), cLeft));

            s.addCell(new Label(5, 1, "In Time", cellFormat));
            if (employeeAttendanceDto.getInTime() != null) {
                s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getInTime(), cLeft));
            } else {
                s.addCell(new Label(5, rowNum, "", cLeft));
            }

            s.addCell(new Label(6, 1, "In Gate", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getEntryGate(), cLeft));

            s.addCell(new Label(7, 1, "Out Time", cellFormat));
            if (employeeAttendanceDto.getOutTime() != null) {
                s.addCell(new Label(7, rowNum, "" + employeeAttendanceDto.getOutTime(), cLeft));
            } else {
                s.addCell(new Label(7, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(8, 1, "Out Gate", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeAttendanceDto.getExitGate(), cLeft));

            s.addCell(new Label(9, 1, "Total Time(HH.MM)", cellFormat));
            if (employeeAttendanceDto.getTotalHours() != null) {
                s.addCell(new Label(9, rowNum, "" + employeeAttendanceDto.getTotalHours(), cLeft));
            } else {
                s.addCell(new Label(9, rowNum, "", cLeft));
            }
            rowNum = rowNum + 1;

        }
        return workbook;
    }

    public List<EvacuationDto> getPermanent(DateDto dateDto) {
        List<EvacuationDto> evacuationDtoList = new ArrayList<>();
        List<EmployeeAttendance> empPermanentContractList = employeeAttendanceRepository.getEmployeeAttendanceBetweenDate(dateDto.startDate, dateDto.endDate);
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
            evacuationDto.setCompanyName("");
            if (e.getInTime() != null && e.getOutTime() != null) {
                evacuationDto.setTotalHours(setTotalHrs(e.getInTime(), e.getOutTime()));
            }
            if (e.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                evacuationDtoList.add(evacuationDto);
            }
        }
        return evacuationDtoList;

    }


    @PostMapping("evacuation-filter/contract")
    public ResponseEntity<List<EvacuationDto>> getContractEmployeeAttendanceReports(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getcontract(dateDto);
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
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 8, 0);
        Label lable = new Label(0, 0, "Contract Employees Report", headerFormat);
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

            s.addCell(new Label(4, 1, "Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDate(), cLeft));

            s.addCell(new Label(5, 1, "In Time", cellFormat));
            if (employeeAttendanceDto.getInTime() != null) {
                s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getInTime(), cLeft));
            } else {
                s.addCell(new Label(5, rowNum, "", cLeft));
            }

            s.addCell(new Label(6, 1, "In Gate", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getEntryGate(), cLeft));

            s.addCell(new Label(7, 1, "Out Time", cellFormat));
            if (employeeAttendanceDto.getOutTime() != null) {
                s.addCell(new Label(7, rowNum, "" + employeeAttendanceDto.getOutTime(), cLeft));
            } else {
                s.addCell(new Label(7, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(8, 1, "Out Gate", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeAttendanceDto.getExitGate(), cLeft));

            s.addCell(new Label(9, 1, "Total Time(HH.MM)", cellFormat));
            if (employeeAttendanceDto.getTotalHours() != null) {
                s.addCell(new Label(9, rowNum, "" + employeeAttendanceDto.getTotalHours(), cLeft));
            } else {
                s.addCell(new Label(9, rowNum, "", cLeft));
            }
            s.addCell(new Label(10, 1, "Company", cellFormat));
            s.addCell(new Label(10, rowNum, "" + employeeAttendanceDto.getCompanyName(), cLeft));
            rowNum = rowNum + 1;

        }
        return workbook;
    }

    public List<EvacuationDto> getcontract(DateDto dateDto) {
        List<EvacuationDto> evacuationDtoList = new ArrayList<>();
        List<PermanentContractAttendance> empPermanentContractList = permanentContractAttendanceRepo.getEmployeeAttendanceBetweenDate(dateDto.startDate, dateDto.endDate);
        for (PermanentContractAttendance e : empPermanentContractList) {
            EvacuationDto evacuationDto = new EvacuationDto();
            EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(e.getEmployeeCode());
            if (empPermanentContract != null) {
                if (empPermanentContract.getDepartmentName() != null) {
                    evacuationDto.setDepartment(empPermanentContract.getDepartmentName());
                } else {
                    evacuationDto.setDepartment("");
                }
                if (empPermanentContract.getContractCompany() != null) {
                    if (empPermanentContract.getContractCompany() != "") {
                        evacuationDto.setCompanyName(empPermanentContract.getContractCompany());
                    } else {
                        evacuationDto.setCompanyName("");
                    }
                } else {
                    evacuationDto.setCompanyName("");
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
                if (e.getEntryGateNumber() != null) {
                    evacuationDto.setEntryGate(e.getEntryGateNumber());
                } else {
                    evacuationDto.setEntryGate("");
                }
                if (e.getExitGateNumber() != null) {
                    evacuationDto.setExitGate(e.getExitGateNumber());
                } else {
                    evacuationDto.setExitGate("");
                }

                if (empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                    evacuationDtoList.add(evacuationDto);
                }
            }
        }
        return evacuationDtoList;

    }

    @PostMapping("evacuation-filter/vehicle")
    public void evacuationDownload(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<VivoInfo> vivoInfoList = viewVivoInfo(dateDto);
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


    public List<VivoInfo> viewVivoInfo(DateDto dateDto) {
        List<VivoInfo> vivoInfoList = vivoInfoService.filterByDates(dateDto.startDate, dateDto.endDate);
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
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.addCell(new Label(0, 1, "#", cellFormat));
        s.addCell(new Label(1, 1, "VehicleType", cellFormat));
        s.addCell(new Label(2, 1, "VehicleNumber", cellFormat));
        s.addCell(new Label(3, 1, "In Time", cellFormat));
        s.addCell(new Label(4, 1, "Out Time", cellFormat));
        s.addCell(new Label(5, 1, "Total Time(HH.MM)", cellFormat));
        s.addCell(new Label(6, 1, "Purpose", cellFormat));

        s.mergeCells(0, 0, 8, 0);
        Label lable = new Label(0, 0, "Vehicle Report", headerFormat);
        s.addCell(lable);

        int rownum = 2;
        for (VivoInfo vivoInfo : vivoInfos) {
            s.addCell(new Label(0, rownum, "" + Integer.valueOf(rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + vivoInfo.getVehicleType().getTypeOfVehicle(), cLeft));
            if (vivoInfo.getVehicleNumber() != null) {
                s.addCell(new Label(2, rownum, "" + vivoInfo.getVehicleNumber(), cLeft));
            } else {
                s.addCell(new Label(2, rownum, "" + " ", cLeft));
            }
            if (vivoInfo.getCheckedIn() != null) {
                s.addCell(new Label(3, rownum, "" + vivoInfo.getCheckedIn(), cLeft));
            } else {
                s.addCell(new Label(3, rownum, "" + " ", cLeft));
            }

            if (vivoInfo.getExitTime() != null) {
                s.addCell(new Label(4, rownum, "" + vivoInfo.getCheckedOut(), cLeft));
            } else {
                s.addCell(new Label(4, rownum, "" + " ", cLeft));
            }

            if (vivoInfo.getCheckedIn() != null && vivoInfo.getCheckedOut() != null) {
                String hrs = setTotalHrs(vivoInfo.getCheckedOut(), vivoInfo.getCheckedIn());
                s.addCell(new Label(5, rownum, "" + hrs, cLeft));
            } else {
                s.addCell(new Label(5, rownum, "" + " ", cLeft));
            }
            s.addCell(new Label(6, rownum, "" + "", cLeft));
            rownum++;
        }
        return workbook;
    }


    @PostMapping("evacuation-filter/visitor")
    public void evacuationVisitorDownload(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Visitor> visitorList = viewVisitorInfo(dateDto);
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

    @PostMapping("evacuation-filter/temp")
    public void evacuationTempDownload(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Visitor> visitorList = viewVisitorInfo(dateDto);
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


    @Autowired
    VisitorPassService visitorPassService;

    private WritableWorkbook visitorDialyReport(WritableWorkbook workbook, HttpServletResponse response, List<Visitor> visitorInfos, int index) throws IOException, WriteException {
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
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.addCell(new Label(0, 1, "#", cellFormat));
        s.addCell(new Label(1, 1, "Name", cellFormat));
        s.addCell(new Label(2, 1, "Mobile Number", cellFormat));
        s.addCell(new Label(3, 1, "Department Name", cellFormat));
        s.addCell(new Label(4, 1, "In Time", cellFormat));
        s.addCell(new Label(5, 1, "In Gate", cellFormat));
        s.addCell(new Label(6, 1, "Out Time", cellFormat));
        s.addCell(new Label(7, 1, "Out Gate", cellFormat));
        s.addCell(new Label(8, 1, "Total Time(HH.MM)", cellFormat));


        s.mergeCells(0, 0, 8, 0);
        Label lable = new Label(0, 0, "Visitor/Vendor Report", headerFormat);
        s.addCell(lable);

        int rownum = 2;
        for (Visitor visitorInfo : visitorInfos) {
            if (visitorInfo.getVisitType().equalsIgnoreCase("Visitor")) {
                s.addCell(new Label(0, rownum, "" + Integer.valueOf(rownum - 1), cLeft));
                s.addCell(new Label(1, rownum, "" + visitorInfo.getFirstName(), cLeft));
                s.addCell(new Label(2, rownum, "" + visitorInfo.getMobileNumber(), cLeft));
                VisitorPass visitorPass = visitorPassService.getByMobileNumber(visitorInfo.getMobileNumber());
                if (visitorPass != null) {
                    if (visitorPass.getDepartmentName() != null) {
                        visitorInfo.setDepartmentName(visitorPass.getDepartmentName());
                    }
                }
                if (visitorInfo.getDepartmentName() != null) {
                    s.addCell(new Label(3, rownum, "" + visitorInfo.getDepartmentName(), cLeft));
                } else {
                    s.addCell(new Label(3, rownum, "" + " ", cLeft));
                }
                if (visitorInfo.getInTime() != null) {
                    s.addCell(new Label(4, rownum, "" + visitorInfo.getInTime(), cLeft));
                } else {
                    s.addCell(new Label(4, rownum, "" + " ", cLeft));
                }

                if (visitorInfo.getEntryGateNumber() != null) {
                    s.addCell(new Label(5, rownum, "" + visitorInfo.getEntryGateNumber(), cLeft));
                } else {
                    s.addCell(new Label(5, rownum, "" + " ", cLeft));
                }

                if (visitorInfo.getOutTime() != null) {
                    s.addCell(new Label(6, rownum, "" + visitorInfo.getOutTime(), cLeft));
                } else {
                    s.addCell(new Label(6, rownum, "" + " ", cLeft));
                }

                if (visitorInfo.getExitGateNumber() != null) {
                    s.addCell(new Label(7, rownum, "" + visitorInfo.getExitGateNumber(), cLeft));
                } else {
                    s.addCell(new Label(7, rownum, "" + " ", cLeft));
                }

                if (visitorInfo.getInTime() != null && visitorInfo.getOutTime() != null) {
                    visitorInfo.setExtraTime(setTotalHrs(visitorInfo.getInTime(), visitorInfo.getOutTime()));
                }
                if (visitorInfo.getExtraTime() != null) {
                    s.addCell(new Label(8, rownum, "" + visitorInfo.getExtraTime(), cLeft));
                } else {
                    s.addCell(new Label(8, rownum, "" + " ", cLeft));
                }

                s.addCell(new Label(9, 1, "Company", cellFormat));
                if (visitorPass != null) {
                    if (visitorPass.getCompanyName() != null) {
                        s.addCell(new Label(9, rownum, "" + visitorPass.getCompanyName(), cLeft));
                    }
                } else {
                    s.addCell(new Label(9, rownum, "" + "", cLeft));
                }

                rownum++;
            }
        }
        return workbook;
    }

    private WritableWorkbook tempDialyReport(WritableWorkbook workbook, HttpServletResponse response, List<Visitor> visitorInfos, int index) throws IOException, WriteException {
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
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
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
        s.addCell(new Label(0, 1, "#", cellFormat));
        s.addCell(new Label(1, 1, "Name", cellFormat));
        s.addCell(new Label(2, 1, "MobileNumber", cellFormat));
        s.addCell(new Label(3, 1, "Department Name", cellFormat));
        s.addCell(new Label(4, 1, "In Time", cellFormat));
        s.addCell(new Label(5, 1, "In Gate", cellFormat));
        s.addCell(new Label(6, 1, "Out Time", cellFormat));
        s.addCell(new Label(7, 1, "Out Gate", cellFormat));
        s.addCell(new Label(8, 1, "Total Time(HH.MM)", cellFormat));


        s.mergeCells(0, 0, 8, 0);
        Label lable = new Label(0, 0, "Temporary Report", headerFormat);
        s.addCell(lable);

        int rownum = 2;
        for (Visitor visitorInfo : visitorInfos) {
            if (visitorInfo.getVisitType().equalsIgnoreCase("Temp")) {
                s.addCell(new Label(0, rownum, "" + Integer.valueOf(rownum - 1), cLeft));
                s.addCell(new Label(1, rownum, "" + visitorInfo.getFirstName(), cLeft));
                s.addCell(new Label(2, rownum, "" + visitorInfo.getMobileNumber(), cLeft));
                VisitorPass visitorPass = visitorPassService.getByMobileNumber(visitorInfo.getMobileNumber());
                if (visitorPass != null) {
                    if (visitorPass.getDepartmentName() != null) {
                        visitorInfo.setDepartmentName(visitorPass.getDepartmentName());
                    }
                }
                if (visitorInfo.getDepartmentName() != null) {
                    s.addCell(new Label(3, rownum, "" + visitorInfo.getDepartmentName(), cLeft));
                } else {
                    s.addCell(new Label(3, rownum, "" + " ", cLeft));
                }
                if (visitorInfo.getInTime() != null) {
                    s.addCell(new Label(4, rownum, "" + visitorInfo.getInTime(), cLeft));
                } else {
                    s.addCell(new Label(4, rownum, "" + " ", cLeft));
                }

                if (visitorInfo.getEntryGateNumber() != null) {
                    s.addCell(new Label(5, rownum, "" + visitorInfo.getEntryGateNumber(), cLeft));
                } else {
                    s.addCell(new Label(5, rownum, "" + " ", cLeft));
                }


                if (visitorInfo.getOutTime() != null) {
                    s.addCell(new Label(6, rownum, "" + visitorInfo.getOutTime(), cLeft));
                } else {
                    s.addCell(new Label(6, rownum, "" + " ", cLeft));
                }

                if (visitorInfo.getExitGateNumber() != null) {
                    s.addCell(new Label(7, rownum, "" + visitorInfo.getExitGateNumber(), cLeft));
                } else {
                    s.addCell(new Label(7, rownum, "" + " ", cLeft));
                }
                if (visitorInfo.getInTime() != null && visitorInfo.getOutTime() != null) {
                    visitorInfo.setExtraTime(setTotalHrs(visitorInfo.getInTime(), visitorInfo.getOutTime()));
                }
                if (visitorInfo.getExtraTime() != null) {
                    s.addCell(new Label(8, rownum, "" + visitorInfo.getExtraTime(), cLeft));
                } else {
                    s.addCell(new Label(8, rownum, "" + " ", cLeft));
                }
                s.addCell(new Label(9, 1, "Company", cellFormat));
                if (visitorPass != null) {
                    if (visitorPass.getCompanyName() != null) {
                        s.addCell(new Label(9, rownum, "" + visitorPass.getCompanyName(), cLeft));
                    }
                } else {
                    s.addCell(new Label(9, rownum, "" + "", cLeft));
                }
                rownum++;
            }
        }
        return workbook;
    }

    public List<Visitor> viewVisitorInfo(DateDto dateDto) {
        List<Visitor> visitorList = visitorRepository.getVisitorsBwnDate(dateDto.startDate, dateDto.endDate);
        return visitorList;
    }

    @PostMapping("evacuation-filter/total")
    public ResponseEntity<List<EvacuationDto>> getTotalReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {


        List<EvacuationDto> permanentList = getPermanent(dateDto);
        List<EvacuationDto> permanentContractList = getPermanentContract(dateDto);
        List<EvacuationDto> contractList = getcontract(dateDto);
        List<VivoInfo> vivoInfoList = viewVivoInfo(dateDto);
        List<Visitor> visitorList = viewVisitorInfo(dateDto);

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
            if (visitorList != null) {
                tempDialyReport(workbook, response, visitorList, 3);
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
        System.out.println("numberD " + numberD + " and d" + d);
        if (numberD.length() == 1) {
            numberD = numberD + "0";
        }
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
}
