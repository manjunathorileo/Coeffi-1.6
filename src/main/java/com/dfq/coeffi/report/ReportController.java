package com.dfq.coeffi.report;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.payroll.EmployeeAttendanceController;
import com.dfq.coeffi.dto.*;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import com.dfq.coeffi.entity.payroll.SalaryApprovalStatus;
import com.dfq.coeffi.foodManagement.FoodReportDto;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.LeaveService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryProcessService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryService;
import com.dfq.coeffi.util.DateUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.org.apache.bcel.internal.generic.LUSHR;
import jxl.write.*;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.math.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.List;

import static jxl.format.Alignment.*;

@RestController
public class ReportController extends BaseController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private AcademicYearService academicYearService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    PermanentContractService permanentContractService;
    @Autowired
    CompanyNameService companyNameService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("report/employee-attendance")
    public ResponseEntity<InputStreamResource> getTodayMarkedEmployeeAttendanceReport(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) throws DocumentException {
        Date inputDate = null;
        if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("DAILY") || employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("WEEKLY")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar c = Calendar.getInstance();
            c.setTime(employeeAttendanceDto.getInputDate()); // Now use today date.
            c.add(Calendar.DATE, 1); // Adding 5 days
            String output = sdf.format(c.getTime());
            inputDate = DateUtil.convertToDate(output);
        }
        List<EmployeeAttendance> employeeData = null;
        if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("DAILY") || employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("MONTHLY")) {
            employeeData = reportService.getTodayMarkedEmployeeAttendanceReport(inputDate, employeeAttendanceDto.getInputMonth(), employeeAttendanceDto.getDepartmentId());
        } else if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("WEEKLY")) {
            Date toDate = DateUtils.addDays(employeeAttendanceDto.getInputDate(), 7);
            employeeData = reportService.getEmployeeAttendanceWeeklyReport(inputDate, toDate, employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        }
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("Employee Attendance Report not found");
        }
        return new ResponseEntity(employeeData, HttpStatus.OK);
    }

    @PostMapping("report/employee/new-employees")
    public ResponseEntity<List<Employee>> getEmployeesByJoiningMonth(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) {
        List<Employee> employeeData = null;
        if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("DAILY") || employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("MONTHLY")) {
            employeeData = reportService.getEmployeesByJoiningMonth(employeeAttendanceDto.getInputDate(), employeeAttendanceDto.getInputMonth(), employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        } else if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("WEEKLY")) {
            Date toDate = DateUtils.addDays(employeeAttendanceDto.getInputDate(), 7);
        }
        return new ResponseEntity<List<Employee>>(employeeData, HttpStatus.OK);
    }

    @PostMapping("report/employee/left-employees")
    public ResponseEntity<List<Employee>> getEmployeesByLeavingMonth(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) {
        List<Employee> employeeData = reportService.getEmployeesByLeavingMonth(employeeAttendanceDto.getInputDate(), employeeAttendanceDto.getInputMonth(), employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("No Ex-EmployeeData Found");
        }
        return new ResponseEntity<List<Employee>>(employeeData, HttpStatus.OK);
    }

    @PostMapping("report/employee/employee-extra-hour")
    public ResponseEntity<InputStreamResource> getEmployeeExtraHoursReport(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) throws DocumentException, ParseException {

        List<EmployeeAttendance> employeeAttendances = null;
        if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("DAILY") || employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("MONTHLY")) {
            employeeAttendances = reportService.getTodayMarkedEmployeeAttendanceReport(employeeAttendanceDto.getInputDate(), employeeAttendanceDto.getInputMonth(), employeeAttendanceDto.getDepartmentId());
        } else if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("WEEKLY")) {
            Date toDate = DateUtils.addDays(employeeAttendanceDto.getInputDate(), 7);
            employeeAttendances = reportService.getEmployeeAttendanceWeeklyReport(employeeAttendanceDto.getInputDate(), toDate, employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        }
        if (CollectionUtils.isEmpty(employeeAttendances)) {
            throw new EntityNotFoundException("employee-extra-hour");
        }
        ArrayList employeeAttendancesList = new ArrayList();
        // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // Date date1 = sdf.parse("8:00");
        for (EmployeeAttendance employeeAttendance : employeeAttendances) {
            if (employeeAttendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                Double workedHours = Double.valueOf(employeeAttendance.getEffectiveOverTime());
                if (workedHours > 0) {
                    employeeAttendancesList.add(employeeAttendance);
                }
            }
        }
        return new ResponseEntity(employeeAttendancesList, HttpStatus.OK);
    }

    @PostMapping("report/employee-professional-tax")
    public ResponseEntity<List<EmployeeSalaryProcess>> getProfessionalTaxByMonth(@RequestBody EmployeeSalaryDto employeeSalaryDto) {
        long monthNumber = Long.valueOf(employeeSalaryDto.inputMonth);
        String inputMonth = Month.of((int) monthNumber).name();
        List<EmployeeSalaryProcess> employeeSalary = reportService.getMonthlyProfessionalTaxReport(inputMonth, employeeSalaryDto.inputYear);
        List<EmployeeSalaryDto> employeeSalaryDtos = new ArrayList<>();
        for (EmployeeSalaryProcess salaryProcess : employeeSalary) {
            EmployeeSalaryDto dto = new EmployeeSalaryDto();
            dto.setEmployeeId(salaryProcess.getEmployee().getId());
            dto.setEmployeeName(salaryProcess.getEmployee().getFirstName() + " " + salaryProcess.getEmployee().getLastName());
            dto.setEmployeeCode(salaryProcess.getEmployee().getEmployeeCode());
            if (salaryProcess.getEmployee().getDepartment() != null) {
                dto.setDepartment(salaryProcess.getEmployee().getDepartment().getName());
            } else {
                dto.setDepartment("");
            }
            if (salaryProcess.getEmployee().getDesignation() != null) {
                dto.setDesignation(salaryProcess.getEmployee().getDesignation().getName());
            } else {
                dto.setDesignation("");
            }
            dto.setProfessionalTax(salaryProcess.getProfessionalTax());
            dto.setMonthName(salaryProcess.getSalaryMonth());
            dto.setYear(salaryProcess.getSalaryYear());
            employeeSalaryDtos.add(dto);
        }

        if (employeeSalary.isEmpty()) {
            throw new EntityNotFoundException("employeeSalaryProcess-monthwise");
        }
        return new ResponseEntity(employeeSalaryDtos, HttpStatus.OK);
    }

    @PostMapping("report/employee-professional-tax-download")
    public void downloadProfessionalTax(@RequestBody EmployeeSalaryDto employeeSalaryDto, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //TODO fc report
        List<EmployeeSalaryDto> employeeSalaryProcesses = getProfessionalTaxByMonthDownload(employeeSalaryDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            formProfTax(workbook, employeeSalaryProcesses, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    public List<EmployeeSalaryDto> getProfessionalTaxByMonthDownload(EmployeeSalaryDto employeeSalaryDto) {
//        long monthNumber = Long.valueOf(employeeSalaryDto.inputMonth);
//        String inputMonth = Month.of((int) monthNumber).name();
        String inputMonth = employeeSalaryDto.monthName;
        List<EmployeeSalaryProcess> employeeSalary = reportService.getMonthlyProfessionalTaxReport(inputMonth, employeeSalaryDto.inputYear);
        List<EmployeeSalaryDto> employeeSalaryDtos = new ArrayList<>();
        for (EmployeeSalaryProcess salaryProcess : employeeSalary) {
            EmployeeSalaryDto dto = new EmployeeSalaryDto();
            dto.setEmployeeId(salaryProcess.getEmployee().getId());
            dto.setEmployeeName(salaryProcess.getEmployee().getFirstName() + " " + salaryProcess.getEmployee().getLastName());
            dto.setEmployeeCode(salaryProcess.getEmployee().getEmployeeCode());
            if (salaryProcess.getEmployee().getDepartment() != null) {
                dto.setDepartment(salaryProcess.getEmployee().getDepartment().getName());
            } else {
                dto.setDepartment("");
            }
            if (salaryProcess.getEmployee().getDesignation() != null) {
                dto.setDesignation(salaryProcess.getEmployee().getDesignation().getName());
            } else {
                dto.setDesignation("");
            }
            dto.setProfessionalTax(salaryProcess.getProfessionalTax());
            dto.setMonthName(salaryProcess.getSalaryMonth());
            dto.setYear(salaryProcess.getSalaryYear());
            employeeSalaryDtos.add(dto);
        }

        if (employeeSalary.isEmpty()) {
            throw new EntityNotFoundException("employeeSalaryProcess-monthwise");
        }
        return employeeSalaryDtos;
    }

    @PostMapping("report/monthly-leave-report")
    public ResponseEntity<List<Leave>> getMonthlyLeaveReport(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) {
        Optional<AcademicYear> acad = academicYearService.getActiveAcademicYear();
        List<Leave> leaves = reportService.getMonthlyLeaveReport(LeaveStatus.APPROVED, employeeAttendanceDto.getInputMonth(), employeeAttendanceDto.getInputYear());
        Map<String, Integer> map = null;
        ArrayList leaveList = new ArrayList();
        for (Leave leave : leaves) {
            Integer ref = leave.getRefId();
            List<Leave> leavesByRefId = leaveService.getEmployeeApprovedLeaveByRefNameRefNumber(LeaveStatus.APPROVED, ref, "EMPLOYEE", acad.get());
            double totalCasuaLeaveCount = 0;
            double totalPaidLeaveCount = 0;
            double totalVacationLeaveCount = 0;
            double totalUnPaidLeaveCount = 0;
            double totalSickLeaveCount = 0;
            for (Leave leave1 : leavesByRefId) {
                if (leave1.getLeaveType().toString().equalsIgnoreCase("CASUAL_LEAVE")) {
                    double casuaLeaveCount = leave1.getTotalLeavesApplied();
                    totalCasuaLeaveCount = totalCasuaLeaveCount + casuaLeaveCount;
                } else if (leave1.getLeaveType().toString().equalsIgnoreCase("PAID_LEAVE")) {
                    double paidLeaveCount = leave1.getTotalLeavesApplied();
                    totalPaidLeaveCount = totalPaidLeaveCount + paidLeaveCount;
                } else if (leave1.getLeaveType().toString().equalsIgnoreCase("VACATION_LEAVE")) {
                    double vacationLeaveCount = leave1.getTotalLeavesApplied();
                    totalVacationLeaveCount = totalVacationLeaveCount + vacationLeaveCount;
                } else if (leave1.getLeaveType().toString().equalsIgnoreCase("UNPAID_LEAVE")) {
                    double unPaidLeaveCount = leave1.getTotalLeavesApplied();
                    totalUnPaidLeaveCount = totalUnPaidLeaveCount + unPaidLeaveCount;
                } else if (leave1.getLeaveType().toString().equalsIgnoreCase("SICK_LEAVE")) {
                    double sickLeaveCount = leave1.getTotalLeavesApplied();
                    totalSickLeaveCount = totalSickLeaveCount + sickLeaveCount;
                }
            }
            long employeeId = Long.valueOf(ref);
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeId);
            if (!employeeObj.isPresent()) {
                throw new EntityNotFoundException("Employee not found for id :" + employeeId);
            }
            Employee employee = employeeObj.get();
            MonthlyLeaveReport monthlyLeaveReport = new MonthlyLeaveReport();
            monthlyLeaveReport.setRefId(ref);
            monthlyLeaveReport.setTotalCasuaLeaveCount(totalCasuaLeaveCount);
            monthlyLeaveReport.setTotalPaidLeaveCount(totalPaidLeaveCount);
            monthlyLeaveReport.setTotalVacationLeaveCount(totalVacationLeaveCount);
            monthlyLeaveReport.setTotalUnPaidLeaveCount(totalUnPaidLeaveCount);
            monthlyLeaveReport.setTotalSickLeaveCount(totalSickLeaveCount);
            Employee employee1 = new Employee();
            employee1.setEmployeeCode(employee.getEmployeeCode());
            employee1.setId(employee.getId());
            employee1.setFirstName(employee.getFirstName());
            employee1.setLastName(employee.getLastName());
            employee1.setUanNumber(employee.getUanNumber());
            if (employee.getDepartment() != null) {
                employee1.setDepartment(employee.getDepartment());
                if (employee.getDesignation() != null) {
                    employee1.setDesignation(employee.getDesignation());
                }
            }
            monthlyLeaveReport.setEmployee(employee1);
            if (!leaveList.contains(monthlyLeaveReport))
                leaveList.add(monthlyLeaveReport);
        }
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("Leaves");
        }
        return new ResponseEntity(leaveList, HttpStatus.OK);
    }

//    @PostMapping("report/employee/employee-salary")
    //TODO uncomment api for type1 salary report
    public ResponseEntity<List<EmployeeSalaryProcess>> getEmployeeSalaryMonthWiseReport(@RequestBody EmployeeAttendanceDto employeeAttendanceDto, HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException {
        List<EmployeeSalaryProcess> employeeData = reportService.getEmployeeSalaryMonthWiseReport(employeeAttendanceDto.getMonthName(), String.valueOf(employeeAttendanceDto.getInputYear()));
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("employeeData");
        }

        OutputStream out = null;
        String fileName = "Salary_Register";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());

        salaryRegister(workbook, employeeData, response, 0);

        workbook.write();
        workbook.close();
        return new ResponseEntity<List<EmployeeSalaryProcess>>(employeeData, HttpStatus.OK);
    }

    private WritableWorkbook salaryRegister(WritableWorkbook workbook, List<EmployeeSalaryProcess> employeeSalaryProcesses, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Monthly-Attendance-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 9);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setAlignment(Alignment.CENTRE);


        int firstRow = 0;
        int secondRow = 1;
        int heightInPoints = 38 * 15;
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);
        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
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
        WritableFont cellFontSimpleLeft = new WritableFont(WritableFont.TIMES, 7);
        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleLeft);
        cellFormatSimpleRight.setAlignment(LEFT);
        cellFormatSimpleRight.setBackground(Colour.ICE_BLUE);
        cellFormatSimpleRight.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        WritableCellFormat actual = new WritableCellFormat(cellFontSimpleLeft);
        actual.setAlignment(LEFT);
        actual.setBackground(Colour.BLUE_GREY);
        actual.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 5);
        s.setColumnView(1, 10);
        s.setColumnView(2, 20);
        s.setColumnView(3, 15);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.mergeCells(0, 0, 30, 0);
        s.mergeCells(0, 1, 30, 1);
        s.mergeCells(13, 2, 20, 2);
        Label lable = new Label(0, 0, "***** PVT LTD", headerFormat);
        s.addCell(lable);
        s.setRowView(firstRow, heightInPoints);
        Label lableSlip = new Label(0, 1, "Salary Register for the month of " + employeeSalaryProcesses.get(0).getSalaryMonth() + "-" + employeeSalaryProcesses.get(0).getSalaryYear(), headerFormat);
        s.addCell(lableSlip);
        s.setRowView(secondRow, heightInPoints);


        int rowNum = 4;
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cellFormatSimpleRight));
            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCode(), cellFormatSimpleRight));
            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeSalaryProcess.getEmployee().getFirstName() + " " + employeeSalaryProcess.getEmployee().getLastName(), cellFormatSimpleRight));
            s.addCell(new Label(3, 3, "Designation Name", cellFormat));
            if (employeeSalaryProcess.getEmployee().getDesignation() != null) {
                s.addCell(new Label(3, rowNum, "" + employeeSalaryProcess.getEmployee().getDesignation().getName(), cellFormatSimpleRight));
            } else {
                s.addCell(new Label(3, rowNum, "" + "", cellFormatSimpleRight));
            }
            s.addCell(new Label(4, 3, "No of days worked during the month", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeSalaryProcess.getNoOfPresent(), cellFormatSimpleRight));

            s.addCell(new Label(5, 3, "No of days on leave", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeSalaryProcess.getAvailCasualLeave(), cellFormatSimpleRight));

            s.addCell(new Label(6, 3, "No of Holidays", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeSalaryProcess.getNoOfHolidays(), cellFormatSimpleRight));

            s.addCell(new Label(7, 3, "Late Atten.", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeSalaryProcess.getLateEntry(), cellFormatSimpleRight));

            s.addCell(new Label(8, 3, "Sunday", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeSalaryProcess.getSundays(), cellFormatSimpleRight));

            s.addCell(new Label(9, 3, "Total no of days", cellFormat));
            s.addCell(new Label(9, rowNum, "" + employeeSalaryProcess.getWorkingDays(), cellFormatSimpleRight));

            s.addCell(new Label(10, 3, "Actual Basic", cellFormat));
            s.addCell(new Label(10, rowNum, "" + employeeSalaryProcess.getBasicSalary(), cellFormatSimpleRight));

            s.addCell(new Label(11, 3, "Basic Amount", cellFormat));
            s.addCell(new Label(11, rowNum, "" + employeeSalaryProcess.getCurrentBasic(), cellFormatSimpleRight));

            s.addCell(new Label(12, 3, "Arrears", cellFormat));
            s.addCell(new Label(12, rowNum, "", cellFormatSimpleRight));

            s.addCell(new Label(13, 3, "Actual_CON", cellFormat));
            s.addCell(new Label(13, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getConveyanceAllowance(), actual));
            s.addCell(new Label(14, 3, "CON", cellFormat));
            s.addCell(new Label(14, rowNum, "" + employeeSalaryProcess.getConveyanceAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(15, 3, "Actual_HRA", cellFormat));
            s.addCell(new Label(15, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getHouseRentAllowance(), actual));
            s.addCell(new Label(16, 3, "HRA", cellFormat));
            s.addCell(new Label(16, rowNum, "" + employeeSalaryProcess.getHouseRentAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(17, 3, "Actual_EDU", cellFormat));
            s.addCell(new Label(17, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getEducationalAllowance(), actual));
            s.addCell(new Label(18, 3, "EDU", cellFormat));
            s.addCell(new Label(18, rowNum, "" + employeeSalaryProcess.getEducationalAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(19, 3, "Actual_MA", cellFormat));
            s.addCell(new Label(19, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMealsAllowance(), actual));
            s.addCell(new Label(20, 3, "MA", cellFormat));
            s.addCell(new Label(20, rowNum, "" + employeeSalaryProcess.getMealsAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(21, 3, "Actual_WA", cellFormat));
            s.addCell(new Label(21, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getWashingAllowance(), actual));
            s.addCell(new Label(22, 3, "WA", cellFormat));
            s.addCell(new Label(22, rowNum, "" + employeeSalaryProcess.getWashingAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(23, 3, "Actual_OTHER ALLOW", cellFormat));
            s.addCell(new Label(23, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getOtherAllowance(), actual));
            s.addCell(new Label(24, 3, "OTHER ALLOW", cellFormat));
            s.addCell(new Label(24, rowNum, "" + employeeSalaryProcess.getOtherAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(25, 3, "Actual_MISC", cellFormat));
            s.addCell(new Label(25, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMiscellaneousAllowance(), actual));
            s.addCell(new Label(26, 3, "MISC", cellFormat));
            s.addCell(new Label(26, rowNum, "" + employeeSalaryProcess.getMiscellaneousAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(27, 3, "Actual_MOBILE", cellFormat));
            s.addCell(new Label(27, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMobileAllowance(), actual));
            s.addCell(new Label(28, 3, "MOBILE", cellFormat));
            s.addCell(new Label(28, rowNum, "" + employeeSalaryProcess.getMobileAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(29, 3, "Actual GROSS", cellFormat));
            s.addCell(new Label(29, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMonthlyCtc(), actual));
            s.addCell(new Label(30, 3, "GROSS", cellFormat));
            s.addCell(new Label(30, rowNum, "" + employeeSalaryProcess.getGrossSalary(), cellFormatSimpleRight));

            s.addCell(new Label(31, 3, "-", cellFormat));
            s.addCell(new Label(31, rowNum, "-", cellFormatSimpleRight));

            s.addCell(new Label(32, 3, "Advance Recovered", cellFormat));
            s.addCell(new Label(32, rowNum, "" + employeeSalaryProcess.getTotalAdvanceDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(33, 3, "Actual_TDS", cellFormat));
            s.addCell(new Label(33, rowNum, "-"  , actual));
            s.addCell(new Label(34, 3, "TDS", cellFormat));
            s.addCell(new Label(34, rowNum, "" + employeeSalaryProcess.getTotalIncomeTaxDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(35, 3, "Actual_ESIC", cellFormat));
            s.addCell(new Label(35, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getEmployeeEsicContribution(), actual));
            s.addCell(new Label(36, 3, "ESIC", cellFormat));
            s.addCell(new Label(36, rowNum, "" + employeeSalaryProcess.getEmployeeEsicContribution(), cellFormatSimpleRight));

            s.addCell(new Label(37, 3, "Meal", cellFormat));
            s.addCell(new Label(37, rowNum, "" + employeeSalaryProcess.getTotalMealDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(38, 3, "Actual_Profession Tax", cellFormat));
            s.addCell(new Label(38, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getTpt(), actual));
            s.addCell(new Label(39, 3, "Profession Tax", cellFormat));
            s.addCell(new Label(39, rowNum, "" + employeeSalaryProcess.getProfessionalTax(), cellFormatSimpleRight));

            s.addCell(new Label(40, 3, "Actual_PF @12%", cellFormat));
            s.addCell(new Label(40, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getEpfContribution(), actual));
            s.addCell(new Label(41, 3, "PF @12%", cellFormat));
            s.addCell(new Label(41, rowNum, "" + employeeSalaryProcess.getEpfContribution(), cellFormatSimpleRight));

            s.addCell(new Label(42, 3, "Total Amount Payable", cellFormat));
            s.addCell(new Label(42, rowNum, "" + employeeSalaryProcess.getNetPaid(), cellFormatSimpleRight));

            s.addCell(new Label(43, 3, "Signature of Payee", cellFormat));
            s.addCell(new Label(43, rowNum, "", cellFormatSimpleRight));

            rowNum++;
        }
        return workbook;
    }

//    private WritableWorkbook salaryRegister(WritableWorkbook workbook, List<EmployeeSalaryProcess> employeeSalaryProcesses, HttpServletResponse response, int index) throws IOException, WriteException {
//        WritableSheet s = workbook.createSheet("Monthly-Attendance-Report", index);
//        s.getSettings().setPrintGridLines(false);
//        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 9);
//        headerFont.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
//        headerFormat.setAlignment(CENTRE);
//        headerFormat.setAlignment(Alignment.CENTRE);
//
//
//        int firstRow = 0;
//        int secondRow = 1;
//        int heightInPoints = 38 * 15;
//        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
//        headerFontLeft.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
//        headerFormatLeft.setAlignment(LEFT);
//        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
//        headerFontRight.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
//        headerFormatRight.setAlignment(RIGHT);
//        WritableFont cellFont = new WritableFont(WritableFont.TIMES,7);
//        cellFont.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
//        cellFormat.setAlignment(CENTRE);
//        cellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
//        cellFormat.setBackground(jxl.format.Colour.GRAY_25);
//        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
//        cellFormatDate.setAlignment(CENTRE);
//        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
//        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
//        cellFontRight.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
//        cellFormatRight.setAlignment(RIGHT);
//        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
//        cellFontLeft.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
//        cellFormatLeft.setAlignment(LEFT);
//        WritableFont cellFontSimpleLeft = new WritableFont(WritableFont.TIMES,7);
//        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleLeft);
//        cellFormatSimpleRight.setAlignment(LEFT);
//        cellFormatSimpleRight.setBackground(Colour.ICE_BLUE);
//        cellFormatSimpleRight.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
//        s.setColumnView(0, 5);
//        s.setColumnView(1, 10);
//        s.setColumnView(2, 20);
//        s.setColumnView(3, 15);
//        s.setColumnView(4, 10);
//        s.setColumnView(5, 10);
//        s.mergeCells(0, 0, 30, 0);
//        s.mergeCells(0, 1, 30, 1);
//        s.mergeCells(13, 2, 20, 2);
//        Label lable = new Label(0, 0, "***** PVT LTD", headerFormat);
//        s.addCell(lable);
//        s.setRowView(firstRow, heightInPoints);
//        Label lableSlip = new Label(0, 1, "Salary Register for the month of " + employeeSalaryProcesses.get(0).getSalaryMonth() + "-" + employeeSalaryProcesses.get(0).getSalaryYear(), headerFormat);
//        s.addCell(lableSlip);
//        s.setRowView(secondRow, heightInPoints);
//
//
//        int rowNum = 4;
//        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
//            s.addCell(new Label(0, 3, "#", cellFormat));
//            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3),cellFormatSimpleRight));
//            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
//            s.addCell(new Label(1, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCode(),cellFormatSimpleRight));
//            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
//            s.addCell(new Label(2, rowNum, "" + employeeSalaryProcess.getEmployee().getFirstName() + " " + employeeSalaryProcess.getEmployee().getLastName(),cellFormatSimpleRight));
//            s.addCell(new Label(3, 3, "Designation Name", cellFormat));
//            if (employeeSalaryProcess.getEmployee().getDesignation() != null) {
//                s.addCell(new Label(3, rowNum, "" + employeeSalaryProcess.getEmployee().getDesignation().getName(),cellFormatSimpleRight));
//            } else {
//                s.addCell(new Label(3, rowNum, "" + "",cellFormatSimpleRight));
//            }
//            s.addCell(new Label(4, 3, "No of days worked during the month", cellFormat));
//            s.addCell(new Label(4, rowNum, "" + employeeSalaryProcess.getNoOfPresent(),cellFormatSimpleRight));
//
//            s.addCell(new Label(5, 3, "No of days on leave", cellFormat));
//            s.addCell(new Label(5, rowNum, "" + employeeSalaryProcess.getAvailCasualLeave(),cellFormatSimpleRight));
//
//            s.addCell(new Label(6, 3, "No of Holidays", cellFormat));
//            s.addCell(new Label(6, rowNum, "" + employeeSalaryProcess.getNoOfHolidays(),cellFormatSimpleRight));
//
//            s.addCell(new Label(7, 3, "Late Atten.", cellFormat));
//            s.addCell(new Label(7, rowNum, "" + employeeSalaryProcess.getLateEntry(),cellFormatSimpleRight));
//
//            s.addCell(new Label(8, 3, "Sunday", cellFormat));
//            s.addCell(new Label(8, rowNum, "" + employeeSalaryProcess.getSundays(),cellFormatSimpleRight));
//
//            s.addCell(new Label(9, 3, "Total no of days", cellFormat));
//            s.addCell(new Label(9, rowNum, "" + employeeSalaryProcess.getWorkingDays(),cellFormatSimpleRight));
//
//            s.addCell(new Label(10, 3, "Actual Basic", cellFormat));
//            s.addCell(new Label(10, rowNum, "" + employeeSalaryProcess.getBasicSalary(),cellFormatSimpleRight));
//
//            s.addCell(new Label(11, 3, "Basic Amount", cellFormat));
//            s.addCell(new Label(11, rowNum, "" + employeeSalaryProcess.getCurrentBasic(),cellFormatSimpleRight));
//
//            s.addCell(new Label(12, 3, "Arrears", cellFormat));
//            s.addCell(new Label(12, rowNum, "",cellFormatSimpleRight));
//
//            s.addCell(new Label(13, 3, "Earned Baisc+VDA", cellFormat));
//            s.addCell(new Label(13, rowNum, "" + employeeSalaryProcess.getCurrentBasic().add(employeeSalaryProcess.getVariableDearnessAllowance()),cellFormatSimpleRight));
//
//            s.addCell(new Label(13, 3, "CON", cellFormat));
//            s.addCell(new Label(13, rowNum, "" + employeeSalaryProcess.getConveyanceAllowance(),cellFormatSimpleRight));
//
//            s.addCell(new Label(14, 3, "HRA", cellFormat));
//            s.addCell(new Label(14, rowNum, "" + employeeSalaryProcess.getHouseRentAllowance(),cellFormatSimpleRight));
//
//            s.addCell(new Label(15, 3, "EDU", cellFormat));
//            s.addCell(new Label(15, rowNum, "" + employeeSalaryProcess.getEducationalAllowance(),cellFormatSimpleRight));
//
//            s.addCell(new Label(16, 3, "MA", cellFormat));
//            s.addCell(new Label(16, rowNum, "" + employeeSalaryProcess.getMealsAllowance(),cellFormatSimpleRight));
//
//            s.addCell(new Label(17, 3, "WA", cellFormat));
//            s.addCell(new Label(17, rowNum, "" + employeeSalaryProcess.getWashingAllowance(),cellFormatSimpleRight));
//
//            s.addCell(new Label(18, 3, "OTHER ALLOW", cellFormat));
//            s.addCell(new Label(18, rowNum, "" + employeeSalaryProcess.getOtherAllowance(),cellFormatSimpleRight));
//
//            s.addCell(new Label(19, 3, "MISC", cellFormat));
//            s.addCell(new Label(19, rowNum, "" + employeeSalaryProcess.getMiscellaneousAllowance(),cellFormatSimpleRight));
//
//            s.addCell(new Label(20, 3, "MOBILE", cellFormat));
//            s.addCell(new Label(20, rowNum, "" + employeeSalaryProcess.getMobileAllowance(),cellFormatSimpleRight));
//
//            s.addCell(new Label(21, 3, "Actual GROSS", cellFormat));
//            s.addCell(new Label(21, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMonthlyCtc(),cellFormatSimpleRight));
//
//            s.addCell(new Label(22, 3, "GROSS", cellFormat));
//            s.addCell(new Label(22, rowNum, "" + employeeSalaryProcess.getGrossSalary(),cellFormatSimpleRight));
//
//            s.addCell(new Label(23, 3, "Advance Recovered", cellFormat));
//            s.addCell(new Label(23, rowNum, "" + employeeSalaryProcess.getTotalAdvanceDeduction(),cellFormatSimpleRight));
//
//            s.addCell(new Label(24, 3, "TDS", cellFormat));
//            s.addCell(new Label(24, rowNum, "" + employeeSalaryProcess.getTotalIncomeTaxDeduction(),cellFormatSimpleRight));
//
//            s.addCell(new Label(25, 3, "ESIC", cellFormat));
//            s.addCell(new Label(25, rowNum, ""+ employeeSalaryProcess.getEmployeeEsicContribution(),cellFormatSimpleRight));
//
//            s.addCell(new Label(26, 3, "Meal", cellFormat));
//            s.addCell(new Label(26, rowNum, "" + employeeSalaryProcess.getTotalMealDeduction(),cellFormatSimpleRight));
//
//            s.addCell(new Label(27, 3, "Profession Tax", cellFormat));
//            s.addCell(new Label(27, rowNum, "" + employeeSalaryProcess.getProfessionalTax(),cellFormatSimpleRight));
//
//            s.addCell(new Label(28, 3, "Provident Fund @12%", cellFormat));
//            s.addCell(new Label(28, rowNum, "" + employeeSalaryProcess.getEpfContribution(),cellFormatSimpleRight));
//
//            s.addCell(new Label(29, 3, "Total Amount Payable", cellFormat));
//            s.addCell(new Label(29, rowNum, "" + employeeSalaryProcess.getNetPaid(),cellFormatSimpleRight));
//
//            s.addCell(new Label(30, 3, "Signature of Payee", cellFormat));
//            s.addCell(new Label(30, rowNum, "",cellFormatSimpleRight));
//
//            rowNum++;
//        }
//        return workbook;
//    }

    /* @PostMapping("report/employee-salary/salary-slips")
    public ResponseEntity<List<EmployeeSalaryProcess>> getEmployeeSalarySlipsReport(@RequestBody EmployeeAttendanceDto employeeAttendanceDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       // List<EmployeeSalaryProcess> employeeData = reportService.getEmployeeSalaryMonthWiseReport(employeeAttendanceDto.getInputDate(), employeeAttendanceDto.getMonthNumber(), employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("employeeData");
        }
        OutputStream out = null;
        //String fileName = "STATION-" +entries.get(0).getStationId();
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + DateUtil.getTodayDate() + ".xls");
        WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
        try {
            List<EmployeeSalaryProcess> entries = new ArrayList<>();
            List<EmployeeSalaryProcess> salaryMonthWiseReport = null;
            if (employeeData.size() < 44) {
                String stationId = employeeData.size() + ".0";
                //salaryMonthWiseReport = reportService.getEmployeeSalaryMonthWiseReport(employeeAttendanceDto.getInputDate(), employeeAttendanceDto.getMonthNumber(), employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
                if (salaryMonthWiseReport != null) {
                    for (EmployeeSalaryProcess salaryProcess:salaryMonthWiseReport) {
                        writeToSheetPayslips(workbook, salaryProcess, response, stationId, 0);
                    }
                }
            }
            if (salaryMonthWiseReport.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<List<EmployeeSalaryProcess>>(employeeData, HttpStatus.OK);
    }
*/
    private WritableWorkbook writeToSheetPayslips(WritableWorkbook workbook, EmployeeSalaryProcess entries, HttpServletResponse response, String stationId, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("EmpId " + entries.getEmployee().getEmployeeCode() + "-" + entries.getEmployee().getFirstName() + " " + entries.getEmployee().getLastName(), index);
        s.getSettings().setShowGridLines(false);
        double CELL_DEFAULT_HEIGHT = 8;
        double CELL_DEFAULT_WIDTH = 4;
        s.addCell(new Label(0, 0, "BankName"));
        s.addCell(new Label(1, 0, "AccountNumber"));
        s.addCell(new Label(2, 0, "BasicSalary"));
        s.addCell(new Label(3, 0, "HRA"));
        s.addCell(new Label(4, 0, "DATE"));
        int j = 0;
        int i = 0;
           /* s.addCell(new Label(j, i+1, ""+entries.getBankName()));
            s.addCell(new Label(j+1, i+1, ""+entries.getAccountNumber()));
            s.addCell(new Label(j+2, i+1, ""+entries.getBasicSalary()));
            s.addCell(new Label(j+3, i+1, ""+entries.getHouseRentAllowance()));*/
        s.addCell(new Label(j + 4, i + 1, "" + entries.getSalaryProcessingDate()));
        return workbook;
    }

    /*Dhwanith */
    @PostMapping("report/employee-salary-process/monthly-yearly-esic")
    public ResponseEntity<EmployeeSalaryProcess> getEmployeeSalaryProcessMonthlyYearlyESIC(@RequestBody EmployeeSalaryDto employeeSalaryDto) {
        ArrayList employeeEsic = new ArrayList();
        BigDecimal totalContribution = BigDecimal.ZERO;
        List<EmployeeSalaryProcess> employeeSalary = reportService.getEmployeeSalaryProcessMonthlyYearlyESIC(employeeSalaryDto.monthName, employeeSalaryDto.inputYear);
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalary) {
            long esicNumber = Long.parseLong(employeeSalaryProcess.getEmployee().getEsiNumber());
            if (esicNumber > 0) {
                EmployeeSalaryDto employeeDetails = new EmployeeSalaryDto();
                employeeDetails.setEsicNumber(Long.parseLong(employeeSalaryProcess.getEmployee().getEsiNumber()));
                employeeDetails.setEmployeeId(employeeSalaryProcess.getEmployee().getId());
                employeeDetails.setMonthName(employeeSalaryProcess.getSalaryMonth());
                employeeDetails.setEmployeeName(employeeSalaryProcess.getEmployee().getFirstName() + " " + employeeSalaryProcess.getEmployee().getLastName());
                employeeDetails.setNoOfPresent(employeeSalaryProcess.getNoOfPresent());
                employeeDetails.setGrossSalary(employeeSalaryProcess.getGrossSalary());
                BigDecimal employeeEsicContribution = employeeSalaryProcess.getEmployeeEsicContribution();
                employeeDetails.setEmployeeEsicContribution(employeeEsicContribution.setScale(0, BigDecimal.ROUND_UP));
                BigDecimal employerContributionESIC = employeeSalaryProcess.getEmployerContributionESIC();
                employeeDetails.setEmployerEsicContribution(employerContributionESIC.setScale(0, BigDecimal.ROUND_UP));
                totalContribution = employeeSalaryProcess.getEmployerContributionESIC().add(employeeSalaryProcess.getEmployerContributionESIC());
                MathContext m = new MathContext(0); // 4 precision
                employeeDetails.setTotalEsicContribution(totalContribution.setScale(0, BigDecimal.ROUND_UP));
                employeeEsic.add(employeeDetails);
            }
        }
        if (employeeSalary.isEmpty()) {
            throw new EntityNotFoundException("No employee salary process monthly or yearly ESIC");
        }
        return new ResponseEntity(employeeEsic, HttpStatus.OK);
    }

    @Autowired
    EmployeeSalaryProcessService employeeSalaryProcessService;

    @PostMapping("report/employee-salary-process/monthly-epf-statement")
    public ResponseEntity<EmployeeSalaryProcess> getEmployeeMonthlyEPFSatement(@RequestBody EmployeeSalaryDto employeeSalaryDto, HttpServletResponse response) {
        List<EmployeeSalaryProcess> employeeSalary = reportService.getEmployeeMonthlyEPFSatement(employeeSalaryDto.monthName, employeeSalaryDto.inputYear);
        System.out.println("XCV: " + employeeSalary.isEmpty());
        ArrayList employeeMonthlyEPF = new ArrayList();
        for (EmployeeSalaryProcess employeesalaryEPF : employeeSalary) {
            if (/*employeesalaryEPF.getEmployee().isEpf()*/true) {
                Employee employee = new Employee();
                System.out.println("XX: " + employeesalaryEPF.getEmployee().getFirstName());
                employee.setId(employeesalaryEPF.getEmployee().getId());
                employee.setFirstName(employeesalaryEPF.getEmployee().getFirstName());
                employee.setLastName(employeesalaryEPF.getEmployee().getLastName());
                employee.setEmployeeCode(employeesalaryEPF.getEmployee().getEmployeeCode());
                employee.setUanNumber(employeesalaryEPF.getEmployee().getUanNumber());
                employeesalaryEPF.setEmployee(employee);
                employeeMonthlyEPF.add(employeesalaryEPF);
            }

        }
        return new ResponseEntity(employeeMonthlyEPF, HttpStatus.OK);
    }

    @PostMapping("report/employee-salary-process/monthly-epf-statement-download")
    public void downloadEpfStatement(@RequestBody EmployeeSalaryDto employeeSalaryDto, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //TODO fc report
        List<EmployeeSalaryProcess> employeeSalaryProcesses = downloadEmployeeMonthlyEPFSatement(employeeSalaryDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            formEpf(workbook, employeeSalaryProcesses, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    public List<EmployeeSalaryProcess> downloadEmployeeMonthlyEPFSatement(EmployeeSalaryDto employeeSalaryDto) {
        List<EmployeeSalaryProcess> employeeSalary = reportService.getEmployeeMonthlyEPFSatement(employeeSalaryDto.monthName, employeeSalaryDto.inputYear);
        System.out.println("XCV: " + employeeSalary.isEmpty());
        ArrayList employeeMonthlyEPF = new ArrayList();
        for (EmployeeSalaryProcess employeesalaryEPF : employeeSalary) {
            if (/*employeesalaryEPF.getEmployee().isEpf()*/true) {
                Employee employee = new Employee();
                System.out.println("XX: " + employeesalaryEPF.getEmployee().getFirstName());
                employee.setId(employeesalaryEPF.getEmployee().getId());
                employee.setFirstName(employeesalaryEPF.getEmployee().getFirstName());
                employee.setLastName(employeesalaryEPF.getEmployee().getLastName());
                employee.setEmployeeCode(employeesalaryEPF.getEmployee().getEmployeeCode());
                employee.setUanNumber(employeesalaryEPF.getEmployee().getUanNumber());
                employeesalaryEPF.setEmployee(employee);
                employeeMonthlyEPF.add(employeesalaryEPF);
            }

        }
        return employeeMonthlyEPF;
    }

    private WritableWorkbook formEpf(WritableWorkbook workbook, List<EmployeeSalaryProcess> employeeSalaryProcesses, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("epf-statement", index);
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

        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 10);
        s.setColumnView(7, 10);
        s.setColumnView(8, 10);
        s.setColumnView(9, 10);
        s.setColumnView(10, 10);
        s.setColumnView(11, 10);
        s.setColumnView(12, 10);

        s.mergeCells(0, 0, 7, 0);
        s.mergeCells(0, 1, 7, 1);

        List<CompanyName> companyNames = companyNameService.getCompany();
        Collections.reverse(companyNames);

        Label lable = new Label(0, 0, companyNames.get(0).getCompanyName(), headerFormat);
        Label lable1 = new Label(0, 1, "EPFO-Report " + "(" + employeeSalaryProcesses.get(0).getSalaryMonth() + " - " + ")", headerFormat);
        s.addCell(lable);
        s.addCell(lable1);
        s.addCell(new Label(0, 2, "SL No", cellFormat));
        s.addCell(new Label(1, 2, "UAN", cellFormat));
        s.addCell(new Label(2, 2, "Emp_id", cellFormat));
        s.addCell(new Label(3, 2, "Emp_name", cellFormat));
        s.addCell(new Label(4, 2, "Gross_wages", cellFormat));
        s.addCell(new Label(5, 2, "EPF_wages", cellFormat));
        s.addCell(new Label(6, 2, "EPS_wages", cellFormat));
        s.addCell(new Label(7, 2, "EPS_contribution", cellFormat));
        s.addCell(new Label(8, 2, "EDL_insurance", cellFormat));
        s.addCell(new Label(9, 2, "Month", cellFormat));
        s.addCell(new Label(10, 2, "Year", cellFormat));

        int rownum = 3;
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
            s.addCell(new Label(0, rownum, "" + (rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + employeeSalaryProcess.getEmployee().getUanNumber(), cLeft));
            s.addCell(new Label(2, rownum, "" + employeeSalaryProcess.getEmployee().getEmployeeCode(), cLeft));
            s.addCell(new Label(3, rownum, "" + employeeSalaryProcess.getEmployee().getFirstName(), cLeft));
            s.addCell(new Label(4, rownum, "" + employeeSalaryProcess.getGrossSalary(), cLeft));
            s.addCell(new Label(5, rownum, "" + employeeSalaryProcess.getEpfWages(), cLeft));
            s.addCell(new Label(6, rownum, "" + "", cLeft));
            s.addCell(new Label(7, rownum, "" + employeeSalaryProcess.getEpfContribution(), cLeft));
            s.addCell(new Label(8, rownum, "" + "", cLeft));
            s.addCell(new Label(9, rownum, "" + employeeSalaryProcess.getSalaryMonth(), cLeft));
            s.addCell(new Label(10, rownum, "" + employeeSalaryProcess.getSalaryYear(), cLeft));
            rownum++;
        }
        return workbook;
    }

    private WritableWorkbook formProfTax(WritableWorkbook workbook, List<EmployeeSalaryDto> employeeSalaryProcesses, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("PROFESSIONAL_TAX", index);
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

        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 10);
        s.setColumnView(7, 10);
        s.setColumnView(8, 10);
        s.setColumnView(9, 10);
        s.setColumnView(10, 10);
        s.setColumnView(11, 10);
        s.setColumnView(12, 10);

        s.mergeCells(0, 0, 7, 0);
        s.mergeCells(0, 1, 7, 1);

        List<CompanyName> companyNames = companyNameService.getCompany();
        Collections.reverse(companyNames);

        Label lable = new Label(0, 0, companyNames.get(0).getCompanyName(), headerFormat);
        Label lable1 = new Label(0, 1, "PROFESSIONAL_TAX-Report " + "(" + employeeSalaryProcesses.get(0).getMonthName() + "  " + ")", headerFormat);
        s.addCell(lable);
        s.addCell(lable1);
        s.addCell(new Label(0, 2, "SL No", cellFormat));
        s.addCell(new Label(1, 2, "Emp_id", cellFormat));
        s.addCell(new Label(2, 2, "Emp_name", cellFormat));
        s.addCell(new Label(3, 2, "Department", cellFormat));
        s.addCell(new Label(4, 2, "Designation", cellFormat));
        s.addCell(new Label(5, 2, "PT_amount", cellFormat));
        s.addCell(new Label(6, 2, "Month", cellFormat));
        s.addCell(new Label(7, 2, "Year", cellFormat));


        int rownum = 3;
        for (EmployeeSalaryDto employeeSalaryProcess : employeeSalaryProcesses) {
            s.addCell(new Label(0, rownum, "" + (rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + employeeSalaryProcess.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, rownum, "" + employeeSalaryProcess.getEmployeeName(), cLeft));
            s.addCell(new Label(3, rownum, "" + employeeSalaryProcess.getDepartment(), cLeft));
            s.addCell(new Label(4, rownum, "" + employeeSalaryProcess.getDesignation(), cLeft));
            s.addCell(new Label(5, rownum, "" + employeeSalaryProcess.getProfessionalTax(), cLeft));
            s.addCell(new Label(6, rownum, "" + employeeSalaryProcess.getMonthName(), cLeft));
            s.addCell(new Label(7, rownum, "" + employeeSalaryProcess.getYear(), cLeft));
            rownum++;
        }
        return workbook;
    }

//    @PostMapping("report/employee-professional-tax-download")

    @PostMapping("report/employee-salary-process/monthly-yearly-payslip")
    public ResponseEntity<List<EmployeeSalaryProcess>> getEmployeeSalaryProcessMonthlyYearlyPaySlip(@RequestBody EmployeeSalaryDto employeeSalaryDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<EmployeeSalaryProcess> employeeSalary = reportService.getEmployeeSalaryProcessMonthlyYearlyPaySlip(employeeSalaryDto.inputMonth, employeeSalaryDto.inputYear);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Salary_Slip-" + employeeSalary.get(0).getSalaryMonth() + "-" + employeeSalary.get(0).getSalaryYear() + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            List<EmployeeSalaryProcess> entries = new ArrayList<>();
            String stationId = employeeSalary.size() + ".0";
            if (employeeSalary != null) {
                for (EmployeeSalaryProcess salaryProcess : employeeSalary) {
                    writeToSheet(workbook, salaryProcess, response, stationId, 0);
                }
                convertToPDF(employeeSalary, response, 0);
            }
            if (employeeSalary.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<List<EmployeeSalaryProcess>>(employeeSalary, HttpStatus.OK);
    }

    @PostMapping("report/employee-attendance/late-arrival")
    public ResponseEntity<EmployeeAttendance> getEmployeeLateArrivalAttendanceReport(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) throws DocumentException, ParseException {
        List<EmployeeAttendance> employeeAttendances = null;
        if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("DAILY") || employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("MONTHLY")) {
            employeeAttendances = reportService.getTodayMarkedEmployeeAttendanceReport(employeeAttendanceDto.getInputDate(), employeeAttendanceDto.getInputMonth(), employeeAttendanceDto.getDepartmentId());
        } else if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("WEEKLY")) {
            Date toDate = DateUtils.addDays(employeeAttendanceDto.getInputDate(), 7);
            employeeAttendances = reportService.getEmployeeAttendanceWeeklyReport(employeeAttendanceDto.getInputDate(), toDate, employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        }
        if (CollectionUtils.isEmpty(employeeAttendances)) {
            throw new EntityNotFoundException("No one is late");
        }
        ArrayList employeeAttendancesList = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date1 = sdf.parse("10:00");
        for (EmployeeAttendance employeeAttendance : employeeAttendances) {
            if (employeeAttendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                Date date2 = sdf.parse(String.valueOf(employeeAttendance.getInTime()));
                if (date2.getTime() > date1.getTime()) {
                    employeeAttendancesList.add(employeeAttendance);
                }
            }
        }
        return new ResponseEntity(employeeAttendancesList, HttpStatus.OK);
    }


    //    @PostMapping("report/employee-attendance/early-checkout")
    public ResponseEntity<InputStreamResource> getEmployeeEarlyDepartureAttendanceReport(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) throws DocumentException, ParseException {
        List<EmployeeAttendance> employeeAttendances = null;
        if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("DAILY") || employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("MONTHLY")) {
            employeeAttendances = reportService.getTodayMarkedEmployeeAttendanceReport(employeeAttendanceDto.getInputDate(), employeeAttendanceDto.getInputMonth(), employeeAttendanceDto.getDepartmentId());
        } else if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("WEEKLY")) {
            Date toDate = DateUtils.addDays(employeeAttendanceDto.getInputDate(), 7);
            employeeAttendances = reportService.getEmployeeAttendanceWeeklyReport(employeeAttendanceDto.getInputDate(), toDate, employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        }
        ArrayList employeeAttendanceList = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date1 = sdf.parse("17:00");
        for (EmployeeAttendance employeeAttendance : employeeAttendances) {
            if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                System.out.println("Employee " + employeeAttendance.getEmployee().getFirstName());
                String outTime = String.valueOf(employeeAttendance.getOutTime());
                Date date2 = employeeAttendance.getOutTime();
                //sdf.parse(String.valueOf(employeeAttendance.getOutTime()));
                if (date2.getTime() < date1.getTime()) {
                    employeeAttendanceList.add(employeeAttendance);
                }
            }
        }
        if (CollectionUtils.isEmpty(employeeAttendances)) {
            throw new EntityNotFoundException("No one checked out early");
        }
        return new ResponseEntity(employeeAttendanceList, HttpStatus.OK);
    }

    @GetMapping("report/employee/adult-register")
    public ResponseEntity<Employee> getAdultEmployeeRegister() {
        List<Employee> adultEmployee = reportService.getAdultEmployeeRegister();
        ArrayList adultEmployeeList = new ArrayList();
        if (adultEmployee.isEmpty()) {
            throw new EntityNotFoundException("No Adult Employee");
        } else {
            for (Employee adultEmployeeRgister : adultEmployee) {
                AdultEmployeeRegisterDto adultEmployeeRegisterDto = new AdultEmployeeRegisterDto();
                adultEmployeeRegisterDto.setNameAddress(adultEmployeeRgister.getFirstName() + "," + adultEmployeeRgister.getCurrentAddress());
                adultEmployeeRegisterDto.setAge(adultEmployeeRgister.getAge());
                adultEmployeeRegisterDto.setFatherName(adultEmployeeRgister.getFatherName());

                adultEmployeeRegisterDto.setDateOfJoining(adultEmployeeRgister.getDateOfJoining());
                adultEmployeeRegisterDto.setEmployeeType(adultEmployeeRgister.getEmployeeType());
                if (adultEmployeeRgister.getDepartment() != null) {
                    adultEmployeeRegisterDto.setJobTitle(adultEmployeeRgister.getDesignation().getName());
                    adultEmployeeRegisterDto.setDepartmentName(adultEmployeeRgister.getDepartment().getName());
                }
                adultEmployeeList.add(adultEmployeeRegisterDto);
            }
            return new ResponseEntity(adultEmployeeList, HttpStatus.OK);
        }
    }

    @PostMapping("report/employee-absent")
    public ResponseEntity<InputStreamResource> getTodayMarkedEmployeeAbsentReport(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) throws DocumentException {
        List<EmployeeAttendance> employeeData = null;
        if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("DAILY") || employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("MONTHLY")) {
            employeeData = reportService.getTodayMarkedEmployeeAbsentReport(employeeAttendanceDto.getInputDate(), employeeAttendanceDto.getInputMonth(), employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        } else if (employeeAttendanceDto.getReportFrequency().equalsIgnoreCase("WEEKLY")) {
            Date toDate = DateUtils.addDays(employeeAttendanceDto.getInputDate(), 7);
            employeeData = reportService.getEmployeeAbsentWeeklyReport(employeeAttendanceDto.getInputDate(), toDate, employeeAttendanceDto.getEmployeeType(), employeeAttendanceDto.getDepartmentId());
        }
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("Employee Attendance Report not found");
        }
        return new ResponseEntity(employeeData, HttpStatus.OK);
    }

    @Autowired
    EmployeeAttendanceController employeeAttendanceController;

    @PostMapping("report/employee-attendance/monthly-absent")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeMonthlyExtraHrs(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = employeeAttendanceController.viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Monthly_Late_Entry_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    @PostMapping("report/view-salary-register")
    public ResponseEntity<List<EmployeeSalaryProcess>> getEmployeeSalaryMonthWiseView(@RequestBody EmployeeAttendanceDto employeeAttendanceDto, HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException {
        List<EmployeeSalaryProcess> employeeData = reportService.getEmployeeSalaryMonthWiseReport(employeeAttendanceDto.getMonthName(), employeeAttendanceDto.getYear());
        List<EmployeeSalaryProcess> employeeSalaryProcesses = new ArrayList<>();
        //------------------------------------------------------------------
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeData) {
            Employee employeeOfSal = employeeSalaryProcess.getEmployee();
            Employee employee = new Employee();
            employee.setId(employeeOfSal.getId());
            employee.setFirstName(employeeOfSal.getFirstName());
            employee.setLastName(employeeOfSal.getLastName());
            employee.setEmployeeCode(employeeOfSal.getEmployeeCode());
            employee.setUanNumber(employeeOfSal.getUanNumber());
            if (employeeOfSal != null) {
                employee.setDepartment(employeeOfSal.getDepartment());
                employee.setDesignation(employeeOfSal.getDesignation());
            }
            employeeSalaryProcess.setEmployee(employee);
            employeeSalaryProcesses.add(employeeSalaryProcess);
        }
        //-----------------------------------------------------------------
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("employeeData");
        }
        return new ResponseEntity<List<EmployeeSalaryProcess>>(employeeSalaryProcesses, HttpStatus.OK);
    }

    @PostMapping("employee-attendance/absent-entry")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeMonthlyAttendanceLateEntryReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = employeeAttendanceController.viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Monthly_Absent_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                absentEntry(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
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

    private WritableWorkbook absentEntry(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("absent", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 8);
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

        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(jxl.format.Colour.GRAY_25);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        cf.setBoldStyle(WritableFont.NO_BOLD);
        WritableCellFormat c = new WritableCellFormat(cf);
        c.setAlignment(CENTRE);
        c.setBackground(jxl.format.Colour.ICE_BLUE);

        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);

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
        s.setColumnView(0, 5);
        s.setColumnView(1, 6);
        s.setColumnView(2, 15);
        s.mergeCells(0, 0, 33, 0);
        s.mergeCells(0, 1, 33, 1);
        s.mergeCells(0, 2, 33, 2);
//        s.mergeCells(2, 18, 5, 18);
        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName() + " PVT LTD", headerFormat);
        s.addCell(lable);
        SimpleDateFormat dff = new SimpleDateFormat("dd-MMM-yyyy");
        String fd = dff.format(fromDate);
        String td = dff.format(toDate);
        Label lableSlip = new Label(0, 1, "Monthly absent report - From:" + fd + "   To:" + td, headerFormat);
//        Label lableSlip1 = new Label(0, 2, "Prescribed under rule 137 of the Karnataka Factories Rules, 1969 under Karnakata M.W. Rules & P.W. Rules 1963", headerFormat);
        s.addCell(lableSlip);
//        s.addCell(lableSlip1);

        int j = 3;
        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
        for (int i = 0; i < dates.size(); i++) {
            s.mergeCells(j, 2, j + 1, 2);
            DateFormat formatter = new SimpleDateFormat("dd");
//            s.addCell(new Label(j, 2, "" + formatter.format(dates.get(i)), cellFormatDate));
            s.addCell(new Label(j, 3, formatter.format(dates.get(i)), cellFormat));
//            s.addCell(new Label(j + 1, 3, "Out-Time", cellFormat));
//            s.addCell(new Label(j + 2, 3, "WH", cellFormat));
//            s.addCell(new Label(j + 3, 3, "OT", cellFormat));
            j = j + 1;
        }
//        s.addCell(new Label(j + 1, 3, "PRESENT", cellFormat));
//        s.addCell(new Label(j + 2, 3, "HOLIDAYS", cellFormat));
//        s.addCell(new Label(j + 3, 3, "WO", cellFormat));
//        s.addCell(new Label(j + 4, 3, "LEAVES", cellFormat));

//        s.addCell(new Label(j + 5, 3, "ABSENT", cellFormat));
//        s.addCell(new Label(j + 6, 3, "COMP_OFF", cellFormat));
//        s.addCell(new Label(j + 7, 3, "TOTAL_HOURS", cellFormat));
//        s.addCell(new Label(j + 8, 3, "TOTAL_OT_HOURS", cellFormat));
//        s.addCell(new Label(j + 9, 3, "LATE_ENTRY", cellFormat));
//        s.addCell(new Label(j + 10, 3, "EARLY_OUT", cellFormat));
//        s.addCell(new Label(j + 11, 3, "CALENDAR_DAYS", cellFormat));

        int rowNum = 4;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cLeft));
            s.addCell(new Label(1, 3, "Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 3, "Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            int colNum = 3;
            double leaves;
            double present;
            long sundays;
            long holidays;
            long absents;
            long compoffs;
            double totalWorkedHours;
            double totalOtHours;
            long lateIn;
            long earlyOut;
            String inTime = null;
//            String outTime = null;
//            String workedHours = null;
//            String overTime = null;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
                s.setColumnView(colNum, 4);
                System.out.println("employeeAttendanceDto.getMonthlyStatus().size(): " + employeeAttendanceDto.getMonthlyStatus().size());
                if (/*employeeAttendanceDto.getMonthlyStatus() != null &&*/ employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays <= employeeAttendanceDto.getMonthlyStatus().size()) {
                    DateFormat timeFormat = new SimpleDateFormat("HH.mm");
                    try {
//                        employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null
                        if (employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null && employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {

//                            inTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getInTime());
//                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
//                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();
                            inTime = "";

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                inTime = "A";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                                inTime = "";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
                                inTime = "";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                                inTime = "";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
//                                inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                                inTime = "";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                if (employeeAttendanceDto.getMonthlyStatus().get(m).getLeaveHalfType() == null) {
                                    inTime = "";
                                }
                                if (employeeAttendanceDto.getMonthlyStatus().get(m).getLeaveHalfType().equalsIgnoreCase("FH")) {
                                    inTime = "";
                                }
                                if (employeeAttendanceDto.getMonthlyStatus().get(m).getLeaveHalfType().equalsIgnoreCase("SH")) {
                                    inTime = "";
                                }

                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
                                inTime = "CO";
                            }
//                            inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                        if (/*employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null &&*/ employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                            outTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime());
//                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
//                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
//                                outTime = "A";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
//                                outTime = "H";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
//                                outTime = "PH";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
//                                outTime = "WO";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
//                                outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
//                                outTime = "HALF-DAY";

                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
//                                outTime = "COMP-OFF";

                            }
//                            outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Index out of bound");
                    }

//                    if (workedHours != null) {
//                        DecimalFormat df = new DecimalFormat("0.00");
//                        workedHours = df.format(Double.valueOf(workedHours));
//                        workedHours = String.valueOf(Double.valueOf(workedHours));
//                    }
                    s.addCell(new Label(colNum, rowNum, "" + inTime, c));
//                    s.addCell(new Label(colNum + 1, rowNum, "" + outTime));
//                    s.addCell(new Label(colNum + 2, rowNum, "" + workedHours));
//                    s.addCell(new Label(colNum + 3, rowNum, "" + overTime));
                    colNum = colNum + 1;
                } else {
                    s.addCell(new Label(colNum, rowNum, "" + "-", c));
//                    s.addCell(new Label(colNum + 1, rowNum, "" + "-"));
//                    s.addCell(new Label(colNum + 2, rowNum, "" + "-"));
//                    s.addCell(new Label(colNum + 3, rowNum, "" + "-"));
                    colNum = colNum + 1;
                }

            }

            rowNum = rowNum + 1;


        }
        return workbook;
    }

//    private WritableWorkbook absentEntry(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
//        WritableSheet s = workbook.createSheet("Monthly-Absent-Report", index);
//        s.getSettings().setPrintGridLines(false);
//        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
//        headerFont.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
//        headerFormat.setAlignment(CENTRE);
//        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
//        headerFontLeft.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
//        headerFormatLeft.setAlignment(LEFT);
//        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
//        headerFontRight.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
//        headerFormatRight.setAlignment(RIGHT);
//        WritableFont cellFont = new WritableFont(WritableFont.TIMES);
//        cellFont.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
//        cellFormat.setAlignment(CENTRE);
//        cellFormat.setBackground(jxl.format.Colour.GRAY_25);
//        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
//        cellFormatDate.setAlignment(CENTRE);
//        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
//        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
//        cellFontRight.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
//        cellFormatRight.setAlignment(RIGHT);
//        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
//        cellFontLeft.setBoldStyle(WritableFont.BOLD);
//        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
//        cellFormatLeft.setAlignment(LEFT);
//        WritableFont cellFontSimpleRight = new WritableFont(WritableFont.TIMES);
//        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleRight);
//        cellFormatSimpleRight.setAlignment(RIGHT);
//        s.setColumnView(0, 5);
//        s.setColumnView(1, 20);
//        s.setColumnView(2, 15);
//        s.setColumnView(3, 10);
//        s.setColumnView(4, 10);
//        s.setColumnView(5, 10);
//        s.mergeCells(0, 0, 64, 0);
//        s.mergeCells(0, 1, 64, 1);
//        s.mergeCells(2, 18, 5, 18);
//        Label lable = new Label(0, 0, "***** PVT LTD", headerFormat);
//        s.addCell(lable);
//        Label lableSlip = new Label(0, 1, "Monthly Attendance Report", headerFormat);
//        s.addCell(lableSlip);
//
//        int j = 5;
//        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
//        for (int i = 0; i < dates.size(); i++) {
//            s.mergeCells(j, 2, j + 1, 2);
//            DateFormat formatter = new SimpleDateFormat("dd");
//            s.addCell(new Label(j, 2, "" + formatter.format(dates.get(i)), cellFormatDate));
//            s.addCell(new Label(j, 3, "Status", cellFormat));
//            j = j + 2;
//        }
//
//        int rowNum = 4;
//        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
//            s.addCell(new Label(0, 3, "#", cellFormat));
//            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3)));
//            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
//            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode()));
//            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
//            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName()));
//            s.addCell(new Label(3, 3, "Department Name", cellFormat));
//            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartmentName()));
//            s.addCell(new Label(4, 3, "Designation Name", cellFormat));
//            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDesignationName()));
//            int colNum = 5;
//            String inTime;
//            String outTime = null;
//            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
//            System.out.println("*******" + noOfDays);
//            System.out.println("*******" + employeeAttendanceDto.getMonthlyStatus().size());
//            for (int m = 0; m <= noOfDays; m++) {
//                if (employeeAttendanceDto.getMonthlyStatus() != null && employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays <= employeeAttendanceDto.getMonthlyStatus().size()) {
//                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//                    if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus() != null) {
//                        inTime = (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().toString());
//
//                    } else {
//                        inTime = "-";
//                    }
//                    s.addCell(new Label(colNum, rowNum, "" + inTime));
//                    colNum = colNum + 2;
//                }
//            }
//            rowNum = rowNum + 1;
//        }
//        return workbook;
//    }

//    private List<MonthlyEmployeeAttendanceDto> viewMonthlyEmployeeAttendance(DateDto dateDto) {
//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
//        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = new ArrayList<MonthlyEmployeeAttendanceDto>();
//        List<Employee> employees = employeeService.findAll();
//        for (Employee employee : employees) {
//            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
//            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
//            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
//            for (EmployeeAttendance employeeAttendance : monthlyEmployeeAttendance) {
//                String day = new SimpleDateFormat("dd").format(employeeAttendance.getMarkedOn());
//                if (employeeAttendance.getAttendanceStatus().toString().equalsIgnoreCase("ABSENT")) {
//                    MonthlyStatusDto dto = new MonthlyStatusDto();
//                    dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
//                    dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
//                    dto.setMarkedOn(employeeAttendance.getMarkedOn());
//                    dto.setInTime(employeeAttendance.getInTime());
//                    dto.setOutTime(employeeAttendance.getOutTime());
//                    dto.setLateEntry(employeeAttendance.getLateEntry());
//                    dto.setExtraHrs(employeeAttendance.getOverTime());
//                    dto.setId(Long.parseLong(day));
//                    monthlyStatusDtos.add(dto);
//                } else {
//                    MonthlyStatusDto dto = new MonthlyStatusDto();
//                    dto.setId(Long.parseLong(day));
//                    monthlyStatusDtos.add(dto);
//                }
//            }
//            mADto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
//            mADto.setEmployeeCode(employee.getEmployeeCode());
//            mADto.setMonthlyStatus(monthlyStatusDtos);
//            mADto.setEmployeeId(employee.getId());
//            if (employee.getDepartment() != null) {
//                mADto.setDepartmentId(employee.getDepartment().getId());
//                mADto.setDepartmentName(employee.getDepartment().getName());
//                mADto.setDesignationId(employee.getDesignation().getId());
//                mADto.setDesignationName(employee.getDesignation().getName());
//            }
//            monthlyEmployeeAttendanceDtos.add(mADto);
//        }
//        return monthlyEmployeeAttendanceDtos;
//    }


    private static DecimalFormat df = new DecimalFormat("0.00");

    public List<MonthlyEmployeeAttendanceDto> viewMonthlyEmployeeAttendance(DateDto dateDto) throws ParseException {
        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
            throw new EntityNotFoundException("Selected Date of Month Should be same");
        }
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);

        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = new ArrayList<MonthlyEmployeeAttendanceDto>();
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            // TODO
//            attendanceCompute(employee.getId(), dateDto.startDate, dateDto.getEndDate());
//          updateAttendanceLeaveWise(employee.getId(),dateDto.startDate,dateDto.getEndDate());
            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
            List<EmployeeAttendance> presentSize = new ArrayList<>();
            List<EmployeeAttendance> holidaySize = new ArrayList<>();
            List<EmployeeAttendance> sundaySize = new ArrayList<>();
            List<EmployeeAttendance> halfDaySize = new ArrayList<>();
            List<EmployeeAttendance> leaveSize = new ArrayList<>();
            List<EmployeeAttendance> compOffs = new ArrayList<>();
            List<EmployeeAttendance> absents = new ArrayList<>();
            double totalWorkedHrsOfEmployee = 0;
            double totalOTHrsOfEmployee = 0;
            long lateEntryCount = 0;
            for (Date date : dates) {
                System.out.println("date " + date + " empId " + employee.getId());
                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, employee.getId());
                MonthlyStatusDto dto = new MonthlyStatusDto();
                if (employeeAttendance != null) {
                    dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
                    dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
                    dto.setMarkedOn(employeeAttendance.getMarkedOn());
                    dto.setInTime(employeeAttendance.getInTime());
                    dto.setOutTime(employeeAttendance.getOutTime());
                    dto.setLateEntry(employeeAttendance.getLateEntry());
                    dto.setWorkedHours(employeeAttendance.getWorkedHours());
                    dto.setExtraHrs(employeeAttendance.getOverTime());
                    if (employeeAttendance.getLeaveHalfType() != null) {
                        dto.setLeaveHalfType(employeeAttendance.getLeaveHalfType());
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                        updateWorkedHours(employeeAttendance);
                        presentSize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                        sundaySize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                        holidaySize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.ML)) {
                        leaveSize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PH)) {
                        holidaySize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                        halfDaySize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
                        compOffs.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                        absents.add(employeeAttendance);
                    }
                    double totalPresent = presentSize.size() + (halfDaySize.size() * 0.5);
                    dto.setNoOfPresentDays(totalPresent);
                    dto.setNoOfSudays(sundaySize.size());
                    dto.setNoOfHolidays(holidaySize.size());
                    dto.setNoOfLeaves(leaveSize.size() + (halfDaySize.size() * 0.5));
                    dto.setNoOfHalfDays(halfDaySize.size());
                    dto.setNoOfCompOffs(compOffs.size());
                    dto.setNoOfAbsent(absents.size());
                    if (employeeAttendance.getWorkedHours() != null) {
                        totalWorkedHrsOfEmployee = totalWorkedHrsOfEmployee + Double.parseDouble(employeeAttendance.getWorkedHours());
                        totalWorkedHrsOfEmployee = Double.parseDouble(df.format(totalWorkedHrsOfEmployee));
                    }
                    if (employeeAttendance.getEffectiveOverTime() != null) {
                        totalOTHrsOfEmployee = totalOTHrsOfEmployee + Double.parseDouble(employeeAttendance.getEffectiveOverTime());
                    }
                    if (employeeAttendance.getLateEntry() != null) {
                        lateEntryCount = lateEntryCount + 1;
                    }
                    dto.setTotalHours(totalWorkedHrsOfEmployee);
                    dto.setTotalOtHours(totalOTHrsOfEmployee);
                    dto.setTotalLateEntry(lateEntryCount);
                } else {
                    dto.setAttendanceStatus(AttendanceStatus.ABSENT);
                    dto.setDay(DateUtil.getDay(date));
                    dto.setMarkedOn(date);
                    absents.add(employeeAttendance);
                    dto.setNoOfAbsent(absents.size());
                    dto.setInTime(null);
                    dto.setOutTime(null);
                    dto.setLateEntry("null");
                    dto.setWorkedHours("0");
                    dto.setExtraHrs("0");
                }
                monthlyStatusDtos.add(dto);
            }
            mADto.setEmployeeName(employee.getFirstName() /*+ " " + employee.getLastName()*/);
            mADto.setEmployeeCode(employee.getEmployeeCode());
            mADto.setMonthlyStatus(monthlyStatusDtos);
            mADto.setEmployeeId(employee.getId());
            if (employee.getDepartment() != null) {
                mADto.setDepartmentId(employee.getDepartment().getId());
                mADto.setDepartmentName(employee.getDepartment().getName());
                if (employee.getDesignation() != null) {
                    mADto.setDesignationId(employee.getDesignation().getId());
                    mADto.setDesignationName(employee.getDesignation().getName());
                }
            }
            if (employee.getDateOfJoining() != null) {
                if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
                    monthlyEmployeeAttendanceDtos.add(mADto);
                }
            }
        }
        return monthlyEmployeeAttendanceDtos;
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, EmployeeSalaryProcess entries, HttpServletResponse response, String stationId, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("" + entries.getEmployee().getEmployeeCode(), index);
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
        s.setColumnView(0, 18);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.mergeCells(0, 0, 5, 0);
        s.mergeCells(0, 1, 5, 1);
        s.mergeCells(2, 18, 5, 18);
        Label lable = new Label(0, 0, "***** PVT LTD", headerFormat);
        s.addCell(lable);
        Label lableSlip = new Label(0, 1, "SALARY SLIP", headerFormat);
        s.addCell(lableSlip);
        s.addCell(new Label(0, 2, "Emp No."));
        s.addCell(new Label(1, 2, "" + entries.getEmployee().getEmployeeCode()));
        Label lableMonth = new Label(3, 2, "Month", cellFormat);
        s.addCell(lableMonth);
        s.addCell(new Label(4, 2, "" + entries.getSalaryMonth()));
        s.addCell(new Label(0, 3, "Emp Personal No."));
        s.addCell(new Label(1, 3, "" + entries.getEmployee().getPhoneNumber()));
        s.addCell(new Label(2, 3, "PF No."));
        s.addCell(new Label(3, 3, "" + entries.getEmployee().getPfNumber()));
        s.addCell(new Label(2, 4, "UAN No."));
        s.addCell(new Label(3, 4, ""));
        s.addCell(new Label(0, 5, "Name of Employee"));
        s.addCell(new Label(1, 5, "" + entries.getEmployee().getFirstName() + " " + entries.getEmployee().getLastName()));
        s.addCell(new Label(2, 5, "Payment Days"));
        s.addCell(new Label(3, 5, ""));
        s.addCell(new Label(0, 6, "Working Days"));
        s.addCell(new Label(1, 6, ""));
        s.addCell(new Label(2, 6, "PH Holiday"));
        s.addCell(new Label(3, 6, ""));
        s.addCell(new Label(0, 7, "Leave"));
        s.addCell(new Label(1, 7, ""));
        s.addCell(new Label(2, 7, "SUNDAY"));
        s.addCell(new Label(3, 7, ""));
        s.addCell(new Label(0, 8, "Attendance"));
        s.addCell(new Label(1, 8, "" + entries.getNoOfPresent()));
        s.addCell(new Label(2, 8, "Late Attn"));
        s.addCell(new Label(3, 8, ""));
        Label lableEarnings = new Label(0, 9, "Earnings", cellFormat);
        s.addCell(lableEarnings);
        s.addCell(new Label(1, 9, ""));
        Label lableDeductions = new Label(2, 9, "Deductions", cellFormat);
        s.addCell(lableDeductions);
        s.addCell(new Label(3, 9, ""));
        s.addCell(new Label(0, 10, "Basic"));
        s.addCell(new Label(1, 10, "" + entries.getBasicSalary()));
        s.addCell(new Label(2, 10, "P.F"));
        Label lablePFRs = new Label(5, 10, "Rs " + entries.getEpfContribution(), cellFormatSimpleRight);
        s.addCell(lablePFRs);
        s.addCell(new Label(0, 11, "Conveyance"));
        s.addCell(new Label(1, 11, "" + entries.getConveyanceAllowance()));
        s.addCell(new Label(2, 11, "E.S.I.C"));
        Label lableESICRs = new Label(5, 11, "Rs ", cellFormatSimpleRight);
        s.addCell(lableESICRs);
        s.addCell(new Label(0, 12, "HRA"));
        s.addCell(new Label(1, 12, "" + entries.getHouseRentAllowance()));
        s.addCell(new Label(2, 12, "Advance/Loan"));
        Label lableLoanRs = new Label(5, 12, "Rs ", cellFormatSimpleRight);
        s.addCell(lableLoanRs);
        s.addCell(new Label(0, 13, "Edu. Allow"));
        s.addCell(new Label(1, 13, "" + entries.getEducationalAllowance()));
        s.addCell(new Label(2, 13, "P.T"));
        Label lablePTRs = new Label(5, 13, "Rs ", cellFormatSimpleRight);
        s.addCell(lablePTRs);
        s.addCell(new Label(0, 14, "MA"));
        s.addCell(new Label(1, 14, "" + entries.getMedical()));
        s.addCell(new Label(2, 14, "TDS"));
        Label lableTDSs = new Label(5, 14, "Rs " + entries.getBonus(), cellFormatSimpleRight);
        s.addCell(lableTDSs);
        s.addCell(new Label(0, 15, "WA"));
        s.addCell(new Label(1, 15, "" + entries.getWashingAllowance()));
        s.addCell(new Label(0, 16, "Other Allow"));
        s.addCell(new Label(1, 16, "" + entries.getOtherAllowance()));
        s.addCell(new Label(0, 17, "Misc Allow"));
        s.addCell(new Label(1, 17, "" + entries.getMiscellaneousAllowance()));
        Label lableTotalDeductions = new Label(2, 17, "Total", cellFormatLeft);
        s.addCell(lableTotalDeductions);
        Label lableTotalDeductionsRs = new Label(5, 17, "Rs ", cellFormatRight);
        s.addCell(lableTotalDeductionsRs);
        s.addCell(new Label(0, 18, "Mobile Allow"));
        s.addCell(new Label(1, 18, "" + entries.getMobileAllowance()));
        Label lableLeave = new Label(2, 18, "Leave", cellFormat);
        s.addCell(lableLeave);
        Label lableCL = new Label(3, 19, "CL", cellFormat);
        s.addCell(lableCL);
        Label lableEL = new Label(4, 19, "EL/PL", cellFormat);
        s.addCell(lableEL);
        Label lableML = new Label(5, 19, "ML/SL", cellFormat);
        s.addCell(lableML);
        Label lableTotalEarnings = new Label(0, 20, "Total", cellFormatLeft);
        s.addCell(lableTotalEarnings);
        Label lableTotalEarningsRs = new Label(1, 20, "Rs ", cellFormatRight);
        s.addCell(lableTotalEarningsRs);
        s.addCell(new Label(2, 20, "Opening"));
        s.addCell(new Label(2, 21, "Availed"));
        Label lableNetPay = new Label(0, 22, "Net Pay", headerFormatLeft);
        s.addCell(lableNetPay);
        Label lableNetPayRs = new Label(1, 22, "Rs ", headerFormatRight);
        s.addCell(lableNetPayRs);
        s.addCell(new Label(2, 22, "Balance"));
        return workbook;
    }

    public static void convertToPDF(List<EmployeeSalaryProcess> employeeSalary, HttpServletResponse response, int index) throws Exception {
        Paragraph para = new Paragraph();
        Document document = new Document();
        OutputStream out = null;
        PdfWriter.getInstance(document, new FileOutputStream("/home/orileo/Documents/Salary_Slip" + employeeSalary.get(0).getSalaryMonth() + "-" + employeeSalary.get(0).getSalaryYear() + ".pdf"));
        document.open();
        for (EmployeeSalaryProcess entries : employeeSalary) {
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font font1 = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);

            para = new Paragraph("***** PVT LTD", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            para = new Paragraph("SALARY SLIP", font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.getDefaultCell().setBorder(0);
            table.setWidthPercentage(100);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Emp No. :", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + entries.getEmployee().getEmployeeCode(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Month :", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("" + entries.getSalaryMonth(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Emp Personal No.:", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + entries.getEmployee().getPhoneNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("PF No. :", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + entries.getEmployee().getPfNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("UAN No.", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Name of Employee: ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + entries.getEmployee().getFirstName() + " " + entries.getEmployee().getLastName(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Payment Days", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);

            table.addCell(new Phrase("Working Days ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("PH Holiday", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Leave ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("SUNDAY", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Attendance ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Late Attn", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Earnings ", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Deductions", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Basic ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("P.F", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs ", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Conveyance ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("E.S.I.C", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs ", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("HRA ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Advance/Loan", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs ", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Edu. Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("P.T", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs ", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("MA", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("TDS", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs ", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("WA ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Other Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Misc Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Total", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));

            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Mobile Allow", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Leave", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("CL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("EL/PL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("ML/SL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Total ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Opening", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Availed", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Net Pay", FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));
            table.addCell(new Phrase("RS", FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Balance", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            document.add(table);
            document.newPage();
        }
        document.close();
    }


    @PostMapping("report/finance/employee-salary")
    public ResponseEntity<List<EmployeeSalaryProcess>> getEmployeeSalaryMonthWiseReportForFinance(@RequestBody EmployeeAttendanceDto employeeAttendanceDto, HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException {
        List<EmployeeSalaryProcess> employeeData = reportService.getEmployeeSalaryMonthWiseReport(employeeAttendanceDto.getMonthName(), employeeAttendanceDto.getYear());
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("employeeData");
        }
        OutputStream out = null;
        String fileName = "Salary_Register";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());

        salaryRegisterforFinance(workbook, employeeData, response, 0);

        workbook.write();
        workbook.close();
        return new ResponseEntity<List<EmployeeSalaryProcess>>(employeeData, HttpStatus.OK);
    }

    private WritableWorkbook salaryRegisterforFinance(WritableWorkbook workbook, List<EmployeeSalaryProcess> employeeSalaryProcesses, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Monthly-Salary-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setAlignment(Alignment.CENTRE);

        int firstRow = 0;
        int secondRow = 1;
        int heightInPoints = 38 * 15;
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
        cellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
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
        s.setColumnView(0, 5);
        s.setColumnView(1, 10);
        s.setColumnView(2, 20);
        s.setColumnView(3, 15);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.mergeCells(0, 0, 30, 0);
        s.mergeCells(0, 1, 30, 1);
        s.mergeCells(13, 2, 20, 2);
        s.mergeCells(2, 18, 5, 18);
        Label lable = new Label(0, 0, "***** PVT LTD", headerFormat);
        s.addCell(lable);
        s.setRowView(firstRow, heightInPoints);
        Label lableSlip = new Label(0, 1, "Salary Register for the month of " + employeeSalaryProcesses.get(0).getSalaryMonth() + "-" + employeeSalaryProcesses.get(0).getSalaryYear(), headerFormat);
        s.addCell(lableSlip);
        s.setRowView(secondRow, heightInPoints);

        int rowNum = 4;
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3)));
            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCode()));
            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeSalaryProcess.getEmployee().getFirstName() + " " + employeeSalaryProcess.getEmployee().getLastName()));
            s.addCell(new Label(3, 3, "Designation Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeSalaryProcess.getEmployee().getDesignation().getName()));
            s.addCell(new Label(4, 3, "TDS", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeSalaryProcess.getTotalIncomeTaxDeduction()));
            s.addCell(new Label(5, 3, "ESIC", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeSalaryProcess.getEmployeeEsicContribution()));
            s.addCell(new Label(6, 3, "PF", cellFormat));
            s.addCell(new Label(6, rowNum, "" + employeeSalaryProcess.getEpfContribution()));
            s.addCell(new Label(7, 3, "PT", cellFormat));
            s.addCell(new Label(7, rowNum, "" + employeeSalaryProcess.getProfessionalTax()));
            rowNum++;
        }
        return workbook;
    }


    @PostMapping("report/employee-type-count")
    public ResponseEntity<List<EmployeeAttendanceDto>> allTypeEmployeeReport(@RequestBody DateDto dateDto) {
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
        List<EmployeeAttendanceDto> employeeAttendanceDtos = new ArrayList<>();
        for (Date date : dates) {
            long permanentCount = 0;
            long contractCount = 0;
            long permanentContractCount = 0;
            EmployeeAttendanceDto employeeAttendanceDto = new EmployeeAttendanceDto();
            List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getTodayMarkedEmployeeAttendance(date);
            List<PermanentContractAttendance> empPermanentContracts = permanentContractAttendanceRepo.findByMarkedOn(date);
            for (EmployeeAttendance employeeAttendance : employeeAttendances) {
                if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                    permanentCount++;
                }
            }
            for (PermanentContractAttendance permanentContractAttendance : empPermanentContracts) {
                EmpPermanentContract empPermanentContract = permanentContractService.get(permanentContractAttendance.getEmpId());
                if (empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                    contractCount++;
                } else if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                    permanentContractCount++;
                }
            }
            employeeAttendanceDto.setDate(date);
            employeeAttendanceDto.setPermanentCount(permanentCount);
            employeeAttendanceDto.setContractCount(contractCount);
            employeeAttendanceDto.setPermanentContractCount(permanentContractCount);
            employeeAttendanceDtos.add(employeeAttendanceDto);
        }
        return new ResponseEntity<>(employeeAttendanceDtos, HttpStatus.OK);
    }

    @PostMapping("attendance-employee-view/{employeeId}/{month}/{year}")
    public ResponseEntity<List<EmployeeAttendance>> employeeAttendanceForEmployee(@PathVariable long employeeId, @PathVariable int month, @PathVariable int year) throws ParseException {
        Date startDate = DateUtil.getStartDateOfMonth(month, year);
        Date endDate = DateUtil.calculateMonthEndDate(month, year);
        System.out.println("Start: " + startDate + " Enddate " + endDate);
        List<EmployeeAttendance> employeeAttendanceList = new ArrayList<>();
        List<Date> dates = DateUtil.getDaysBetweenDates(startDate, endDate);
        for (Date date : dates) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String d = sdf.format(date);
            date = sdf.parse(d);
            System.out.println("DAte: " + date);
            EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, employeeId);
            if (employeeAttendance != null) {
                employeeAttendanceList.add(employeeAttendance);
            }
        }
        return new ResponseEntity<>(employeeAttendanceList, HttpStatus.OK);
    }
}
