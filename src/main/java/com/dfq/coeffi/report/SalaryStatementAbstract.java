package com.dfq.coeffi.report;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.EmployeeAttendanceDto;
import com.dfq.coeffi.dto.EmployeeSalaryDto;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryProcessService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.util.HeaderFooterPageEvent;
import com.dfq.coeffi.vivo.service.CompanyService;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.mail.smtp.SMTPTransport;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import static com.dfq.coeffi.entity.hr.employee.AttendanceStatus.WO;
import static com.dfq.coeffi.report.ReportController.convertToPDF;
import static jxl.format.Alignment.*;
import static jxl.format.Alignment.LEFT;

@RestController
public class SalaryStatementAbstract extends BaseController {
    @Autowired
    private ReportService reportService;

    @Autowired
    EmployeeSalaryProcessService employeeSalaryProcessService;
    @Autowired
    CompanyNameService companyNameService;

    @PostMapping("report/employee/employee-salary")
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
        salaryAbstract(workbook, employeeData, response, 1);

        workbook.write();
        workbook.close();
        return new ResponseEntity<List<EmployeeSalaryProcess>>(employeeData, HttpStatus.OK);
    }

    private WritableWorkbook salaryRegister(WritableWorkbook workbook, List<EmployeeSalaryProcess> employeeSalaryProcesses, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Salary-statement", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 9);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setAlignment(Alignment.CENTRE);


        int firstRow = 0;
        int secondRow = 1;
        int thirdRow = 2;
        int heightInPoints = 38 * 15;
        int heightInPoint2 = 10 * 10;
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
        cellFormat.setWrap(true);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
        cellFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.MEDIUM);
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

        WritableCellFormat cleftHeader = new WritableCellFormat(cellFontLeft);
        cleftHeader.setAlignment(LEFT);
        cleftHeader.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.MEDIUM);

        s.setColumnView(0, 5);
        s.setColumnView(1, 10);
        s.setColumnView(2, 20);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 20);
        s.setColumnView(7, 10);
        s.mergeCells(0, 0, 30, 0);
        s.mergeCells(0, 1, 30, 1);
        s.mergeCells(0, 2, 6, 2);
        s.mergeCells(7, 2, 16, 2);
        s.mergeCells(17, 2, 26, 2);
        s.mergeCells(27, 2, 40, 2);
        s.mergeCells(41, 2, 52, 2);
        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName()+" PVT LTD", headerFormat);
        s.addCell(lable);
//        s.setRowView(firstRow, heightInPoints);
        Label lableSlip = new Label(0, 1, "Salary Register for the month of " + employeeSalaryProcesses.get(0).getSalaryMonth() + "-" + employeeSalaryProcesses.get(0).getSalaryYear(), headerFormat);
        s.addCell(lableSlip);
//        s.setRowView(secondRow, heightInPoints);
        s.addCell(new Label(0, 2, "Employee_info", cleftHeader));
        s.addCell(new Label(7, 2, "Attendance", cleftHeader));
        s.addCell(new Label(17, 2, "Actual_salary", cleftHeader));
        s.addCell(new Label(27, 2, "Earned_salary", cleftHeader));
        s.addCell(new Label(41, 2, "Deductions", cleftHeader));

        int rowNum = 4;
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cellFormatSimpleRight));

            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCode(), cellFormatSimpleRight));

            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeSalaryProcess.getEmployee().getFirstName() + " " + employeeSalaryProcess.getEmployee().getLastName(), cellFormatSimpleRight));

            s.addCell(new Label(3, 3, "Grade", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeSalaryProcess.getEmployee().getLevel(), cellFormatSimpleRight));

            s.addCell(new Label(4, 3, "Department", cellFormat));
            if (employeeSalaryProcess.getEmployee().getDepartment() != null) {
                s.addCell(new Label(4, rowNum, "" + employeeSalaryProcess.getEmployee().getDepartment().getName(), cellFormatSimpleRight));
            } else {
                s.addCell(new Label(4, rowNum, "" + "", cellFormatSimpleRight));
            }

            s.addCell(new Label(5, 3, "Designation", cellFormat));
            if (employeeSalaryProcess.getEmployee().getDesignation() != null) {
                s.addCell(new Label(5, rowNum, "" + employeeSalaryProcess.getEmployee().getDesignation().getName(), cellFormatSimpleRight));
            } else {
                s.addCell(new Label(5, rowNum, "" + "", cellFormatSimpleRight));
            }

            s.addCell(new Label(6, 3, "Emp_type", cellFormat));
            if (employeeSalaryProcess.getEmployee().getEmployeeType() != null) {
                s.addCell(new Label(6, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeType(), cellFormatSimpleRight));
            } else {
                s.addCell(new Label(6, rowNum, "" + "", cellFormatSimpleRight));
            }

            long calenderDays = DateUtil.getCalenderDays(Integer.parseInt(employeeSalaryProcess.getSalaryYear()), DateUtil.getMonthNumber(employeeSalaryProcess.getSalaryMonth()));
            s.addCell(new Label(7, 3, "Calender_days", cellFormat));
            s.addCell(new Label(7, rowNum, "" + calenderDays, cellFormatSimpleRight));
//            Lop / ab;


            double noOfAbsentSize = calenderDays - (employeeSalaryProcess.getNoOfPresent() + employeeSalaryProcess.getNoOfHolidays() + employeeSalaryProcess.getSundays()+employeeSalaryProcess.getNoOfLeaves());

            s.addCell(new Label(8, 3, "LOP/AB", cellFormat));
            s.addCell(new Label(8, rowNum, "" + noOfAbsentSize, cellFormatSimpleRight));

            s.addCell(new Label(9, 3, "Leaves", cellFormat));
            s.addCell(new Label(9, rowNum, "" + employeeSalaryProcess.getNoOfLeaves(), cellFormatSimpleRight));

//            Compoff;

            s.addCell(new Label(10, 3, "CompOff", cellFormat));
            s.addCell(new Label(10, rowNum, "" + employeeSalaryProcess.getAvailCasualLeave(), cellFormatSimpleRight));

            s.addCell(new Label(11, 3, "Holidays", cellFormat));
            s.addCell(new Label(11, rowNum, "" + employeeSalaryProcess.getNoOfHolidays(), cellFormatSimpleRight));

            s.addCell(new Label(12, 3, "WO", cellFormat));
            s.addCell(new Label(12, rowNum, "" + employeeSalaryProcess.getSundays(), cellFormatSimpleRight));

            s.addCell(new Label(13, 3, "LateIn_hrs", cellFormat));
            s.addCell(new Label(13, rowNum, "" + convertOt(String.valueOf(employeeSalaryProcess.getTotalLateEntryHrs())), cellFormatSimpleRight));

            s.addCell(new Label(14, 3, "EarlyOut_hrs", cellFormat));
            s.addCell(new Label(14, rowNum, "" + convertOt(String.valueOf(employeeSalaryProcess.getTotalEarlyOutHrs())), cellFormatSimpleRight));

            s.addCell(new Label(15, 3, "OT_hrs", cellFormat));
            s.addCell(new Label(15, rowNum, "" + convertOt(String.valueOf(employeeSalaryProcess.getOtHrs())), cellFormatSimpleRight));

            s.addCell(new Label(16, 3, "Present days", cellFormat));
            s.addCell(new Label(16, rowNum, "" + employeeSalaryProcess.getNoOfPresent(), cellFormatSimpleRight));


            //---------------ACTUAL------------------------------------------------
            s.addCell(new Label(17, 3, "Actual Basic+VDA", cellFormat));
            s.addCell(new Label(17, rowNum, "" + employeeSalaryProcess.getBasicSalary(), actual));

            s.addCell(new Label(18, 3, "Actual_CON", cellFormat));
            s.addCell(new Label(18, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getConveyanceAllowance(), actual));

            s.addCell(new Label(19, 3, "Actual_HRA", cellFormat));
            s.addCell(new Label(19, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getHouseRentAllowance(), actual));

            s.addCell(new Label(20, 3, "Actual_EDU", cellFormat));
            s.addCell(new Label(20, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getEducationalAllowance(), actual));

            s.addCell(new Label(21, 3, "Actual_MA", cellFormat));
            s.addCell(new Label(21, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMealsAllowance(), actual));

            s.addCell(new Label(22, 3, "Actual_WA", cellFormat));
            s.addCell(new Label(22, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getWashingAllowance(), actual));

            s.addCell(new Label(23, 3, "Actual_OTHER ALLOW", cellFormat));
            s.addCell(new Label(23, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getOtherAllowance(), actual));

            s.addCell(new Label(24, 3, "Actual_MISC", cellFormat));
            s.addCell(new Label(24, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMiscellaneousAllowance(), actual));

            s.addCell(new Label(25, 3, "Actual_MOBILE", cellFormat));
            s.addCell(new Label(25, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMobileAllowance(), actual));

            s.addCell(new Label(26, 3, "Actual GROSS", cellFormat));
            s.addCell(new Label(26, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMonthlyCtc(), actual));

            //---------------ACTUAL------------------------------------------------


            //---------------EARNED-------------------------------------------------
            s.addCell(new Label(27, 3, "Basic+VDA", cellFormat));
            s.addCell(new Label(27, rowNum, "" + employeeSalaryProcess.getCurrentBasic(), cellFormatSimpleRight));

            s.addCell(new Label(28, 3, "Arrears", cellFormat));
            s.addCell(new Label(28, rowNum, "", cellFormatSimpleRight));


            s.addCell(new Label(29, 3, "CON", cellFormat));
            s.addCell(new Label(29, rowNum, "" + employeeSalaryProcess.getConveyanceAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(30, 3, "HRA", cellFormat));
            s.addCell(new Label(30, rowNum, "" + employeeSalaryProcess.getHouseRentAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(31, 3, "EDU", cellFormat));
            s.addCell(new Label(31, rowNum, "" + employeeSalaryProcess.getEducationalAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(32, 3, "MA", cellFormat));
            s.addCell(new Label(32, rowNum, "" + employeeSalaryProcess.getMealsAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(33, 3, "WA", cellFormat));
            s.addCell(new Label(33, rowNum, "" + employeeSalaryProcess.getWashingAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(34, 3, "OTHER ALLOW", cellFormat));
            s.addCell(new Label(34, rowNum, "" + employeeSalaryProcess.getOtherAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(35, 3, "MISC", cellFormat));
            s.addCell(new Label(35, rowNum, "" + employeeSalaryProcess.getMiscellaneousAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(36, 3, "MOBILE", cellFormat));
            s.addCell(new Label(36, rowNum, "" + employeeSalaryProcess.getMobileAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(37, 3, "Others", cellFormat));
            s.addCell(new Label(37, rowNum, "" + employeeSalaryProcess.getOtPay(), cellFormatSimpleRight));

            BigDecimal totEarning = BigDecimal.ZERO;
            BigDecimal bonus = employeeSalaryProcess.getBonus();
            BigDecimal others = BigDecimal.ZERO;
            if (employeeSalaryProcess.getOtPay() != null) {
                others = employeeSalaryProcess.getOtPay();
            }
            totEarning = employeeSalaryProcess.getGrossSalary().add(bonus).add(others);

            s.addCell(new Label(38, 3, "Bonus", cellFormat));
            s.addCell(new Label(38, rowNum, ""+bonus, cellFormatSimpleRight));

            s.addCell(new Label(39, 3, "GROSS", cellFormat));
            s.addCell(new Label(39, rowNum, "" + employeeSalaryProcess.getGrossSalary(), cellFormatSimpleRight));

            s.addCell(new Label(40, 3, "Total_earnings", cellFormat));
            s.addCell(new Label(40, rowNum, "" + totEarning, cellFormatSimpleRight));



            //---------------EARNED----------------------------------------------------

            //----------------DEDUCTION-----------------------------------------------
            BigDecimal otherDed = BigDecimal.ZERO;
            if (employeeSalaryProcess.getLateEntryLoss() != null) {
                otherDed = employeeSalaryProcess.getLateEntryLoss();
            }
            if (employeeSalaryProcess.getEarlyOutLoss() != null) {
                otherDed = employeeSalaryProcess.getEarlyOutLoss();
            }
            if (employeeSalaryProcess.getLateEntryLoss() != null && employeeSalaryProcess.getEarlyOutLoss() != null) {
                otherDed = employeeSalaryProcess.getLateEntryLoss().add(employeeSalaryProcess.getEarlyOutLoss());
            }

            s.addCell(new Label(41, 3, "Advance Recovered", cellFormat));
            s.addCell(new Label(41, rowNum, "" + employeeSalaryProcess.getTotalAdvanceDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(42, 3, "Actual_TDS", cellFormat));
            s.addCell(new Label(42, rowNum, "-", actual));
            s.addCell(new Label(43, 3, "TDS", cellFormat));
            s.addCell(new Label(43, rowNum, "" + employeeSalaryProcess.getTotalIncomeTaxDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(44, 3, "Actual_ESIC", cellFormat));
            s.addCell(new Label(44, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getEmployeeEsicContribution(), actual));
            s.addCell(new Label(45, 3, "ESIC", cellFormat));
            s.addCell(new Label(45, rowNum, "" + employeeSalaryProcess.getEmployeeEsicContribution(), cellFormatSimpleRight));

            s.addCell(new Label(46, 3, "Meal", cellFormat));
            s.addCell(new Label(46, rowNum, "" + employeeSalaryProcess.getTotalMealDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(47, 3, "Actual_Profession Tax", cellFormat));
            s.addCell(new Label(47, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getTpt(), actual));
            s.addCell(new Label(48, 3, "Profession Tax", cellFormat));
            s.addCell(new Label(48, rowNum, "" + employeeSalaryProcess.getProfessionalTax(), cellFormatSimpleRight));

            s.addCell(new Label(49, 3, "Actual_PF @12%", cellFormat));
            s.addCell(new Label(49, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getEpfContribution(), actual));

            s.addCell(new Label(50, 3, "PF @12%", cellFormat));
            s.addCell(new Label(50, rowNum, "" + employeeSalaryProcess.getEpfContribution(), cellFormatSimpleRight));

            s.addCell(new Label(51, 3, "Other_deduction", cellFormat));
            s.addCell(new Label(51, rowNum, "" + otherDed, cellFormatSimpleRight));

            s.addCell(new Label(52, 3, "Total_deduction", cellFormat));
            s.addCell(new Label(52, rowNum, "" + employeeSalaryProcess.getTotalDeduction().add(otherDed), cellFormatSimpleRight));


            s.addCell(new Label(53, 3, "Net salary", cellFormat));
            s.addCell(new Label(53, rowNum, "" + employeeSalaryProcess.getNetPaid(), cellFormatSimpleRight));

            s.addCell(new Label(54, 3, "Signature of Payee", cellFormat));
            s.addCell(new Label(54, rowNum, "", cellFormatSimpleRight));

            rowNum++;
        }
        return workbook;
    }

    private WritableWorkbook salaryAbstract(WritableWorkbook workbook, List<EmployeeSalaryProcess> employeeSalaryProcesses, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Salary-Abstract", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 9);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setAlignment(Alignment.CENTRE);


        int firstRow = 0;
        int secondRow = 1;
        int thirdRow = 2;
        int heightInPoints = 38 * 15;
        int heightInPoint2 = 10 * 10;
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
        cellFormat.setWrap(true);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
        cellFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.MEDIUM);
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

        WritableCellFormat cleftHeader = new WritableCellFormat(cellFontLeft);
        cleftHeader.setAlignment(LEFT);
        cleftHeader.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.MEDIUM);

        s.setColumnView(0, 5);
        s.setColumnView(1, 10);
        s.setColumnView(2, 20);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 20);
        s.setColumnView(7, 10);
        s.mergeCells(0, 0, 30, 0);
        s.mergeCells(0, 1, 30, 1);
        s.mergeCells(0, 2, 6, 2);
        s.mergeCells(7, 2, 13, 2);
        s.mergeCells(14, 2, 27, 2);
        s.mergeCells(28, 2, 36, 2);
        s.mergeCells(37, 2, 49, 2);
        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName()+" PVT LTD", headerFormat);
        s.addCell(lable);
//        s.setRowView(firstRow, heightInPoints);
        Label lableSlip = new Label(0, 1, "Salary Abstract for the month of " + employeeSalaryProcesses.get(0).getSalaryMonth() + "-" + employeeSalaryProcesses.get(0).getSalaryYear(), headerFormat);
        s.addCell(lableSlip);
//        s.setRowView(secondRow, heightInPoints);
        s.addCell(new Label(0, 2, "Employee_info", cleftHeader));
        s.addCell(new Label(7, 2, "Attendance", cleftHeader));
        s.addCell(new Label(14, 2, "Earned_salary", cleftHeader));
        s.addCell(new Label(28, 2, "Deductions", cleftHeader));

        int rowNum = 4;
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cellFormatSimpleRight));

            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCode(), cellFormatSimpleRight));

            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeSalaryProcess.getEmployee().getFirstName() + " " + employeeSalaryProcess.getEmployee().getLastName(), cellFormatSimpleRight));

            s.addCell(new Label(3, 3, "Grade", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeSalaryProcess.getEmployee().getLevel(), cellFormatSimpleRight));

            s.addCell(new Label(4, 3, "Actual_Gross", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMonthlyCtc(), actual));



            s.addCell(new Label(5, 3, "Department", cellFormat));
            if (employeeSalaryProcess.getEmployee().getDepartment() != null) {
                s.addCell(new Label(5, rowNum, "" + employeeSalaryProcess.getEmployee().getDepartment().getName(), cellFormatSimpleRight));
            } else {
                s.addCell(new Label(5, rowNum, "" + "", cellFormatSimpleRight));
            }



            s.addCell(new Label(6, 3, "Emp_type", cellFormat));
            if (employeeSalaryProcess.getEmployee().getEmployeeType() != null) {
                s.addCell(new Label(6, rowNum, "" + employeeSalaryProcess.getEmployee().getEmployeeType(), cellFormatSimpleRight));
            } else {
                s.addCell(new Label(6, rowNum, "" + "", cellFormatSimpleRight));
            }

            long calenderDays = DateUtil.getCalenderDays(Integer.parseInt(employeeSalaryProcess.getSalaryYear()), DateUtil.getMonthNumber(employeeSalaryProcess.getSalaryMonth()));
            s.addCell(new Label(7, 3, "Calender_days", cellFormat));
            s.addCell(new Label(7, rowNum, "" + calenderDays, cellFormatSimpleRight));
//            Lop / ab;


            s.addCell(new Label(8, 3, "Leaves", cellFormat));
            s.addCell(new Label(8, rowNum, "" + employeeSalaryProcess.getAvailCasualLeave(), cellFormatSimpleRight));

//            Compoff;

            s.addCell(new Label(9, 3, "CompOff", cellFormat));
            s.addCell(new Label(9, rowNum, "" + employeeSalaryProcess.getAvailCasualLeave(), cellFormatSimpleRight));

            s.addCell(new Label(10, 3, "Holidays", cellFormat));
            s.addCell(new Label(10, rowNum, "" + employeeSalaryProcess.getNoOfHolidays(), cellFormatSimpleRight));

            s.addCell(new Label(11, 3, "WO", cellFormat));
            s.addCell(new Label(11, rowNum, "" + employeeSalaryProcess.getSundays(), cellFormatSimpleRight));

            s.addCell(new Label(12, 3, "OT_hrs", cellFormat));
            s.addCell(new Label(12, rowNum, "" + convertOt(String.valueOf(employeeSalaryProcess.getOtHrs())), cellFormatSimpleRight));

            s.addCell(new Label(13, 3, "Present days", cellFormat));
            s.addCell(new Label(13, rowNum, "" + employeeSalaryProcess.getNoOfPresent(), cellFormatSimpleRight));


            //---------------EARNED-------------------------------------------------
            s.addCell(new Label(14, 3, "Basic+VDA", cellFormat));
            s.addCell(new Label(14, rowNum, "" + employeeSalaryProcess.getCurrentBasic(), cellFormatSimpleRight));

            s.addCell(new Label(15, 3, "Arrears", cellFormat));
            s.addCell(new Label(15, rowNum, "", cellFormatSimpleRight));


            s.addCell(new Label(16, 3, "CON", cellFormat));
            s.addCell(new Label(16, rowNum, "" + employeeSalaryProcess.getConveyanceAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(17, 3, "HRA", cellFormat));
            s.addCell(new Label(17, rowNum, "" + employeeSalaryProcess.getHouseRentAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(18, 3, "EDU", cellFormat));
            s.addCell(new Label(18, rowNum, "" + employeeSalaryProcess.getEducationalAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(19, 3, "MA", cellFormat));
            s.addCell(new Label(19, rowNum, "" + employeeSalaryProcess.getMealsAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(20, 3, "WA", cellFormat));
            s.addCell(new Label(20, rowNum, "" + employeeSalaryProcess.getWashingAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(21, 3, "OTHER ALLOW", cellFormat));
            s.addCell(new Label(21, rowNum, "" + employeeSalaryProcess.getOtherAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(22, 3, "MISC", cellFormat));
            s.addCell(new Label(22, rowNum, "" + employeeSalaryProcess.getMiscellaneousAllowance(), cellFormatSimpleRight));


            s.addCell(new Label(23, 3, "MOBILE", cellFormat));
            s.addCell(new Label(23, rowNum, "" + employeeSalaryProcess.getMobileAllowance(), cellFormatSimpleRight));

            s.addCell(new Label(24, 3, "Others", cellFormat));
            s.addCell(new Label(24, rowNum, "" + employeeSalaryProcess.getOtPay(), cellFormatSimpleRight));

            BigDecimal totEarning = BigDecimal.ZERO;
            BigDecimal bonus = employeeSalaryProcess.getBonus();
            BigDecimal others = BigDecimal.ZERO;
            if (employeeSalaryProcess.getOtPay() != null) {
                others = employeeSalaryProcess.getOtPay();
            }
            totEarning = employeeSalaryProcess.getGrossSalary().add(bonus).add(others);

            s.addCell(new Label(25, 3, "Bonus", cellFormat));
            s.addCell(new Label(25, rowNum, ""+bonus, cellFormatSimpleRight));

            s.addCell(new Label(26, 3, "GROSS", cellFormat));
            s.addCell(new Label(26, rowNum, "" + employeeSalaryProcess.getGrossSalary(), cellFormatSimpleRight));

            s.addCell(new Label(27, 3, "Total_earnings", cellFormat));
            s.addCell(new Label(27, rowNum, "" + totEarning, cellFormatSimpleRight));

            //---------------EARNED----------------------------------------------------

            //----------------DEDUCTION-----------------------------------------------
            s.addCell(new Label(28, 3, "-", cellFormat));
            s.addCell(new Label(28, rowNum, "-", cellFormatSimpleRight));

            s.addCell(new Label(29, 3, "Advance Recovered", cellFormat));
            s.addCell(new Label(29, rowNum, "" + employeeSalaryProcess.getTotalAdvanceDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(30, 3, "TDS", cellFormat));
            s.addCell(new Label(30, rowNum, "" + employeeSalaryProcess.getTotalIncomeTaxDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(31, 3, "ESIC", cellFormat));
            s.addCell(new Label(31, rowNum, "" + employeeSalaryProcess.getEmployeeEsicContribution(), cellFormatSimpleRight));

            s.addCell(new Label(32, 3, "Meal", cellFormat));
            s.addCell(new Label(32, rowNum, "" + employeeSalaryProcess.getTotalMealDeduction(), cellFormatSimpleRight));

            s.addCell(new Label(33, 3, "Profession Tax", cellFormat));
            s.addCell(new Label(33, rowNum, "" + employeeSalaryProcess.getProfessionalTax(), cellFormatSimpleRight));

            s.addCell(new Label(34, 3, "PF @12%", cellFormat));
            s.addCell(new Label(34, rowNum, "" + employeeSalaryProcess.getEpfContribution(), cellFormatSimpleRight));
            BigDecimal otherDed = BigDecimal.ZERO;
            if (employeeSalaryProcess.getLateEntryLoss() != null) {
                otherDed = employeeSalaryProcess.getLateEntryLoss();
            }
            if (employeeSalaryProcess.getEarlyOutLoss() != null) {
                otherDed = employeeSalaryProcess.getEarlyOutLoss();
            }
            if (employeeSalaryProcess.getLateEntryLoss() != null && employeeSalaryProcess.getEarlyOutLoss() != null) {
                otherDed = employeeSalaryProcess.getLateEntryLoss().add(employeeSalaryProcess.getEarlyOutLoss());
            }

            s.addCell(new Label(35, 3, "Other_deductions", cellFormat));
            s.addCell(new Label(35, rowNum, ""+otherDed, cellFormatSimpleRight));

            s.addCell(new Label(36, 3, "Total_deduction", cellFormat));
            s.addCell(new Label(36, rowNum, "" + employeeSalaryProcess.getTotalDeduction().add(otherDed), cellFormatSimpleRight));

            s.addCell(new Label(37, 3, "Net salary", cellFormat));
            s.addCell(new Label(37, rowNum, "" + employeeSalaryProcess.getNetPaid(), cellFormatSimpleRight));

            s.addCell(new Label(38, 3, "Signature of Payee", cellFormat));
            s.addCell(new Label(38, rowNum, "", cellFormatSimpleRight));

            rowNum++;
        }
        return workbook;
    }

    @Autowired
    EmployeeAttendanceService employeeAttendanceService;

    @PostMapping("employee-salary-process/pay-slip/{empId}/{year}/{month}")
    public Document sendSalaryPaySlipPDF(@PathVariable long empId, @PathVariable String year, @PathVariable String month, HttpServletResponse response) {
        Optional<EmployeeSalaryProcess> employeeSalaryOpt = employeeSalaryProcessService.getEmployeeSalaryCreatedByMonth(empId, month, year);
        EmployeeSalaryProcess employeeSalary = null;
        if (employeeSalaryOpt.isPresent()) {
            employeeSalary = employeeSalaryOpt.get();
        } else {
            throw new EntityNotFoundException("No data for employee " + empId);
        }

        Document document = new Document();
        try {
            Paragraph para = new Paragraph();

            response.setContentType("application/pdf");
            PdfWriter.getInstance(document, response.getOutputStream()); //to download
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //PDF to send email
            PdfWriter writer = PdfWriter.getInstance(document, baos); // PDF to send email
            document.open();
            com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font font1 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 10, Font.BOLD);

//            Image img1 = Image.getInstance("https://res.cloudinary.com/hanumanth-cloudinary/image/upload/v1557470801/weldomac_logo.jpg");//WELDOMAC
            Image img1 = Image.getInstance("https://res.cloudinary.com/ddtcc2d1m/image/upload/v1613403308/spy/paramount_f6idlg.jpg");//Paramount
            img1.scaleAbsolute(50f, 50f);
            img1.scaleToFit(70f, 50f);
            img1.setAbsolutePosition(90, 750);
            document.add(img1);

            int size = 8;

            List<CompanyName> companyName = companyNameService.getCompany();
            Collections.reverse(companyName);

            para = new Paragraph(companyName.get(0).getCompanyName()+" Pvt ltd", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            para = new Paragraph("-------------", font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph("Pay-slip for the month of " + employeeSalary.getSalaryMonth() + " " + employeeSalary.getSalaryYear(), font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(6);
            // TODO uc old pdf format
//            table.setWidths(new int[]{2, 3, 2, 1, 1, 2});
            table.setWidths(new int[]{2, 2, 2, 2, 2, 2});
            table.getDefaultCell().setBorder(0);
            table.setWidthPercentage(100);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Month:", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new PdfPCell(new Phrase(":" + employeeSalary.getSalaryMonth(), FontFactory.getFont(FontFactory.COURIER, size))));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));


            String designation = "";
            if (employeeSalary.getEmployee().getDesignation() != null) {
                designation = employeeSalary.getEmployee().getDesignation().getName();
            }

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Emp Id", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCode(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("PF No.", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getPfNumber(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Aadhar", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getAdharNumber(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);

            String level = "";
            if (employeeSalary.getEmployee().getLevel() != null) {
                employeeSalary.getEmployee().getLevel();
            }
            String department = "";
            if (employeeSalary.getEmployee().getDepartment() != null) {
                department = employeeSalary.getEmployee().getDepartment().getName();
            }

            table.addCell(new Phrase("Grade", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + level, FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Department", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + department, FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Designation", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + designation, FontFactory.getFont(FontFactory.COURIER, size)));

            String bankName = "";
            String accNo = "";
            if (employeeSalary.getEmployee().getEmployeeBank() != null) {
                bankName = employeeSalary.getEmployee().getEmployeeBank().getBankName();
                accNo = employeeSalary.getEmployee().getEmployeeBank().getAccountNumber();
            }
            table.addCell(new Phrase("Caterory", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeType(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Account No", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + accNo, FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Bank Name", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + bankName, FontFactory.getFont(FontFactory.COURIER, size)));

            table.addCell(new Phrase("ESIC No", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEsiNumber(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("UAN No", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            PdfPCell pdfPCell = new PdfPCell(new Phrase(":" + employeeSalary.getEmployee().getUanNumber(), FontFactory.getFont(FontFactory.COURIER, size)));
            pdfPCell.setColspan(2);
            pdfPCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(pdfPCell);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));

            int monthNumber = DateUtil.getMonthNumber(month);
            int yearInt = Integer.parseInt(year);
            double noOfLeaves = employeeSalary.getNoOfLeaves();
            long calenderDays = DateUtil.getCalenderDays(yearInt, monthNumber);
            double noOfAbsentSize = calenderDays - (employeeSalary.getNoOfPresent() + employeeSalary.getNoOfHolidays() + employeeSalary.getSundays()+employeeSalary.getNoOfLeaves());
            double paymentDays = (employeeSalary.getNoOfPresent() + employeeSalary.getNoOfHolidays() + employeeSalary.getSundays()+employeeSalary.getNoOfLeaves());
            table.addCell(new Phrase("Employee Name", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(1);

            table.addCell(new Phrase("Absent", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + noOfAbsentSize, FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Holidays(PH/DH)", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getNoOfHolidays(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Comp-offs", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + 0, FontFactory.getFont(FontFactory.COURIER, size)));


            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Leaves", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + noOfLeaves, FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Sundays/WO", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getSundays(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Payment Days", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + paymentDays, FontFactory.getFont(FontFactory.COURIER, size)));

            table.addCell(new Phrase("Attendance", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getNoOfPresent(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Working Days", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + employeeSalary.getWorkingDays(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Calender Days", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(":" + calenderDays, FontFactory.getFont(FontFactory.COURIER, size)));


            table.getDefaultCell().setBorder(1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Salary components ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase(" Actual", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Earned", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Deduction comp", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));


            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Basic+VDA ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCTCData().getBasicSalary(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getCurrentBasic(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("P.F", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEpfContribution(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Conveyance ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCTCData().getConveyanceAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getConveyanceAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("E.S.I.C", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployeeEsicContribution(), FontFactory.getFont(FontFactory.COURIER, size)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("HRA ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCTCData().getHouseRentAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getHouseRentAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Advance/Loan", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            BigDecimal advance_loan = employeeSalary.getTotalAdvanceDeduction()
                    .add(employeeSalary.getTotalMealDeduction())
                    .add(employeeSalary.getTotalIncomeTaxDeduction())
                    .add(employeeSalary.getTotalOthers())
                    .add(employeeSalary.getTotalOtherDeduction());
            table.addCell(new Phrase(":" + advance_loan, FontFactory.getFont(FontFactory.COURIER, size)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Edu. Allow ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCTCData().getEducationalAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEducationalAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("P.T", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getProfessionalTax(), FontFactory.getFont(FontFactory.COURIER, size)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("MA", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCTCData().getMealsAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getMealsAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("TDS", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getTotalIncomeTaxDeduction(), FontFactory.getFont(FontFactory.COURIER, size)));

            BigDecimal otherDed = BigDecimal.ZERO;
            if (employeeSalary.getLateEntryLoss() != null) {
                otherDed = employeeSalary.getLateEntryLoss();
            }
            if (employeeSalary.getEarlyOutLoss() != null) {
                otherDed = employeeSalary.getEarlyOutLoss();
            }
            if (employeeSalary.getLateEntryLoss() != null && employeeSalary.getEarlyOutLoss() != null) {
                otherDed = employeeSalary.getLateEntryLoss().add(employeeSalary.getEarlyOutLoss());
            }

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("WA ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCTCData().getWashingAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getWashingAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Others", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("" +otherDed, FontFactory.getFont(FontFactory.COURIER, size)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Other Allow ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCTCData().getOtherAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getOtherAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));


            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Misc Allow ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCTCData().getMiscellaneousAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getMiscellaneousAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
//            table.getDefaultCell().setBorder(1);
//            table.addCell(new Phrase("Total", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("Rs " + employeeSalary.getTotalDeduction(), FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));


            //------------------------------------------------------set1----------------------------
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Mobile Allow", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":" + employeeSalary.getMobileAllowance(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);

            BigDecimal totEarning = BigDecimal.ZERO;
            BigDecimal bonus = employeeSalary.getBonus();
            BigDecimal others = BigDecimal.ZERO;
            if (employeeSalary.getOtPay() != null) {
                others = employeeSalary.getOtPay();
            }
            totEarning = employeeSalary.getGrossSalary().add(bonus).add(others);


            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Bonus", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("" + bonus, FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Others", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase(":", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("" + others, FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("|", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);

            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));

//            BigDecimal totDeduction = BigDecimal.ZERO;
//            totDeduction = employeeSalary.getTotalDeduction();
//            totDeduction = totDeduction.add(employeeSalary.getLateEntryLoss());
//            totDeduction = totDeduction.add(employeeSalary.getEarlyOutLoss());

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Total ", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("Rs " + employeeSalary.getEmployee().getEmployeeCTCData().getMonthlyCtc(), FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Rs " + totEarning, FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
//            table.addCell(new Phrase("" , FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("" , FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("" , FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Rs " + employeeSalary.getTotalDeduction().add(otherDed), FontFactory.getFont(FontFactory.COURIER_BOLD, size)));

//            table.getDefaultCell().setBorder(1);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.getDefaultCell().setBorder(0);
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));

            table.getDefaultCell().setBorderWidth(1);
            table.getDefaultCell().setBorder(PdfPCell.ANCHOR);
            table.addCell(new Phrase("Net Pay", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("Rs " + employeeSalary.getNetPaid(), FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));

            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            //------------------------------------------------------set1----------------------------

            //------------------------------------------------------set2----------------------------
            table.getDefaultCell().setBorder(1);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("Leave details", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);

//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("CL", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("EL/PL", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("ML/SL", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));


//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(Rectangle.BOX);
            table.addCell(new Phrase("Opening", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("" + employeeSalary.getOpeningCasualLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("" + employeeSalary.getOpeningEarnLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("" + employeeSalary.getOpeningMedicalLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));


//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(Rectangle.BOX);
            table.addCell(new Phrase("Availed", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("" + employeeSalary.getAvailCasualLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("" + employeeSalary.getAvailEarnLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("" + employeeSalary.getAvailMeadicalLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));

//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(Rectangle.BOX);
            table.addCell(new Phrase("Balance", FontFactory.getFont(FontFactory.COURIER_BOLD, size)));
            table.addCell(new Phrase("" + employeeSalary.getClosingCasualLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("" + employeeSalary.getClosingEarnLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("" + employeeSalary.getClosingMedicalLeave(), FontFactory.getFont(FontFactory.COURIER, size)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));

//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, size)));
            //------------------------------------------------------set2----------------------------

            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);


            para = new Paragraph("******** This is a computer-generated document. No signature is required ********", font1);

            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.close();

            //Email Config
            try {
                Session mailSession = Session.getInstance(System.getProperties());
                Transport transport = new SMTPTransport(mailSession, new URLName("smtp.gmail.com"));
                transport = mailSession.getTransport("smtps");
                transport.connect("smtp.gmail.com", 465, "orileotest@gmail.com", "yakanna@123");

                MimeMessage m = new MimeMessage(mailSession);
                m.setFrom(new InternetAddress("orileotest@gmail.com"));
                if (employeeSalary.getEmployee().getEmployeeLogin() == null) {
                    throw new EntityNotFoundException("Email id is not found for employee id: " + employeeSalary.getEmployee().getEmployeeCode());
                }
                Address[] toAddr = new InternetAddress[]{
//                        new InternetAddress(employeeSalary.getEmployee().getEmployeeLogin().getEmail())
                        new InternetAddress("varunvaru044@gmail.com")
                };
                m.setRecipients(Message.RecipientType.TO, toAddr);
                m.setHeader("Content-Type", "multipart/mixed");
                m.setSubject("Pay Slip for " + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName() + " for " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear());
                m.setSentDate(new Date());

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Dear " + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName() + ",\nPlease find the attached payslip for month of " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".\n \n\n\n\n *******" +
                        "THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL *******");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName("PaySlip-" + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".pdf");
                multipart.addBodyPart(messageBodyPart);
                m.setContent(multipart);
                transport.sendMessage(m, m.getAllRecipients());
                transport.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public String convertOt(String d) {
        String hrs = String.valueOf((long) (Double.parseDouble(d)));
        String min;
        String numberD = String.valueOf(d);
        numberD = numberD.substring(numberD.indexOf(".")).substring(1);
        System.out.println("numberD "+numberD +" and d"+ d );
        if (numberD.length()==1){
            numberD = numberD+"0";
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

//    @PostMapping("employee-salary-process/pay-slip/{empId}/{year}/{month}")
//    public Document sendSalaryPaySlipPDF(@PathVariable long empId, @PathVariable String year, @PathVariable String month, HttpServletResponse response) {
//        Optional<EmployeeSalaryProcess> employeeSalaryOpt = employeeSalaryProcessService.getEmployeeSalaryCreatedByMonth(empId, month, year);
//        EmployeeSalaryProcess employeeSalary = null;
//        if (employeeSalaryOpt.isPresent()) {
//            employeeSalary = employeeSalaryOpt.get();
//        } else {
//            throw new EntityNotFoundException("No data for employee " + empId);
//        }
//
//        Document document = new Document();
//        try {
//            Paragraph para = new Paragraph();
//
//            response.setContentType("application/pdf");
//            PdfWriter.getInstance(document, response.getOutputStream()); //to download
//            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //PDF to send email
//            PdfWriter writer = PdfWriter.getInstance(document, baos); // PDF to send email
//            document.open();
//            com.itextpdf.text.Font fontHeader = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
//            com.itextpdf.text.Font font1 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, size, Font.BOLD);
//
//            Image img1 = Image.getInstance("https://res.cloudinary.com/hanumanth-cloudinary/image/upload/v1557470801/weldomac_logo.jpg");
//            img1.scaleAbsolute(50f, 50f);
//            img1.scaleToFit(70f, 50f);
//            img1.setAbsolutePosition(90, 750);
//            document.add(img1);
//
//            para = new Paragraph("***** PVT LTD", fontHeader);
//            para.setAlignment(Element.ALIGN_CENTER);
//            document.add(para);
//            para = new Paragraph("-------------", font1);
//            para.setAlignment(Element.ALIGN_CENTER);
//            document.add(para);
//
//            para = new Paragraph("Pay-slip for the month of " + employeeSalary.getSalaryMonth() + " " + employeeSalary.getSalaryYear(), font1);
//            para.setAlignment(Element.ALIGN_CENTER);
//            document.add(para);
//            document.add(Chunk.NEWLINE);
//            PdfPTable table = new PdfPTable(6);
//            //TODO uc old pdf format
////            table.setWidths(new int[]{2, 3, 2, 1, 1, 2});
//            table.setWidths(new int[]{2, 2, 2, 2, 2, 2});
//            table.getDefaultCell().setBorder(0);
//            table.setWidthPercentage(100);
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Month:", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            PdfPCell pmonth = new PdfPCell(new Phrase("" + employeeSalary.getSalaryMonth(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            pmonth.setColspan(2);
//            pmonth.setBorder(Rectangle.NO_BORDER);
//            table.addCell(pmonth);
//
//            table.addCell(new Phrase("Emp Id:", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getEmployee().getEmployeeCode(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("PF No. :", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getEmployee().getPfNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            String level = "";
//            if (employeeSalary.getEmployee().getLevel()!=null){
//                employeeSalary.getEmployee().getLevel();
//            }
//            table.addCell(new Phrase("Grade:", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(""+level, FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Emp Id:", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Emp Id:", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Emp Id:", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Emp Id:", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.addCell(new Phrase("ESIC No", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEsiNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("UAN No", FontFactory.getFont(FontFactory.COURIER, 10)));
//            PdfPCell pdfPCell = new PdfPCell(new Phrase(":" + employeeSalary.getEmployee().getUanNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            pdfPCell.setColspan(2);
//            pdfPCell.setBorder(Rectangle.NO_BORDER);
//            table.addCell(pdfPCell);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setBorder(1);
//            table.addCell(new Phrase("Employee Name: ", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Payment Days", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(":" + employeeSalary.getPaymentDays(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setBorder(1);
//
//            table.addCell(new Phrase("Late Attn", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(":" + employeeSalary.getLateEntry(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("PH Holiday", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(":" + employeeSalary.getNoOfHolidays(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            BigDecimal noOfLeaves = employeeSalary.getAvailEarnLeave().add(employeeSalary.getAvailCasualLeave()).add(employeeSalary.getAvailMeadicalLeave());
//
//            table.getDefaultCell().setBorder(0);
//            table.addCell(new Phrase("Leave", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(":" + noOfLeaves, FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("SUNDAY", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(":" + employeeSalary.getSundays(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.addCell(new Phrase("Attendance", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(":" + employeeSalary.getNoOfPresent(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Working Days", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase(":" + employeeSalary.getWorkingDays(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setBorder(1);
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(new Phrase("Earnings ", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(new Phrase("Deductions", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setBorder(0);
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("Basic ", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getCurrentBasic(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("P.F", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getEpfContribution(), FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("Conveyance ", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getConveyanceAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("E.S.I.C", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getEmployeeEsicContribution(), FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("HRA ", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getHouseRentAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("Advance/Loan", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            BigDecimal advance_loan = employeeSalary.getTotalAdvanceDeduction()
//                    .add(employeeSalary.getTotalMealDeduction())
//                    .add(employeeSalary.getTotalIncomeTaxDeduction())
//                    .add(employeeSalary.getTotalOthers())
//                    .add(employeeSalary.getTotalOtherDeduction());
//            table.addCell(new Phrase("" + advance_loan, FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("Edu. Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getEducationalAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("P.T", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getProfessionalTax(), FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("MA", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getMealsAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("TDS", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getTotalIncomeTaxDeduction(), FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("WA ", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getWashingAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("Other Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getOtherAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            BigDecimal totDeduction = BigDecimal.ZERO;
//            totDeduction = totDeduction.add(employeeSalary.getEpfContribution());
//            totDeduction = totDeduction.add(employeeSalary.getProfessionalTax());
//            totDeduction = totDeduction.add(employeeSalary.getTotalAdvanceDeduction());
//            totDeduction = totDeduction.add(employeeSalary.getTotalIncomeTaxDeduction());
//
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("Misc Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getMiscellaneousAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setBorder(1);
//            table.addCell(new Phrase("Total", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Rs " + employeeSalary.getTotalDeduction(), FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.getDefaultCell().setBorder(0);
//            table.addCell(new Phrase("Mobile Allow", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(new Phrase("" + employeeSalary.getMobileAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setBorder(1);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("Leave", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setBorder(0);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(new Phrase("CL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            table.addCell(new Phrase("EL/PL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            table.addCell(new Phrase("ML/SL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//
//            BigDecimal totEarning = BigDecimal.ZERO;
//            totEarning = totEarning.add(employeeSalary.getCurrentBasic());
//            totEarning = totEarning.add(employeeSalary.getConveyanceAllowance());
//            totEarning = totEarning.add(employeeSalary.getHouseRentAllowance());
//            totEarning = totEarning.add(employeeSalary.getEducationalAllowance());
//            totEarning = totEarning.add(employeeSalary.getMealsAllowance());
//            totEarning = totEarning.add(employeeSalary.getWashingAllowance());
//            totEarning = totEarning.add(employeeSalary.getMobileAllowance());
//            totEarning = totEarning.add(employeeSalary.getOtherAllowance());
//            totEarning = totEarning.add(employeeSalary.getMiscellaneousAllowance());
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.getDefaultCell().setBorder(1);
//            table.addCell(new Phrase("Total ", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//            table.addCell(new Phrase("Rs " + totEarning, FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
//
//            table.getDefaultCell().setBorder(0);
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("Opening", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getOpeningCasualLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getOpeningEarnLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getOpeningMedicalLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setBorder(1);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setBorder(0);
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(new Phrase("Availed", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getAvailCasualLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getAvailEarnLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getAvailMeadicalLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            table.getDefaultCell().setBorderWidth(1);
//            table.getDefaultCell().setBorder(PdfPCell.ANCHOR);
//            table.addCell(new Phrase("Net Pay", FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));
//            table.addCell(new Phrase("Rs " + totEarning.subtract(totDeduction), FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));
//
//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.getDefaultCell().setBorderWidth(0);
//            table.addCell(new Phrase("Balance", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getClosingCasualLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getClosingEarnLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("" + employeeSalary.getClosingMedicalLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setBorderWidth(1);
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.getDefaultCell().setBorderWidth(0);
//
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
//
//            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
//            writer.setPageEvent(event);
//            document.add(table);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//            document.add(Chunk.NEWLINE);
//
//
//            para = new Paragraph("******** This is a computer-generated document. No signature is required ********", font1);
//
//            para.setAlignment(Element.ALIGN_CENTER);
//            document.add(para);
//            document.close();
//
//            //Email Config
//            try {
//                Session mailSession = Session.getInstance(System.getProperties());
//                Transport transport = new SMTPTransport(mailSession, new URLName("smtp.gmail.com"));
//                transport = mailSession.getTransport("smtps");
//                transport.connect("smtp.gmail.com", 465, "orileotest@gmail.com", "yakanna@123");
//
//                MimeMessage m = new MimeMessage(mailSession);
//                m.setFrom(new InternetAddress("orileotest@gmail.com"));
//                if (employeeSalary.getEmployee().getEmployeeLogin() == null) {
//                    throw new EntityNotFoundException("Email id is not found for employee id: " + employeeSalary.getEmployee().getEmployeeCode());
//                }
//                Address[] toAddr = new InternetAddress[]{
////                        new InternetAddress(employeeSalary.getEmployee().getEmployeeLogin().getEmail())
//                        new InternetAddress("varunvaru044@gmail.com")
//                };
//                m.setRecipients(Message.RecipientType.TO, toAddr);
//                m.setHeader("Content-Type", "multipart/mixed");
//                m.setSubject("Pay Slip for " + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName() + " for " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear());
//                m.setSentDate(new Date());
//
//                MimeBodyPart messageBodyPart = new MimeBodyPart();
//                messageBodyPart.setText("Dear " + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName() + ",\nPlease find the attached payslip for month of " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".\n \n\n\n\n *******" +
//                        "THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL *******");
//                Multipart multipart = new MimeMultipart();
//                multipart.addBodyPart(messageBodyPart);
//                messageBodyPart = new MimeBodyPart();
//                DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
//                messageBodyPart.setDataHandler(new DataHandler(source));
//                messageBodyPart.setFileName("PaySlip-" + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".pdf");
//                multipart.addBodyPart(messageBodyPart);
//                m.setContent(multipart);
//                transport.sendMessage(m, m.getAllRecipients());
//                transport.close();
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return document;
//    }
}
