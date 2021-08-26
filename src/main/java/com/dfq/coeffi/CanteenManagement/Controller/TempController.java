package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.*;
import com.dfq.coeffi.CanteenManagement.Service.*;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.util.DateUtil;
import jxl.write.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;

@RestController
public class TempController extends BaseController {

    @Autowired
    CounterDetailsService counterDetailsService;

    @Autowired
    FoodMasterService foodMasterService;

    @Autowired
    DailyFoodMenuService dailyFoodMenuService;

    @Autowired
    FoodTimeService foodTimeService;

    @Autowired
    BuildingDetailsService buildingDetailsService;

    @GetMapping("canteen/dailyfoodmenu/template-download")
    private void createMenuDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<FoodMaster> foodMasterList = getFoodMaster();
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Menu.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeVehicleToSheet(workbook, response, 0);
            if (foodMasterList != null) {
                templateReference(workbook, foodMasterList, response, 1);
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeVehicleToSheet(WritableWorkbook workbook, HttpServletResponse response, int index) throws IOException, WriteException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
//        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.GRAY_25);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);

        //s.addCell(new Label(0, 0, "#", headerFormat));
        s.addCell(new Label(1, 0, "Food Id", headerFormat));


        return workbook;
    }

    private WritableWorkbook templateReference(WritableWorkbook workbook, List<FoodMaster> foodMasterList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Reference-Sheet", index);
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

        int rowNum = 1;
        for (FoodMaster foodMaster : foodMasterList) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum), cLeft));
            s.addCell(new Label(1, 0, "Food Type", cellFormat));
            s.addCell(new Label(1, rowNum, "" + foodMaster.getFoodType().getFoodType(), cLeft));
            s.addCell(new Label(2, 0, "Food Id", cellFormat));
            s.addCell(new Label(2, rowNum, "" + foodMaster.getId(), cLeft));
            s.addCell(new Label(3, 0, "Food Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + foodMaster.getFoodName(), cLeft));
            s.addCell(new Label(4, 0, "Food Code", cellFormat));
            s.addCell(new Label(4, rowNum, "" + foodMaster.getFoodCode(), cLeft));
            s.addCell(new Label(5, 0, "Units", cellFormat));
            s.addCell(new Label(5, rowNum, "" + foodMaster.getUnit(), cLeft));
            rowNum = rowNum + 1;

        }
        return workbook;
    }

    public List<FoodMaster> getFoodMaster() {
        List<FoodMaster> foodMasterList = foodMasterService.getAllFoodMaster();

        return foodMasterList;
    }


    @PostMapping("canteen/dailyfoodmenu/menu-upload/{foodTypeId}/{cId}/{bId}")
    public ResponseEntity<DailyFoodMenu> menuBulkUpload(@RequestParam("file") MultipartFile file, @PathVariable("foodTypeId") long foodTypeId, @PathVariable("cId") long cId, @PathVariable long bId) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);

        XSSFRow row1 = sheet.getRow(1);
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        Date todate = new Date();
        Date today = DateUtil.mySqlFormatDate(todate);
        Calendar calendarObj = Calendar.getInstance();
        calendarObj.setTime(todate);
        int week = calendarObj.get(Calendar.WEEK_OF_MONTH);
        String day = formatter.format(todate);
        List<DailyFoodMenu> dailyFoodMenuObj = new ArrayList<>();

        FoodTimeMasterAdv foodTimeMasterAdv = foodTimeService.getFoodTime(foodTypeId);
        BuildingDetails buildingDetails = buildingDetailsService.getBuildingDetails(bId);
        List<CounterDetailsAdv> counterDetailsAdvList = new ArrayList<>();

        if (cId == 0) {
            counterDetailsAdvList = counterDetailsService.getCounterDetailsAdvByBuilding(buildingDetails);
        } else {
            counterDetailsAdvList.add(counterDetailsService.getCounterDetails(cId));
        }

        dataDublicatValidation(counterDetailsAdvList, foodTimeMasterAdv, day, week, today);

        for (CounterDetailsAdv counterDetailsAdv : counterDetailsAdvList) {
            DailyFoodMenu dailyFoodMenu = new DailyFoodMenu();
            dailyFoodMenu.setFoodType(foodTimeMasterAdv);
            dailyFoodMenu.setEffectiveFrom(todate);
            dailyFoodMenu.setEffictiveTo(todate);
            dailyFoodMenu.setCreatedDate(todate);
            dailyFoodMenu.setWeekNo(week);
            dailyFoodMenu.setDayName(day);
            dailyFoodMenu.setIsDayWise(true);
            dailyFoodMenu.setBuildingDetails(buildingDetails);

            List<FoodMaster> foodMasterList = new ArrayList<>();

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = sheet.getRow(i);
                long foodId = (long) row.getCell(1).getNumericCellValue();
                FoodMaster foodMaster = foodMasterService.getFoodMaster(foodId);
                foodMasterList.add(foodMaster);
            }
            dailyFoodMenu.setFoodList(foodMasterList);
            dailyFoodMenu.setCounterDetailsAdv(counterDetailsAdv);
            dailyFoodMenuObj.add(dailyFoodMenuService.saveDailyFood(dailyFoodMenu));
        }
        return new ResponseEntity(dailyFoodMenuObj, HttpStatus.OK);
    }

    private void dataDublicatValidation(List<CounterDetailsAdv> counterDetailsAdvList, FoodTimeMasterAdv foodTimeMasterAdv, String day, long week, Date toDay) {
        Date toDate = dateMySqlFormatDate(toDay);
        for (CounterDetailsAdv counterDetailsAdvObj:counterDetailsAdvList) {
            List<DailyFoodMenu> dailyFoodMenus = dailyFoodMenuService.getDailyFoodMenuByCounterByFoodType(counterDetailsAdvObj, foodTimeMasterAdv);
            for (DailyFoodMenu dailyFoodMenuObj : dailyFoodMenus) {
                if (dailyFoodMenuObj.getIsDayWise().equals(true)) {
                    if (dailyFoodMenuObj.getEffectiveFrom().equals(toDate) && dailyFoodMenuObj.getEffictiveTo().equals(toDate)
                            && dailyFoodMenuObj.getDayName().equals(day) && dailyFoodMenuObj.getWeekNo() == week) {
                        throw new EntityNotFoundException("This day data already exist.");
                    }
                }
            }
        }
    }

    public static Date dateMySqlFormatDate(Date date1) {
        Date date = date1;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}