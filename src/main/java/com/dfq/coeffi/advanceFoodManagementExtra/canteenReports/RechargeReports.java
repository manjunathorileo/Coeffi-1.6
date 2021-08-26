package com.dfq.coeffi.advanceFoodManagementExtra.canteenReports;

import com.dfq.coeffi.CanteenManagement.Entity.CatererSettingsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.EmployeeRecharge;
import com.dfq.coeffi.CanteenManagement.Service.CatererSettingsService;
import com.dfq.coeffi.CanteenManagement.Service.EmployeeRechargeService;
import com.dfq.coeffi.advanceFoodManagementExtra.EmployeeCanteenDetails;
import com.dfq.coeffi.advanceFoodManagementExtra.EmployeeCanteenDetailsService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.MonthlyStatusDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.foodManagement.FoodReportDto;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.*;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;
import static jxl.format.Alignment.LEFT;

@RestController
public class RechargeReports extends BaseController {
    @Autowired
    EmployeeRechargeService employeeRechargeService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeCanteenDetailsService employeeCanteenDetailsService;
    @Autowired
    CatererSettingsService catererSettingsService;

    @GetMapping("reports/employee/recharge/detailed")
    public ResponseEntity<List<Employee>> getEmployeeExport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<EmployeeRecharge> employeeRechargeDetailsDtos = getRechargeInfoFilter(dateDto);
        OutputStream out = null;
        String fileName = "Employee_Details";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRechargeDetailsDtos != null) {
                employeeRechargeInfoDetails(dateDto, workbook, employeeRechargeDetailsDtos, response, 0);
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

    private WritableWorkbook employeeRechargeInfoDetails(DateDto dateDto, WritableWorkbook workbook, List<EmployeeRecharge> employeeRechargeDetailsDtoList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Employee-Recharge-Details", index);
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

        s.mergeCells(0, 0, 5, 0);
        Label lable = new Label(0, 0, "Employee Recharge Information Report for the Period of" + " " + dateDto.startDate + " To " + dateDto.endDate, headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (EmployeeRecharge recharge : employeeRechargeDetailsDtoList) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + recharge.getEmployee().getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + recharge.getEmployee().getFirstName() + " " + recharge.getEmployee().getLastName(), cLeft));
            s.addCell(new Label(3, 1, "Employee Category", cellFormat));
            s.addCell(new Label(3, rowNum, "" + recharge.getEmployeeCategory(), cLeft));
            s.addCell(new Label(4, 1, "Date", cellFormat));
            s.addCell(new Label(4, rowNum, "" + recharge.getRechargeDate(), cLeft));
            s.addCell(new Label(4, 1, "Amount", cellFormat));
            s.addCell(new Label(4, rowNum, "" + recharge.getRechargeAmount(), cLeft));

        }
        rowNum = rowNum + 1;


        return workbook;
    }

    @GetMapping("reports/employee/recharge/total")
    public ResponseEntity<List<Employee>> getEmployeeTotalReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<EmployeeRecharge> employeeRechargeDetailsDtos = getRechargeInfoFilter(dateDto);
        OutputStream out = null;
        String fileName = "Employee_Details";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRechargeDetailsDtos != null) {
                employeeRechargeInfoDetailsTotal(dateDto, workbook, employeeRechargeDetailsDtos, response, 0);
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

    private WritableWorkbook employeeRechargeInfoDetailsTotal(DateDto dateDto, WritableWorkbook workbook, List<EmployeeRecharge> employeeRechargeDetailsDtoList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Employee-Recharge-Details", index);
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

        s.mergeCells(0, 0, 5, 0);
        Label lable = new Label(0, 0, "Employee Recharge Information Report for the Period of" + " " + dateDto.startDate + " To " + dateDto.endDate, headerFormat);
        s.addCell(lable);


        int rowNum = 2;

        for (EmployeeRecharge recharge : employeeRechargeDetailsDtoList) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + recharge.getEmployee().getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + recharge.getEmployee().getFirstName() + " " + recharge.getEmployee().getLastName(), cLeft));
            s.addCell(new Label(3, 1, "Joining Date", cellFormat));
            if (recharge.getEmployee().getDateOfJoining() != null) {
                s.addCell(new Label(3, rowNum, "" + recharge.getEmployee().getDateOfJoining(), cLeft));
            } else {
                s.addCell(new Label(3, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(4, 1, "Leaving Date", cellFormat));
            if (recharge.getEmployee().getDateOfLeaving() != null) {
                s.addCell(new Label(4, rowNum, "" + recharge.getEmployee().getDateOfLeaving(), cLeft));
            } else {
                s.addCell(new Label(4, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(5, 1, "Total Recharge Currency", cellFormat));
            s.addCell(new Label(5, rowNum, "" + recharge.getTotalRechargeAmount(), cLeft));


        }

        rowNum = rowNum + 1;


        return workbook;
    }


    public List<EmployeeRecharge> getRechargeInfoFilter(DateDto dateDto) {
        Date fromDate = DateUtil.mySqlFormatDate(dateDto.getStartDate());
        Date toDate = DateUtil.mySqlFormatDate(dateDto.getEndDate());
        List<EmployeeRecharge> employeeRechargeList = employeeRechargeService.getEmployeeRechargeByDate(fromDate, toDate);
        if (employeeRechargeList.isEmpty()) {
            throw new EntityNotFoundException("There is no data.");
        }
        Collections.reverse(employeeRechargeList);
        return employeeRechargeList;
    }


    public ResponseEntity<List<Employee>> downloadEmployeeFoodConsumptionReportAdvance(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        OutputStream out = null;
        String fileName = "Employee_Details";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());

        List<FoodReportDto> foodTrackers = foodConsumptionsFilter(dateDto);

        try {
            if (foodTrackers != null) {
                if (dateDto.getEmployeeType().equalsIgnoreCase("CONTRACT")) {
                    foodRechargeReportForContractAdvance(workbook, dateDto, foodTrackers, response, 0);
                } else {
                    foodConsumptionReportAdvance(workbook, dateDto, foodTrackers, response, 0);
                }
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


    private WritableWorkbook foodConsumptionReportAdvance(WritableWorkbook workbook, DateDto dateDto, List<FoodReportDto> foodTrackers, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Food-Consume-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 10);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 10);
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
        cLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

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
        s.setColumnView(13, 10);
        s.setColumnView(14, 10);
        s.setColumnView(15, 10);
        s.setColumnView(16, 10);
        s.setColumnView(17, 10);

        s.mergeCells(0, 0, 11, 0);
        Label lable = new Label(0, 0, "Food Consumption report " + "(" + dateDto.startDate + " - " + dateDto.endDate + ")", headerFormat);
        s.addCell(lable);
        s.addCell(new Label(0, 1, "SL No", cellFormat));
        s.addCell(new Label(1, 1, "Date", cellFormat));
        s.addCell(new Label(2, 1, "Employee_Type", cellFormat));
        s.addCell(new Label(3, 1, "BF Availed", cellFormat));
        s.addCell(new Label(4, 1, "BF Rate", cellFormat));
        s.addCell(new Label(5, 1, "Lunch Availed", cellFormat));
        s.addCell(new Label(6, 1, "Lunch Rate", cellFormat));
        s.addCell(new Label(7, 1, "Dinner Availed", cellFormat));
        s.addCell(new Label(8, 1, "Dinner Rate", cellFormat));
        s.addCell(new Label(9, 1, "Snacks Availed", cellFormat));
        s.addCell(new Label(10, 1, "Snacks Rate", cellFormat));
        s.addCell(new Label(11, 1, "Total", cellFormat));


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = simpleDateFormat.format(dateDto.startDate);
        String endDate = simpleDateFormat.format(dateDto.endDate);

        int rownum = 2;
        long grandTotal = 0;
        for (FoodReportDto foodTracker : foodTrackers) {
            grandTotal = grandTotal + foodTracker.getTotalToday();
            s.addCell(new Label(0, rownum, "" + (rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + foodTracker.getMarkedOn(), cLeft));
            s.addCell(new Label(2, rownum, "" + foodTracker.getEmployeeType(), cLeft));
            s.addCell(new Label(3, rownum, "" + foodTracker.getBfAvailed(), cLeft));
            s.addCell(new Label(4, rownum, "" + foodTracker.getBfRate(), cLeft));
            s.addCell(new Label(5, rownum, "" + foodTracker.getLunchAvailed(), cLeft));
            s.addCell(new Label(6, rownum, "" + foodTracker.getLunchRate(), cLeft));
            s.addCell(new Label(7, rownum, "" + foodTracker.getDinnerAvailed(), cLeft));
            s.addCell(new Label(8, rownum, "" + foodTracker.getDinnerRate(), cLeft));
            s.addCell(new Label(9, rownum, "" + foodTracker.getSnacksAvailed(), cLeft));
            s.addCell(new Label(10, rownum, "" + foodTracker.getSnacksRate(), cLeft));
            s.addCell(new Label(11, rownum, "" + foodTracker.getTotalToday(), cLeft));
            rownum++;
        }

        rownum = rownum + 2;
        s.addCell(new Label(11, rownum, "" + "GrandTotal", cellFormat));
        s.addCell(new Label(11, rownum + 1, "" + grandTotal, cLeft));

        return workbook;
    }

    private WritableWorkbook foodRechargeReportForContractAdvance(WritableWorkbook workbook, DateDto dateDto, List<FoodReportDto> foodTrackers, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Food-Consume-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 10);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 10);
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
        cLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

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
        s.setColumnView(13, 10);
        s.setColumnView(14, 10);
        s.setColumnView(15, 10);
        s.setColumnView(16, 10);
        s.setColumnView(17, 10);

        s.mergeCells(0, 0, 11, 0);
        Label lable = new Label(0, 0, "Food Consumption report " + "(" + dateDto.startDate + " - " + dateDto.endDate + ")", headerFormat);
        s.addCell(lable);
        s.addCell(new Label(0, 1, "SL No", cellFormat));
        s.addCell(new Label(1, 1, "Date", cellFormat));
        s.addCell(new Label(2, 1, "Employee_Type", cellFormat));
        s.addCell(new Label(3, 1, "BF Availed", cellFormat));
        s.addCell(new Label(4, 1, "BF Rate", cellFormat));
        s.addCell(new Label(5, 1, "Lunch Availed", cellFormat));
        s.addCell(new Label(6, 1, "Lunch Rate", cellFormat));
        s.addCell(new Label(7, 1, "Dinner Availed", cellFormat));
        s.addCell(new Label(8, 1, "Dinner Rate", cellFormat));
        s.addCell(new Label(9, 1, "Snacks Availed", cellFormat));
        s.addCell(new Label(10, 1, "Snacks Rate", cellFormat));
        s.addCell(new Label(11, 1, "Opening balance", cellFormat));
        s.addCell(new Label(12, 1, "Debited/availed", cellFormat));
        s.addCell(new Label(13, 1, "Credit/Recharge", cellFormat));
        s.addCell(new Label(14, 1, "Closing balance", cellFormat));


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = simpleDateFormat.format(dateDto.startDate);
        String endDate = simpleDateFormat.format(dateDto.endDate);

        int rownum = 2;
        long grandTotal = 0;
        for (FoodReportDto foodTracker : foodTrackers) {
            grandTotal = grandTotal + foodTracker.getTotalToday();
            s.addCell(new Label(0, rownum, "" + (rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + foodTracker.getMarkedOn(), cLeft));
            s.addCell(new Label(2, rownum, "" + foodTracker.getEmployeeType(), cLeft));
            s.addCell(new Label(3, rownum, "" + foodTracker.getBfAvailed(), cLeft));
            s.addCell(new Label(4, rownum, "" + foodTracker.getBfRate(), cLeft));
            s.addCell(new Label(5, rownum, "" + foodTracker.getLunchAvailed(), cLeft));
            s.addCell(new Label(6, rownum, "" + foodTracker.getLunchRate(), cLeft));
            s.addCell(new Label(7, rownum, "" + foodTracker.getDinnerAvailed(), cLeft));
            s.addCell(new Label(8, rownum, "" + foodTracker.getDinnerRate(), cLeft));
            s.addCell(new Label(9, rownum, "" + foodTracker.getSnacksAvailed(), cLeft));
            s.addCell(new Label(10, rownum, "" + foodTracker.getSnacksRate(), cLeft));
            s.addCell(new Label(11, rownum, "" + foodTracker.getOpeningBalance(), cLeft));
            s.addCell(new Label(12, rownum, "" + foodTracker.getTotalToday(), cLeft));
            s.addCell(new Label(13, rownum, "" + foodTracker.getCredited(), cLeft));
            s.addCell(new Label(14, rownum, "" + foodTracker.getClosingBalance(), cLeft));
            rownum++;
        }

        rownum = rownum + 2;
        s.addCell(new Label(11, rownum, "" + "GrandTotal", cellFormat));
        s.addCell(new Label(11, rownum + 1, "" + grandTotal, cLeft));

        return workbook;
    }


    //TODO IMP for canteen

    public List<FoodReportDto> foodConsumptionsFilter(DateDto dateDto) throws ParseException {
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
        List<FoodReportDto> foodTrackerDtoList = new ArrayList<>();
        List<Employee> employeeList = employeeService.findAll();
        for (Employee employee : employeeList) {
            FoodReportDto foodReportDtoEmp = new FoodReportDto();
            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<>();
            long bfAvailedEmp = 0;
            long lunchAvailedEmp = 0;
            long snacksAvailedEmp = 0;
            long dinnerAvailedEmp = 0;

            for (Date date : dates) {
                MonthlyStatusDto monthlyStatusDto = new MonthlyStatusDto();
                long singleBfEmp = 0;
                long singlelunchEmp = 0;
                long singleSnaksEmp = 0;
                long singledinnerEmp = 0;

                foodReportDtoEmp.setEmployeeType(dateDto.getEmployeeType());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date mk = sdf.parse(sdf.format(date));
                System.out.println("trackSize----" + mk + " -- " + employee.getEmployeeCode());
                List<EmployeeCanteenDetails> foodTrackerEmp = employeeCanteenDetailsService.findByMarkedOnAndEmployeeCode(mk, employee.getEmployeeCode());
                foodReportDtoEmp.setOpeningBalance(foodTrackerEmp.get(0).getOpeningBalance());
                Collections.reverse(foodTrackerEmp);
                foodReportDtoEmp.setClosingBalance(foodTrackerEmp.get(0).getClosingBalance());
                Collections.reverse(foodTrackerEmp);
                System.out.println("trackSize----" + foodTrackerEmp.size());
                for (EmployeeCanteenDetails foodTracker : foodTrackerEmp) {
                    System.out.println("A");
                    if (foodTracker.getFoodTypeName().equalsIgnoreCase("BREAK_FAST")) {
                        System.out.println("B");
                        CatererSettingsAdv catererSettings = catererSettingsService.getCatererSetting(foodTracker.getFoodTypeName(), foodTracker.getEmployeeType());
                        foodReportDtoEmp.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        bfAvailedEmp++;
                        singleBfEmp++;
                    }
                    if (foodTracker.getFoodTypeName().equalsIgnoreCase("LUNCH")) {
                        System.out.println("C");
                        CatererSettingsAdv catererSettings = catererSettingsService.getCatererSetting(foodTracker.getFoodTypeName(), foodTracker.getEmployeeType());
                        foodReportDtoEmp.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        lunchAvailedEmp++;
                        singlelunchEmp++;
                    }
                    if (foodTracker.getFoodTypeName().equalsIgnoreCase("SNACK")) {
                        System.out.println("D");
                        CatererSettingsAdv catererSettings = catererSettingsService.getCatererSetting(foodTracker.getFoodTypeName(), foodTracker.getEmployeeType());
                        foodReportDtoEmp.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        snacksAvailedEmp++;
                        singleSnaksEmp++;
                    }
                    if (foodTracker.getFoodTypeName().equalsIgnoreCase("DINNER")) {
                        System.out.println("E");
                        CatererSettingsAdv catererSettings = catererSettingsService.getCatererSetting(foodTracker.getFoodTypeName(), foodTracker.getEmployeeType());
                        foodReportDtoEmp.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        dinnerAvailedEmp++;
                        singledinnerEmp++;
                    }

                }
                monthlyStatusDto.setSingleEntryBf(singleBfEmp);
                monthlyStatusDto.setSingleEntryLaunch(singlelunchEmp);
                monthlyStatusDto.setSingleEntrySnaks(singleSnaksEmp);
                monthlyStatusDto.setSingleEntryDinner(singledinnerEmp);
                monthlyStatusDto.setMarkedOn(date);
                monthlyStatusDtos.add(monthlyStatusDto);

            }
            foodReportDtoEmp.setMonthlyStatusDtos(monthlyStatusDtos);
            foodReportDtoEmp.setEmployeeId(employee.getId());
            foodReportDtoEmp.setEmployeeName(employee.getFirstName());
            if (employee.getDesignation() != null) {
                foodReportDtoEmp.setDesignation(employee.getDesignation().getName());
            }
            if (employee.getDepartmentName() != null) {
                foodReportDtoEmp.setDepartment(employee.getDepartmentName());
            }
            if (employee.getDepartment() != null) {
                foodReportDtoEmp.setDepartment(employee.getDepartment().getName());
            }
            foodReportDtoEmp.setEmployeeCode(employee.getEmployeeCode());
            foodReportDtoEmp.setBfAvailed(bfAvailedEmp);
            foodReportDtoEmp.setLunchAvailed(lunchAvailedEmp);
            foodReportDtoEmp.setEmployeeType(dateDto.getEmployeeType());
            foodReportDtoEmp.setSnacksAvailed(snacksAvailedEmp);
            foodReportDtoEmp.setDinnerAvailed(dinnerAvailedEmp);
            foodReportDtoEmp.setTotalToday((foodReportDtoEmp.getBfRate() * bfAvailedEmp) + (foodReportDtoEmp.getSnacksRate() * snacksAvailedEmp) + (foodReportDtoEmp.getLunchRate() * lunchAvailedEmp) + (foodReportDtoEmp.getDinnerRate() * dinnerAvailedEmp));
            foodTrackerDtoList.add(foodReportDtoEmp);

        }
        return foodTrackerDtoList;
    }

//    @PostMapping("recharge-reports/food-punch-logs-adv") --
//    all emptype
//    @PostMapping("recharge-reports/food-consumption-adv") -
//    all emptype
//    @PostMapping("recharge-reports/caterer-adv") -
//    all emptype
//    @PostMapping("recharge-reports/employee-recharge-adv") -contract
//    @PostMapping("recharge-reports/employee-total-recharge-adv") -contract
}
