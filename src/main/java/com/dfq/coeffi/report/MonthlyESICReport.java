package com.dfq.coeffi.report;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.dto.EmployeeSalaryDto;
import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import com.dfq.coeffi.vivo.entity.Company;
import com.dfq.coeffi.vivo.service.CompanyService;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.*;

import static jxl.format.Alignment.CENTRE;

@RestController
@Slf4j
public class MonthlyESICReport extends BaseController {

    private final CompanyService companyService;
    private final ReportService reportService;

    @Autowired
    public MonthlyESICReport(CompanyService companyService, ReportService reportService) {
        this.companyService = companyService;
        this.reportService = reportService;
    }

    @PostMapping("/esic-report-monthwise-download")
    public void monthlyEsicReport(HttpServletResponse response, @RequestBody EmployeeSalaryDto employeeSalaryDto) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Monthly_ESIC_Report.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, employeeSalaryDto, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("SomeThing Went Wrong", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    @Autowired
    CompanyNameService companyNameService;

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, EmployeeSalaryDto employeeSalaryDto, int index) throws IOException, WriteException {
        Date today = new Date();
        List<CompanyName> companies = companyNameService.getCompany();
        Collections.reverse(companies);

        List<EmployeeSalaryDto> employeeSalaryProcesses = getEmployeeSalaryProcessMonthlyYearlyESIC(employeeSalaryDto);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String toDay = sdf.format(today);

        WritableSheet s = workbook.createSheet("Sheet-1", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont mainHeaderFont = new WritableFont(WritableFont.TIMES, 14);
        mainHeaderFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat mainHeaderFormat = new WritableCellFormat(mainHeaderFont);
        mainHeaderFormat.setAlignment(CENTRE);
        mainHeaderFormat.setVerticalAlignment(jxl.format.VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        mainHeaderFormat.setWrap(true);
        mainHeaderFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 12);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setVerticalAlignment(jxl.format.VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        headerFormat.setWrap(true);
        headerFormat.setBackground(jxl.format.Colour.GRAY_25);
        headerFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, jxl.format.Colour.GRAY_80);

        WritableFont bodyFont = new WritableFont(WritableFont.TIMES, 12);
        WritableCellFormat bodyFormat = new WritableCellFormat(bodyFont);
        bodyFormat.setAlignment(CENTRE);
        bodyFormat.setVerticalAlignment(VerticalAlignment.getAlignment(HSSFCellStyle.VERTICAL_CENTER));
        bodyFormat.setWrap(true);
        bodyFormat.setBackground(jxl.format.Colour.ICE_BLUE);
        bodyFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        s.setColumnView(0, 8);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 20);
        s.setColumnView(7, 20);
        s.setColumnView(8, 20);

        s.setRowView(0, 500);
        s.setRowView(1, 500);
        s.setRowView(2, 400);

        s.mergeCells(0, 0, 8, 0);
        s.mergeCells(0, 1, 8, 1);

        s.addCell(new Label(0, 0, "" + companies.get(0).getCompanyName(), mainHeaderFormat));
        s.addCell(new Label(0, 1, "ESIC Report for " + employeeSalaryDto.monthName+""+employeeSalaryDto.inputYear, mainHeaderFormat));
        s.addCell(new Label(0, 2, "Sl No.", headerFormat));
        s.addCell(new Label(1, 2, "Emp ID", headerFormat));
        s.addCell(new Label(2, 2, "Emp Name", headerFormat));
        s.addCell(new Label(3, 2, "ESIC Number", headerFormat));
        s.addCell(new Label(4, 2, "No of Days", headerFormat));
        s.addCell(new Label(5, 2, "Gross Salary", headerFormat));
        s.addCell(new Label(6, 2, "Employee Contribution", headerFormat));
        s.addCell(new Label(7, 2, "Employeer Contribution", headerFormat));
        s.addCell(new Label(8, 2, "Total Contribution", headerFormat));

        int slno = 1;
        int mainRow = 3;
        BigDecimal totalContribution = BigDecimal.ZERO;
        for (EmployeeSalaryDto employeeSalaryProcess : employeeSalaryProcesses) {
            s.addCell(new Label(0, mainRow, String.valueOf(slno), bodyFormat));
            s.addCell(new Label(1, mainRow, "" + employeeSalaryProcess.getEmployeeCode(), bodyFormat));
            s.addCell(new Label(2, mainRow, "" + employeeSalaryProcess.getEmployeeName(), bodyFormat));
            s.addCell(new Label(3, mainRow, "" + employeeSalaryProcess.getEsicNumber(), bodyFormat));
            s.addCell(new Label(4, mainRow, "" + employeeSalaryProcess.getNoOfPresent(), bodyFormat));
            s.addCell(new Label(5, mainRow, "" + employeeSalaryProcess.getGrossSalary(), bodyFormat));
            s.addCell(new Label(6, mainRow, "" + employeeSalaryProcess.getEmployeeEsicContribution(), bodyFormat));
            s.addCell(new Label(7, mainRow, "" + employeeSalaryProcess.getEmployerEsicContribution(), bodyFormat));
            s.addCell(new Label(8, mainRow, "" + employeeSalaryProcess.getTotalEsicContribution(), bodyFormat));

            mainRow += 1;
            slno += 1;
        }
        return workbook;
    }

    public List<EmployeeSalaryDto> getEmployeeSalaryProcessMonthlyYearlyESIC(EmployeeSalaryDto employeeSalaryDto) {
        ArrayList employeeEsic = new ArrayList();
        BigDecimal totalContribution = BigDecimal.ZERO;
        List<EmployeeSalaryProcess> employeeSalary = reportService.getEmployeeSalaryProcessMonthlyYearlyESIC(employeeSalaryDto.monthName, employeeSalaryDto.inputYear);
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalary) {
            long esicNumber = Long.parseLong(employeeSalaryProcess.getEmployee().getEsiNumber());
            if (esicNumber > 0) {
                EmployeeSalaryDto employeeDetails = new EmployeeSalaryDto();
                employeeDetails.setEsicNumber(Long.parseLong(employeeSalaryProcess.getEmployee().getEsiNumber()));
                employeeDetails.setEmployeeId(employeeSalaryProcess.getEmployee().getId());
                employeeDetails.setEmployeeCode(employeeSalaryProcess.getEmployee().getEmployeeCode());
                employeeDetails.setMonthName(employeeSalaryProcess.getSalaryMonth());
                employeeDetails.setEmployeeName(employeeSalaryProcess.getEmployee().getFirstName() + " " + employeeSalaryProcess.getEmployee().getLastName());
                employeeDetails.setNoOfPresent(employeeSalaryProcess.getNoOfPresent());
                employeeDetails.setGrossSalary(employeeSalaryProcess.getGrossSalary());
                BigDecimal employeeEsicContribution = employeeSalaryProcess.getEmployeeEsicContribution();
                employeeDetails.setEmployeeEsicContribution(employeeEsicContribution.setScale(0, BigDecimal.ROUND_UP));
                BigDecimal employerContributionESIC = employeeSalaryProcess.getEmployerContributionESIC();
                employeeDetails.setEmployerEsicContribution(employerContributionESIC.setScale(0, BigDecimal.ROUND_UP));
                totalContribution = employeeSalaryProcess.getEmployerContributionESIC().add(employeeSalaryProcess.getEmployerContributionESIC());
                MathContext m = new MathContext(0); // 4 precision
                employeeDetails.setTotalEsicContribution(totalContribution.setScale(0, BigDecimal.ROUND_UP));
                employeeEsic.add(employeeDetails);
            }
        }
        if (employeeSalary.isEmpty()) {
            throw new EntityNotFoundException("No employee salary process monthly or yearly ESIC");
        }
        return employeeEsic;
    }

}
