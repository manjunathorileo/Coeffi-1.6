package com.dfq.coeffi.foodManagement;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.MonthlyStatusDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;
import static jxl.format.Alignment.LEFT;

@RestController
@Slf4j
public class FoodConsumeReport extends BaseController {

    @Autowired
    FoodReportArun foodReportArun;
    @Autowired
    FoodTrackerRepository foodTrackerRepository;
    @Autowired
    CanteenService canteenService;

    @PostMapping("food-tracker/food-summary-consume")
    public void todaysFoodConsumptionDate(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        //TODO fc report
        List<FoodReportDto> foodTrackers = null;
        if (dateDto.getCompanyName() != null && dateDto.getLocationName() != null && dateDto.getEmployeeType() != null) {
            foodTrackers = foodReportArun.foodConsumptionsFilter(dateDto);
        } else {
            foodTrackers = foodReportArun.foodConsumptions(dateDto);
        }

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            foodConsumeReport(workbook, dateDto, foodTrackers, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook foodConsumeReport(WritableWorkbook workbook, DateDto dateDto, List<FoodReportDto> foodTrackers, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Food-Consume-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 7);
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
        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
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
        cLeft.setBorder(Border.ALL, BorderLineStyle.THIN);

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
        s.addCell(new Label(1, 1, "From to To", cellFormat));
        s.addCell(new Label(2, 1, "Employee_Id", cellFormat));
        s.addCell(new Label(3, 1, "Name", cellFormat));
        s.addCell(new Label(4, 1, "Employee_Type", cellFormat));

        s.addCell(new Label(5, 1, "Company", cellFormat));
        s.addCell(new Label(6, 1, "Location", cellFormat));
        s.addCell(new Label(7, 1, "Department", cellFormat));

        s.addCell(new Label(8, 1, "BF Availed", cellFormat));
        s.addCell(new Label(9, 1, "BF Rate", cellFormat));
        s.addCell(new Label(10, 1, "Lunch Availed", cellFormat));
        s.addCell(new Label(11, 1, "Lunch Rate", cellFormat));
        s.addCell(new Label(12, 1, "Dinner Availed", cellFormat));
        s.addCell(new Label(13, 1, "Dinner Rate", cellFormat));
        s.addCell(new Label(14, 1, "Snacks Availed", cellFormat));
        s.addCell(new Label(15, 1, "Snacks Rate", cellFormat));
        s.addCell(new Label(16, 1, "Midnight Snacks Availed", cellFormat));
        s.addCell(new Label(17, 1, "Midnight Snacks Rate", cellFormat));
        s.addCell(new Label(18, 1, "Total", cellFormat));


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = simpleDateFormat.format(dateDto.startDate);
        String endDate = simpleDateFormat.format(dateDto.endDate);

        int rownum = 2;
        long grandTotal = 0;
        for (FoodReportDto foodTracker : foodTrackers) {
            grandTotal = grandTotal + foodTracker.getTotalToday();
            String company = "";
            String location = "";
            String department = "";
            if (foodTracker.getCompany() != null) {
                company = foodTracker.getCompany();
            }
            if (foodTracker.getDepartment() != null) {
                department = foodTracker.getDepartment();
            }
            if (foodTracker.getLocation() != null) {
                location = foodTracker.getLocation();
            }
            s.addCell(new Label(0, rownum, "" + (rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + startDate + " to " + endDate, cLeft));
            s.addCell(new Label(2, rownum, "" + foodTracker.getEmployeeCode(), cLeft));
            s.addCell(new Label(3, rownum, "" + foodTracker.getEmployeeName(), cLeft));
            s.addCell(new Label(4, rownum, "" + foodTracker.getEmployeeType(), cLeft));

            s.addCell(new Label(5, rownum, "" + company, cLeft));
            s.addCell(new Label(6, rownum, "" + location, cLeft));
            s.addCell(new Label(7, rownum, "" + department, cLeft));

            s.addCell(new Label(8, rownum, "" + foodTracker.getBfAvailed(), cLeft));
            s.addCell(new Label(9, rownum, "" + foodTracker.getBfRate(), cLeft));
            s.addCell(new Label(10, rownum, "" + foodTracker.getLunchAvailed(), cLeft));
            s.addCell(new Label(11, rownum, "" + foodTracker.getLunchRate(), cLeft));
            s.addCell(new Label(12, rownum, "" + foodTracker.getDinnerAvailed(), cLeft));
            s.addCell(new Label(13, rownum, "" + foodTracker.getDinnerRate(), cLeft));
            s.addCell(new Label(14, rownum, "" + foodTracker.getSnacksAvailed(), cLeft));
            s.addCell(new Label(15, rownum, "" + foodTracker.getSnacksRate(), cLeft));
            s.addCell(new Label(16, rownum, "" + foodTracker.getMidnightSnackAvailed(), cLeft));
            s.addCell(new Label(17, rownum, "" + foodTracker.getMidnightSnackRate(), cLeft));
            s.addCell(new Label(18, rownum, "" + foodTracker.getTotalToday(), cLeft));
            rownum++;
        }

        rownum = rownum + 2;
        s.addCell(new Label(19, rownum, "" + "GrandTotal", cellFormat));
        s.addCell(new Label(19, rownum + 1, "" + grandTotal, cLeft));

        return workbook;
    }
}
