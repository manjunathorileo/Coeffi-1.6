package com.dfq.coeffi.report;

import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.MonthlyEmployeeAttendanceDto;
import com.dfq.coeffi.dto.MonthlyStatusDto;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import com.sun.mail.smtp.SMTPTransport;
import jxl.format.Colour;
import jxl.write.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.*;

@RestController
public class ReportEmailController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeAttendanceService employeeAttendanceService;

    @PostMapping("employee-attendance-email/monthly-attendance")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeMonthlyAttendanceReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
            throw new EntityNotFoundException("Selected Date of Month Should be same");
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Attendance_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
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

        Workbook workbook1 = (Workbook) workbook;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook1.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    private List<MonthlyEmployeeAttendanceDto> viewMonthlyEmployeeAttendance(DateDto dateDto) throws ParseException {
        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
            throw new EntityNotFoundException("Selected Date of Month Should be same");
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = new ArrayList<MonthlyEmployeeAttendanceDto>();
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            // TODO
            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId
                    (DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
            List<EmployeeAttendance> presentSize = new ArrayList<>();
            List<EmployeeAttendance> holidaySize = new ArrayList<>();
            List<EmployeeAttendance> sundaySize = new ArrayList<>();
            List<EmployeeAttendance> halfDaySize = new ArrayList<>();
            List<EmployeeAttendance> leaveSize = new ArrayList<>();
            for (EmployeeAttendance employeeAttendance : monthlyEmployeeAttendance) {
                MonthlyStatusDto dto = new MonthlyStatusDto();
                dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
                dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
                dto.setMarkedOn(employeeAttendance.getMarkedOn());
                dto.setInTime(employeeAttendance.getInTime());
                dto.setOutTime(employeeAttendance.getOutTime());
                dto.setLateEntry(employeeAttendance.getLateEntry());
                dto.setWorkedHours(employeeAttendance.getWorkedHours());
                dto.setExtraHrs(employeeAttendance.getOverTime());
                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.PRESENT) {
                    presentSize.add(employeeAttendance);
                }
                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.SUNDAY) {
                    sundaySize.add(employeeAttendance);
                }
                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.HOLIDAY) {
                    holidaySize.add(employeeAttendance);
                }
                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.LEAVE || employeeAttendance.getAttendanceStatus() == AttendanceStatus.EL || employeeAttendance.getAttendanceStatus() == AttendanceStatus.CL || employeeAttendance.getAttendanceStatus() == AttendanceStatus.ML) {
                    leaveSize.add(employeeAttendance);
                }
                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.PH) {
                    holidaySize.add(employeeAttendance);
                }
                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.HALF_DAY) {
                    halfDaySize.add(employeeAttendance);
                }
                double totalPresent = presentSize.size() + (halfDaySize.size() * 0.5);
                dto.setNoOfPresentDays(totalPresent);
                dto.setNoOfSudays(sundaySize.size());
                dto.setNoOfHolidays(holidaySize.size());
                dto.setNoOfLeaves(leaveSize.size() + (halfDaySize.size() * 0.5));
                monthlyStatusDtos.add(dto);
            }
            mADto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            mADto.setEmployeeCode(employee.getEmployeeCode());
            mADto.setMonthlyStatus(monthlyStatusDtos);
            mADto.setEmployeeId(employee.getId());
            if (employee.getDepartment() != null) {
                mADto.setDepartmentId(employee.getDepartment().getId());
                mADto.setDepartmentName(employee.getDepartment().getName());
                mADto.setDesignationId(employee.getDesignation().getId());
                mADto.setDesignationName(employee.getDesignation().getName());
            }
            if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
                monthlyEmployeeAttendanceDtos.add(mADto);
            }
        }
        return monthlyEmployeeAttendanceDtos;
    }

    private WritableWorkbook attendanceEntry(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Monthly-Attendance-Report", index);
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
        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.mergeCells(0, 0, 64, 0);
        s.mergeCells(0, 1, 64, 1);
        s.mergeCells(2, 18, 5, 18);
        Label lable = new Label(0, 0, "***** PVT LTD", headerFormat);
        s.addCell(lable);
        String monthName = DateUtil.getMonthName(fromDate);
        Label lableSlip = new Label(0, 1, "Monthly Attendance Report:  " + monthName, headerFormat);
        s.addCell(lableSlip);

        int j = 5;
        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
        for (int i = 0; i < dates.size(); i++) {
            s.mergeCells(j, 2, j + 1, 2);
            java.text.DateFormat formatter = new SimpleDateFormat("dd");
            s.addCell(new Label(j, 2, "" + formatter.format(dates.get(i)), cellFormatDate));
            s.addCell(new Label(j, 3, "In-Time", cellFormat));
            s.addCell(new Label(j + 1, 3, "Out-Time", cellFormat));
            s.addCell(new Label(j + 2, 3, "WH", cellFormat));
            s.addCell(new Label(j + 3, 3, "OT", cellFormat));
            j = j + 4;
        }
        s.addCell(new Label(j + 1, 3, "Present", cellFormat));
        s.addCell(new Label(j + 2, 3, "Holidays", cellFormat));
        s.addCell(new Label(j + 3, 3, "Sundays", cellFormat));
        s.addCell(new Label(j + 4, 3, "Leaves", cellFormat));

        int rowNum = 4;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3)));
            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode()));
            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName()));
            s.addCell(new Label(3, 3, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartmentName()));
            s.addCell(new Label(4, 3, "Designation Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDesignationName()));
            int colNum = 5;
            double leaves;
            double present;
            long sundays;
            long holidays;
            String inTime = null;
            String outTime = null;
            String workedHours = null;
            String overTime = null;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
                if (/*employeeAttendanceDto.getMonthlyStatus() != null &&*/ employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays <= employeeAttendanceDto.getMonthlyStatus().size()) {
                    DateFormat timeFormat = new SimpleDateFormat("HH.mm");
                    try {
//                        employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null
                        if (employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null && employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {

                            inTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getInTime());
                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                inTime = "AB";
                                workedHours = "0";
                                overTime = "0";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                                inTime = "H";
                                workedHours = "0";
                                overTime = "0";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
                                inTime = "PH";
                                workedHours = "0";
                                overTime = "0";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                                inTime = "S";
                                workedHours = "0";
                                overTime = "0";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
                                inTime = "L";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                inTime = "HALF-DAY";

                            }
//                            inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                        if (/*employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null &&*/ employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                            outTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime());
                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                outTime = "AB";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                                outTime = "H";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
                                outTime = "PH";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                                outTime = "S";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
                                outTime = "L";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                outTime = "HALF-DAY";

                            }
//                            outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Index out of bound");
                    }

                    if (workedHours != null) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        workedHours = df.format(Double.valueOf(workedHours));
                        workedHours = String.valueOf(Double.valueOf(workedHours));
                    }
                    s.addCell(new Label(colNum, rowNum, "" + inTime));
                    s.addCell(new Label(colNum + 1, rowNum, "" + outTime));
                    s.addCell(new Label(colNum + 2, rowNum, "" + workedHours));
                    s.addCell(new Label(colNum + 3, rowNum, "" + overTime));
                    colNum = colNum + 4;
                } else {
                    s.addCell(new Label(colNum, rowNum, "" + "-"));
                    s.addCell(new Label(colNum + 1, rowNum, "" + "-"));
                    s.addCell(new Label(colNum + 2, rowNum, "" + "-"));
                    s.addCell(new Label(colNum + 3, rowNum, "" + "-"));
                    colNum = colNum + 4;
                }

            }

            for (int i = noOfDays + 1; i <= noOfDays + 4; i++) {
                if (employeeAttendanceDto.getMonthlyStatus() != null && employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays < employeeAttendanceDto.getMonthlyStatus().size()) {
                    present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfPresentDays();
//                  present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHalfDays();
                    sundays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfSudays();
                    holidays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHolidays();
                    leaves = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfLeaves();
                    s.addCell(new Label(j + 1, rowNum, "" + present));
                    s.addCell(new Label(j + 2, rowNum, "" + holidays));
                    s.addCell(new Label(j + 3, rowNum, "" + sundays));
                    s.addCell(new Label(j + 4, rowNum, "" + leaves));
                } else {
                    s.addCell(new Label(j + 1, rowNum, "" + "-"));
                    s.addCell(new Label(j + 2, rowNum, "" + "-"));
                    s.addCell(new Label(j + 3, rowNum, "" + "-"));
                }
            }
            rowNum = rowNum + 1;


        }
        return workbook;
    }


    @GetMapping("email-check")
    public void configEmail(Employee employee, byte[] baos) {
        Optional<Employee> emp = employeeService.getEmployee(Long.valueOf(6));
        employee = emp.get();
        try {
            Session mailSession = Session.getInstance(System.getProperties());
            Transport transport = new SMTPTransport(mailSession, new URLName("smtp.gmail.com"));
            transport = mailSession.getTransport("smtps");
            transport.connect("smtp.gmail.com", 465, "orileotest@gmail.com", "yakanna@123");

            MimeMessage m = new MimeMessage(mailSession);
            m.setFrom(new InternetAddress("orileotest@gmail.com"));
            if (employee.getEmployeeLogin() == null) {
//                throw new EntityNotFoundException("Email id is not found for employee id: " + employee.getEmployeeCode());
                System.out.println("null");
            }
            Address[] toAddr = new InternetAddress[]{
                    new InternetAddress("kurubaspy@gmail.com")
            };
            m.setRecipients(Message.RecipientType.TO, toAddr);
            m.setHeader("Content-Type", "multipart/mixed");
            m.setSubject("Pay Slip for " + employee.getFirstName() + " " + employee.getLastName() + " for " + "**");
            m.setSentDate(new Date());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Dear " + employee.getFirstName() + " " + employee.getLastName() + ",\nPlease find the attached payslip for month of " + "-" + ".\n \n\n\n\n *******" +
                    "THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL *******");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
//            DataSource source = new ByteArrayDataSource(employee.getDocuments().get(0).getData(), "application/pdf");
            DataSource source = new ByteArrayDataSource(employee.getDocuments().get(0).getData(), employee.getDocuments().get(0).getFileType());
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("PaySlip-" + employee.getFirstName() + ".pdf");
            multipart.addBodyPart(messageBodyPart);
            m.setContent(multipart);
            transport.sendMessage(m, m.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
