package com.dfq.coeffi.report;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.payroll.EmployeeAttendanceController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.MonthlyEmployeeAttendanceDto;
import com.dfq.coeffi.dto.MonthlyStatusDto;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.master.assignShifts.EmployeeShiftAssignmentService;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftRepository;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static jxl.format.Alignment.*;

@RestController
@Slf4j
public class InOutReport extends BaseController {


    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeAttendanceController employeeAttendanceController;
    @Autowired
    ShiftRepository shiftRepository;

    @PostMapping("report/in-out")
    public ResponseEntity<List<EmployeeAttendance>> form22(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = employeeAttendanceController.viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Monthly_Late_Entry_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                form22(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
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

    @Autowired
    CompanyNameService companyNameService;

    private static DecimalFormat df = new DecimalFormat("0.00");

    private WritableWorkbook form22(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("In-out", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 9);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);

        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        headerFormatLeft.setBorder(Border.ALL, BorderLineStyle.THIN);

        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);

        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(jxl.format.Colour.GRAY_25);
        cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.GRAY_80);

        WritableFont cellFont1 = new WritableFont(WritableFont.TIMES, 7);
        cellFont1.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat1 = new WritableCellFormat(cellFont1);
        cellFormat1.setAlignment(LEFT);
        cellFormat1.setBackground(jxl.format.Colour.GRAY_25);
        cellFormat1.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.GRAY_80);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        cf.setBoldStyle(WritableFont.NO_BOLD);
        WritableCellFormat c = new WritableCellFormat(cf);
        c.setAlignment(CENTRE);
        c.setBackground(jxl.format.Colour.ICE_BLUE);
        c.setBorder(Border.ALL, BorderLineStyle.THIN);

        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
        cLeft.setBorder(Border.ALL, BorderLineStyle.THIN);

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

        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName() + " STAFF", headerFormat);
        s.addCell(lable);
        String monthName = DateUtil.getMonthName(fromDate);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String fd = df.format(fromDate);
        String td = df.format(toDate);
        Label lableSlip = new Label(0, 1, "Monthly In and Out Report (From:  " + fd + "     To " + td + ")", headerFormat);
        s.addCell(lableSlip);

        int rowNum = 2;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            String dep ="";
            String des = "";
            if (employeeAttendanceDto.getDepartmentName()!=null){
                dep = employeeAttendanceDto.getDepartmentName();
            }
            if (employeeAttendanceDto.getDesignationName()!=null){
                des = employeeAttendanceDto.getDesignationName();
            }
            int colNum = 1;
            s.mergeCells(0, rowNum, 30, rowNum);
            s.addCell(new Label(0, rowNum, "Name/Code: " + employeeAttendanceDto.getEmployeeName() + "/" + employeeAttendanceDto.getEmployeeCode() + "    " + "Department: " + dep + "    " + "Designation: " + des + "    " + "Emp_Type: " + employeeAttendanceDto.getEmployeeType(), cellFormat1));
            s.addCell(new Label(0, rowNum + 1, "Days", cellFormat));
            s.addCell(new Label(0, rowNum + 2, "In", cellFormat));
            s.addCell(new Label(0, rowNum + 3, "Lunch_out", cellFormat));
            s.addCell(new Label(0, rowNum + 4, "Lunch_in", cellFormat));
            s.addCell(new Label(0, rowNum + 5, "Out", cellFormat));
            s.addCell(new Label(0, rowNum + 6, "Late_in", cellFormat));
            s.addCell(new Label(0, rowNum + 7, "Early_out", cellFormat));
            s.addCell(new Label(0, rowNum + 8, "OT", cellFormat));
            s.addCell(new Label(0, rowNum + 9, "T_Hrs", cellFormat));

            int j = colNum;
            List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
            for (int i = 0; i < dates.size(); i++) {
                s.mergeCells(0, rowNum, j, rowNum);
                java.text.DateFormat formatter = new SimpleDateFormat("dd");
                s.addCell(new Label(j, rowNum + 1, formatter.format(dates.get(i)), cellFormat));
                j = j + 1;
            }
            s.setColumnView(j + 1, 12);
            s.addCell(new Label(j + 1, rowNum + 2, "Total work hrs", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 3, "Total lunch hrs", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 4, "Total lateIn hrs", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 5, "Total earlyGoing hrs", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 6, "Total OT hrs", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 7, "Monthly planned hrs", cellFormat));
            s.addCell(new Label(j + 1, rowNum + 8, "Exact worked hrs", cellFormat));

            double leaves;
            double present;
            long sundays;
            long holidays;
            long absents;
            long compoffs;
            double totalWorkedHours = 0;
            double totalOtHours = 0;
            double lateIn = 0;
            double earlyOut = 0;
            double exactHrs = 0;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
                String inTime = null;
                String outTime = null;
                String workedHours = null;
                String li = "";
                String eo = "";
                String overTime = "";
                s.setColumnView(colNum, 5);
                System.out.println("employeeAttendanceDto.getMonthlyStatus().size(): " + employeeAttendanceDto.getMonthlyStatus().size());
                if (/*employeeAttendanceDto.getMonthlyStatus() != null &&*/ employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays <= employeeAttendanceDto.getMonthlyStatus().size()) {
                    DateFormat timeFormat = new SimpleDateFormat("HH.mm");
                    try {
//                        employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null
                        if (employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null && employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {

                            inTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getInTime());
//                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
//                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();
//                            inTime = "P";

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                inTime = "";
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
                                inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                inTime = "HALF_DAY";

                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
                                if (employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null) {
                                    inTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getInTime());
                                }
//                                workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
//                                overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();
                            }
//                            inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                        if (/*employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null &&*/ employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null) {
                                outTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime());
                                workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs() != null) {
                                overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getLateEntry() != null) {
                                li = employeeAttendanceDto.getMonthlyStatus().get(m).getLateEntry();
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getEarlyOut() != null) {
                                eo = employeeAttendanceDto.getMonthlyStatus().get(m).getEarlyOut();
                            }

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                outTime = "";
                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                                outTime = "";
                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
                                outTime = "";
                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                                outTime = "";
                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
                                outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                outTime = "HALF_DAY";

                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
                                outTime = "";

                            }
//                            outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Index out of bound");
                    }
//                    lateIn = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalLateEntry();
//                    earlyOut = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalEarlyCheckOut();
//                    totalOtHours = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalOtHours();
                    s.addCell(new Label(colNum, rowNum + 2, inTime, c));
                    s.addCell(new Label(colNum, rowNum + 3, "", c));
                    s.addCell(new Label(colNum, rowNum + 4, "", c));
                    s.addCell(new Label(colNum, rowNum + 5, outTime, c));
                    s.addCell(new Label(colNum, rowNum + 6,li , c));
                    s.addCell(new Label(colNum, rowNum + 7, eo, c));
                    s.addCell(new Label(colNum, rowNum + 8, overTime, c));
                    s.addCell(new Label(colNum, rowNum + 9, workedHours, c));
                    colNum = colNum + 1;
                } else {
//                    s.addCell(new Label(colNum, rowNum, "" + "-", c));
                    s.addCell(new Label(colNum, rowNum + 2, "-", c));
                    s.addCell(new Label(colNum, rowNum + 3, "-", c));
                    s.addCell(new Label(colNum, rowNum + 4, "-", c));
                    s.addCell(new Label(colNum, rowNum + 5, "-", c));
                    s.addCell(new Label(colNum, rowNum + 6, "-", c));
                    colNum = colNum + 1;
                }

            }

            for (int i = noOfDays + 1; i <= noOfDays + 8; i++) {
                if (employeeAttendanceDto.getMonthlyStatus() != null && employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays < employeeAttendanceDto.getMonthlyStatus().size()) {
                    present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfPresentDays();
//                  present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHalfDays();
                    sundays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfSudays();
                    holidays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHolidays();
                    leaves = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfLeaves();

                    absents = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfAbsent();
                    compoffs = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfCompOffs();
                    totalWorkedHours = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalHours();
                    totalOtHours = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalOtHours();

                    lateIn = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalLateEntry();
                    earlyOut = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalEarlyCheckOut();
                    exactHrs = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalExactWorkedHrs();

                } else {

                }
            }
            s.addCell(new Label(j + 2, rowNum + 2, "08.00", c));
            s.addCell(new Label(j + 2, rowNum + 3, "00.30", c));
            s.addCell(new Label(j + 2, rowNum + 4, String.valueOf(lateIn), c));
            s.addCell(new Label(j + 2, rowNum + 5, String.valueOf(earlyOut), c));
            s.addCell(new Label(j + 2, rowNum + 6, String.valueOf(totalOtHours), c));
            s.addCell(new Label(j + 2, rowNum + 7, "" + totalWorkedHours, c));
            s.addCell(new Label(j + 2, rowNum + 8, "" + exactHrs, c));
            rowNum = rowNum + 11;


        }
        return workbook;
    }

}
