package com.dfq.coeffi.excel;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagement;
import com.dfq.coeffi.employeePerformanceManagement.service.EmployeePerformanceManagementService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import jxl.format.Colour;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
public class ExcelDownload extends BaseController {

    @Autowired
    EmployeePerformanceManagementService employeePerformanceManagementService;
    @Autowired
    EmployeeService employeeService;

    @GetMapping("goal-excel-download/{empid}")
    public void createCustomersDetails(@PathVariable("empid") long empid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeePerformanceManagement> employeePerformanceManagements = employeePerformanceManagementService.getEmployeePerformanceManagementNyEmplId(employee.get());
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, employeePerformanceManagements, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            out.close();
        }
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, List<EmployeePerformanceManagement> employeePerformanceManagements, HttpServletResponse response, int index) throws WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.GRAY_25);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.addCell(new Label(0, 0, "GoalNo", headerFormat));
        s.addCell(new Label(1, 0, "Description", headerFormat));
        int rownum = 1;
        for (EmployeePerformanceManagement employeePerformanceManagement : employeePerformanceManagements) {
            s.addCell(new Label(0, rownum, "" + employeePerformanceManagement.getGoalName()));
            s.addCell(new Label(1, rownum, "" + employeePerformanceManagement.getGoalDiscription()));
            rownum++;
        }
        return workbook;
    }

    @GetMapping("goal-excel-download")
    public void createCustomersDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Employee-Performance.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            out.close();
        }
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, HttpServletResponse response, int index) throws WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.GRAY_25);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.addCell(new Label(0, 0, "GoalNo", headerFormat));
        s.addCell(new Label(1, 0, "Description", headerFormat));
        return workbook;
    }

}
