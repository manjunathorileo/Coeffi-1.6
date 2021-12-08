package com.dfq.coeffi.foodManagement.orderTracking.foodOrderReport;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.foodManagement.FoodTimeRepository;
import com.dfq.coeffi.foodManagement.FoodTracker;
import com.dfq.coeffi.foodManagement.FoodTrackerRepository;
import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTracking;
import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTrackingService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;

import static jxl.format.Alignment.CENTRE;
import static jxl.format.Alignment.LEFT;

@RestController
@Slf4j
public class FoodOrderReportResource extends BaseController {

    private final FoodOrderTrackingService foodOrderTrackingService;
    private final FoodTimeRepository foodTimeRepository;
    private final EmployeeService employeeService;
    private final FoodTrackerRepository foodTrackerRepository;
    private final PermanentContractRepo permanentContractRepo;
    private final VisitorPassService visitorService;

    @Autowired
    public FoodOrderReportResource(FoodOrderTrackingService foodOrderTrackingService, FoodTimeRepository foodTimeRepository, EmployeeService employeeService, FoodTrackerRepository foodTrackerRepository, PermanentContractRepo permanentContractRepo, VisitorPassService visitorService) {
        this.foodOrderTrackingService = foodOrderTrackingService;
        this.foodTimeRepository = foodTimeRepository;
        this.employeeService = employeeService;
        this.foodTrackerRepository = foodTrackerRepository;
        this.permanentContractRepo = permanentContractRepo;
        this.visitorService = visitorService;
    }

    @PostMapping("/food-estimation-report")
    public void getFoodEstimationReport(HttpServletRequest request, HttpServletResponse response, @RequestBody FoodOrderReportDto foodOrderReportDto) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Food_Estimation_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            List<FoodOrderReportDto> foodOrderReportDtoObj = foodEstimationCalculation(foodOrderReportDto);
            writeToSheet(workbook, foodOrderReportDtoObj, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("SomeThing Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, List<FoodOrderReportDto> foodOrderReportDto, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Food Estimation Report", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(jxl.format.Colour.GRAY_25);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        WritableFont bodyFont = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat bodyFormat = new WritableCellFormat(bodyFont);
        bodyFormat.setAlignment(LEFT);
        bodyFormat.setBackground(Colour.ICE_BLUE);
        bodyFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 8);
        s.setColumnView(1, 25);
        s.setColumnView(2, 15);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 20);
        s.setColumnView(8, 10);
        s.setColumnView(9, 10);
        s.setColumnView(10, 10);
        s.setColumnView(11, 10);

        s.setRowView(0, 500);

        Label lable = new Label(0, 0, "Food Estimation Report ", headerFormat);
        s.addCell(lable);

        s.addCell(new Label(0, 1, "Sl.No.", headerFormat));
        s.addCell(new Label(1, 1, "From to To", headerFormat));
        s.addCell(new Label(2, 1, "Employee Id", headerFormat));
        s.addCell(new Label(3, 1, "Name", headerFormat));
        s.addCell(new Label(4, 1, "Employee Type", headerFormat));
        s.addCell(new Label(5, 1, "Company", headerFormat));
        s.addCell(new Label(6, 1, "Location", headerFormat));
        s.addCell(new Label(7, 1, "Department", headerFormat));
        s.addCell(new Label(8, 1, "BF Estimate", headerFormat));
        s.addCell(new Label(9, 1, "Lunch Estimate", headerFormat));
        s.addCell(new Label(10, 1, "Dinner Estimate", headerFormat));
        s.addCell(new Label(11, 1, "Snacks Estimate", headerFormat));
        s.addCell(new Label(12, 1, "Midnight Snacks Estimate", headerFormat));


        int slno = 1;
        int mainRow = 2;
        long grandTotal = 0;
        for (FoodOrderReportDto foodOrderReportDtoObj : foodOrderReportDto) {
            s.addCell(new Label(0, mainRow, String.valueOf(slno), bodyFormat));
            s.addCell(new Label(1, mainRow, String.valueOf(foodOrderReportDtoObj.getFromDateStr() + " to " + foodOrderReportDtoObj.getToDateStr()), bodyFormat));
            s.addCell(new Label(2, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeCode()), bodyFormat));
            s.addCell(new Label(3, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeName()), bodyFormat));
            s.addCell(new Label(4, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeType()), bodyFormat));
            s.addCell(new Label(5, mainRow, String.valueOf(foodOrderReportDtoObj.getCompanyName()), bodyFormat));
            s.addCell(new Label(6, mainRow, String.valueOf(foodOrderReportDtoObj.getLocation()), bodyFormat));
            s.addCell(new Label(7, mainRow, String.valueOf(foodOrderReportDtoObj.getDept()), bodyFormat));
            s.addCell(new Label(8, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalBfEstimate()), bodyFormat));
            s.addCell(new Label(9, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalLunchEstimate()), bodyFormat));
            s.addCell(new Label(10, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalDinnerEstimate()), bodyFormat));
            s.addCell(new Label(11, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalSnacksEstimate()), bodyFormat));
            s.addCell(new Label(12, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalSnacksEstimate()), bodyFormat));

            mainRow = mainRow + 1;
            slno = slno + 1;
        }
        return workbook;
    }

    private List<FoodOrderReportDto> foodEstimationCalculation(FoodOrderReportDto foodOrderReportDto) {
        List<FoodOrderReportDto> foodOrderReportDtos = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fromDateDateStr = dateFormat.format(foodOrderReportDto.getFromDate());
        String toDateDateStr = dateFormat.format(foodOrderReportDto.getToDate());
        Date fromDate = DateUtil.convertToDate(fromDateDateStr);
        Date toDate = DateUtil.convertToDate(toDateDateStr);

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("EMPLOYEE") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<Employee> employeeList = employeeService.findAll();
            for (Employee employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true) {
                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true && employee.getCompany().equals(foodOrderReportDto.getCompanyName())) {

                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true && employee.getCompany().equals(foodOrderReportDto.getCompanyName())
                                && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {

                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                }
            }
        }

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("CONTRACT") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<EmpPermanentContract> employeeList = permanentContractRepo.findAll();
            for (EmpPermanentContract employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {

                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        if (!foodOrderTrackingList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                            }
                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                            foodOrderReportDtoObj.setLocation(employee.getLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                        if (employee.getContractCompany().equals(foodOrderReportDto.getCompanyName())) {

                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                        if (employee.getContractCompany().equals(foodOrderReportDto.getCompanyName())
                                && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {

                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                }
            }
        }

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("VISITOR") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<VisitorPass> employeeList = visitorService.getAllVisitors();
            for (VisitorPass employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                    List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                    long totalBfEstimate = 0;
                    long totalLunchEstimate = 0;
                    long totalDinnerEstimate = 0;
                    long totalSnacksEstimate = 0;
                    long midnightSnackEstimate = 0;
                    if (!foodOrderTrackingList.isEmpty()) {
                        for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                            if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                    && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                    totalBfEstimate = totalBfEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                    totalLunchEstimate = totalLunchEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                    totalSnacksEstimate = totalSnacksEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                    totalDinnerEstimate = totalDinnerEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                    midnightSnackEstimate = midnightSnackEstimate + 1;
                                }
                            }
                        }
                        foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                        foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                        foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                        foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                        foodOrderReportDtoObj.setEmployeeType("VISITOR");
                        foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                        foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                        foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                        foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                        foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                        foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                        foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                        foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                        foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                        foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                        foodOrderReportDtos.add(foodOrderReportDtoObj);
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getCompanyName().equals(foodOrderReportDto.getCompanyName())) {
                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        if (!foodOrderTrackingList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                            }
                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("VISITOR");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                            foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                } else {
                    if (employee.getCompanyName().equals(foodOrderReportDto.getCompanyName())
                            && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {
                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        if (!foodOrderTrackingList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                            }
                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("VISITOR");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                            foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                }
            }
        }

        return foodOrderReportDtos;
    }

    @PostMapping("/food-estimation-and-usage-report")
    public void getFoodEstimationAndUsageReport(HttpServletRequest request, HttpServletResponse
            response, @RequestBody FoodOrderReportDto foodOrderReportDto) throws
            ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Food_Estimation_And_Usage_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            List<FoodOrderReportDto> foodOrderReportDtoObj = foodEstimationAndUsageCalculation(foodOrderReportDto);
            writeToSheetUsage(workbook, foodOrderReportDtoObj, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("SomeThing Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheetUsage(WritableWorkbook workbook, List<FoodOrderReportDto> foodOrderReportDto, int index) throws IOException, WriteException {

        WritableSheet s = workbook.createSheet("Food Estimation Report", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(jxl.format.Colour.GRAY_25);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        WritableFont bodyFont = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat bodyFormat = new WritableCellFormat(bodyFont);
        bodyFormat.setAlignment(LEFT);
        bodyFormat.setBackground(Colour.ICE_BLUE);
        bodyFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 8);
        s.setColumnView(1, 25);
        s.setColumnView(2, 15);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 20);
        s.setColumnView(8, 10);
        s.setColumnView(9, 10);
        s.setColumnView(10, 10);
        s.setColumnView(11, 10);
        s.setColumnView(12, 10);
        s.setColumnView(13, 10);
        s.setColumnView(14, 10);
        s.setColumnView(15, 10);

        s.setRowView(0, 500);
        Label lable = new Label(0, 0, "Food Estimation Report ", headerFormat);
        s.addCell(lable);

        s.addCell(new Label(0, 1, "Sl.No.", headerFormat));
        s.addCell(new Label(1, 1, "From to To", headerFormat));
        s.addCell(new Label(2, 1, "Employee Id", headerFormat));
        s.addCell(new Label(3, 1, "Name", headerFormat));
        s.addCell(new Label(4, 1, "Employee Type", headerFormat));
        s.addCell(new Label(5, 1, "Company", headerFormat));
        s.addCell(new Label(6, 1, "Location", headerFormat));
        s.addCell(new Label(7, 1, "Department", headerFormat));
        s.addCell(new Label(8, 1, "BF Estimate", headerFormat));
        s.addCell(new Label(9, 1, "BF Usage", headerFormat));
        s.addCell(new Label(10, 1, "Lunch Estimate", headerFormat));
        s.addCell(new Label(11, 1, "Lunch Usage", headerFormat));
        s.addCell(new Label(12, 1, "Dinner Estimate", headerFormat));
        s.addCell(new Label(13, 1, "Dinner Usage", headerFormat));
        s.addCell(new Label(14, 1, "Snacks Estimate", headerFormat));
        s.addCell(new Label(15, 1, "Snacks Usage", headerFormat));

        int slno = 1;
        int mainRow = 2;
        for (FoodOrderReportDto foodOrderReportDtoObj : foodOrderReportDto) {
            s.addCell(new Label(0, mainRow, String.valueOf(slno), bodyFormat));
            s.addCell(new Label(1, mainRow, String.valueOf(foodOrderReportDtoObj.getFromDateStr() + " to " + foodOrderReportDtoObj.getToDateStr()), bodyFormat));
            s.addCell(new Label(2, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeCode()), bodyFormat));
            s.addCell(new Label(3, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeName()), bodyFormat));
            s.addCell(new Label(4, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeType()), bodyFormat));
            s.addCell(new Label(5, mainRow, String.valueOf(foodOrderReportDtoObj.getCompanyName()), bodyFormat));
            s.addCell(new Label(6, mainRow, String.valueOf(foodOrderReportDtoObj.getLocation()), bodyFormat));
            s.addCell(new Label(7, mainRow, String.valueOf(foodOrderReportDtoObj.getDept()), bodyFormat));
            s.addCell(new Label(8, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalBfEstimate()), bodyFormat));
            s.addCell(new Label(9, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalBfUsage()), bodyFormat));
            s.addCell(new Label(10, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalLunchEstimate()), bodyFormat));
            s.addCell(new Label(11, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalLunchUsage()), bodyFormat));
            s.addCell(new Label(12, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalDinnerEstimate()), bodyFormat));
            s.addCell(new Label(13, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalDinnerUsage()), bodyFormat));
            s.addCell(new Label(14, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalSnacksEstimate()), bodyFormat));
            s.addCell(new Label(15, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalSnacksUsage()), bodyFormat));

            mainRow = mainRow + 1;
            slno = slno + 1;
        }
        return workbook;
    }

    private List<FoodOrderReportDto> foodEstimationAndUsageCalculation(FoodOrderReportDto foodOrderReportDto) {
        List<FoodOrderReportDto> foodOrderReportDtos = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fromDateDateStr = dateFormat.format(foodOrderReportDto.getFromDate());
        String toDateDateStr = dateFormat.format(foodOrderReportDto.getToDate());
        Date fromDate = DateUtil.convertToDate(fromDateDateStr);
        Date toDate = DateUtil.convertToDate(toDateDateStr);

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("EMPLOYEE") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<Employee> employeeList = employeeService.findAll();
            for (Employee employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true) {
                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            long totalBfUsage = 0;
                            long totalLunchUsage = 0;
                            long totalDinnerUsage = 0;
                            long totalSnacksUsage = 0;
                            long totalMidnightSnacksUsage = 0;


                            if (foodTrackerList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }

                                for (FoodTracker foodTrackerObj : foodTrackerList) {
                                    if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                            && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                        if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfUsage = totalBfUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchUsage = totalLunchUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalDinnerUsage = totalDinnerUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalSnacksUsage = totalSnacksUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                        }
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                                foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                                foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                                foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                                foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true && employee.getCompany().equals(foodOrderReportDto.getCompanyName())) {
                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            long totalBfUsage = 0;
                            long totalLunchUsage = 0;
                            long totalDinnerUsage = 0;
                            long totalSnacksUsage = 0;
                            long totalMidnightSnacksUsage = 0;

                            if (foodTrackerList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }

                                for (FoodTracker foodTrackerObj : foodTrackerList) {
                                    if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                            && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                        if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfUsage = totalBfUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchUsage = totalLunchUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalDinnerUsage = totalDinnerUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalSnacksUsage = totalSnacksUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                        }
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                                foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                                foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                                foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                                foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true && employee.getCompany().equals(foodOrderReportDto.getCompanyName())
                                && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {
                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            long totalBfUsage = 0;
                            long totalLunchUsage = 0;
                            long totalDinnerUsage = 0;
                            long totalSnacksUsage = 0;
                            long totalMidnightSnacksUsage = 0;

                            if (foodTrackerList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }

                                for (FoodTracker foodTrackerObj : foodTrackerList) {
                                    if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                            && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                        if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfUsage = totalBfUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchUsage = totalLunchUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalDinnerUsage = totalDinnerUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalSnacksUsage = totalSnacksUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                        }
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                                foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                                foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                                foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                                foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                }
            }
        }

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("CONTRACT") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<EmpPermanentContract> employeeList = permanentContractRepo.findAll();
            for (EmpPermanentContract employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                        List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        long totalBfUsage = 0;
                        long totalLunchUsage = 0;
                        long totalDinnerUsage = 0;
                        long totalSnacksUsage = 0;
                        long totalMidnightSnacksUsage = 0;

                        if (foodTrackerList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                            }

                            for (FoodTracker foodTrackerObj : foodTrackerList) {
                                if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                        && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                    if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfUsage = totalBfUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchUsage = totalLunchUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalDinnerUsage = totalDinnerUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalSnacksUsage = totalSnacksUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                    }
                                }
                            }

                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                            foodOrderReportDtoObj.setLocation(employee.getLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                            foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                            foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                            foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                            foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                        if (employee.getContractCompany().equals(foodOrderReportDto.getCompanyName())) {
                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                            List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            long totalBfUsage = 0;
                            long totalLunchUsage = 0;
                            long totalDinnerUsage = 0;
                            long totalSnacksUsage = 0;
                            long totalMidnightSnacksUsage = 0;

                            if (foodTrackerList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }

                                for (FoodTracker foodTrackerObj : foodTrackerList) {
                                    if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                            && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                        if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfUsage = totalBfUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchUsage = totalLunchUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalDinnerUsage = totalDinnerUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalSnacksUsage = totalSnacksUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                        }
                                    }
                                }

                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                                foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                                foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                                foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                                foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                        if (employee.getContractCompany().equals(foodOrderReportDto.getCompanyName())
                                && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {
                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                            List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("CONTRACT", employee.getEmployeeCode());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            long totalBfUsage = 0;
                            long totalLunchUsage = 0;
                            long totalDinnerUsage = 0;
                            long totalSnacksUsage = 0;
                            long totalMidnightSnacksUsage = 0;

                            if (foodTrackerList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                            && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                        if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfEstimate = totalBfEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchEstimate = totalLunchEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalSnacksEstimate = totalSnacksEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalDinnerEstimate = totalDinnerEstimate + 1;
                                        } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            midnightSnackEstimate = midnightSnackEstimate + 1;
                                        }
                                    }
                                }

                                for (FoodTracker foodTrackerObj : foodTrackerList) {
                                    if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                            && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                        if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                            totalBfUsage = totalBfUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                            totalLunchUsage = totalLunchUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                            totalDinnerUsage = totalDinnerUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                            totalSnacksUsage = totalSnacksUsage + 1;
                                        } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                            totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                        }
                                    }
                                }

                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                                foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                                foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                                foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                                foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                }
            }
        }

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("VISITOR") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<VisitorPass> employeeList = visitorService.getAllVisitors();
            for (VisitorPass employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (true) {
                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                        List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        long totalBfUsage = 0;
                        long totalLunchUsage = 0;
                        long totalDinnerUsage = 0;
                        long totalSnacksUsage = 0;
                        long totalMidnightSnacksUsage = 0;

                        if (!foodTrackerList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                            }

                            for (FoodTracker foodTrackerObj : foodTrackerList) {
                                if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                        && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                    if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfUsage = totalBfUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchUsage = totalLunchUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalDinnerUsage = totalDinnerUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalSnacksUsage = totalSnacksUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                    }
                                }
                            }

                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("VISITOR");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                            foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                            foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                            foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                            foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                            foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getCompanyName().equals(foodOrderReportDto.getCompanyName())) {
                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                        List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        long totalBfUsage = 0;
                        long totalLunchUsage = 0;
                        long totalDinnerUsage = 0;
                        long totalSnacksUsage = 0;
                        long totalMidnightSnacksUsage = 0;


                        if (!foodTrackerList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }

                                }
                            }

                            for (FoodTracker foodTrackerObj : foodTrackerList) {
                                if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                        && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                    if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfUsage = totalBfUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchUsage = totalLunchUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalDinnerUsage = totalDinnerUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalSnacksUsage = totalSnacksUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                    }
                                }
                            }

                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("VISITOR");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                            foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                            foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                            foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                            foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                            foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                } else {
                    if (employee.getCompanyName().equals(foodOrderReportDto.getCompanyName())
                            && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {
                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                        List<FoodTracker> foodTrackerList = foodTrackerRepository.findByEmployeeTypeByEmployeeCode("VISITOR", employee.getMobileNumber());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        long totalBfUsage = 0;
                        long totalLunchUsage = 0;
                        long totalDinnerUsage = 0;
                        long totalSnacksUsage = 0;
                        long totalMidnightSnacksUsage = 0;

                        if (!foodTrackerList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                            }

                            for (FoodTracker foodTrackerObj : foodTrackerList) {
                                if ((foodTrackerObj.getMarkedOn().equals(fromDate) || foodTrackerObj.getMarkedOn().after(fromDate))
                                        && (foodTrackerObj.getMarkedOn().equals(toDate) || foodTrackerObj.getMarkedOn().before(toDate))) {
                                    if (foodTrackerObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfUsage = totalBfUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchUsage = totalLunchUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalDinnerUsage = totalDinnerUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalSnacksUsage = totalSnacksUsage + 1;
                                    } else if (foodTrackerObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        totalMidnightSnacksUsage = totalMidnightSnacksUsage + 1;
                                    }
                                }
                            }

                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("VISITOR");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                            foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtoObj.setTotalBfUsage(totalBfUsage);
                            foodOrderReportDtoObj.setTotalLunchUsage(totalLunchUsage);
                            foodOrderReportDtoObj.setTotalDinnerUsage(totalDinnerUsage);
                            foodOrderReportDtoObj.setTotalSnacksUsage(totalSnacksUsage);
                            foodOrderReportDtoObj.setTotalMidnightSnacksUsage(totalMidnightSnacksUsage);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                }
            }
        }

        return foodOrderReportDtos;
    }

    @GetMapping("/food-estimation-canteen-report")
    public void getFoodEstimationCanteenReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Food_Estimation_Current_Day_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            Date today = new Date();
            FoodOrderReportDto foodOrderReportDto = new FoodOrderReportDto();
            foodOrderReportDto.setFromDate(today);
            foodOrderReportDto.setToDate(today);
            foodOrderReportDto.setCompanyName("ALL");
            foodOrderReportDto.setDept("ALL");
            foodOrderReportDto.setEmployeeType("ALL");
            List<FoodOrderReportDto> foodOrderReportDtoObj = foodEstimationCanteenCalculation(foodOrderReportDto);
            writeToCanteenSheet(workbook, foodOrderReportDtoObj, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("SomeThing Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToCanteenSheet(WritableWorkbook workbook, List<FoodOrderReportDto> foodOrderReportDto, int index) throws IOException, WriteException {

        WritableSheet s = workbook.createSheet("Food Estimation Report", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(jxl.format.Colour.GRAY_25);
        headerFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        WritableFont bodyFont = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat bodyFormat = new WritableCellFormat(bodyFont);
        bodyFormat.setAlignment(LEFT);
        bodyFormat.setBackground(Colour.ICE_BLUE);
        bodyFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 8);
        s.setColumnView(1, 25);
        s.setColumnView(2, 15);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 20);
        s.setColumnView(8, 10);
        s.setColumnView(9, 10);
        s.setColumnView(10, 10);
        s.setColumnView(11, 10);

        s.setRowView(0, 500);
        Label lable = new Label(0, 0, "Food Estimation Report ", headerFormat);
        s.addCell(lable);

        s.addCell(new Label(0, 1, "Sl.No.", headerFormat));
        s.addCell(new Label(1, 1, "Date", headerFormat));
        s.addCell(new Label(2, 1, "Employee Id", headerFormat));
        s.addCell(new Label(3, 1, "Name", headerFormat));
        s.addCell(new Label(4, 1, "Employee Type", headerFormat));
        s.addCell(new Label(5, 1, "Company", headerFormat));
        s.addCell(new Label(6, 1, "Location", headerFormat));
        s.addCell(new Label(7, 1, "Department", headerFormat));
        s.addCell(new Label(8, 1, "BF Estimate", headerFormat));
        s.addCell(new Label(9, 1, "Lunch Estimate", headerFormat));
        s.addCell(new Label(10, 1, "Dinner Estimate", headerFormat));
        s.addCell(new Label(11, 1, "Snacks Estimate", headerFormat));

        int slno = 1;
        int mainRow = 2;
        for (FoodOrderReportDto foodOrderReportDtoObj : foodOrderReportDto) {
            s.addCell(new Label(0, mainRow, String.valueOf(slno), bodyFormat));
            s.addCell(new Label(1, mainRow, String.valueOf(foodOrderReportDtoObj.getFromDateStr()), bodyFormat));
            s.addCell(new Label(2, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeCode()), bodyFormat));
            s.addCell(new Label(3, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeName()), bodyFormat));
            s.addCell(new Label(4, mainRow, String.valueOf(foodOrderReportDtoObj.getEmployeeType()), bodyFormat));
            s.addCell(new Label(5, mainRow, String.valueOf(foodOrderReportDtoObj.getCompanyName()), bodyFormat));
            s.addCell(new Label(6, mainRow, String.valueOf(foodOrderReportDtoObj.getLocation()), bodyFormat));
            s.addCell(new Label(7, mainRow, String.valueOf(foodOrderReportDtoObj.getDept()), bodyFormat));
            s.addCell(new Label(8, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalBfEstimate()), bodyFormat));
            s.addCell(new Label(9, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalLunchEstimate()), bodyFormat));
            s.addCell(new Label(10, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalDinnerEstimate()), bodyFormat));
            s.addCell(new Label(11, mainRow, String.valueOf(foodOrderReportDtoObj.getTotalSnacksEstimate()), bodyFormat));

            mainRow = mainRow + 1;
            slno = slno + 1;
        }
        return workbook;
    }

    /**
     * For today
     *
     * @param foodOrderReportDto
     * @return
     */
    private List<FoodOrderReportDto> foodEstimationCanteenCalculation(FoodOrderReportDto foodOrderReportDto) {
        List<FoodOrderReportDto> foodOrderReportDtos = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fromDateDateStr = dateFormat.format(foodOrderReportDto.getFromDate());
        String toDateDateStr = dateFormat.format(foodOrderReportDto.getToDate());
        Date fromDate = DateUtil.convertToDate(fromDateDateStr);
        Date toDate = DateUtil.convertToDate(toDateDateStr);

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("EMPLOYEE") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<Employee> employeeList = employeeService.findAll();
            for (Employee employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") &&
                        foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) ||
                            employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true) {
                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
//                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCode("EMPLOYEE", employee.getEmployeeCode());
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("EMPLOYEE", employee.getEmployeeCode(), DateUtil.getTodayDate());
                            System.out.println("sizeeeeeeeeeeee " + foodOrderTrackingList.size());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true && employee.getCompany().equals(foodOrderReportDto.getCompanyName())) {

                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("EMPLOYEE", employee.getEmployeeCode(), DateUtil.getTodayDate());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else {
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        if (employee.getStatus() == true && employee.getCompany().equals(foodOrderReportDto.getCompanyName())
                                && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {

                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("EMPLOYEE", employee.getEmployeeCode(), DateUtil.getTodayDate());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("EMPLOYEE");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                }
            }
        }

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("CONTRACT") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<EmpPermanentContract> employeeList = permanentContractRepo.findAll();
            for (EmpPermanentContract employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {

                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("CONTRACT", employee.getEmployeeCode(), DateUtil.getTodayDate());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;

                        if (!foodOrderTrackingList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
//                            if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
//                                    && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                    totalBfEstimate = totalBfEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                    totalLunchEstimate = totalLunchEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                    totalSnacksEstimate = totalSnacksEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                    totalDinnerEstimate = totalDinnerEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                    midnightSnackEstimate = midnightSnackEstimate + 1;
                                }
//                            }
                            }
                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                            foodOrderReportDtoObj.setLocation(employee.getLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                        if (employee.getContractCompany().equals(foodOrderReportDto.getCompanyName())) {

                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("CONTRACT", employee.getEmployeeCode(), DateUtil.getTodayDate());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
//                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
//                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
//                                }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                } else {
                    if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                        if (employee.getContractCompany().equals(foodOrderReportDto.getCompanyName())
                                && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {

                            FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                            List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("CONTRACT", employee.getEmployeeCode(), DateUtil.getTodayDate());
                            long totalBfEstimate = 0;
                            long totalLunchEstimate = 0;
                            long totalDinnerEstimate = 0;
                            long totalSnacksEstimate = 0;
                            long midnightSnackEstimate = 0;

                            if (!foodOrderTrackingList.isEmpty()) {
                                for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
//                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
//                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
//                                }
                                }
                                foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                                foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                                foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                                foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                                foodOrderReportDtoObj.setEmployeeType("CONTRACT");
                                foodOrderReportDtoObj.setEmployeeCode(employee.getEmployeeCode());
                                foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                                foodOrderReportDtoObj.setCompanyName(employee.getContractCompany());
                                foodOrderReportDtoObj.setLocation(employee.getLocation());
                                foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                                foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                                foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                                foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                                foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                                foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                                foodOrderReportDtos.add(foodOrderReportDtoObj);
                            }
                        }
                    }
                }
            }
        }

        if (foodOrderReportDto.getEmployeeType().equalsIgnoreCase("VISITOR") || foodOrderReportDto.getEmployeeType().equalsIgnoreCase("ALL")) {
            List<VisitorPass> employeeList = visitorService.getAllVisitors();
            for (VisitorPass employee : employeeList) {
                if (foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                    List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("VISITOR", employee.getMobileNumber(), DateUtil.getTodayDate());
                    long totalBfEstimate = 0;
                    long totalLunchEstimate = 0;
                    long totalDinnerEstimate = 0;
                    long totalSnacksEstimate = 0;
                    long midnightSnackEstimate = 0;
                    if (!foodOrderTrackingList.isEmpty()) {
                        for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
//                        if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
//                                && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                            if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                totalBfEstimate = totalBfEstimate + 1;
                            } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                totalLunchEstimate = totalLunchEstimate + 1;
                            } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                totalSnacksEstimate = totalSnacksEstimate + 1;
                            } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                totalDinnerEstimate = totalDinnerEstimate + 1;
                            } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                midnightSnackEstimate = midnightSnackEstimate + 1;
                            }
//                        }
                        }
                        foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                        foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                        foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                        foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                        foodOrderReportDtoObj.setEmployeeType("VISITOR");
                        foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                        foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                        foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                        foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                        foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                        foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                        foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                        foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                        foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                        foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                        foodOrderReportDtos.add(foodOrderReportDtoObj);
                    }
                } else if (!foodOrderReportDto.getCompanyName().equalsIgnoreCase("ALL") && foodOrderReportDto.getDept().equalsIgnoreCase("ALL")) {
                    if (employee.getCompanyName().equals(foodOrderReportDto.getCompanyName())) {
                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("VISITOR", employee.getMobileNumber(), DateUtil.getTodayDate());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        if (!foodOrderTrackingList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
//                            if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
//                                    && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                    totalBfEstimate = totalBfEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                    totalLunchEstimate = totalLunchEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                    totalSnacksEstimate = totalSnacksEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                    totalDinnerEstimate = totalDinnerEstimate + 1;
                                } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                    midnightSnackEstimate = midnightSnackEstimate + 1;
                                }
//                            }
                            }
                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("VISITOR");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                            foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                } else {
                    if (employee.getCompanyName().equals(foodOrderReportDto.getCompanyName())
                            && employee.getDepartmentName().equals(foodOrderReportDto.getDept())) {
                        FoodOrderReportDto foodOrderReportDtoObj = new FoodOrderReportDto();
                        List<FoodOrderTracking> foodOrderTrackingList = foodOrderTrackingService.getByEmployeeTypeByEmployeeCodeAndMarkedOn("VISITOR", employee.getMobileNumber(), DateUtil.getTodayDate());
                        long totalBfEstimate = 0;
                        long totalLunchEstimate = 0;
                        long totalDinnerEstimate = 0;
                        long totalSnacksEstimate = 0;
                        long midnightSnackEstimate = 0;
                        if (!foodOrderTrackingList.isEmpty()) {
                            for (FoodOrderTracking foodOrderTrackingObj : foodOrderTrackingList) {
                                if ((foodOrderTrackingObj.getOrderedOn().equals(fromDate) || foodOrderTrackingObj.getOrderedOn().after(fromDate))
                                        && (foodOrderTrackingObj.getOrderedOn().equals(toDate) || foodOrderTrackingObj.getOrderedOn().before(toDate))) {
                                    if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("BREAK_FAST")) {
                                        totalBfEstimate = totalBfEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("LUNCH")) {
                                        totalLunchEstimate = totalLunchEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("SNACK")) {
                                        totalSnacksEstimate = totalSnacksEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("DINNER")) {
                                        totalDinnerEstimate = totalDinnerEstimate + 1;
                                    } else if (foodOrderTrackingObj.getFoodType().equalsIgnoreCase("MIDNIGHT_SNACK")) {
                                        midnightSnackEstimate = midnightSnackEstimate + 1;
                                    }
                                }
                            }
                            foodOrderReportDtoObj.setFromDate(foodOrderReportDto.getFromDate());
                            foodOrderReportDtoObj.setToDate(foodOrderReportDto.getToDate());
                            foodOrderReportDtoObj.setFromDateStr(fromDateDateStr);
                            foodOrderReportDtoObj.setToDateStr(toDateDateStr);
                            foodOrderReportDtoObj.setEmployeeType("VISITOR");
                            foodOrderReportDtoObj.setEmployeeCode(employee.getMobileNumber());
                            foodOrderReportDtoObj.setEmployeeName(employee.getFirstName());
                            foodOrderReportDtoObj.setCompanyName(employee.getCompanyName());
                            foodOrderReportDtoObj.setLocation(employee.getVisitorLocation());
                            foodOrderReportDtoObj.setDept(employee.getDepartmentName());
                            foodOrderReportDtoObj.setTotalBfEstimate(totalBfEstimate);
                            foodOrderReportDtoObj.setTotalLunchEstimate(totalLunchEstimate);
                            foodOrderReportDtoObj.setTotalDinnerEstimate(totalDinnerEstimate);
                            foodOrderReportDtoObj.setTotalSnacksEstimate(totalSnacksEstimate);
                            foodOrderReportDtoObj.setTotalMidnightSnackEstimate(midnightSnackEstimate);
                            foodOrderReportDtos.add(foodOrderReportDtoObj);
                        }
                    }
                }
            }
        }

        return foodOrderReportDtos;
    }

}
