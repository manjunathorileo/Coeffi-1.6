package com.dfq.coeffi.EvacuationDashboard;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.EmployeePass;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.EmployeePassRepo;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.repository.payroll.EmployeeAttendanceRepository;
import com.dfq.coeffi.vivo.entity.VivoInfo;
import com.dfq.coeffi.vivo.service.VivoInfoService;
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
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;

@RestController
@Slf4j
public class EvacuationDashboard {

    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    EmployeePassRepo employeePassRepo;
    @Autowired
    EmployeeAttendanceRepository employeeAttendanceRepository;
    @Autowired
    VivoInfoService vivoInfoService;
    @Autowired
    PermanentContractService permanentContractService;


    @GetMapping("evacuation/permanent-contract")
    public ResponseEntity<List<EvacuationDto>> getContractEmployeeAttendanceReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getPermanentContract();
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
        WritableSheet s = workbook.createSheet("Permanent-Contract-Dialy-Report", index);
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
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeId()));
            s.addCell(new Label(2, 0, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName()));
            s.addCell(new Label(3, 0, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment()));
            s.addCell(new Label(4, 0, "Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDate()));
            s.addCell(new Label(5, 0, "CheckIn Time", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getInTime()));
            s.addCell(new Label(6, 0, "CheckOut Time", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getOutTime()));
            s.addCell(new Label(6, 0, "Total Hours", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getTotalHours()));
            rowNum = rowNum + 1;

        }
        return workbook;
    }

    public List<EvacuationDto> getPermanentContract() {
        Date date = new Date();
        List<EvacuationDto> evacuationDtoList = new ArrayList<>();
        List<PermanentContractAttendance> empPermanentContractList = permanentContractAttendanceRepo.findByMarkedOn(date);
        for (PermanentContractAttendance e : empPermanentContractList) {
            EvacuationDto evacuationDto = new EvacuationDto();
            ;
            EmployeePass employeePass = employeePassRepo.findByEmpId(e.getEmpId());
            EmpPermanentContract empPermanentContract = employeePass.getEmpPermanentContract();
            if (empPermanentContract != null && empPermanentContract.getDepartmentName() != null) {
                evacuationDto.setDepartment(empPermanentContract.getDepartmentName());
            }
            evacuationDto.setEmployeeId(e.getEmpId());
            evacuationDto.setEmployeeName(empPermanentContract.getFirstName());
            evacuationDto.setDate(date);
            evacuationDto.setInTime(e.getInTime());
            evacuationDto.setOutTime(e.getOutTime());
            evacuationDto.setTotalHours(e.getWorkedHours());
            if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                evacuationDtoList.add(evacuationDto);
            }
        }
        return evacuationDtoList;

    }

    @GetMapping("evacuation/permanent")
    public ResponseEntity<List<EvacuationDto>> getPermanentReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getPermanent();
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
        WritableSheet s = workbook.createSheet("Permanent-Dialy-Report", index);
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
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeId()));
            s.addCell(new Label(2, 0, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName()));
            s.addCell(new Label(3, 0, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment()));
            s.addCell(new Label(4, 0, "Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDate()));
            s.addCell(new Label(5, 0, "CheckIn Time", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getInTime()));
            s.addCell(new Label(6, 0, "CheckOut Time", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getOutTime()));
            s.addCell(new Label(6, 0, "Total Hours", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getTotalHours()));
            rowNum = rowNum + 1;

        }
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
            evacuationDto.setDate(date);
            evacuationDto.setInTime(e.getInTime());
            evacuationDto.setOutTime(e.getOutTime());
            evacuationDto.setTotalHours(e.getWorkedHours());
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
        List<EvacuationDto> monthlyEmployeeAttendanceDtos = getcontract();
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
        WritableSheet s = workbook.createSheet("Permanent-Contract-Dialy-Report", index);
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
        for (EvacuationDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeId()));
            s.addCell(new Label(2, 0, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName()));
            s.addCell(new Label(3, 0, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartment()));
            s.addCell(new Label(4, 0, "Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDate()));
            s.addCell(new Label(5, 0, "CheckIn Time", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getInTime()));
            s.addCell(new Label(6, 0, "CheckOut Time", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getOutTime()));
            s.addCell(new Label(6, 0, "Total Hours", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeAttendanceDto.getTotalHours()));
            rowNum = rowNum + 1;

        }
        return workbook;
    }

    public List<EvacuationDto> getcontract() {
        Date date = new Date();
        List<EvacuationDto> evacuationDtoList = new ArrayList<>();
        List<PermanentContractAttendance> empPermanentContractList = permanentContractAttendanceRepo.findByMarkedOn(date);
        for (PermanentContractAttendance e : empPermanentContractList) {
            EvacuationDto evacuationDto = new EvacuationDto();
            EmpPermanentContract empPermanentContract = permanentContractService.get(e.getEmployeeCode());
            if (empPermanentContract != null && empPermanentContract.getDepartmentName() != null) {
                evacuationDto.setDepartment(empPermanentContract.getDepartmentName());
            }
            evacuationDto.setEmployeeId(e.getEmpId());
            evacuationDto.setEmployeeName(empPermanentContract.getFirstName());
            evacuationDto.setDate(date);
            evacuationDto.setInTime(e.getInTime());
            evacuationDto.setOutTime(e.getOutTime());
            evacuationDto.setTotalHours(e.getWorkedHours());

            if (empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                evacuationDtoList.add(evacuationDto);
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
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.GRAY_25);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.addCell(new Label(0, 0, "DateOfVisit", headerFormat));
        s.addCell(new Label(1, 0, "VehicleType", headerFormat));
        s.addCell(new Label(2, 0, "VehicleNumber", headerFormat));
        s.addCell(new Label(3, 0, "EntryTime", headerFormat));
        s.addCell(new Label(4, 0, "ExitTime", headerFormat));
        s.addCell(new Label(5, 0, "ExtraTime", headerFormat));
        s.addCell(new Label(6, 0, "Purpose", headerFormat));
        int rownum = 1;
        for (VivoInfo vivoInfo : vivoInfos) {
            s.addCell(new Label(0, rownum, "" + vivoInfo.getMarkedOn()));
            s.addCell(new Label(1, rownum, "" + vivoInfo.getVehicleType().getTypeOfVehicle()));
            s.addCell(new Label(2, rownum, "" + vivoInfo.getVehicleNumber()));
            s.addCell(new Label(3, rownum, "" + vivoInfo.getEntryTime()));
            s.addCell(new Label(4, rownum, "" + vivoInfo.getExitTime()));
            s.addCell(new Label(5, rownum, "" + vivoInfo.getExtraTime()));
            s.addCell(new Label(6, rownum, "" + vivoInfo.getPurpose()));
            rownum++;
        }
        return workbook;
    }
}
