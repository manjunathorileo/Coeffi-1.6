package com.dfq.coeffi.foodManagement;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.MonthlyStatusDto;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.repository.hr.EmployeeRepository;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import com.dfq.coeffi.visitor.Services.VisitorService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static jxl.format.Alignment.*;
import static jxl.format.Alignment.LEFT;

@RestController
@Slf4j
public class FoodReportArun extends BaseController {
    @Autowired
    FoodTrackerRepository foodTrackerRepository;
    @Autowired
    CanteenService canteenService;
    @Autowired
    FoodTimeRepository foodTimeRepository;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    PermanentContractRepo permanentContractRepo;
    @Autowired
    VisitorPassService visitorService;

    private String caterer = "";


    @GetMapping("food-tracker/view-todays-report")
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

    @GetMapping("food-tracker/food-punch-log")
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
            if (out != null)
                out.close();
        }
    }

    @GetMapping("food-tracker/food-consumption-report")
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
            foodConsumeReportdirect(workbook, dateDto, foodTrackers, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook foodConsumeReportdirect(WritableWorkbook workbook, DateDto dateDto, List<FoodReportDto> foodTrackers, HttpServletResponse response, int index) throws IOException, WriteException {
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
        s.addCell(new Label(11, 1, "Midnight-Snack Availed", cellFormat));
        s.addCell(new Label(12, 1, "Midnight-Snack Rate", cellFormat));
        s.addCell(new Label(13, 1, "Total", cellFormat));


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
            s.addCell(new Label(11, rownum, "" + foodTracker.getMidnightSnackAvailed(), cLeft));
            s.addCell(new Label(12, rownum, "" + foodTracker.getMidnightSnackRate(), cLeft));
            s.addCell(new Label(13, rownum, "" + foodTracker.getTotalToday(), cLeft));
            rownum++;
        }

        rownum = rownum + 2;
        s.addCell(new Label(13, rownum, "" + "GrandTotal", cellFormat));
        s.addCell(new Label(13, rownum + 1, "" + grandTotal, cLeft));

        return workbook;
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
            long midnightAvailedEmp = 0;

            long bfAvailedVis = 0;
            long lunchAvailedVis = 0;
            long snacksAvailedVis = 0;
            long dinnerAvailedVis = 0;
            long midnightAvailedVis =0;

            long bfAvailedCon = 0;
            long lunchAvailedCon = 0;
            long snacksAvailedCon = 0;
            long dinnerAvailedCon = 0;
            long midnightAvailedCon = 0;


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
                }else if (foodTracker.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("MIDNIGHT_SNACK", "EMPLOYEE");
                    foodReportDtoEmp.setMidnightSnackRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    midnightAvailedEmp++;
                }

            }
            foodReportDtoEmp.setBfAvailed(bfAvailedEmp);
            foodReportDtoEmp.setLunchAvailed(lunchAvailedEmp);
            foodReportDtoEmp.setMarkedOn(date);

            foodReportDtoEmp.setEmployeeType("EMPLOYEE");
            foodReportDtoEmp.setSnacksAvailed(snacksAvailedEmp);
            foodReportDtoEmp.setDinnerAvailed(dinnerAvailedEmp);
            foodReportDtoEmp.setMidnightSnackAvailed(midnightAvailedEmp);
            foodReportDtoEmp.setTotalToday((foodReportDtoEmp.getBfRate() * bfAvailedEmp) +
                    (foodReportDtoEmp.getSnacksRate() * snacksAvailedEmp) +
                    (foodReportDtoEmp.getLunchRate() * lunchAvailedEmp) +
                    (foodReportDtoEmp.getMidnightSnackRate() * midnightAvailedEmp) +
                    (foodReportDtoEmp.getDinnerRate() * dinnerAvailedEmp));


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
                }else if (foodTracker.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("MIDNIGHT_SNACK", "VISITOR");
                    foodReportDtoVis.setMidnightSnackRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    midnightAvailedVis++;
                }
            }
            foodReportDtoVis.setBfAvailed(bfAvailedVis);
            foodReportDtoVis.setLunchAvailed(lunchAvailedVis);
            foodReportDtoVis.setSnacksAvailed(snacksAvailedVis);
            foodReportDtoVis.setEmployeeType("VISITOR");
            foodReportDtoVis.setDinnerAvailed(dinnerAvailedVis);
            foodReportDtoVis.setMidnightSnackAvailed(midnightAvailedVis);
            foodReportDtoVis.setMarkedOn(date);

            foodReportDtoVis.setTotalToday((foodReportDtoVis.getBfRate() * bfAvailedVis) +
                    (foodReportDtoVis.getSnacksRate() * snacksAvailedVis) +
                    (foodReportDtoVis.getLunchRate() * lunchAvailedVis) +
                    (foodReportDtoVis.getMidnightSnackRate() * midnightAvailedVis) +
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
                }else if (foodTracker.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                    CatererSettings catererSettings = canteenService.getCatererSetting("MIDNIGHT_SNACK", "CONTRACT");
                    foodReportDtoCont.setMidnightSnackRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                    midnightAvailedCon++;
                }
            }
            foodReportDtoCont.setBfAvailed(bfAvailedCon);
            foodReportDtoCont.setLunchAvailed(lunchAvailedCon);
            foodReportDtoCont.setSnacksAvailed(snacksAvailedCon);
            foodReportDtoCont.setDinnerAvailed(dinnerAvailedCon);
            foodReportDtoCont.setMidnightSnackAvailed(midnightAvailedCon);
            foodReportDtoCont.setEmployeeType("CONTRACT");
            foodReportDtoCont.setMarkedOn(date);
            foodReportDtoCont.setTotalToday((foodReportDtoCont.getBfRate() * bfAvailedCon) +
                    (foodReportDtoCont.getSnacksRate() * snacksAvailedCon) +
                    (foodReportDtoCont.getLunchRate() * lunchAvailedCon) +
                    (foodReportDtoCont.getMidnightSnackRate() * midnightAvailedCon) +
                    (foodReportDtoCont.getDinnerRate() * dinnerAvailedCon));
            //--con


            foodTrackerDtoList.add(foodReportDtoEmp);
            foodTrackerDtoList.add(foodReportDtoVis);
            foodTrackerDtoList.add(foodReportDtoCont);


        }
        return foodTrackerDtoList;
    }

    @GetMapping("food-tracker/caterer-report")
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
            if (out != null)
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
        s.addCell(new Label(8, 1, "Department", cellFormat));

        s.addCell(new Label(9, 1, "Food Type", cellFormat));
        s.addCell(new Label(10, 1, "Food Rate", cellFormat));
        s.addCell(new Label(11, 1, "Availed", cellFormat));
        s.addCell(new Label(12, 1, "Total", cellFormat));

        int rownum = 2;
        long grandTotal = 0;
        for (FoodTracker foodTracker : foodTrackers) {
            CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
            long foodRate = catererSettings.getEmployerRate() + catererSettings.getEmployeeRate();
            grandTotal = grandTotal + foodRate;
            s.addCell(new Label(0, rownum, "" + (rownum - 1), cLeft));
            s.addCell(new Label(1, rownum, "" + foodTracker.getMarkedOn(), cLeft));
            s.addCell(new Label(2, rownum, "" + getCaterer(), cLeft));
            s.addCell(new Label(3, rownum, "" + foodTracker.getEmployeeType(), cLeft));

            s.addCell(new Label(4, rownum, "" + foodTracker.getEmployeeCode(), cLeft));
            s.addCell(new Label(5, rownum, "" + foodTracker.getEmployeeName(), cLeft));
            s.addCell(new Label(6, rownum, "" + foodTracker.getCompanyName(), cLeft));
            s.addCell(new Label(7, rownum, "" + foodTracker.getLocationName(), cLeft));
            if (foodTracker.getDepartmentName() == null) {
                s.addCell(new Label(8, rownum, "" + "", cLeft));
            } else {
                s.addCell(new Label(8, rownum, "" + foodTracker.getDepartmentName(), cLeft));
            }


            s.addCell(new Label(9, rownum, "" + foodTracker.getFoodType(), cLeft));
            s.addCell(new Label(10, rownum, "" + foodRate, cLeft));
            s.addCell(new Label(11, rownum, "" + "1", cLeft));
            s.addCell(new Label(12, rownum, "" + foodRate, cLeft));
            rownum++;
        }

        rownum = rownum + 2;
        s.addCell(new Label(12, rownum, "" + "GrandTotal", cellFormat));
        s.addCell(new Label(12, rownum + 1, "" + grandTotal, cLeft));


        return workbook;
    }


    private WritableWorkbook foodConsumeReport(WritableWorkbook workbook, DateDto dateDto, List<FoodReportDto> foodTrackers, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Food Consumption Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 9);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBorder(jxl.format.Border.BOTTOM, jxl.format.BorderLineStyle.THIN);

        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        headerFormatLeft.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);

        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(jxl.format.Colour.GRAY_25);
        cellFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.GRAY_80);

        WritableFont cellFont1 = new WritableFont(WritableFont.TIMES, 7);
        cellFont1.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat1 = new WritableCellFormat(cellFont1);
        cellFormat1.setAlignment(LEFT);
        cellFormat1.setBackground(jxl.format.Colour.GRAY_25);
        cellFormat1.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.GRAY_80);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        cf.setBoldStyle(WritableFont.NO_BOLD);
        WritableCellFormat c = new WritableCellFormat(cf);
        c.setAlignment(CENTRE);
        c.setBackground(jxl.format.Colour.ICE_BLUE);
        c.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

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
        s.setColumnView(0, 15);
        s.setColumnView(1, 5);
        s.mergeCells(0, 0, 30, 0);
        s.mergeCells(0, 1, 30, 1);
        s.addCell(new Label(0, 0, "Monthly Detailed Food Consumption Report (From: " + dateDto.startDate + " to " + dateDto.endDate + ")", headerFormat));


        int rowNum = 1;
        for (FoodReportDto foodReportDto : foodTrackers) {
            String department = "";
            if (foodReportDto.getDepartment() != null) {
                department = foodReportDto.getDepartment();
            }
            String desig = "";
            if (foodReportDto.getDesignation() != null) {
                desig = foodReportDto.getDesignation();
            }
            int colNum = 1;
            s.mergeCells(0, rowNum, 30, rowNum);
            s.addCell(new Label(0, rowNum, "Name/Code: " + foodReportDto.getEmployeeName() + "/" + foodReportDto.getEmployeeCode() + "    " + "Department: " + department + "    " + "Designation: " + desig + "    " + "Emp_Type: " + foodReportDto.getEmployeeType(), cellFormat1));
            s.addCell(new Label(0, rowNum + 1, "Days", cellFormat));
            s.addCell(new Label(0, rowNum + 2, "BREAKFAST", cellFormat));
            s.addCell(new Label(0, rowNum + 3, "LUNCH", cellFormat));
            s.addCell(new Label(0, rowNum + 4, "SNACK", cellFormat));
            s.addCell(new Label(0, rowNum + 5, "DINNER", cellFormat));
            s.addCell(new Label(0, rowNum + 6, "MIDNIGHT SNACK", cellFormat));

            int j = colNum;
            List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
            for (int i = 0; i < dates.size(); i++) {
                s.mergeCells(0, rowNum, j, rowNum);
                java.text.DateFormat formatter = new SimpleDateFormat("dd");
                s.addCell(new Label(j, rowNum + 1, formatter.format(dates.get(i)), cellFormat));
                j = j + 1;
            }
            s.setColumnView(j + 1, 12);
            s.addCell(new Label(j + 1, rowNum + 2, "Total Breakfast", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 3, "Total Lunch", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 4, "Total Snack", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 5, "Total Dinner", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 6, "Total Midnight Snack", cellFormat));

            int noOfDays = DateUtil.calculateDaysBetweenDate(dateDto.startDate, dateDto.endDate);
            for (int m = 0; m <= noOfDays; m++) {

                s.addCell(new Label(colNum, rowNum + 2, String.valueOf(foodReportDto.getMonthlyStatusDtos().get(m).getSingleEntryBf()), c));
                s.addCell(new Label(colNum, rowNum + 3, String.valueOf(foodReportDto.getMonthlyStatusDtos().get(m).getSingleEntryLaunch()), c));
                s.addCell(new Label(colNum, rowNum + 4, String.valueOf(foodReportDto.getMonthlyStatusDtos().get(m).getSingleEntrySnaks()), c));
                s.addCell(new Label(colNum, rowNum + 5, String.valueOf(foodReportDto.getMonthlyStatusDtos().get(m).getSingleEntryDinner()), c));
                s.addCell(new Label(colNum, rowNum + 6, String.valueOf(foodReportDto.getMonthlyStatusDtos().get(m).getSingleEntryMidnightSnack()), c));
                colNum = colNum + 1;

            }

            s.addCell(new Label(j + 2, rowNum + 2, String.valueOf(foodReportDto.getBfAvailed()), c));
            s.addCell(new Label(j + 2, rowNum + 3, String.valueOf(foodReportDto.getLunchAvailed()), c));
            s.addCell(new Label(j + 2, rowNum + 4, String.valueOf(foodReportDto.getSnacksAvailed()), c));
            s.addCell(new Label(j + 2, rowNum + 5, String.valueOf(foodReportDto.getDinnerAvailed()), c));
            s.addCell(new Label(j + 2, rowNum + 6, String.valueOf(foodReportDto.getMidnightSnackAvailed()), c));
            rowNum = rowNum + 8;
        }
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
            s.addCell(new Label(3, rownum, "" + getCaterer(), cLeft));
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


    @PostMapping("food-tracker/food-punch-log")
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
            if (out != null)
                out.close();
        }
    }

    @PostMapping("food-tracker/food-consumption-report")
    public void todaysFoodConsumptionDate(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        //TODO fc report
        List<FoodReportDto> foodTrackers = null;
        if (dateDto.getCompanyName() != null && dateDto.getLocationName() != null && dateDto.getDepartmentName() != null && dateDto.getEmployeeType() != null) {
            foodTrackers = foodConsumptionsFilter(dateDto);
        } else if (dateDto.getEmployeeType() != null) {
            foodTrackers = foodConsumptionsFilter(dateDto);
        } else {
            foodTrackers = foodConsumptions(dateDto);
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

    @PostMapping("food-tracker/caterer-report")
    public void todaysCatererDate(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO fc report
        List<FoodReportDto> foodTrackers = null;
        if ( dateDto.getEmployeeType() != null && dateDto.getFoodType() != null) {
            if(dateDto.getEmployeeType().equalsIgnoreCase("ALL")){
             foodTrackers = foodCatererFun((dateDto));
            }else {
                foodTrackers = foodCatererFunFilter(dateDto);
            }
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
            if (out != null)
                out.close();
        }
    }

    public List<FoodTracker> foodPunchLogs(DateDto dateDto) {
        List<FoodTracker> foodTrackers = new ArrayList<>();
        if (dateDto.getEmployeeCode() != null) {
            List<FoodTracker> foodTrackers1 = foodTrackerRepository.findAll();
            for (FoodTracker foodTracker : foodTrackers1) {
                if (foodTracker.getEmployeeCode().equalsIgnoreCase(dateDto.getEmployeeCode())) {
                    foodTrackers.add(foodTracker);
                }
            }
        }else {
            List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);

            for (Date date : dates) {
                List<FoodTracker> foodTrackersDate = foodTrackerRepository.findByMarkedOn(date);
                for (FoodTracker foodTracker : foodTrackersDate) {
                    System.out.println("com: " + dateDto.getCompanyName());
                    if (dateDto.getEmployeeCode() != null) {
                        if (foodTracker.getEmployeeCode().equalsIgnoreCase(dateDto.getEmployeeCode())) {
                            foodTrackers.add(foodTracker);
                        }
                    } else if (dateDto.getCompanyName() != null &&
                            dateDto.getLocationName() != null &&
                            dateDto.getDepartmentName() != null &&
                            dateDto.getEmployeeType() != null &&
                            dateDto.getFoodType() != null) {
                        if (foodTracker.getLocationName().equalsIgnoreCase(dateDto.getLocationName()) &&
                                foodTracker.getCompanyName().equalsIgnoreCase(dateDto.getCompanyName()) &&
                                foodTracker.getDepartmentName().equalsIgnoreCase(dateDto.getDepartmentName()) &&
                                foodTracker.getEmployeeType().equalsIgnoreCase(dateDto.getEmployeeType()) &&
                                foodTracker.getFoodType().equalsIgnoreCase(dateDto.getFoodType())) {
                            foodTrackers.add(foodTracker);
                        } else if (foodTracker.getFoodType().equalsIgnoreCase(dateDto.getFoodType()) &&
                                foodTracker.getEmployeeType().equalsIgnoreCase(dateDto.getEmployeeType())) {
                            foodTrackers.add(foodTracker);
                        }
                    } else {
                        foodTrackers.add(foodTracker);
                    }
                }
            }
        }
        return foodTrackers;
    }


    public List<FoodReportDto> foodConsumptionsFilter(DateDto dateDto) throws ParseException {
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);
        List<FoodReportDto> foodTrackerDtoList = new ArrayList<>();
        if (dateDto.getEmployeeType().equalsIgnoreCase("EMPLOYEE")||dateDto.getEmployeeType().equalsIgnoreCase("All")) {
            List<Employee> employeeList = employeeService.findAll();
            for (Employee employee : employeeList) {
                if(employee.getEmployeeType().equals(EmployeeType.PERMANENT)||employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                    if (employee.getStatus()==true) {
                        FoodReportDto foodReportDtoEmp = new FoodReportDto();
                        List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<>();
                        long bfAvailedEmp = 0;
                        long lunchAvailedEmp = 0;
                        long snacksAvailedEmp = 0;
                        long dinnerAvailedEmp = 0;
                        long midnightSnackAvailedEmp = 0;
                        String companyName = "";
                        String location = "";

                        for (Date date : dates) {
                            MonthlyStatusDto monthlyStatusDto = new MonthlyStatusDto();
                            long singleBfEmp = 0;
                            long singlelunchEmp = 0;
                            long singleSnaksEmp = 0;
                            long singledinnerEmp = 0;
                            long singleMidnightSnackEmp = 0;

                            foodReportDtoEmp.setEmployeeType(dateDto.getEmployeeType());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date mk = sdf.parse(sdf.format(date));
                            System.out.println("trackSize----" + mk + " -- " + employee.getEmployeeCode());
                            List<FoodTracker> foodTrackerEmp = foodTrackerRepository.findByMarkedOnAndEmployeeCode(mk, employee.getEmployeeCode());
                            System.out.println("trackSize----" + foodTrackerEmp.size());
                            for (FoodTracker foodTracker : foodTrackerEmp) {
                                companyName = foodTracker.getCompanyName();
                                location = foodTracker.getLocationName();
                                System.out.println("A");
                                if (foodTracker.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                    System.out.println("B");
                                    CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                    foodReportDtoEmp.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                    bfAvailedEmp++;
                                    singleBfEmp++;
                                }
                                if (foodTracker.getFoodType().equalsIgnoreCase("LUNCH")) {
                                    System.out.println("C");
                                    CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                    foodReportDtoEmp.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                    lunchAvailedEmp++;
                                    singlelunchEmp++;
                                }
                                if (foodTracker.getFoodType().equalsIgnoreCase("SNACK")) {
                                    System.out.println("D");
                                    CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                    foodReportDtoEmp.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                    snacksAvailedEmp++;
                                    singleSnaksEmp++;
                                }
                                if (foodTracker.getFoodType().equalsIgnoreCase("DINNER")) {
                                    System.out.println("E");
                                    CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                    foodReportDtoEmp.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                    dinnerAvailedEmp++;
                                    singledinnerEmp++;
                                }
                                if (foodTracker.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                    System.out.println("B");
                                    CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                    foodReportDtoEmp.setMidnightSnackRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                    midnightSnackAvailedEmp++;
                                    singleMidnightSnackEmp++;
                                }

                            }
                            monthlyStatusDto.setSingleEntryBf(singleBfEmp);
                            monthlyStatusDto.setSingleEntryLaunch(singlelunchEmp);
                            monthlyStatusDto.setSingleEntrySnaks(singleSnaksEmp);
                            monthlyStatusDto.setSingleEntryDinner(singledinnerEmp);
                            monthlyStatusDto.setSingleEntryMidnightSnack(singleMidnightSnackEmp);
                            monthlyStatusDto.setMarkedOn(date);
                            monthlyStatusDtos.add(monthlyStatusDto);

                        }
                        foodReportDtoEmp.setMonthlyStatusDtos(monthlyStatusDtos);
                        foodReportDtoEmp.setEmployeeId(employee.getId());
                        foodReportDtoEmp.setEmployeeName(employee.getFirstName());
                        foodReportDtoEmp.setCompany(companyName);
                        foodReportDtoEmp.setLocation(location);
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
                        foodReportDtoEmp.setEmployeeType("EMPLOYEE");
                        foodReportDtoEmp.setSnacksAvailed(snacksAvailedEmp);
                        foodReportDtoEmp.setDinnerAvailed(dinnerAvailedEmp);
                        foodReportDtoEmp.setMidnightSnackAvailed(midnightSnackAvailedEmp);
                        foodReportDtoEmp.setTotalToday((foodReportDtoEmp.getBfRate() * bfAvailedEmp) +
                                (foodReportDtoEmp.getSnacksRate() * snacksAvailedEmp) +
                                (foodReportDtoEmp.getLunchRate() * lunchAvailedEmp) +
                                (foodReportDtoEmp.getMidnightSnackRate() * midnightSnackAvailedEmp)+
                                (foodReportDtoEmp.getDinnerRate() * dinnerAvailedEmp));
                        foodTrackerDtoList.add(foodReportDtoEmp);

                    }
                }
            }
        }

        //CONTRACT
        if (dateDto.getEmployeeType().equalsIgnoreCase("CONTRACT")||dateDto.getEmployeeType().equalsIgnoreCase("All")) {
            List<EmpPermanentContract> employeeList = permanentContractRepo.findAll();
            for (EmpPermanentContract employee : employeeList) {
                if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                    FoodReportDto foodReportDtoEmp = new FoodReportDto();
                    List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<>();
                    long bfAvailedEmp = 0;
                    long lunchAvailedEmp = 0;
                    long snacksAvailedEmp = 0;
                    long dinnerAvailedEmp = 0;
                    long midnightSnackAvailedEmp = 0;
                    String companyName="";
                    String location ="";

                    for (Date date : dates) {
                        MonthlyStatusDto monthlyStatusDto = new MonthlyStatusDto();
                        long singleBfEmp = 0;
                        long singlelunchEmp = 0;
                        long singleSnaksEmp = 0;
                        long singledinnerEmp = 0;
                        long singleMidnightSnackEmp = 0;

                        foodReportDtoEmp.setEmployeeType(dateDto.getEmployeeType());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date mk = sdf.parse(sdf.format(date));
                        System.out.println("trackSize----" + mk + " -- " + employee.getEmployeeCode());
                        List<FoodTracker> foodTrackerEmp = foodTrackerRepository.findByMarkedOnAndEmployeeCode(mk, employee.getEmployeeCode());
                        System.out.println("trackSize----" + foodTrackerEmp.size());
                        for (FoodTracker foodTracker : foodTrackerEmp) {
                            companyName = foodTracker.getCompanyName();
                            location =foodTracker.getLocationName();
                            System.out.println("A");
                            if (foodTracker.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                System.out.println("B");
                                CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                foodReportDtoEmp.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                bfAvailedEmp++;
                                singleBfEmp++;
                            }
                            if (foodTracker.getFoodType().equalsIgnoreCase("LUNCH")) {
                                System.out.println("C");
                                CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                foodReportDtoEmp.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                lunchAvailedEmp++;
                                singlelunchEmp++;
                            }
                            if (foodTracker.getFoodType().equalsIgnoreCase("SNACK")) {
                                System.out.println("D");
                                CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                foodReportDtoEmp.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                snacksAvailedEmp++;
                                singleSnaksEmp++;
                            }
                            if (foodTracker.getFoodType().equalsIgnoreCase("DINNER")) {
                                System.out.println("E");
                                CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                foodReportDtoEmp.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                dinnerAvailedEmp++;
                                singledinnerEmp++;
                            }
                            if (foodTracker.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                System.out.println("B");
                                CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                                foodReportDtoEmp.setMidnightSnackRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                                midnightSnackAvailedEmp++;
                                singleMidnightSnackEmp++;
                            }

                        }
                        monthlyStatusDto.setSingleEntryBf(singleBfEmp);
                        monthlyStatusDto.setSingleEntryLaunch(singlelunchEmp);
                        monthlyStatusDto.setSingleEntrySnaks(singleSnaksEmp);
                        monthlyStatusDto.setSingleEntryDinner(singledinnerEmp);
                        monthlyStatusDto.setSingleEntryMidnightSnack(singleMidnightSnackEmp);
                        monthlyStatusDto.setMarkedOn(date);
                        monthlyStatusDtos.add(monthlyStatusDto);

                    }
                    foodReportDtoEmp.setMonthlyStatusDtos(monthlyStatusDtos);
                    foodReportDtoEmp.setEmployeeId(employee.getId());
                    foodReportDtoEmp.setEmployeeName(employee.getFirstName());
                    foodReportDtoEmp.setEmployeeCode(employee.getEmployeeCode());
                    foodReportDtoEmp.setBfAvailed(bfAvailedEmp);
                    foodReportDtoEmp.setLunchAvailed(lunchAvailedEmp);
                    foodReportDtoEmp.setEmployeeType("CONTRACT");
                    foodReportDtoEmp.setSnacksAvailed(snacksAvailedEmp);
                    foodReportDtoEmp.setDinnerAvailed(dinnerAvailedEmp);
                    foodReportDtoEmp.setMidnightSnackAvailed(midnightSnackAvailedEmp);
                    if (employee.getDepartmentName() != null) {
                        foodReportDtoEmp.setDepartment(employee.getDepartmentName());
                    }
                    foodReportDtoEmp.setCompany(companyName);
                    foodReportDtoEmp.setLocation(location);
                    foodReportDtoEmp.setTotalToday((foodReportDtoEmp.getBfRate() * bfAvailedEmp) +
                            (foodReportDtoEmp.getSnacksRate() * snacksAvailedEmp) +
                            (foodReportDtoEmp.getLunchRate() * lunchAvailedEmp) +
                            (foodReportDtoEmp.getMidnightSnackRate() * midnightSnackAvailedEmp)+
                            (foodReportDtoEmp.getDinnerRate() * dinnerAvailedEmp));
                    foodTrackerDtoList.add(foodReportDtoEmp);

                }
            }
        }

        if (dateDto.getEmployeeType().equalsIgnoreCase("VISITOR")||dateDto.getEmployeeType().equalsIgnoreCase("All")) {
            List<VisitorPass> employeeList = visitorService.getAllVisitors();
            for (VisitorPass employee : employeeList) {
                FoodReportDto foodReportDtoEmp = new FoodReportDto();
                List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<>();
                long bfAvailedEmp = 0;
                long lunchAvailedEmp = 0;
                long snacksAvailedEmp = 0;
                long dinnerAvailedEmp = 0;
                long midnightSnackAvailedEmp = 0;
                String companyName = "";
                String location = "";

                for (Date date : dates) {
                    MonthlyStatusDto monthlyStatusDto = new MonthlyStatusDto();
                    long singleBfEmp = 0;
                    long singlelunchEmp = 0;
                    long singleSnaksEmp = 0;
                    long singledinnerEmp = 0;
                    long singleMidnightSnackEmp = 0;

                    foodReportDtoEmp.setEmployeeType(dateDto.getEmployeeType());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date mk = sdf.parse(sdf.format(date));
                    System.out.println("trackSize----" + mk + " -- " + employee.getMobileNumber());
                    List<FoodTracker> foodTrackerEmp = foodTrackerRepository.findByMarkedOnAndEmployeeCode(mk, employee.getMobileNumber());
                    System.out.println("trackSize----" + foodTrackerEmp.size());
                    for (FoodTracker foodTracker : foodTrackerEmp) {
                        companyName = foodTracker.getCompanyName();
                        location =foodTracker.getLocationName();
                        System.out.println("A");
                        if (foodTracker.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                            System.out.println("B");
                            CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                            foodReportDtoEmp.setBfRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                            bfAvailedEmp++;
                            singleBfEmp++;
                        }
                        if (foodTracker.getFoodType().equalsIgnoreCase("LUNCH")) {
                            System.out.println("C");
                            CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                            foodReportDtoEmp.setLunchRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                            lunchAvailedEmp++;
                            singlelunchEmp++;
                        }
                        if (foodTracker.getFoodType().equalsIgnoreCase("SNACK")) {
                            System.out.println("D");
                            CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                            foodReportDtoEmp.setSnacksRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                            snacksAvailedEmp++;
                            singleSnaksEmp++;
                        }
                        if (foodTracker.getFoodType().equalsIgnoreCase("DINNER")) {
                            System.out.println("E");
                            CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                            foodReportDtoEmp.setDinnerRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                            dinnerAvailedEmp++;
                            singledinnerEmp++;
                        }
                        if (foodTracker.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                            System.out.println("B");
                            CatererSettings catererSettings = canteenService.getCatererSetting(foodTracker.getFoodType(), foodTracker.getEmployeeType());
                            foodReportDtoEmp.setMidnightSnackRate(catererSettings.getEmployeeRate() + catererSettings.getEmployerRate());
                            midnightSnackAvailedEmp++;
                            singleMidnightSnackEmp++;
                        }


                    }
                    monthlyStatusDto.setSingleEntryBf(singleBfEmp);
                    monthlyStatusDto.setSingleEntryLaunch(singlelunchEmp);
                    monthlyStatusDto.setSingleEntrySnaks(singleSnaksEmp);
                    monthlyStatusDto.setSingleEntryDinner(singledinnerEmp);
                    monthlyStatusDto.setSingleEntryMidnightSnack(singleMidnightSnackEmp);
                    monthlyStatusDto.setMarkedOn(date);
                    monthlyStatusDtos.add(monthlyStatusDto);

                }
                foodReportDtoEmp.setMonthlyStatusDtos(monthlyStatusDtos);
                foodReportDtoEmp.setEmployeeId(employee.getId());
                foodReportDtoEmp.setEmployeeName(employee.getFirstName());
                foodReportDtoEmp.setEmployeeCode(employee.getMobileNumber());
                foodReportDtoEmp.setBfAvailed(bfAvailedEmp);
                foodReportDtoEmp.setLunchAvailed(lunchAvailedEmp);
                foodReportDtoEmp.setEmployeeType("VISITOR");
                foodReportDtoEmp.setSnacksAvailed(snacksAvailedEmp);
                foodReportDtoEmp.setDinnerAvailed(dinnerAvailedEmp);
                foodReportDtoEmp.setMidnightSnackAvailed(midnightSnackAvailedEmp);
                if (employee.getDepartmentName() != null) {
                    foodReportDtoEmp.setDepartment(employee.getDepartmentName());
                }
                foodReportDtoEmp.setCompany(companyName);
                foodReportDtoEmp.setLocation(location);
                foodReportDtoEmp.setTotalToday((foodReportDtoEmp.getBfRate() * bfAvailedEmp) +
                        (foodReportDtoEmp.getSnacksRate() * snacksAvailedEmp) +
                        (foodReportDtoEmp.getLunchRate() * lunchAvailedEmp) +
                        (foodReportDtoEmp.getMidnightSnackRate() * midnightSnackAvailedEmp) +
                        (foodReportDtoEmp.getDinnerRate() * dinnerAvailedEmp));
                foodTrackerDtoList.add(foodReportDtoEmp);

            }
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
            foodReportDto.setCaterer(getCaterer());
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
        CatererSettings catererSetting = canteenService.getCatererSetting(dateDto.getFoodType(), dateDto.getEmployeeType());
//        if(catererSetting==null){
//            catererSetting = canteenService.getCatererSettings();
//        }
        FoodReportDto foodReportDto = new FoodReportDto();
        long foodRate = catererSetting.getEmployeeRate() + catererSetting.getEmployerRate();
        long availed = 0;
        foodReportDto.setFoodRate(foodRate);
        for (Date date : dates) {
            List<FoodTracker> foodTrackers = foodTrackerRepository.findByMarkedOnAndEmployeeTypeAndFoodType(date, catererSetting.getEmployeeType(), catererSetting.getFoodType());
            availed = availed + foodTrackers.size();
        }
        foodReportDto.setCaterer(getCaterer());
        foodReportDto.setEmployeeType(catererSetting.getEmployeeType());
        foodReportDto.setFoodType(catererSetting.getFoodType());
        foodReportDto.setAvailed(availed);
        foodReportDto.setTotal(availed * foodRate);
        foodReportDtoList.add(foodReportDto);
        return foodReportDtoList;
    }

    public String getCaterer(){
        String cat = "";
        List<CatereDetails> catereDetailsList = canteenService.getCatererDetails();
        if (!catereDetailsList.isEmpty()){
            Collections.reverse(catereDetailsList);
             cat = catereDetailsList.get(0).getName();
        }
        return cat;
    }
}
