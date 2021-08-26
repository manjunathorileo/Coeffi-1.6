package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.StoreManagement.Entity.EmployeeRequest;
import com.dfq.coeffi.StoreManagement.Entity.Materials;
import com.dfq.coeffi.StoreManagement.Repository.EmployeeRequestRepository;
import com.dfq.coeffi.controller.BaseController;
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
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;
import static jxl.format.Alignment.RIGHT;

@RestController
public class FoodController extends BaseController {

    private WritableWorkbook catererReport(WritableWorkbook workbook, List<EmployeeRequest> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Caterer-Report", index);
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
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 10);
        s.setColumnView(7, 10);
        s.setColumnView(8, 10);


        int rowNum = 1;
        for (EmployeeRequest  employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "SL No", cellFormat));
            s.addCell(new Label(1, 0, "Date From", cellFormat));
            s.addCell(new Label(2, 0, "Date To", cellFormat));
            s.addCell(new Label(3, 0, "Caterer", cellFormat));
            s.addCell(new Label(4, 0, "Type", cellFormat));
            s.addCell(new Label(5, 0, "Food Type", cellFormat));
            s.addCell(new Label(6, 0, "Food Rate", cellFormat));
            s.addCell(new Label(7, 0, "Availed", cellFormat));
            s.addCell(new Label(8, 0, "Total", cellFormat));

            }

            rowNum = rowNum  + 1;

        return workbook;
    }

    private WritableWorkbook foodConsumeReport(WritableWorkbook workbook, List<EmployeeRequest> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Food-Consume-Report", index);
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


        int rowNum = 1;
        for (EmployeeRequest  employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "SL No", cellFormat));
            s.addCell(new Label(1, 0, "Date From", cellFormat));
            s.addCell(new Label(2, 0, "Date To", cellFormat));
            s.addCell(new Label(3, 0, "Employee Id", cellFormat));
            s.addCell(new Label(4, 0, "Name", cellFormat));
            s.addCell(new Label(5, 0, "Company", cellFormat));
            s.addCell(new Label(6, 0, "Location", cellFormat));
            s.addCell(new Label(7, 0, "Department", cellFormat));
            s.addCell(new Label(8, 0, "Employee Type", cellFormat));
            s.addCell(new Label(9, 0, "BF Availed", cellFormat));
            s.addCell(new Label(10, 0, "BF Rate", cellFormat));
            s.addCell(new Label(11, 0, "Lunch Availed", cellFormat));
            s.addCell(new Label(12, 0, "Lunch Rate", cellFormat));
            s.addCell(new Label(13, 0, "Dinner Availed", cellFormat));
            s.addCell(new Label(14, 0, "Dinner Rate", cellFormat));
            s.addCell(new Label(15, 0, "Snacks Availed", cellFormat));
            s.addCell(new Label(16, 0, "Snacks Rate", cellFormat));
            s.addCell(new Label(17, 0, "Total", cellFormat));

        }

        rowNum = rowNum  + 1;

        return workbook;
    }

    private WritableWorkbook foodAvailed(WritableWorkbook workbook, List<EmployeeRequest> employeeRequestList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Food-Availed-Report", index);
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


        int rowNum = 1;
        for (EmployeeRequest  employeeRequest : employeeRequestList) {
            s.addCell(new Label(0, 0, "SL No", cellFormat));
            s.addCell(new Label(1, 0, "Date From", cellFormat));
            s.addCell(new Label(2, 0, "Date To", cellFormat));
            s.addCell(new Label(3, 0, "Employee Id", cellFormat));
            s.addCell(new Label(4, 0, "Name", cellFormat));
            s.addCell(new Label(5, 0, "Company", cellFormat));
            s.addCell(new Label(6, 0, "Location", cellFormat));
            s.addCell(new Label(7, 0, "Department", cellFormat));
            s.addCell(new Label(8, 0, "Employee Type", cellFormat));
            s.addCell(new Label(9, 0, "Food Type", cellFormat));
            s.addCell(new Label(10, 0, "Food Rate", cellFormat));
            s.addCell(new Label(11, 0, "Food Availed", cellFormat));
            s.addCell(new Label(12, 0, "Total", cellFormat));


        }

        rowNum = rowNum  + 1;

        return workbook;
    }




}
