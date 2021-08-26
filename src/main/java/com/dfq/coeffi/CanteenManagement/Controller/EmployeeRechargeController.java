package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.EmployeeRecharge;
import com.dfq.coeffi.CanteenManagement.Service.CatererDetailsService;
import com.dfq.coeffi.CanteenManagement.Service.EmployeeRechargeService;
import com.dfq.coeffi.CanteenManagement.employeeBalance.EmployeeBalance;
import com.dfq.coeffi.CanteenManagement.employeeBalance.EmployeeBalanceService;
import com.dfq.coeffi.StoreManagement.Entity.RequestNumber;
import com.dfq.coeffi.StoreManagement.Repository.RequestNumberRepository;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.BorderLineStyle;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Boolean;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static jxl.format.Alignment.*;

@RestController
public class EmployeeRechargeController extends BaseController {

    private final EmployeeService employeeService;
    private final CatererDetailsService catererDetailsService;
    private final EmployeeRechargeService employeeRechargeService;
    private final RequestNumberRepository requestNumberRepository;
    private final EmployeeBalanceService employeeBalanceService;

    @Autowired
    public EmployeeRechargeController(EmployeeService employeeService, CatererDetailsService catererDetailsService, EmployeeRechargeService employeeRechargeService, RequestNumberRepository requestNumberRepository, EmployeeBalanceService employeeBalanceService) {
        this.employeeService = employeeService;
        this.catererDetailsService = catererDetailsService;
        this.employeeRechargeService = employeeRechargeService;
        this.requestNumberRepository = requestNumberRepository;
        this.employeeBalanceService = employeeBalanceService;
    }

    @PostMapping("canteen/employee-recharge")
    public ResponseEntity<EmployeeRecharge> createEmployeeRecharge(@RequestBody EmployeeRecharge employeeRecharge) {
        List<EmployeeRecharge> employeeRechargeList = employeeRechargeService.getRecharges();
        for (EmployeeRecharge employeeRechargeObj : employeeRechargeList) {
            if (employeeRechargeObj.getTransactionRefNumber().equals(employeeRecharge.getTransactionRefNumber()) || employeeRechargeObj.getPaymentRefNumber().equals(employeeRecharge.getPaymentRefNumber())) {
                throw new EntityNotFoundException("Ref Number should not be duplicate.");
            }
        }
        List<EmployeeRecharge> employeeRechargeListByEmpId = employeeRechargeService.getEmployeeRechargeByEmpId(employeeRecharge.getEmployee().getId());
        if (employeeRechargeListByEmpId.isEmpty()) {
            employeeRecharge.setTotalRechargeAmount(employeeRecharge.getRechargeAmount());
            employeeRecharge.setActualBalance(employeeRecharge.getRechargeAmount());
            employeeRecharge.setIsNew(true);
        } else {
            Collections.reverse(employeeRechargeListByEmpId);
            employeeRecharge.setTotalRechargeAmount(employeeRechargeListByEmpId.get(0).getTotalRechargeAmount() + employeeRecharge.getRechargeAmount());
            employeeRecharge.setActualBalance(employeeRechargeListByEmpId.get(0).getActualBalance() + employeeRecharge.getRechargeAmount());
            employeeRecharge.setIsNew(true);
        }
        employeeRecharge.setEmpId(employeeRecharge.getEmployee().getId());
        EmployeeRecharge employeeRechargeObj = employeeRechargeService.saveRecharge(employeeRecharge);

        for (int i = 0; i < employeeRechargeListByEmpId.size(); i++) {
            employeeRechargeListByEmpId.get(i).setIsNew(false);
            employeeRechargeService.saveRecharge(employeeRechargeListByEmpId.get(i));
        }

        EmployeeBalance employeeBalanceNew = new EmployeeBalance();
        EmployeeBalance employeeBalance = employeeBalanceService.getByEmpId(employeeRecharge.getEmployee());
        if (employeeBalance == null) {
            employeeBalanceNew.setEmpId(employeeRecharge.getEmpId());
            employeeBalanceNew.setEmpName(employeeRecharge.getEmployee().getFirstName());
            employeeBalanceNew.setEmpType(employeeRecharge.getEmployeeCategory());
            employeeBalanceNew.setEmployee(employeeRecharge.getEmployee());
            employeeBalanceNew.setMinimumBalanceAmount(employeeRecharge.getMinimumBalanceAmount());
            employeeBalanceNew.setActualBalance(employeeRecharge.getActualBalance());
            employeeBalanceNew.setIsBalanceLow(Boolean.FALSE);
            employeeBalanceNew.setTotalCredit(employeeRecharge.getRechargeAmount());
            employeeBalanceNew.setTotaldebit(0);
            employeeBalanceService.saveEmployeeBalance(employeeBalanceNew);
        } else {
            employeeBalance.setActualBalance(employeeRecharge.getActualBalance());
            employeeBalance.setMinimumBalanceAmount(employeeRecharge.getMinimumBalanceAmount());
            employeeBalance.setTotalCredit(employeeRecharge.getTotalRechargeAmount());
            employeeBalanceService.saveEmployeeBalance(employeeBalance);
        }
        return new ResponseEntity(employeeRechargeObj, HttpStatus.OK);
    }

    @PostMapping("canteen/employee-recharge-update")
    public ResponseEntity<EmployeeRecharge> updateEmployeeRecharge(@RequestBody EmployeeRecharge employeeRecharge) {
        employeeRecharge.setEmpId(employeeRecharge.getEmployee().getId());
        List<EmployeeRecharge> employeeRechargeListByEmpId = employeeRechargeService.getEmployeeRechargeByEmpId(employeeRecharge.getEmployee().getId());
        Collections.reverse(employeeRechargeListByEmpId);
        //if (employeeRechargeListByEmpId.get(0).getId() == employeeRecharge.getId()) {
        if (employeeRechargeListByEmpId.size() > 1) {
            employeeRecharge.setTotalRechargeAmount(employeeRechargeListByEmpId.get(1).getTotalRechargeAmount() + employeeRecharge.getRechargeAmount());
            employeeRecharge.setActualBalance(employeeRechargeListByEmpId.get(1).getActualBalance() + employeeRecharge.getRechargeAmount());
            employeeRecharge.setIsNew(true);
        } else {
            employeeRecharge.setTotalRechargeAmount(employeeRecharge.getRechargeAmount());
            employeeRecharge.setActualBalance(employeeRecharge.getRechargeAmount());
            employeeRecharge.setIsNew(true);
        }
        EmployeeRecharge employeeRechargeObj = employeeRechargeService.saveRecharge(employeeRecharge);

        EmployeeBalance employeeBalanceNew = new EmployeeBalance();
        EmployeeBalance employeeBalance = employeeBalanceService.getByEmpId(employeeRecharge.getEmployee());
        if (employeeBalance != null) {
            employeeBalance.setActualBalance(employeeRecharge.getActualBalance());
            employeeBalance.setMinimumBalanceAmount(employeeRecharge.getMinimumBalanceAmount());
            employeeBalance.setTotalCredit(employeeRecharge.getTotalRechargeAmount());
            employeeBalanceService.saveEmployeeBalance(employeeBalance);
        }
        return new ResponseEntity(employeeRechargeObj, HttpStatus.OK);
    }

    @GetMapping("canteen/employee-recharge")
    public ResponseEntity<EmployeeRecharge> getEmployeeRecharge() {
        List<EmployeeRecharge> employeeRecharge = employeeRechargeService.getRecharges();
        if (employeeRecharge.isEmpty()) {
            throw new EntityNotFoundException("There is no recharge list.");
        }
        Collections.reverse(employeeRecharge);
        return new ResponseEntity(employeeRecharge, HttpStatus.OK);
    }

    @GetMapping("canteen/employee-recharge/{id}")
    public ResponseEntity<EmployeeRecharge> getEmployeeRecharge(@PathVariable long id) {
        EmployeeRecharge employeeRecharge = employeeRechargeService.getEmployeeRecharge(id);
        return new ResponseEntity(employeeRecharge, HttpStatus.OK);
    }

    @GetMapping("canteen/employee-recharge-by-employee/{empId}")
    public ResponseEntity<EmployeeRecharge> getEmployeeRechargeByEployeeId(@PathVariable long empId) {
        EmployeeRecharge employeeRecharge = new EmployeeRecharge();
        List<EmployeeRecharge> employeeRechargeList = employeeRechargeService.getEmployeeRechargeByEmpId(empId);
        if (employeeRechargeList.isEmpty()) {
            employeeRecharge.setEmpId(empId);
            employeeRecharge.setActualBalance(0);
            employeeRecharge.setMinimumBalanceAmount(0);
            employeeRecharge.setTotalRechargeAmount(0);
        } else {
            Collections.reverse(employeeRechargeList);
            employeeRecharge = employeeRechargeList.get(0);
        }
        return new ResponseEntity(employeeRecharge, HttpStatus.OK);
    }

    @DeleteMapping("canteen/employee-recharge/{id}")
    public void deleteRecharge(@PathVariable("id") long id) {
        employeeRechargeService.delete(id);
    }

    @PostMapping("canteen/employee-recharge-by-date")
    public ResponseEntity<List<EmployeeRecharge>> getRechargeFilter(@RequestBody DateDto dateDto) {
        Date fromDate = DateUtil.mySqlFormatDate(dateDto.getStartDate());
        Date toDate = DateUtil.mySqlFormatDate(dateDto.getEndDate());
        List<EmployeeRecharge> employeeRechargeList = employeeRechargeService.getEmployeeRechargeByDate(fromDate, toDate);
        if (employeeRechargeList.isEmpty()) {
            throw new EntityNotFoundException("There is no data.");
        }
        Collections.reverse(employeeRechargeList);
        return new ResponseEntity<>(employeeRechargeList, HttpStatus.OK);
    }

    @GetMapping("employee-recharge-export")
    public ResponseEntity<List<Employee>> getEmployeeExport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<EmployeeRecharge> employeeRechargeDetailsDtos = getRechargeInfoFilter(dateDto);
        OutputStream out = null;
        String fileName = "Employee_Details";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (employeeRechargeDetailsDtos != null) {
                employeeRechargeInfoDetails(workbook, employeeRechargeDetailsDtos, response, 0);
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(workbook, HttpStatus.OK);
    }

    private WritableWorkbook employeeRechargeInfoDetails(WritableWorkbook workbook, List<EmployeeRecharge> employeeRechargeDetailsDtoList, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Employee-Recharge-Details", index);
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
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.mergeCells(0, 0, 10, 0);
        Label lable = new Label(0, 0, "Employee Recharge Details", headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (EmployeeRecharge recharge : employeeRechargeDetailsDtoList) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + recharge.getEmployee().getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Employee Category", cellFormat));
            s.addCell(new Label(2, rowNum, "" + recharge.getEmployeeCategory(), cLeft));
            s.addCell(new Label(3, 1, "Employee Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + recharge.getEmployee().getFirstName() + " " + recharge.getEmployee().getLastName(), cLeft));
            s.addCell(new Label(4, 1, "Department", cellFormat));
            if (recharge.getEmployee().getDepartment() != null) {
                s.addCell(new Label(4, rowNum, "" + recharge.getEmployee().getDepartment().getName(), cLeft));
            } else {
                s.addCell(new Label(4, rowNum, "" + " ", cLeft));
            }

            s.addCell(new Label(5, 1, "Joining Date", cellFormat));
            if (recharge.getEmployee().getDateOfJoining() != null) {
                s.addCell(new Label(5, rowNum, "" + recharge.getEmployee().getDateOfJoining(), cLeft));
            } else {
                s.addCell(new Label(5, rowNum, "" + " ", cLeft));
            }
            s.addCell(new Label(6, 1, "Transaction Ref", cellFormat));
            s.addCell(new Label(6, rowNum, "" + recharge.getTransactionRefNumber(), cLeft));
            s.addCell(new Label(7, 1, "Payment ref", cellFormat));
            s.addCell(new Label(7, rowNum, "" + recharge.getPaymentRefNumber(), cLeft));
            s.addCell(new Label(8, 1, "Total Recharge AMt", cellFormat));
            s.addCell(new Label(8, rowNum, "" + recharge.getTotalRechargeAmount(), cLeft));
            s.addCell(new Label(9, 1, "Minimum Balance", cellFormat));
            s.addCell(new Label(9, rowNum, "" + recharge.getMinimumBalanceAmount(), cLeft));
            s.addCell(new Label(10, 1, "Actual Balance", cellFormat));
            s.addCell(new Label(10, rowNum, "" + recharge.getActualBalance(), cLeft));

        }
        rowNum = rowNum + 1;
        return workbook;
    }

    public List<EmployeeRecharge> getRechargeInfoFilter(DateDto dateDto) {
        List<EmployeeRecharge> employeeRechargeList = employeeRechargeService.getEmployeeRechargeByDate(dateDto.getStartDate(), dateDto.getEndDate());

        return employeeRechargeList;
    }

    @GetMapping("get/transactionrefnumber")
    public ResponseEntity<RequestNumber> getRequestNumbers() {
        RequestNumber requestNumber = requestNumberRepository.findOne((long) 1);
        Date date = new Date();
        ZonedDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault());
        Calendar cal = Calendar.getInstance();
        String d = String.valueOf(cal.get(Calendar.DATE));
        String m = String.valueOf(localDate.getMonthValue());
        String y = String.valueOf(cal.get(Calendar.YEAR));
        requestNumber.setDateStr(d + m + y);
        requestNumber.setNumber(requestNumber.getNumber() + 1);
        requestNumberRepository.save(requestNumber);
        return new ResponseEntity<>(requestNumber, HttpStatus.OK);
    }

    @GetMapping("employee-type-filter/{type}")
    public ResponseEntity<Employee> getEmployeeType(@PathVariable("type") EmployeeType type) {
        List<Employee> employeeList = employeeService.getEmployeeByType(type, true);
        List<Employee> employeeList1 = new ArrayList<>();
        for (Employee e : employeeList) {
            if (e.getEmployeeType().equals(type)) {
                employeeList1.add(e);
            }

        }
        return new ResponseEntity(employeeList1, HttpStatus.OK);
    }
}