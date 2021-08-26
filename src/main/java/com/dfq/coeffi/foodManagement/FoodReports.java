package com.dfq.coeffi.foodManagement;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;

@RestController
@Slf4j
public class FoodReports extends BaseController {
    @Autowired
    FoodTrackerRepository foodTrackerRepository;
    @Autowired
    CanteenService canteenService;
    @Autowired
    FoodTimeRepository foodTimeRepository;

    //    @GetMapping("food-tracker/view-todays-report")
    public ResponseEntity<List<FoodTrackerDto>> todaysScreenView() {
        List<FoodTrackerDto> foodTrackerDtos = new ArrayList<>();
        List<FoodTimeMaster> foodTimeMasters = canteenService.getFoodTimes();
        for (FoodTimeMaster foodTimeMaster : foodTimeMasters) {
            FoodTrackerDto foodTrackerDto = new FoodTrackerDto();
            List<FoodTracker> foodTrackersEmp = foodTrackerRepository.findByMarkedOnAndEmployeeTypeAndFoodType(new Date(), "EMPLOYEE", foodTimeMaster.getFoodType());
            List<FoodTracker> foodTrackersVis = foodTrackerRepository.findByMarkedOnAndEmployeeTypeAndFoodType(new Date(), "VISITOR", foodTimeMaster.getFoodType());
            List<FoodTracker> foodTrackersCont = foodTrackerRepository.findByMarkedOnAndEmployeeTypeAndFoodType(new Date(), "CONTRACT", foodTimeMaster.getFoodType());

            foodTrackerDto.setFoodType(foodTimeMaster.getFoodType());
            foodTrackerDto.setEmployeeCount(foodTrackersEmp.size());
            foodTrackerDto.setVisitorCount(foodTrackersVis.size());
            foodTrackerDto.setContractCount(foodTrackersCont.size());
            foodTrackerDtos.add(foodTrackerDto);
        }
        return new ResponseEntity<>(foodTrackerDtos, HttpStatus.OK);

    }

    //    @GetMapping("food-tracker/food-punch-log")
    public void todaysPunchLogReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<FoodTracker> foodTrackers = foodTrackerRepository.findByMarkedOn(new Date());
        DateDto dateDto = new DateDto();
        dateDto.setStartDate(new Date());
        dateDto.setEndDate(new Date());
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            foodPunchLog(workbook, dateDto, foodTrackers, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            out.close();
        }
    }

    //    @GetMapping("food-tracker/food-consumption-report")
    public void todaysFoodConsumption(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO fc report
        DateDto dateDto = new DateDto();
        dateDto.setStartDate(new Date());
        dateDto.setEndDate(new Date());
        List<FoodReportDto> foodTrackers = foodConsumptions(dateDto);
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
            out.close();
        }
    }

    //    @GetMapping("food-tracker/caterer-report")
    public void todaysCaterer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO fc report

        DateDto dateDto = new DateDto();
        dateDto.setStartDate(new Date());
        dateDto.setEndDate(new Date());
        List<FoodReportDto> foodTrackers = foodCatererFun(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            catererReport(workbook, dateDto, foodTrackers, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            out.close();
        }
    }

    private WritableWorkbook foodPunchLog(WritableWorkbook workbook, DateDto dateDto, List<FoodTracker> foodTrackers, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Punch logs", index);
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
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.setColumnView(6, 10);
        s.setColumnView(7, 10);
        s.setColumnView(8, 10);
        s.mergeCells(0, 0, 11, 0);
        Label lable = new Label(0, 0, "Food punch logs " + "(" + dateDto.startDate + " - " + dateDto.endDate + ")", headerFormat);
        s.addCell(lable);
        s.addCell(new Label(0, 1, "SL No", cellFormat));
        s.addCell(new Label(1, 1, "Date", cellFormat));
        s.addCell(new Label(2, 1, "Caterer", cellFormat));
        s.addCell(new Label(3, 1, "Type", cellFormat));

        s.addCell(new Label(4, 1, "EmployeeId", cellFormat));
        s.addCell(new Label(5, 1, "Name", cellFormat));
        s.addCell(new Label(6, 1, "Company", cellFormat));
        s.addCell(new Label(7, 1, "Location", cellFormat));

        s.addCell(new Label(8, 1, "Food Type", cellFormat));
        s.addCell(new Label(9, 1, "Food Rate", cellFormat));
        s.addCell(new Label(10, 1, "Availed", cellFormat));
        s.addCell(new Label(11, 1, "Total", cellFormat));

        int rownum = 2;
        long grandTotal = 0;
        for (FoodTracker foodTracker : foodTrackers) {
            CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
            long foodRate = catererSettings.getEmployerRate() + catererSettings.getEmployeeRate();
            grandTotal = grandTotal + foodRate;
            s.addCell(new Label(0, rownum, "" + (rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + foodTracker.getMarkedOn(), cLeft));
            s.addCell(new Label(2, rownum, "" + foodTracker.getCaterer(), cLeft));
            s.addCell(new Label(3, rownum, "" + foodTracker.getEmployeeType(), cLeft));

            s.addCell(new Label(4, rownum, "" + foodTracker.getEmployeeCode(), cLeft));
            s.addCell(new Label(5, rownum, "" + foodTracker.getEmployeeName(), cLeft));
            s.addCell(new Label(6, rownum, "" + foodTracker.getCompanyName(), cLeft));
            s.addCell(new Label(7, rownum, "" + foodTracker.getLocationName(), cLeft));

            s.addCell(new Label(8, rownum, "" + foodTracker.getFoodType(), cLeft));
            s.addCell(new Label(9, rownum, "" + foodRate, cLeft));
            s.addCell(new Label(10, rownum, "" + "1", cLeft));
            s.addCell(new Label(11, rownum, "" + foodRate, cLeft));
            rownum++;
        }

        rownum = rownum + 2;
        s.addCell(new Label(11, rownum, "" + "GrandTotal", cellFormat));
        s.addCell(new Label(11, rownum + 1, "" + grandTotal, cLeft));


        return workbook;
    }


    private WritableWorkbook foodConsumeReport(WritableWorkbook workbook, DateDto dateDto, List<FoodReportDto> foodTrackers, HttpServletResponse response, int index) throws IOException, WriteException {
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

    private WritableWorkbook catererReport(WritableWorkbook workbook, DateDto dateDto, List<FoodReportDto> foodTrackers, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Caterer report", index);
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
        Label lable = new Label(0, 0, "Caterer Report " + "(" + dateDto.startDate + " - " + dateDto.endDate + ")", headerFormat);
        s.addCell(lable);
        s.addCell(new Label(0, 1, "SL No", cellFormat));
        s.addCell(new Label(1, 1, "Date From", cellFormat));
        s.addCell(new Label(2, 1, "Date To", cellFormat));
        s.addCell(new Label(3, 1, "Caterer", cellFormat));
        s.addCell(new Label(4, 1, "Employee Type", cellFormat));
        s.addCell(new Label(5, 1, "Food Type", cellFormat));
        s.addCell(new Label(6, 1, "Food Rate", cellFormat));
        s.addCell(new Label(7, 1, "Availed", cellFormat));
        s.addCell(new Label(8, 1, "Total", cellFormat));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = simpleDateFormat.format(dateDto.startDate);
        String endDate = simpleDateFormat.format(dateDto.endDate);

        int rownum = 2;
        long grandTotal = 0;
        for (FoodReportDto foodTracker : foodTrackers) {
            grandTotal = grandTotal + foodTracker.getTotal();
            s.addCell(new Label(0, rownum, "" + (rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + startDate, cLeft));
            s.addCell(new Label(2, rownum, "" + endDate, cLeft));
            s.addCell(new Label(3, rownum, "" + "-", cLeft));
            s.addCell(new Label(4, rownum, "" + foodTracker.getEmployeeType(), cLeft));
            s.addCell(new Label(5, rownum, "" + foodTracker.getFoodType(), cLeft));
            s.addCell(new Label(6, rownum, "" + foodTracker.getFoodRate(), cLeft));
            s.addCell(new Label(7, rownum, "" + foodTracker.getAvailed(), cLeft));
            s.addCell(new Label(8, rownum, "" + foodTracker.getTotal(), cLeft));
            rownum++;
        }
        rownum = rownum + 2;
        s.addCell(new Label(8, rownum, "" + "GrandTotal", cellFormat));
        s.addCell(new Label(8, rownum + 1, "" + grandTotal, cLeft));
        return workbook;
    }


    //    @PostMapping("food-tracker/food-punch-log")
    public void todaysPunchLogReportDate(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<FoodTracker> foodTrackers = foodPunchLogs(dateDto);
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            foodPunchLog(workbook, dateDto, foodTrackers, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            out.close();
        }
    }

//    @PostMapping("food-tracker/food-summary-consume")
//    public void todaysFoodConsumptionDate(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        //TODO fc report
//        List<FoodReportDto> foodTrackers = null;
//        /*if (dateDto.getCompanyName() != "" && dateDto.getLocationName() != "" && dateDto.getDepartmentName() != "" && dateDto.getEmployeeType() != "") {
//            foodTrackers = foodConsumptionsFilter(dateDto);
//        } else*/
//        if (dateDto.getCompanyName() != null && dateDto.getLocationName() != null && dateDto.getDepartmentName() != null && dateDto.getEmployeeType() != null) {
//            foodTrackers = foodConsumptionsFilter(dateDto);
//        } else {
//            foodTrackers = foodConsumptions(dateDto);
//        }
//
//        OutputStream out = null;
//        response.setContentType("application/vnd.ms-excel");
//        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
//        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
//        try {
//            foodConsumeReport(workbook, dateDto, foodTrackers, response, 0);
//            workbook.write();
//            workbook.close();
//        } catch (Exception e) {
//            throw new ServletException("Exception in excel download", e);
//        } finally {
//            if (out != null)
//                out.close();
//        }
//    }

    //    @PostMapping("food-tracker/caterer-report")
    public void todaysCatererDate(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO fc report
        List<FoodReportDto> foodTrackers = null;
        if (dateDto.getCompanyName() != "" && dateDto.getLocationName() != "" && dateDto.getDepartmentName() != "" && dateDto.getEmployeeType() != "" && dateDto.getFoodType() != "") {
            foodTrackers = foodCatererFunFilter(dateDto);
        } else {
            foodTrackers = foodCatererFun(dateDto);
        }
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            catererReport(workbook, dateDto, foodTrackers, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            out.close();
        }
    }

    public List<FoodTracker> foodPunchLogs(DateDto dateDto) {
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
        List<FoodTracker> foodTrackers = new ArrayList<>();
        for (Date date : dates) {
            List<FoodTracker> foodTrackersDate = foodTrackerRepository.findByMarkedOn(date);
            for (FoodTracker foodTracker : foodTrackersDate) {
                System.out.println("com: " + dateDto.getCompanyName());
                if (dateDto.getEmployeeCode() != "") {
                    if (foodTracker.getEmployeeCode().equalsIgnoreCase(dateDto.getEmployeeCode())) {
                        foodTrackers.add(foodTracker);
                    }
                } else if (dateDto.getCompanyName() != "" &&
                        dateDto.getLocationName() != "" &&
                        dateDto.getDepartmentName() != "" &&
                        dateDto.getEmployeeType() != "" &&
                        dateDto.getFoodType() != "") {
                    if (foodTracker.getLocationName().equalsIgnoreCase(dateDto.getLocationName()) &&
                            foodTracker.getCompanyName().equalsIgnoreCase(dateDto.getCompanyName()) &&
                            foodTracker.getDepartmentName().equalsIgnoreCase(dateDto.getDepartmentName()) &&
                            foodTracker.getEmployeeType().equalsIgnoreCase(dateDto.getEmployeeType()) &&
                            foodTracker.getFoodType().equalsIgnoreCase(dateDto.getFoodType())) {
                        foodTrackers.add(foodTracker);
                    }
                } else {
                    foodTrackers.add(foodTracker);
                }
            }
        }
        return foodTrackers;
    }

    public List<FoodReportDto> foodConsumptions(DateDto dateDto) {
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
        System.out.println(dates.size());
        List<FoodReportDto> foodTrackerDtoList = new ArrayList<>();
        for (Date date : dates) {
            System.out.println("dates " + date);

            List<FoodTracker> foodTrackerEmp = foodTrackerRepository.findByMarkedOnAndEmployeeType(date, "EMPLOYEE");
            System.out.println("FempSize: " + foodTrackerEmp.size());
            List<FoodTracker> foodTrackerVis = foodTrackerRepository.findByMarkedOnAndEmployeeType(date, "VISITOR");
            List<FoodTracker> foodTrackerCont = foodTrackerRepository.findByMarkedOnAndEmployeeType(date, "CONTRACT");

            FoodReportDto foodReportDtoEmp = new FoodReportDto();
            FoodReportDto foodReportDtoVis = new FoodReportDto();
            FoodReportDto foodReportDtoCont = new FoodReportDto();

            long bfAvailedEmp = 0;
            long lunchAvailedEmp = 0;
            long snacksAvailedEmp = 0;
            long dinnerAvailedEmp = 0;

            long bfAvailedVis = 0;
            long lunchAvailedVis = 0;
            long snacksAvailedVis = 0;
            long dinnerAvailedVis = 0;

            long bfAvailedCon = 0;
            long lunchAvailedCon = 0;
            long snacksAvailedCon = 0;
            long dinnerAvailedCon = 0;


            foodReportDtoEmp.setEmployeeType("EMPLOYEE");
            foodReportDtoVis.setEmployeeType("VISITOR");
            foodReportDtoCont.setEmployeeType("CONTRACT");

            for (FoodTracker foodTracker : foodTrackerEmp) {
                if (foodTracker.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("BREAK_FAST", "EMPLOYEE");
                    foodReportDtoEmp.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    bfAvailedEmp++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("LUNCH")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("LUNCH", "EMPLOYEE");
                    foodReportDtoEmp.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    lunchAvailedEmp++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("SNACK")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("SNACK", "EMPLOYEE");
                    System.out.println("CAte--" + catererSettings);
                    foodReportDtoEmp.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    snacksAvailedEmp++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("DINNER")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("DINNER", "EMPLOYEE");
                    foodReportDtoEmp.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    dinnerAvailedEmp++;
                }

            }
            foodReportDtoEmp.setBfAvailed(bfAvailedEmp);
            foodReportDtoEmp.setLunchAvailed(lunchAvailedEmp);
            foodReportDtoEmp.setMarkedOn(date);

            foodReportDtoEmp.setEmployeeType("EMPLOYEE");
            foodReportDtoEmp.setSnacksAvailed(snacksAvailedEmp);
            foodReportDtoEmp.setDinnerAvailed(dinnerAvailedEmp);
            foodReportDtoEmp.setTotalToday((foodReportDtoEmp.getBfRate() * bfAvailedEmp) + (foodReportDtoEmp.getSnacksRate() * snacksAvailedEmp) + (foodReportDtoEmp.getLunchRate() * lunchAvailedEmp) + (foodReportDtoEmp.getDinnerRate() * dinnerAvailedEmp));


            for (FoodTracker foodTracker : foodTrackerVis) {
                if (foodTracker.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("BREAK_FAST", "VISITOR");
                    foodReportDtoVis.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    bfAvailedVis++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("LUNCH")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("LUNCH", "VISITOR");
                    foodReportDtoVis.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    lunchAvailedVis++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("SNACK")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("SNACK", "VISITOR");
                    foodReportDtoVis.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    snacksAvailedVis++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("DINNER")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("DINNER", "VISITOR");
                    foodReportDtoVis.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    dinnerAvailedVis++;
                }
            }
            foodReportDtoVis.setBfAvailed(bfAvailedVis);
            foodReportDtoVis.setLunchAvailed(lunchAvailedVis);
            foodReportDtoVis.setSnacksAvailed(snacksAvailedVis);
            foodReportDtoVis.setEmployeeType("VISITOR");
            foodReportDtoVis.setDinnerAvailed(dinnerAvailedVis);
            foodReportDtoVis.setMarkedOn(date);

            foodReportDtoVis.setTotalToday((foodReportDtoVis.getBfRate() * bfAvailedVis) +
                    (foodReportDtoVis.getSnacksRate() * snacksAvailedVis) +
                    (foodReportDtoVis.getLunchRate() * lunchAvailedVis) +
                    (foodReportDtoVis.getDinnerRate() * dinnerAvailedVis));


            //--con
            for (FoodTracker foodTracker : foodTrackerCont) {
                if (foodTracker.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("BREAK_FAST", "CONTRACT");
                    foodReportDtoCont.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    bfAvailedCon++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("LUNCH")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("LUNCH", "CONTRACT");
                    foodReportDtoCont.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    lunchAvailedCon++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("SNACK")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("SNACK", "CONTRACT");
                    System.out.println("CAte--" + catererSettings);
                    foodReportDtoCont.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    snacksAvailedCon++;
                } else if (foodTracker.getFoodType().equalsIgnoreCase("DINNER")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("DINNER", "CONTRACT");
                    foodReportDtoCont.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    dinnerAvailedCon++;
                }
            }
            foodReportDtoCont.setBfAvailed(bfAvailedCon);
            foodReportDtoCont.setLunchAvailed(lunchAvailedCon);
            foodReportDtoCont.setSnacksAvailed(snacksAvailedCon);
            foodReportDtoCont.setDinnerAvailed(dinnerAvailedCon);
            foodReportDtoCont.setEmployeeType("CONTRACT");
            foodReportDtoCont.setMarkedOn(date);
            foodReportDtoCont.setTotalToday((foodReportDtoCont.getBfRate() * bfAvailedCon) +
                    (foodReportDtoCont.getSnacksRate() * snacksAvailedCon) +
                    (foodReportDtoCont.getLunchRate() * lunchAvailedCon) +
                    (foodReportDtoCont.getDinnerRate() * dinnerAvailedCon));
            //--con


            foodTrackerDtoList.add(foodReportDtoEmp);
            foodTrackerDtoList.add(foodReportDtoVis);
            foodTrackerDtoList.add(foodReportDtoCont);


        }
        return foodTrackerDtoList;
    }

    public List<FoodReportDto> foodConsumptionsFilter(DateDto dateDto) {
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
        System.out.println(dates.size());
        List<FoodReportDto> foodTrackerDtoList = new ArrayList<>();
        for (Date date : dates) {
            System.out.println("dates " + date);

            List<FoodTracker> foodTrackerEmp = foodTrackerRepository.findByMarkedOnAndEmployeeType(date, dateDto.getEmployeeType());
            System.out.println("FempSize: " + foodTrackerEmp.size());

            FoodReportDto foodReportDtoEmp = new FoodReportDto();

            long bfAvailedEmp = 0;
            long lunchAvailedEmp = 0;
            long snacksAvailedEmp = 0;
            long dinnerAvailedEmp = 0;


            foodReportDtoEmp.setEmployeeType(dateDto.getEmployeeType());

            for (FoodTracker foodTracker : foodTrackerEmp) {
                if (foodTracker.getLocationName().equalsIgnoreCase(dateDto.getLocationName()) &&
                        foodTracker.getCompanyName().equalsIgnoreCase(dateDto.getCompanyName())) {
                    if (foodTracker.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                        CatererSettings catererSettings = canteenService.getCatererSetting("BREAK_FAST", dateDto.getEmployeeType());
                        foodReportDtoEmp.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        bfAvailedEmp++;
                    } else if (foodTracker.getFoodType().equalsIgnoreCase("LUNCH")) {
                        CatererSettings catererSettings = canteenService.getCatererSetting("LUNCH", dateDto.getEmployeeType());
                        foodReportDtoEmp.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        lunchAvailedEmp++;
                    } else if (foodTracker.getFoodType().equalsIgnoreCase("SNACK")) {
                        CatererSettings catererSettings = canteenService.getCatererSetting("SNACK", dateDto.getEmployeeType());
                        System.out.println("CAte--" + catererSettings);
                        foodReportDtoEmp.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        snacksAvailedEmp++;
                    } else if (foodTracker.getFoodType().equalsIgnoreCase("DINNER")) {
                        CatererSettings catererSettings = canteenService.getCatererSetting("DINNER", dateDto.getEmployeeType());
                        foodReportDtoEmp.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        dinnerAvailedEmp++;
                    }
                }else {
                    if (foodTracker.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                        CatererSettings catererSettings = canteenService.getCatererSetting("BREAK_FAST", dateDto.getEmployeeType());
                        foodReportDtoEmp.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        bfAvailedEmp++;
                    } else if (foodTracker.getFoodType().equalsIgnoreCase("LUNCH")) {
                        CatererSettings catererSettings = canteenService.getCatererSetting("LUNCH", dateDto.getEmployeeType());
                        foodReportDtoEmp.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        lunchAvailedEmp++;
                    } else if (foodTracker.getFoodType().equalsIgnoreCase("SNACK")) {
                        CatererSettings catererSettings = canteenService.getCatererSetting("SNACK", dateDto.getEmployeeType());
                        System.out.println("CAte--" + catererSettings);
                        foodReportDtoEmp.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        snacksAvailedEmp++;
                    } else if (foodTracker.getFoodType().equalsIgnoreCase("DINNER")) {
                        CatererSettings catererSettings = canteenService.getCatererSetting("DINNER", dateDto.getEmployeeType());
                        foodReportDtoEmp.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                        dinnerAvailedEmp++;
                    }
                }

            }
            foodReportDtoEmp.setBfAvailed(bfAvailedEmp);
            foodReportDtoEmp.setLunchAvailed(lunchAvailedEmp);
            foodReportDtoEmp.setMarkedOn(date);
            foodReportDtoEmp.setEmployeeType(dateDto.getEmployeeType());
            foodReportDtoEmp.setSnacksAvailed(snacksAvailedEmp);
            foodReportDtoEmp.setDinnerAvailed(dinnerAvailedEmp);
            foodReportDtoEmp.setTotalToday((foodReportDtoEmp.getBfRate() * bfAvailedEmp) + (foodReportDtoEmp.getSnacksRate() * snacksAvailedEmp) + (foodReportDtoEmp.getLunchRate() * lunchAvailedEmp) + (foodReportDtoEmp.getDinnerRate() * dinnerAvailedEmp));


            foodTrackerDtoList.add(foodReportDtoEmp);
        }
        return foodTrackerDtoList;
    }

    public List<FoodReportDto> foodCatererFun(DateDto dateDto) {
        List<FoodReportDto> foodReportDtoList = new ArrayList<>();
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
        List<CatererSettings> catererSettings = canteenService.getCatererSettings();

        for (CatererSettings catererSetting : catererSettings) {
            FoodReportDto foodReportDto = new FoodReportDto();
            long foodRate = catererSetting.getEmployeeRate() + catererSetting.getEmployerRate();
            long availed = 0;
            foodReportDto.setFoodRate(foodRate);
            for (Date date : dates) {
                List<FoodTracker> foodTrackers = foodTrackerRepository.findByMarkedOnAndEmployeeTypeAndFoodType(date, catererSetting.getEmployeeType(), catererSetting.getFoodType());
                availed = availed + foodTrackers.size();
            }
            foodReportDto.setCaterer("");
            foodReportDto.setEmployeeType(catererSetting.getEmployeeType());
            foodReportDto.setFoodType(catererSetting.getFoodType());
            foodReportDto.setAvailed(availed);
            foodReportDto.setTotal(availed * foodRate);
            foodReportDtoList.add(foodReportDto);
        }
        return foodReportDtoList;
    }

    public List<FoodReportDto> foodCatererFunFilter(DateDto dateDto) {
        List<FoodReportDto> foodReportDtoList = new ArrayList<>();
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
        List<CatererSettings> catererSettings = canteenService.getCatererSettings();
        CatererSettings catererSetting = canteenService.getCatererSetting(dateDto.getFoodType(), dateDto.getEmployeeType());
        FoodReportDto foodReportDto = new FoodReportDto();
        long foodRate = catererSetting.getEmployeeRate() + catererSetting.getEmployerRate();
        long availed = 0;
        foodReportDto.setFoodRate(foodRate);
        for (Date date : dates) {
            List<FoodTracker> foodTrackers = foodTrackerRepository.findByMarkedOnAndEmployeeTypeAndFoodType(date, catererSetting.getEmployeeType(), catererSetting.getFoodType());
            availed = availed + foodTrackers.size();
        }
        foodReportDto.setCaterer("");
        foodReportDto.setEmployeeType(catererSetting.getEmployeeType());
        foodReportDto.setFoodType(catererSetting.getFoodType());
        foodReportDto.setAvailed(availed);
        foodReportDto.setTotal(availed * foodRate);
        foodReportDtoList.add(foodReportDto);
        return foodReportDtoList;
    }
}
