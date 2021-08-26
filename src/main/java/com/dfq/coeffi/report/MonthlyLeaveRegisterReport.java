package com.dfq.coeffi.report;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceController;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.LeaveService;
import com.dfq.coeffi.vivo.entity.Company;
import com.dfq.coeffi.vivo.service.CompanyService;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

import static jxl.format.Alignment.CENTRE;
import static jxl.format.Alignment.RIGHT;

@RestController
@Slf4j
public class MonthlyLeaveRegisterReport extends BaseController {

    private final CompanyService companyService;
    private final EmployeeLeaveBalanceController employeeLeaveBalanceController;
    private final EmployeeLeaveBalanceService employeeLeaveBalanceService;
    private final AcademicYearService academicYearService;
    private final LeaveService leaveService;
    private final EmployeeService employeeService;

    @Autowired
    public MonthlyLeaveRegisterReport(CompanyService companyService, EmployeeLeaveBalanceController employeeLeaveBalanceController, EmployeeLeaveBalanceService employeeLeaveBalanceService, AcademicYearService academicYearService, LeaveService leaveService, EmployeeService employeeService) {
        this.companyService = companyService;
        this.employeeLeaveBalanceController = employeeLeaveBalanceController;
        this.employeeLeaveBalanceService = employeeLeaveBalanceService;
        this.academicYearService = academicYearService;
        this.leaveService = leaveService;
        this.employeeService = employeeService;
    }

    @GetMapping("/leave-register-report-monthwise-download")
    public void monthLeaveRegisterReport(HttpServletResponse response) throws ServletException, IOException, WriteException {
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Monthly_Leave_Register.xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, 0);
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

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, int index) throws IOException, WriteException {
        Date today = new Date();
        List<CompanyName> companies = companyNameService.getCompany();
        Collections.reverse(companies);
        List<EmployeeLeaveBalance> employeeLeaveBalances = employeeLeaveBalanceByFinancialYearId();

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
        s.setColumnView(1, 15);
        s.setColumnView(2, 15);
        s.setColumnView(3, 15);
        s.setColumnView(4, 15);

        s.setRowView(0, 500);
        s.setRowView(1, 500);
        s.setRowView(2, 350);

        s.mergeCells(0, 0, 13, 0);
        s.mergeCells(0, 1, 13, 1);
        s.mergeCells(5, 2, 7, 2);
        s.mergeCells(8, 2, 10, 2);
        s.mergeCells(11, 2, 13, 2);
        s.mergeCells(0, 2, 0, 3);
        s.mergeCells(1, 2, 1, 3);
        s.mergeCells(2, 2, 2, 3);
        s.mergeCells(3, 2, 3, 3);
        s.mergeCells(4, 2, 4, 3);
        //s.mergeCells(5, 2, 5, 3);

        s.addCell(new Label(0, 0, "" + companies.get(0).getCompanyName(), mainHeaderFormat));
        s.addCell(new Label(0, 1, "LEAVE REGISTER Till " + toDay, mainHeaderFormat));
        s.addCell(new Label(0, 2, "Sl No.", headerFormat));
        s.addCell(new Label(1, 2, "Emp ID", headerFormat));
        s.addCell(new Label(2, 2, "Emp Name", headerFormat));
        s.addCell(new Label(3, 2, "Designation", headerFormat));
        s.addCell(new Label(4, 2, "Department", headerFormat));
        s.addCell(new Label(5, 2, "Opening Leaves", headerFormat));
        s.addCell(new Label(8, 2, "Availed Leaves", headerFormat));
        s.addCell(new Label(11, 2, "Closing Leaves", headerFormat));
        s.addCell(new Label(5, 3, "EL", headerFormat));
        s.addCell(new Label(6, 3, "CL", headerFormat));
        s.addCell(new Label(7, 3, "SL", headerFormat));
        s.addCell(new Label(8, 3, "EL", headerFormat));
        s.addCell(new Label(9, 3, "CL", headerFormat));
        s.addCell(new Label(10, 3, "SL", headerFormat));
        s.addCell(new Label(11, 3, "EL", headerFormat));
        s.addCell(new Label(12, 3, "CL", headerFormat));
        s.addCell(new Label(13, 3, "SL", headerFormat));

        int slno = 1;
        int mainRow = 4;

        for (EmployeeLeaveBalance employeeLeaveBalancesObj : employeeLeaveBalances) {
            s.addCell(new Label(0, mainRow, String.valueOf(slno), bodyFormat));
            s.addCell(new Label(1, mainRow, "" + employeeLeaveBalancesObj.getEmployee().getEmployeeCode(), bodyFormat));
            s.addCell(new Label(2, mainRow, "" + employeeLeaveBalancesObj.getEmployee().getFirstName(), bodyFormat));
            s.addCell(new Label(3, mainRow, "" + employeeLeaveBalancesObj.getEmployee().getDesignation().getName(), bodyFormat));
            s.addCell(new Label(4, mainRow, "" + employeeLeaveBalancesObj.getEmployee().getDepartment().getName(), bodyFormat));
            s.addCell(new Label(5, mainRow, "" + employeeLeaveBalancesObj.getOpeningLeave().getEarnLeave(), bodyFormat));
            s.addCell(new Label(6, mainRow, "" + employeeLeaveBalancesObj.getOpeningLeave().getClearanceLeave(), bodyFormat));
            s.addCell(new Label(7, mainRow, "" + employeeLeaveBalancesObj.getOpeningLeave().getMedicalLeave(), bodyFormat));
            s.addCell(new Label(8, mainRow, "" + employeeLeaveBalancesObj.getAvailLeave().getEarnLeave(), bodyFormat));
            s.addCell(new Label(9, mainRow, "" + employeeLeaveBalancesObj.getAvailLeave().getClearanceLeave(), bodyFormat));
            s.addCell(new Label(10, mainRow, "" + employeeLeaveBalancesObj.getAvailLeave().getMedicalLeave(), bodyFormat));
            s.addCell(new Label(11, mainRow, "" + employeeLeaveBalancesObj.getClosingLeave().getEarnLeave(), bodyFormat));
            s.addCell(new Label(12, mainRow, "" + employeeLeaveBalancesObj.getClosingLeave().getClearanceLeave(), bodyFormat));
            s.addCell(new Label(13, mainRow, "" + employeeLeaveBalancesObj.getClosingLeave().getMedicalLeave(), bodyFormat));
            mainRow += 1;
            slno += 1;
        }
        return workbook;
    }

    public List<EmployeeLeaveBalance> employeeLeaveBalanceByFinancialYearId() {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
        AcademicYear academicYearObj = academicYear.get();
        List<EmployeeLeaveBalance> finalEB = new ArrayList<>();
        List<Employee> employeeList = employeeService.findAll();
        for (Employee e : employeeList) {
            EmployeeLeaveBalance getEmployeeLeaveBalanceByFinancialYearIdAndEmp = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(e.getId(), academicYearObj.getId());
            EmployeeLeaveBalance elb = new EmployeeLeaveBalance();
            Optional<Employee> emp = employeeService.getEmployee(e.getId());
            Employee empnew = new Employee();
            empnew.setId(emp.get().getId());
            empnew.setFirstName(emp.get().getFirstName());
            empnew.setLastName(emp.get().getLastName());
            empnew.setEmployeeCode(emp.get().getEmployeeCode());
            empnew.setDesignation(emp.get().getDesignation());
            empnew.setDepartment(emp.get().getDepartment());
            elb.setId(getEmployeeLeaveBalanceByFinancialYearIdAndEmp.getId());
            elb.setAvailLeave(getEmployeeLeaveBalanceByFinancialYearIdAndEmp.getAvailLeave());
            elb.setOpeningLeave(getEmployeeLeaveBalanceByFinancialYearIdAndEmp.getOpeningLeave());
            elb.setClosingLeave(getEmployeeLeaveBalanceByFinancialYearIdAndEmp.getClosingLeave());
            elb.setEmployee(empnew);
            finalEB.add(elb);
        }
        return finalEB;
    }
}
