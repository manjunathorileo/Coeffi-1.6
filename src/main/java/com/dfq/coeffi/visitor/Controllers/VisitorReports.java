package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.report.ReportController;
import com.dfq.coeffi.visitor.Entities.Visitor;
import com.dfq.coeffi.visitor.Entities.VisitorDateDto;
import com.dfq.coeffi.visitor.Entities.VisitorDto;
import com.dfq.coeffi.visitor.Services.VisitorService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jxl.format.Alignment.*;

/*Evacuation report – List of visitors available on-site during any emergency situation
Location wise report - NA
Visitor wise report *****  ****VisitorWise
Department wise report  *******     ******view
Visit Type wise report  *******     *****Type
Visitor Engagement Hours --> Test   */

@RestController
public class VisitorReports extends BaseController {
    @Autowired
    private final VisitorService visitorService;

    public VisitorReports(VisitorService visitorService) {
        this.visitorService = visitorService;
    }


    @PostMapping("visitor-report/all-visitors-report")
    private void visitorwiseReport(@RequestBody VisitorDateDto visitorDateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Visitor> vivoInfoList = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());
        if (vivoInfoList.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Visitor-by-Department.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet5(workbook, response, 0, vivoInfoList);
            //workbook.createSheet()
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet5(WritableWorkbook workbook, HttpServletResponse response, int index, List<Visitor> vivoInfoList) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);


        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat1.setBackground(Colour.GRAY_50);

        s.setColumnView(0, 20);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.mergeCells(0, 0, 6, 0);

        s.addCell(new Label(0, 0, "Overall Reports of Visitor", headerFormat));
        s.addCell(new Label(0, 1, "VisitorName", headerFormat1));
        s.addCell(new Label(1, 1, "EmailId", headerFormat1));
        s.addCell(new Label(2, 1, "DateOfVisit", headerFormat1));
        s.addCell(new Label(3, 1, "TypeOfVisit", headerFormat1));
        s.addCell(new Label(4, 1, "PersonToMeet", headerFormat1));
        s.addCell(new Label(5, 1, "CheckInTime", headerFormat1));
        s.addCell(new Label(6, 1, "CheckOutTime", headerFormat1));

        int rownum = 2;

        for (Visitor vivoInfo : vivoInfoList) {
            s.addCell(new Label(0, rownum, "" + vivoInfo.getFirstName()));
            s.addCell(new Label(1, rownum, "" + vivoInfo.getEmail()));
            s.addCell(new Label(2, rownum, "" + vivoInfo.getDateOfVisit()));
            s.addCell(new Label(3, rownum, "" + vivoInfo.getVisitType()));
            s.addCell(new Label(4, rownum, "" + vivoInfo.getPersonToVisit()));
            s.addCell(new Label(5, rownum, "" + vivoInfo.getCheckInTime()));
            s.addCell(new Label(6, rownum, "" + vivoInfo.getCheckOutTime()));
            rownum++;
        }
        return workbook;
    }


    @PostMapping("visitor-report/all-visitors-report-view")
    public List<Visitor> allVisitorsReportView(@RequestBody VisitorDateDto visitorDateDto) {
        List<Visitor> visitorObj = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());

        if (visitorObj.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }

        return visitorObj;
    }

    @PostMapping("visitor-report/dept-wise-report")
    private void departmentReportt(@RequestBody VisitorDateDto visitorDateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Visitor> vivoInfoList = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());
        if (vivoInfoList.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitors = new ArrayList<>();
        for (Visitor visitor : vivoInfoList) {
            if (visitor.getVisitorPass().getDepartmentName().equalsIgnoreCase(visitorDateDto.getDepartmentName())) {
                visitors.add(visitor);
            }
        }
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Visitor-by-Department.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, 0, visitors);
            //workbook.createSheet()
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, HttpServletResponse response, int index, List<Visitor> visitorList) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);


        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat1.setBackground(Colour.GRAY_50);

        s.setColumnView(0, 20);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.mergeCells(0, 0, 6, 0);

        s.addCell(new Label(0, 0, "Visitor Report Of HR Department", headerFormat));
        s.addCell(new Label(0, 1, "VisitorName", headerFormat1));
        s.addCell(new Label(1, 1, "EmailId", headerFormat1));
        s.addCell(new Label(2, 1, "DateOfVisit", headerFormat1));
        s.addCell(new Label(3, 1, "TypeOfVisit", headerFormat1));
        s.addCell(new Label(4, 1, "PersonToMeet", headerFormat1));
        s.addCell(new Label(5, 1, "CheckInTime", headerFormat1));
        s.addCell(new Label(6, 1, "CheckOutTime", headerFormat1));

        int rownum = 2;
        for (Visitor vivoInfo : visitorList) {
            s.addCell(new Label(0, rownum, "" + vivoInfo.getFirstName()));
            s.addCell(new Label(1, rownum, "" + vivoInfo.getEmail()));
            s.addCell(new Label(2, rownum, "" + vivoInfo.getDateOfVisit()));
            s.addCell(new Label(3, rownum, "" + vivoInfo.getVisitType()));
            s.addCell(new Label(4, rownum, "" + vivoInfo.getPersonToVisit()));
            s.addCell(new Label(5, rownum, "" + vivoInfo.getCheckInTime()));
            s.addCell(new Label(6, rownum, "" + vivoInfo.getCheckOutTime()));
            rownum++;
        }
        return workbook;
    }


    @PostMapping("visitor-report/dept-wise-report-view")
    public List<Visitor> filterVisitorByDept(@RequestBody VisitorDateDto visitorDateDto) {
        List<Visitor> visitorObj = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());
        if (visitorObj.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitor = new ArrayList<>();
        for (Visitor visitor1 : visitorObj) {
            if (visitor1.getVisitorPass().getDepartmentName().equalsIgnoreCase(visitorDateDto.getDepartmentName())) {
                visitor.add(visitor1);
            }
        }
        return visitor;
    }

    @PostMapping("visitor-report/visit-type-wise-report")
    private void visitorTypeWiseReport(@RequestBody VisitorDateDto visitorDateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Visitor> vivoInfo = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());
        if (vivoInfo.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitors = new ArrayList<>();
        for (Visitor visitor : vivoInfo) {
            if (visitor.getVisitorPass().getVisitType().equalsIgnoreCase(visitorDateDto.getVisitType())) {
                visitors.add(visitor);
            }
        }
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Visit-type-wise-report.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet1(workbook, response, 0, visitors);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet1(WritableWorkbook workbook, HttpServletResponse response, int index, List<Visitor> visitors) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);


        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat1.setBackground(Colour.GRAY_50);

        s.setColumnView(0, 20);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 20);
        s.mergeCells(0, 0, 6, 0);
        s.addCell(new Label(0, 0, "Details of Visitor of Official type visit", headerFormat));
        s.addCell(new Label(0, 1, "VisitorName", headerFormat1));
        s.addCell(new Label(1, 1, "EmailId", headerFormat1));
        s.addCell(new Label(2, 1, "DateOfVisit", headerFormat1));
        s.addCell(new Label(4, 1, "PersonToMeet", headerFormat1));
        s.addCell(new Label(3, 1, "Department", headerFormat1));
        s.addCell(new Label(5, 1, "CheckInTime", headerFormat1));
        s.addCell(new Label(6, 1, "CheckOutTime", headerFormat1));


        int rownum = 2;
        for (Visitor vivoInfo : visitors) {
            s.addCell(new Label(0, rownum, "" + vivoInfo.getFirstName()));
            s.addCell(new Label(1, rownum, "" + vivoInfo.getEmail()));
            s.addCell(new Label(2, rownum, "" + vivoInfo.getDateOfVisit()));
            s.addCell(new Label(3, rownum, "" + vivoInfo.getVisitType()));
            s.addCell(new Label(4, rownum, "" + vivoInfo.getPersonToVisit()));
            s.addCell(new Label(5, rownum, "" + vivoInfo.getCheckInTime()));
            s.addCell(new Label(6, rownum, "" + vivoInfo.getCheckOutTime()));
            rownum++;
        }
        return workbook;
    }

    @PostMapping("visitor-report/visit-type-wise-report-view")
    public List<Visitor> filterVisitorTypeWise(@RequestBody VisitorDateDto visitorDateDto) {
        List<Visitor> visitorObj = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());
        if (visitorObj.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitor = new ArrayList<>();
        for (Visitor visitor1 : visitorObj) {
            if (visitor1.getVisitorPass().getVisitType().equalsIgnoreCase(visitorDateDto.getVisitType())) {
                visitor.add(visitor1);
            }
        }
        return visitor;
    }

    @PostMapping("visitor-report/visitor-wise-report")
    private void visitorWiseReport(@RequestBody VisitorDateDto visitordateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Visitor> vivoInfo = visitorService.allVisitorsDateWise(visitordateDto.getStartDate(), visitordateDto.getEndDate());
        if (vivoInfo.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitors = new ArrayList<>();
        for (Visitor visitor : vivoInfo) {
            if (visitor.getFirstName().equalsIgnoreCase(visitordateDto.getFirstName())) {
                visitors.add(visitor);
            }

        }
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Visitor-wise-report.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet2(workbook, response, 0, visitors);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet2(WritableWorkbook workbook, HttpServletResponse response, int index, List<Visitor> visitors) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);
        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat1.setBackground(Colour.GRAY_50);

        s.setColumnView(0, 20);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.mergeCells(0, 0, 4, 0);

        s.addCell(new Label(0, 0, "Report of employee", headerFormat));
        // s.addCell(new Label(0, 1, "VisitorName", headerFormat1));
        s.addCell(new Label(0, 1, "DateOfVisit", headerFormat1));
        s.addCell(new Label(1, 1, "Department", headerFormat1));
        s.addCell(new Label(2, 1, "PersonToMeet", headerFormat1));
        s.addCell(new Label(3, 1, "CheckInTime", headerFormat1));
        s.addCell(new Label(4, 1, "CheckOutTime", headerFormat1));

        int rownum = 2;
        for (Visitor vivoInfo : visitors) {
            s.addCell(new Label(1, rownum, "" + vivoInfo.getEmail()));
            s.addCell(new Label(2, rownum, "" + vivoInfo.getDateOfVisit()));
            s.addCell(new Label(3, rownum, "" + vivoInfo.getVisitType()));
            s.addCell(new Label(4, rownum, "" + vivoInfo.getPersonToVisit()));
            s.addCell(new Label(5, rownum, "" + vivoInfo.getCheckInTime()));
            s.addCell(new Label(6, rownum, "" + vivoInfo.getCheckOutTime()));
            rownum++;
        }

        return workbook;
    }

    @PostMapping("visitor-report/visitor-wise-report-view")
    public List<Visitor> visitorwise(@RequestBody VisitorDateDto visitorDateDto) {
        List<Visitor> visitorObj = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());
        if (visitorObj.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitor = new ArrayList<>();
        for (Visitor visitor1 : visitorObj) {
            if (visitor1.getFirstName().equalsIgnoreCase(visitorDateDto.getFirstName())) {
                visitor.add(visitor1);
            }
        }
        return visitor;
    }

    @PostMapping("visitor-report/visitors-extra-time-by-department")
    private void visitorExtraHours(VisitorDateDto visitordateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Visitor> vivoInfo = visitorService.allVisitorsDateWise(visitordateDto.getStartDate(), visitordateDto.getEndDate());
        if (vivoInfo.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitors = new ArrayList<>();
        for (Visitor visitor : vivoInfo) {
            if (visitor.getDepartmentName().equalsIgnoreCase(visitordateDto.getDepartmentName())) {
                visitors.add(visitor);
            }
        }
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Visitor-Engagement-Hours.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet3(workbook, response, 0, visitors);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet3(WritableWorkbook workbook, HttpServletResponse response, int index, List<Visitor> visitors) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);
        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat1.setBackground(Colour.GRAY_50);
        s.setColumnView(0, 20);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.mergeCells(0, 0, 7, 0);
        s.addCell(new Label(0, 0, "Extra time exit hours", headerFormat));
        s.addCell(new Label(0, 1, "DateOfVisit", headerFormat1));
        s.addCell(new Label(0, 1, "VisitType", headerFormat1));
        s.addCell(new Label(2, 1, "PersonToVisit", headerFormat1));
        s.addCell(new Label(3, 1, "CheckInTime", headerFormat1));
        s.addCell(new Label(4, 1, "CheckOutTime", headerFormat1));

        int rownum = 2;
        for (Visitor vivoInfo : visitors) {
            s.addCell(new Label(1, rownum, "" + vivoInfo.getExtraTime()));
            s.addCell(new Label(2, rownum, "" + vivoInfo.getDateOfVisit()));
            s.addCell(new Label(3, rownum, "" + vivoInfo.getVisitType()));
            s.addCell(new Label(4, rownum, "" + vivoInfo.getPersonToVisit()));
            s.addCell(new Label(5, rownum, "" + vivoInfo.getCheckInTime()));
            s.addCell(new Label(6, rownum, "" + vivoInfo.getCheckOutTime()));
            rownum++;
        }
        return workbook;
    }

    @PostMapping("visitor-report/visitors-extra-time-by-department-view")
    public List<Visitor> visitorsExtraTimeView(@RequestBody VisitorDateDto visitorDateDto) {
        List<Visitor> visitorObj = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());
        if (visitorObj.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitor = new ArrayList<>();
        for (Visitor visitor1 : visitorObj) {
            if (visitor1.getExtraTime() != null && visitor1.getVisitorPass().getDepartmentName().equalsIgnoreCase(visitorDateDto.getDepartmentName())) {
                visitor.add(visitor1);
            }
        }
        return visitor;
    }

    @PostMapping("visitor-report/visitor-payment-by-dept")
    private void visitorPayment(@RequestBody VisitorDateDto visitordateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Visitor> vivoInfo = visitorService.allVisitorsDateWise(visitordateDto.getStartDate(), visitordateDto.getEndDate());
        if (vivoInfo.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitors = new ArrayList<>();
        for (Visitor visitor : vivoInfo) {
            if (visitor.getVisitorPass().getDepartmentName().equalsIgnoreCase(visitordateDto.getDepartmentName())) {
                visitors.add(visitor);
            }
        }
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Visitor-Engagement-Hours.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet6(workbook, response, 0, visitors);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet6(WritableWorkbook workbook, HttpServletResponse response, int index, List<Visitor> visitors) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.BLUE);
        WritableFont headerFont1 = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat1 = new WritableCellFormat(headerFont1);
        headerFormat1.setAlignment(CENTRE);
        headerFormat1.setBackground(Colour.GRAY_50);
        s.setColumnView(0, 20);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.mergeCells(0, 0, 7, 0);
        s.addCell(new Label(0, 0, "Extra time exit hours", headerFormat));
        s.addCell(new Label(0, 1, "DateOfVisit", headerFormat1));
        s.addCell(new Label(0, 1, "VisitType", headerFormat1));
        s.addCell(new Label(2, 1, "PersonToVisit", headerFormat1));
        s.addCell(new Label(3, 1, "CheckInTime", headerFormat1));
        s.addCell(new Label(4, 1, "CheckOutTime", headerFormat1));

        int rownum = 1;
        for (Visitor vivoInfo : visitors) {
            s.addCell(new Label(1, rownum, "" + vivoInfo.getExtraTime()));
            s.addCell(new Label(2, rownum, "" + vivoInfo.getDateOfVisit()));
            s.addCell(new Label(3, rownum, "" + vivoInfo.getVisitType()));
            s.addCell(new Label(4, rownum, "" + vivoInfo.getPersonToVisit()));
            s.addCell(new Label(5, rownum, "" + vivoInfo.getCheckInTime()));
            s.addCell(new Label(6, rownum, "" + vivoInfo.getCheckOutTime()));
            rownum++;
        }
        return workbook;
    }

    @PostMapping("visitor-report/visitor-payment-by-dept-view")
    public List<Visitor> visitorsPayments(@RequestBody VisitorDateDto visitorDateDto) {
        List<Visitor> visitorObj = visitorService.allVisitorsDateWise(visitorDateDto.getStartDate(), visitorDateDto.getEndDate());
        if (visitorObj.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        List<Visitor> visitor = new ArrayList<>();
        for (Visitor visitor1 : visitorObj) {
            if (visitor1.getExtraTime() != null && visitor1.getVisitorPass().getDepartmentName().equalsIgnoreCase(visitorDateDto.getDepartmentName())) {
                visitor.add(visitor1);
            }
        }
        return visitor;
    }


    /**
     * @param visitorDateDto
     * @return Denied visitor
     */

    @PostMapping("visitor-report/view-denied-visitor")
    public ResponseEntity<List<Visitor>> viewDeniedVisitors(@RequestBody VisitorDateDto visitorDateDto) {
        List<Visitor> visitors = visitorService.allVisitorsDateWise(visitorDateDto.startDate, visitorDateDto.endDate);
        List<Visitor> visitorList = new ArrayList<>();
        if (visitors.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        for (Visitor visitor : visitors) {
            if (visitor.getVisitorPass().isAllowedOrDenied() == false) {
                visitorList.add(visitor);
            }
        }
        return new ResponseEntity<>(visitorList, HttpStatus.OK);
    }

    @PostMapping("visitor-report/view-denied-company")
    public ResponseEntity<List<Visitor>> viewDeniedVisitorsCompany(@RequestBody VisitorDateDto visitorDateDto) {
        List<Visitor> visitors = visitorService.allVisitorsDateWise(visitorDateDto.startDate, visitorDateDto.endDate);
        List<Visitor> visitorList = new ArrayList<>();
        if (visitors.isEmpty()) {
            throw new EntityNotFoundException("No data for visitors");
        }
        for (Visitor visitor : visitors) {
            if (visitor.getVisitorPass().getCompanyName().equalsIgnoreCase(visitorDateDto.getCompanyName())) {
                visitorList.add(visitor);
            }
        }
        return new ResponseEntity<>(visitorList, HttpStatus.OK);
    }


    @GetMapping("get-daily-visitor-report")
    private void createExcel( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= VisitorDailyReport.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try
        {
            dailyEntry(workbook, response, 0);
            workbook.write();
            workbook.close();
        }
        catch (Exception e)
        {
            throw new ServletException("Exception in Excel download", e);
        }
        finally
        {
            if (out != null)
                out.close();
        }
    }
    private WritableWorkbook dailyEntry(WritableWorkbook workbook, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Visitor-Daily-Report", index);
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
        int rowNum = 1;
        Date date=new Date();
        List<Visitor> visitorList1=visitorService.getByMobileDate(date);
        for (Visitor  visitor: visitorList1) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum)));
            s.addCell(new Label(1, 0, "Date", cellFormat));
            s.addCell(new Label(1, rowNum, "" + visitor.getLoggedOn()));
            s.addCell(new Label(2, 0, "Visitor Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + visitor.getFirstName()));
            s.addCell(new Label(3, 0, "Visitor Company Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + visitor.getVisitorPass().getCompanyName()));
            s.addCell(new Label(4, 0, "Citizen", cellFormat));
            s.addCell(new Label(4, rowNum, "" + visitor.getVisitorPass().getVisitorType()));
            s.addCell(new Label(5, 0, "InTime", cellFormat));
            s.addCell(new Label(5, rowNum, "" + visitor.getCheckInTime()));
            s.addCell(new Label(6, 0, "Temp (Deg F)", cellFormat));
            s.addCell(new Label(6, rowNum, "" + visitor.getEntryBodyTemperature()));
            s.addCell(new Label(7, 0, "Entery Gate", cellFormat));
            s.addCell(new Label(7, rowNum, "" + visitor.getEntryGateNumber()));
            s.addCell(new Label(8, 0, "Out Time", cellFormat));
            s.addCell(new Label(8, rowNum, "" + visitor.getCheckOutTime()));
            s.addCell(new Label(9, 0, "Temp (Deg F)", cellFormat));
            s.addCell(new Label(9, rowNum, "" + visitor.getExitBodyTemperature()));
            s.addCell(new Label(10, 0, "Exit Gate", cellFormat));
            s.addCell(new Label(10, rowNum, "" + visitor.getExitGateNumber()));
            s.addCell(new Label(11, 0, "Site/Factory", cellFormat));
            s.addCell(new Label(11, rowNum, "" + visitor.getVisitorPass().getSiteName()));
            s.addCell(new Label(12, 0, "Visited Department", cellFormat));
            s.addCell(new Label(12, rowNum, "" + visitor.getDepartmentName()));
            s.addCell(new Label(13, 0, "Visited Employee", cellFormat));
            s.addCell(new Label(13, rowNum, "" + visitor.getPersonToVisit()));
            s.addCell(new Label(14, 0, "Number Of Hours", cellFormat));
            s.addCell(new Label(14, rowNum, "" + visitor.getExtraTime()));
            s.addCell(new Label(15, 0, "Payment", cellFormat));
            s.addCell(new Label(15, rowNum, "" + visitor.getPaymentAmt()));
            rowNum = rowNum + 1;

        }
        return workbook;
    }
}