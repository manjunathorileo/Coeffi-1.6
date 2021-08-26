package com.dfq.coeffi.controller.payroll;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.dto.EmployeeCTCDataDto;
import com.dfq.coeffi.dto.EmployeeSalaryCalculationDto;
import com.dfq.coeffi.dto.EmployeeSalaryDto;
import com.dfq.coeffi.dto.FinanceRejection;
import com.dfq.coeffi.entity.holiday.Holiday;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.*;
import com.dfq.coeffi.repository.payroll.EmployeeCtcDataRepo;
import com.dfq.coeffi.repository.payroll.EmployeeSalaryProcessRepository;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.holiday.HolidayService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.LeaveService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryDeductionService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryProcessService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryService;
import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.util.HeaderFooterPageEvent;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.math.BigDecimal.ROUND_HALF_UP;

@RestController
public class EmployeeSalaryProcessController extends BaseController {
    @Autowired
    private EmployeeSalaryProcessService employeeSalaryProcessService;
    private EmployeeService employeeService;
    private EmployeeSalaryService employeeSalaryService;
    private LeaveService leaveService;
    private AcademicYearService academicYearService;
    private EmployeeAttendanceService employeeAttendanceService;
    private HolidayService holidayService;
    private EmployeeSalaryDeductionService employeeSalaryDeductionService;
    private EmployeeLeaveBalanceService employeeLeaveBalanceService;

    @Autowired
    EmployeeCtcDataRepo employeeCtcDataRepo;
    @Autowired
    EmployeeSalaryProcessRepository employeeSalaryProcessRepository;

    private static DecimalFormat df2 = new DecimalFormat("#.##");
    YearMonth yearMonth;  // January of 2015.
    LocalDate monthName;


    @Autowired
    public EmployeeSalaryProcessController(EmployeeSalaryProcessService employeeSalaryProcessService, EmployeeSalaryService employeeSalaryService,
                                           EmployeeService employeeService, LeaveService leaveService, AcademicYearService academicYearService,
                                           EmployeeAttendanceService employeeAttendanceService, HolidayService holidayService,
                                           EmployeeSalaryDeductionService employeeSalaryDeductionService,
                                           EmployeeLeaveBalanceService employeeLeaveBalanceService) {
        this.employeeSalaryProcessService = employeeSalaryProcessService;
        this.employeeService = employeeService;
        this.employeeSalaryService = employeeSalaryService;
        this.leaveService = leaveService;
        this.academicYearService = academicYearService;
        this.employeeAttendanceService = employeeAttendanceService;
        this.holidayService = holidayService;
        this.employeeSalaryDeductionService = employeeSalaryDeductionService;
        this.employeeLeaveBalanceService = employeeLeaveBalanceService;
    }

    @GetMapping("employee-salary-process")
    public ResponseEntity<List<EmployeeSalaryProcess>> listAllEmployeeSalaryProcess() {
        List<EmployeeSalaryProcess> employeeSalaryProcessList = employeeSalaryProcessService.listAllEmployeeSalaryProcess();
        if (CollectionUtils.isEmpty(employeeSalaryProcessList)) {
            throw new EntityNotFoundException("employeeSalaryProcessList");
        }
        return new ResponseEntity<>(employeeSalaryProcessList, HttpStatus.OK);
    }

    @PostMapping("employee-salary-process/by-month")
    public ResponseEntity<EmployeeSalaryProcess> getEmployeeSalaryProcessByMonth(@RequestBody EmployeeSalaryDto employeeSalaryDto) {
        Optional<EmployeeSalaryProcess> employeeSalary = employeeSalaryProcessService.getEmployeeSalaryProcessByMonth(employeeSalaryDto.employeeId, employeeSalaryDto.inputMonth, employeeSalaryDto.inputYear);
        if (!employeeSalary.isPresent()) {
            throw new EntityNotFoundException("employeeSalaryProcess-monthwise");
        }
        Date date = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("MMM"); // three digit abbreviation
        return new ResponseEntity<>(employeeSalary.get(), HttpStatus.OK);
    }


    @Autowired
    EmployeeAttendanceController employeeAttendanceController;

    /*
     prepare employee salary from CTC Master by EmployeeId
     */
    @GetMapping("employee-salary/generate-by-month/{year}/{month}")
    public ResponseEntity<List<EmployeeSalaryProcess>> createEmployeeSalaryProcess(@PathVariable("year") int year, @PathVariable("month") int month) throws Exception {
        List<EmployeeSalaryProcess> employeeSalaryProcessList = new ArrayList<>();
        List<Employee> employeeList = employeeService.getEmployeeByType(EmployeeType.PERMANENT, true);
        List<Employee> employeeListW = employeeService.getEmployeeByType(EmployeeType.PERMANENT_WORKER, true);
        List<Employee> employeeListCont = employeeService.getEmployeeByType(EmployeeType.CONTRACT, true);
        employeeList.addAll(employeeListW);
        employeeList.addAll(employeeListCont);

        if (employeeList != null && employeeList.size() > 0) {
            for (Employee employee : employeeList) {
                Optional<EmployeeSalaryProcess> employeeSalaryObj = employeeSalaryProcessService.getEmployeeSalaryCreatedByMonth(employee.getId(), Month.of(month).name(), String.valueOf(year));
                if (employeeSalaryObj.isPresent()) {
//                    throw new EntityNotFoundException("Employee Salary Already Generated");
                    employeeSalaryProcessService.delete(employeeSalaryObj.get().getId());
                }
                if (employee.getEmployeeCTCData() != null) {
                    EmployeeCTCData employeeCTCData = employee.getEmployeeCTCData();
                    EmployeeSalaryProcess employeeSalaryProcess = createSalaryProcess(employeeCTCData, year, month);
                    employeeSalaryProcess.setRefId(employee.getId());
                    //---------------------------------------------
                    Employee employee1 = new Employee();
                    employee1.setId(employee.getId());
                    employee1.setFirstName(employee.getFirstName());
                    employee1.setLastName(employee.getLastName());
                    employee1.setEmployeeCode(employee.getEmployeeCode());
                    employee1.setDateOfJoining(employee.getDateOfJoining());
                    employee1.setEmployeeCTCData(employee.getEmployeeCTCData());
                    employee1.setEmployeeType(employee.getEmployeeType());
                    employee1.setOtRequired(employee.isOtRequired());
                    //---------------------------------------------
                    employeeSalaryProcess.setEmployee(employee1);
                    System.out.println("Employee /in id: " + employee.getId());
                    employeeSalaryProcess = checkLateEntryCount(employeeSalaryProcess, null, null);
                    employeeSalaryProcess = checkLossOfPay(employeeSalaryProcess, null, null);
                    employeeSalaryProcess = getCurrentMonthEmployeeLeaveBalanceByEmployeeId(employeeSalaryProcess);
//                    BigDecimal arrears = checkArrears(employeeSalaryProcess);
                    employeeSalaryProcess.setCurrentBasic(employeeSalaryProcess.getCurrentBasic());
                    employeeSalaryProcessService.createEmployeeSalaryProcess(employeeSalaryProcess);
                    employeeSalaryProcessList.add(employeeSalaryProcess);
                }
            }
        }
        System.out.println("Total employees " + employeeList.size() + " salary list " + employeeSalaryProcessList.size());
//        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcessList) {
//            employeeSalaryProcessService.createEmployeeSalaryProcess(employeeSalaryProcess);
//        }

//        if (employeeList.size() == employeeSalaryProcessList.size()) {
//            for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcessList) {
//                employeeSalaryProcessService.createEmployeeSalaryProcess(employeeSalaryProcess);
//            }
//        } else {
//            throw new Exception("Few employees data missing");
//        }
        return new ResponseEntity(employeeSalaryProcessList, HttpStatus.OK);
    }

    @PostMapping("employee-salary-process/finance-approval")
    public ResponseEntity<EmployeeSalaryProcess> employeeSalaryProcessForFinanceApprovalReject(@RequestBody EmployeeSalaryDto employeeSalaryDto, HttpServletRequest request, HttpServletResponse response) {
        EmployeeSalaryProcess employeeSalaryProcess = null;

        for (Long id : employeeSalaryDto.approveIds) {
            Optional<EmployeeSalaryProcess> employeeSalaryProcessObj = employeeSalaryProcessService.getEmployeeSalaryProcess(id);
            employeeSalaryProcess = employeeSalaryProcessObj.get();
            employeeSalaryProcess.setSalaryApprovalStatus(SalaryApprovalStatus.FINANCE_APPROVED);
            EmployeeSalaryProcess salaryProcess = employeeSalaryProcessService.createEmployeeSalaryProcess(employeeSalaryProcess);
            if (salaryProcess != null) {
                sendSalaryPaySlipPDF(salaryProcess, response);
            }
        }


        for (FinanceRejection financeRejection : employeeSalaryDto.financeRejections) {
            Optional<EmployeeSalaryProcess> employeeSalaryProcessObj = employeeSalaryProcessService.getEmployeeSalaryProcess(financeRejection.id);
            employeeSalaryProcess = employeeSalaryProcessObj.get();
            employeeSalaryProcess.setSalaryApprovalStatus(SalaryApprovalStatus.FINANCE_REJECTED);
            employeeSalaryProcess.setRejectionNote(financeRejection.rejectionNote);
            employeeSalaryProcessService.createEmployeeSalaryProcess(employeeSalaryProcess);
        }

        return new ResponseEntity<>(employeeSalaryProcess, HttpStatus.OK);
    }

    @GetMapping("employee-salary-process/hr-approved-list")
    public ResponseEntity<List<EmployeeSalaryProcess>> listHRApprovedSalary() {
        long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        long previousMonth = Calendar.getInstance().get(Calendar.MONTH);
        List<EmployeeSalaryProcess> employeeSalaryProcessList = new ArrayList<>();

        List<EmployeeSalaryProcess> employeeSalaryProcesses = employeeSalaryProcessService.getApprovedList(SalaryApprovalStatus.HR_APPROVED);
        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
            Employee employee = new Employee();
            Employee e = employeeSalaryProcess.getEmployee();
            employee.setId(e.getId());
            employee.setFirstName(e.getFirstName());
            employee.setLastName(e.getLastName());
            employee.setEmployeeCode(e.getEmployeeCode());
            employeeSalaryProcess.setEmployee(employee);
            employeeSalaryProcessList.add(employeeSalaryProcess);

        }


//        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
//            long m = DateUtil.getMonthNumber(employeeSalaryProcess.getSalaryMonth());
//            if (m == currentMonth || m == previousMonth) {
//                employeeSalaryProcessList.add(employeeSalaryProcess);
//            }
//        }
        if (CollectionUtils.isEmpty(employeeSalaryProcesses)) {
            throw new EntityNotFoundException("Employee Salary List not found");
        }
        return new ResponseEntity(employeeSalaryProcessList, HttpStatus.OK);
    }

    @GetMapping("employee-salary-process/finance-rejected-list")
    public ResponseEntity<List<EmployeeSalaryProcess>> listOfRejectedSalary() {
        List<EmployeeSalaryProcess> employeeSalaryProcesses = employeeSalaryProcessService.getApprovedList(SalaryApprovalStatus.FINANCE_REJECTED);
        if (CollectionUtils.isEmpty(employeeSalaryProcesses)) {
            throw new EntityNotFoundException("Employee Salary FINANCE_REJECTED List not found");
        }
        return new ResponseEntity(employeeSalaryProcesses, HttpStatus.OK);
    }

    @GetMapping("employee-salary-process/finance-approved-list")
    public ResponseEntity<EmployeeSalaryProcess> employeeSalaryProcessForFinanceApprovedList() {
        long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        long previousMonth = Calendar.getInstance().get(Calendar.MONTH);
        List<EmployeeSalaryProcess> employeeSalaryProcesses = employeeSalaryProcessService.getApprovedList(SalaryApprovalStatus.FINANCE_APPROVED);
        List<EmployeeSalaryProcess> employeeSalaryProcessList = new ArrayList<>();

        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
            Employee employee = new Employee();
            Employee e = employeeSalaryProcess.getEmployee();
            employee.setId(e.getId());
            employee.setFirstName(e.getFirstName());
            employee.setLastName(e.getLastName());
            employee.setEmployeeCode(e.getEmployeeCode());
            employeeSalaryProcess.setEmployee(employee);
            employeeSalaryProcessList.add(employeeSalaryProcess);

        }
//        for (EmployeeSalaryProcess employeeSalaryProcess : employeeSalaryProcesses) {
//            long m = DateUtil.getMonthNumber(employeeSalaryProcess.getSalaryMonth());
//            if (m == currentMonth || m == previousMonth) {
//                employeeSalaryProcessList.add(employeeSalaryProcess);
//            }
//        }
        if (CollectionUtils.isEmpty(employeeSalaryProcesses)) {
            throw new EntityNotFoundException("Employee Salary List not found");
        }
        return new ResponseEntity(employeeSalaryProcessList, HttpStatus.OK);
    }

    @PostMapping("employee-salary-process/update/{id}")
    public ResponseEntity<EmployeeSalaryProcess> updateEmployeeSalaryProcess(@PathVariable long id, @RequestBody EmployeeSalaryProcess employeeSalaryProcess) {
        Optional<EmployeeSalaryProcess> employeeSalaryProcessObj = employeeSalaryProcessService.getEmployeeSalaryProcess(id);
        if (!employeeSalaryProcessObj.isPresent()) {
            throw new EntityNotFoundException("EmployeeSalaryProcess not found");
        }
        employeeSalaryProcess.setId(id);
        employeeSalaryProcess.setSalaryApprovalStatus(SalaryApprovalStatus.HR_APPROVED);
        employeeSalaryProcessService.createEmployeeSalaryProcess(employeeSalaryProcess);
        return new ResponseEntity(employeeSalaryProcess, HttpStatus.OK);
    }

    // Bring the unpaid leave of employee
    // If unpaid leave found the deduct the amount from net payable i.e take ctc devide 365 => deductable amount for 1 day
    // lets say if he is absent for two days multiply by 2 into duductable salary ie you will get lossOfPay\
    // substract lossOfpay fom net payable
    // set the substracted amount to the object and persist it


    public EmployeeSalaryProcess createSalaryProcess(EmployeeCTCData ctcData, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);  // January of 2015.
        LocalDate firstOfMonth = yearMonth.atDay(1);

        EmployeeSalaryProcess salaryProcess = new EmployeeSalaryProcess();
        salaryProcess.setBasicSalary(ctcData.getBasicSalary());
        salaryProcess.setVariableDearnessAllowance(ctcData.getVariableDearnessAllowance());
        salaryProcess.setConveyanceAllowance(ctcData.getConveyanceAllowance());
        salaryProcess.setHouseRentAllowance(ctcData.getHouseRentAllowance());
        salaryProcess.setEducationalAllowance(ctcData.getEducationalAllowance());
        salaryProcess.setMealsAllowance(ctcData.getMealsAllowance());
        salaryProcess.setWashingAllowance(ctcData.getWashingAllowance());
        salaryProcess.setOtherAllowance(ctcData.getOtherAllowance());
        salaryProcess.setMiscellaneousAllowance(ctcData.getMiscellaneousAllowance());
        salaryProcess.setMobileAllowance(ctcData.getMobileAllowance());
        salaryProcess.setRla(ctcData.getRla());
        salaryProcess.setTpt(ctcData.getTpt());
        salaryProcess.setUniformAllowance(ctcData.getUniformAllowance());
        salaryProcess.setShoeAllowance(ctcData.getShoeAllowance());
        salaryProcess.setBonus(ctcData.getBonus());
        salaryProcess.setGratuity(ctcData.getGratuity());
        salaryProcess.setMedical(ctcData.getMedicalReimbursement());
        salaryProcess.setMedicalPolicy(ctcData.getMedicalPolicy());
        salaryProcess.setLeaveTravelAllowance(ctcData.getLeaveTravelAllowance());
        salaryProcess.setRoyalty(ctcData.getRoyalty());
        salaryProcess.setEmployeeEsicContribution(ctcData.getEmployeeEsicContribution());
        salaryProcess.setEmployerContributionESIC(ctcData.getEmployeeEsicContribution());
        //this is employeer contribution
        long pt = getPT(ctcData);
        salaryProcess.setProfessionalTax(BigDecimal.valueOf(pt));
        salaryProcess.setSalaryYear("" + firstOfMonth.getYear());
        salaryProcess.setSalaryMonth("" + firstOfMonth.getMonth());
        salaryProcess.setSalaryApprovalStatus(SalaryApprovalStatus.HR_APPROVED);
        return salaryProcess;
    }

    public long getPT(EmployeeCTCData ctcDataDto) {
        BigDecimal totalEarningsPerMonth;
        totalEarningsPerMonth = ctcDataDto.getBasicSalary();
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getVariableDearnessAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getConveyanceAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getHouseRentAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getEducationalAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getWashingAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getOtherAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMiscellaneousAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMobileAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMealsAllowance());
        Double gross = totalEarningsPerMonth.doubleValue();
        long pt = 0;
        BigDecimal bonus = BigDecimal.ZERO;
        BigDecimal grossSalary = BigDecimal.valueOf(gross.doubleValue());
        BigDecimal grossLim = BigDecimal.valueOf(15000);
        if (gross.doubleValue() >= 15000) {
            pt = 200;
        } else {
            pt = 0;
        }
        return pt;
    }

    //Export-to-pdf.
    @PostMapping("employee-salary-process/pay-slip")
    public ResponseEntity<EmployeeSalaryProcess> getEmployeeSalaryPaySlipPDF(@RequestBody EmployeeSalaryDto employeeSalaryDto, HttpServletRequest request, HttpServletResponse response) {
        Optional<EmployeeSalaryProcess> employeeSalaryObj = employeeSalaryProcessService.getEmployeeSalaryProcessByMonth(employeeSalaryDto.employeeId, employeeSalaryDto.inputMonth, employeeSalaryDto.inputYear);
        if (!employeeSalaryObj.isPresent()) {
            throw new EntityNotFoundException("employeeSalaryProcess-monthwise");
        }
        EmployeeSalaryProcess employeeSalary = employeeSalaryObj.get();
        try {
            Paragraph para = new Paragraph();
            Document document = new Document();
            response.setContentType("application/pdf");
            // PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); //to download
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //PDF to send email
            PdfWriter.getInstance(document, baos); // PDF to send email
            document.open();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font font1 = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);

            Image img1 = Image.getInstance("https://res.cloudinary.com/hanumanth-cloudinary/image/upload/v1557470801/weldomac_logo.jpg");
            img1.scaleAbsolute(50f, 50f);
            img1.scaleToFit(70f, 50f);
            img1.setAbsolutePosition(90, 750);
            document.add(img1);

            para = new Paragraph("***** PVT LTD", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            para = new Paragraph("Belur Industrial Area,Dharwad", font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph("Pay-slip for the month of " + employeeSalary.getSalaryMonth() + " " + employeeSalary.getSalaryYear(), font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(6);
            table.setWidths(new int[]{2, 3, 2, 1, 1, 2});
            table.getDefaultCell().setBorder(0);
            table.setWidthPercentage(100);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Month:", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            PdfPCell pmonth = new PdfPCell(new Phrase("" + employeeSalary.getSalaryMonth(), FontFactory.getFont(FontFactory.COURIER, 10)));
            pmonth.setColspan(2);
            pmonth.setBorder(Rectangle.NO_BORDER);
            table.addCell(pmonth);


            table.addCell(new Phrase("Emp Id", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCode(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("PF No", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getPfNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("ESIC No", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEsiNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("UAN No", FontFactory.getFont(FontFactory.COURIER, 10)));
            PdfPCell pdfPCell = new PdfPCell(new Phrase(":" + employeeSalary.getEmployee().getUanNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
            pdfPCell.setColspan(2);
            pdfPCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(pdfPCell);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Employee Name", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Payment Days", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getPaymentDays(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);

            table.addCell(new Phrase("Late Attn", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getLateEntry(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("PH Holiday", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getNoOfHolidays(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));


            double noOfLeaves = employeeSalary.getNoOfLeaves();
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Leave", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + noOfLeaves, FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("SUNDAY", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getSundays(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Attendance", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getNoOfPresent(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Working Days", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getWorkingDays(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Earnings ", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Deductions", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Basic ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getCurrentBasic(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("P.F", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getEpfContribution(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Conveyance ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getConveyanceAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("E.S.I.C", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" /*+ employeeSalary.getEmployeeContributionESIC()*/, FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("HRA ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getHouseRentAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Advance/Loan", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getTotalAdvanceDeduction(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Edu. Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getEducationalAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("P.T", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getProfessionalTax(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("MA", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getMealsAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("TDS", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getTotalIncomeTaxDeduction(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("WA ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getWashingAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Other Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getOtherAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            BigDecimal totDeduction = BigDecimal.ZERO;
            totDeduction = totDeduction.add(employeeSalary.getEpfContribution());
            //totDeduction = totDeduction.add(employeeSalary.getEmployeeContributionESIC());
            totDeduction = totDeduction.add(employeeSalary.getProfessionalTax());
            totDeduction = totDeduction.add(employeeSalary.getTotalAdvanceDeduction());
            totDeduction = totDeduction.add(employeeSalary.getTotalIncomeTaxDeduction());


            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Misc Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getMiscellaneousAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Total", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs " + totDeduction, FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Mobile Allow", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getMobileAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Leave", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("CL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("EL/PL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("ML/SL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));

            BigDecimal totEarning = BigDecimal.ZERO;
            totEarning = totEarning.add(employeeSalary.getCurrentBasic());
            totEarning = totEarning.add(employeeSalary.getConveyanceAllowance());
            totEarning = totEarning.add(employeeSalary.getHouseRentAllowance());
            totEarning = totEarning.add(employeeSalary.getEducationalAllowance());
            totEarning = totEarning.add(employeeSalary.getMealsAllowance());
            totEarning = totEarning.add(employeeSalary.getWashingAllowance());
            totEarning = totEarning.add(employeeSalary.getMobileAllowance());
            totEarning = totEarning.add(employeeSalary.getOtherAllowance());
            totEarning = totEarning.add(employeeSalary.getMiscellaneousAllowance());

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Total ", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("Rs " + totEarning, FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Opening", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Availed", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorderWidth(1);
            table.getDefaultCell().setBorder(PdfPCell.ANCHOR);
            table.addCell(new Phrase("Net Pay", FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));
            table.addCell(new Phrase("Rs " + totEarning.subtract(totDeduction), FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorderWidth(0);
            table.addCell(new Phrase("Balance", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorderWidth(1);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorderWidth(0);

            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            //writer.setPageEvent(event);
            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);


            para = new Paragraph("******** This is a computer-generated document. No signature is required ********", font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.close();
            //Email Config
            try {
                Session mailSession = Session.getInstance(System.getProperties());
                Transport transport = new SMTPTransport(mailSession, new URLName("smtp.gmail.com"));
                transport = mailSession.getTransport("smtps");
                transport.connect("smtp.gmail.com", 465, "orileotest@gmail.com", "yakanna@123");

                MimeMessage m = new MimeMessage(mailSession);
                m.setFrom(new InternetAddress("orileotest@gmail.com"));
                Address[] toAddr = new InternetAddress[]{
                        new InternetAddress(employeeSalary.getEmployee().getEmployeeLogin().getEmail())
                };
                m.setRecipients(Message.RecipientType.TO, toAddr);
                m.setHeader("Content-Type", "multipart/mixed");
                m.setSubject("Pay Slip for " + employeeSalary.getEmployee().getFirstName() + " for " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear());
                m.setSentDate(new Date());

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Dear " + employeeSalary.getEmployee().getFirstName() + ",\nPlease find the attached payslip for month of " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".\n  *******" +
                        "THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL *******");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName("PaySlip-" + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".pdf");
                multipart.addBodyPart(messageBodyPart);
                m.setContent(multipart);
                transport.sendMessage(m, m.getAllRecipients());
                transport.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(employeeSalary, HttpStatus.OK);
    }

    public ResponseEntity<EmployeeSalaryProcess> sendSalaryPaySlipPDF(EmployeeSalaryProcess employeeSalary, HttpServletResponse response) {
        try {
            Paragraph para = new Paragraph();
            Document document = new Document();
            response.setContentType("application/pdf");
            //PdfWriter.getInstance(document, response.getOutputStream()); //to download
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //PDF to send email
            PdfWriter writer = PdfWriter.getInstance(document, baos); // PDF to send email
            document.open();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font font1 = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);

            Image img1 = Image.getInstance("https://res.cloudinary.com/hanumanth-cloudinary/image/upload/v1557470801/weldomac_logo.jpg");
            img1.scaleAbsolute(50f, 50f);
            img1.scaleToFit(70f, 50f);
            img1.setAbsolutePosition(90, 750);
            document.add(img1);

            para = new Paragraph("***** PVT LTD", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            para = new Paragraph("Belur Industrial Area,Dharwad", font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph("Pay-slip for the month of " + employeeSalary.getSalaryMonth() + " " + employeeSalary.getSalaryYear(), font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(6);
            table.setWidths(new int[]{2, 3, 2, 1, 1, 2});
            table.getDefaultCell().setBorder(0);
            table.setWidthPercentage(100);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Month:", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            PdfPCell pmonth = new PdfPCell(new Phrase("" + employeeSalary.getSalaryMonth(), FontFactory.getFont(FontFactory.COURIER, 10)));
            pmonth.setColspan(2);
            pmonth.setBorder(Rectangle.NO_BORDER);
            table.addCell(pmonth);

            table.addCell(new Phrase("Emp Id:", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getEmployee().getEmployeeCode(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("PF No. :", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getEmployee().getPfNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("ESIC No", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEsiNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("UAN No", FontFactory.getFont(FontFactory.COURIER, 10)));
            PdfPCell pdfPCell = new PdfPCell(new Phrase(":" + employeeSalary.getEmployee().getUanNumber(), FontFactory.getFont(FontFactory.COURIER, 10)));
            pdfPCell.setColspan(2);
            pdfPCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(pdfPCell);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Employee Name: ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Payment Days", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getPaymentDays(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);

            table.addCell(new Phrase("Late Attn", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getLateEntry(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("PH Holiday", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getNoOfHolidays(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            BigDecimal noOfLeaves = employeeSalary.getAvailEarnLeave().add(employeeSalary.getAvailCasualLeave()).add(employeeSalary.getAvailMeadicalLeave());

            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Leave", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + noOfLeaves, FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("SUNDAY", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getSundays(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Attendance", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getNoOfPresent(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Working Days", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getWorkingDays(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Earnings ", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Deductions", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Basic ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getCurrentBasic(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("P.F", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getEpfContribution(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Conveyance ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getConveyanceAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("E.S.I.C", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getEmployeeEsicContribution(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("HRA ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getHouseRentAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Advance/Loan", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            BigDecimal advance_loan = employeeSalary.getTotalAdvanceDeduction()
                    .add(employeeSalary.getTotalMealDeduction())
                    .add(employeeSalary.getTotalIncomeTaxDeduction())
                    .add(employeeSalary.getTotalOthers())
                    .add(employeeSalary.getTotalOtherDeduction());
            table.addCell(new Phrase("" + advance_loan, FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Edu. Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getEducationalAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("P.T", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getProfessionalTax(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("MA", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getMealsAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("TDS", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getTotalIncomeTaxDeduction(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("WA ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getWashingAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Other Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getOtherAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            BigDecimal totDeduction = BigDecimal.ZERO;
            totDeduction = totDeduction.add(employeeSalary.getEpfContribution());
            totDeduction = totDeduction.add(employeeSalary.getProfessionalTax());
            totDeduction = totDeduction.add(employeeSalary.getTotalAdvanceDeduction());
            totDeduction = totDeduction.add(employeeSalary.getTotalIncomeTaxDeduction());


            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Misc Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getMiscellaneousAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Total", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Rs " + employeeSalary.getTotalDeduction(), FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("Mobile Allow", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getMobileAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Leave", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(0);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("CL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("EL/PL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("ML/SL", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));

            BigDecimal totEarning = BigDecimal.ZERO;
            totEarning = totEarning.add(employeeSalary.getCurrentBasic());
            totEarning = totEarning.add(employeeSalary.getConveyanceAllowance());
            totEarning = totEarning.add(employeeSalary.getHouseRentAllowance());
            totEarning = totEarning.add(employeeSalary.getEducationalAllowance());
            totEarning = totEarning.add(employeeSalary.getMealsAllowance());
            totEarning = totEarning.add(employeeSalary.getWashingAllowance());
            totEarning = totEarning.add(employeeSalary.getMobileAllowance());
            totEarning = totEarning.add(employeeSalary.getOtherAllowance());
            totEarning = totEarning.add(employeeSalary.getMiscellaneousAllowance());

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("Total ", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            table.addCell(new Phrase("Rs " + totEarning, FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));

            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Opening", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getOpeningCasualLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getOpeningEarnLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getOpeningMedicalLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorder(1);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Availed", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getAvailCasualLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getAvailEarnLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getAvailMeadicalLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setBorderWidth(1);
            table.getDefaultCell().setBorder(PdfPCell.ANCHOR);
            table.addCell(new Phrase("Net Pay", FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));
            table.addCell(new Phrase("Rs " + totEarning.subtract(totDeduction), FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setBorderWidth(0);
            table.addCell(new Phrase("Balance", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getClosingCasualLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getClosingEarnLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("" + employeeSalary.getClosingMedicalLeave(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorderWidth(1);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setBorderWidth(0);

            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            writer.setPageEvent(event);
            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);


            para = new Paragraph("******** This is a computer-generated document. No signature is required ********", font1);

            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.close();

            //Email Config
            try {
                Session mailSession = Session.getInstance(System.getProperties());
                Transport transport = new SMTPTransport(mailSession, new URLName("smtp.gmail.com"));
                transport = mailSession.getTransport("smtps");
                transport.connect("smtp.gmail.com", 465, "orileotest@gmail.com", "yakanna@123");

                MimeMessage m = new MimeMessage(mailSession);
                m.setFrom(new InternetAddress("orileotest@gmail.com"));
                if (employeeSalary.getEmployee().getEmployeeLogin() == null) {
                    throw new EntityNotFoundException("Email id is not found for employee id: " + employeeSalary.getEmployee().getEmployeeCode());
                }
                Address[] toAddr = new InternetAddress[]{
//                        new InternetAddress(employeeSalary.getEmployee().getEmployeeLogin().getEmail())
                        new InternetAddress("varunvaru044@gmail.com")
                };
                m.setRecipients(Message.RecipientType.TO, toAddr);
                m.setHeader("Content-Type", "multipart/mixed");
                m.setSubject("Pay Slip for " + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName() + " for " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear());
                m.setSentDate(new Date());

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Dear " + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName() + ",\nPlease find the attached payslip for month of " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".\n \n\n\n\n *******" +
                        "THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL *******");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName("PaySlip-" + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".pdf");
                multipart.addBodyPart(messageBodyPart);
                m.setContent(multipart);
                transport.sendMessage(m, m.getAllRecipients());
                transport.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(employeeSalary, HttpStatus.OK);
    }

    //Rule for EPF Generate as per formula
    public static EmployeeSalaryProcess generateEPF(EmployeeSalaryProcess employeeSalaryProcess) {
        BigDecimal fixedEPFWage = new BigDecimal(15000.00);
        employeeSalaryProcess.setEpfWages(BigDecimal.valueOf(15000));
        int result = employeeSalaryProcess.getEpfWages().compareTo(fixedEPFWage);
        if (result == -1) {
            employeeSalaryProcess.setEpsWages(employeeSalaryProcess.getEpfWages());
            employeeSalaryProcess.setEdliWages(employeeSalaryProcess.getEpfWages());

        } else if (result == 0) {
            employeeSalaryProcess.setEpsWages(employeeSalaryProcess.getEpfWages());
            employeeSalaryProcess.setEdliWages(employeeSalaryProcess.getEpfWages());

        } else if (result == 1) {
            employeeSalaryProcess.setEpsWages(fixedEPFWage);
            employeeSalaryProcess.setEdliWages(fixedEPFWage);
        }
        BigDecimal eeShareRemitted = employeeSalaryProcess.getEpfWages().multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(100));
        BigDecimal epsContributionRemitted = employeeSalaryProcess.getEpfWages().multiply(BigDecimal.valueOf(8.33)).divide(BigDecimal.valueOf(100));
        BigDecimal edliInsuranceFund = employeeSalaryProcess.getEpfWages().multiply(BigDecimal.valueOf(0.5)).divide(BigDecimal.valueOf(100));

        eeShareRemitted = eeShareRemitted.setScale(0, BigDecimal.ROUND_UP);
        epsContributionRemitted = epsContributionRemitted.setScale(0, BigDecimal.ROUND_UP);
        edliInsuranceFund = edliInsuranceFund.setScale(0, BigDecimal.ROUND_UP);
        BigDecimal erShareRemitted = eeShareRemitted.subtract(epsContributionRemitted);

        employeeSalaryProcess.setEeShareRemitted(eeShareRemitted);
        employeeSalaryProcess.setEpsContributionRemitted(epsContributionRemitted);
        employeeSalaryProcess.setErShareRemitted(erShareRemitted);
        employeeSalaryProcess.setEdliInsuranceFund(edliInsuranceFund);
        return employeeSalaryProcess;
    }

    public EmployeeSalaryProcess checkLossOfPay(EmployeeSalaryProcess employeeSalaryProcess, Date sd, Date ed) throws Exception {

        Calendar calendar = Calendar.getInstance();
        int day = 1;

        int year = Integer.parseInt(employeeSalaryProcess.getSalaryYear());
        int month = Month.valueOf(employeeSalaryProcess.getSalaryMonth().toUpperCase()).getValue();
        YearMonth yearMonth = YearMonth.of(year, month);

        calendar.set(year, month - 1, day);
        int numOfDaysInMonth = yearMonth.lengthOfMonth();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        Date endDate = calendar.getTime();

        if (sd != null && ed != null) {
            startDate = sd;
            endDate = ed;
        }
        if (employeeSalaryProcess.getEmployee().getDateOfJoining().after(startDate)) {
            startDate = employeeSalaryProcess.getEmployee().getDateOfJoining();
        } else {
            startDate = startDate;
        }
        employeeAttendanceController.attendanceCompute(employeeSalaryProcess.getEmployee().getId(), startDate, endDate);
        Optional<AcademicYear> academicYearObj = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear = academicYearObj.get();
        List<Leave> earnLeave = leaveService.getEmployeeLeaveDetails(LeaveStatus.APPROVED, LeaveType.EARN_LEAVE, Integer.parseInt("" + employeeSalaryProcess.getEmployee().getId()), "EMPLOYEE", academicYear, DateUtil.convertDateToFormat(startDate), DateUtil.convertDateToFormat(endDate));
        List<Leave> MedicalLeave = leaveService.getEmployeeLeaveDetails(LeaveStatus.APPROVED, LeaveType.MEDICAL_LEAVE, Integer.parseInt("" + employeeSalaryProcess.getEmployee().getId()), "EMPLOYEE", academicYear, DateUtil.convertDateToFormat(startDate), DateUtil.convertDateToFormat(endDate));
        List<Leave> casualLeave = leaveService.getEmployeeLeaveDetails(LeaveStatus.APPROVED, LeaveType.CASUAL_LEAVE, Integer.parseInt("" + employeeSalaryProcess.getEmployee().getId()), "EMPLOYEE", academicYear, DateUtil.convertDateToFormat(startDate), DateUtil.convertDateToFormat(endDate));
        List<Leave> wopLeave = leaveService.getEmployeeLeaveDetails(LeaveStatus.APPROVED, LeaveType.WOP, Integer.parseInt("" + employeeSalaryProcess.getEmployee().getId()), "EMPLOYEE", academicYear, DateUtil.convertDateToFormat(startDate), DateUtil.convertDateToFormat(endDate));

        if (earnLeave.size() > 0 && earnLeave != null) {
            double lopLeave = 0;
            for (Leave leave : earnLeave) {
                lopLeave = lopLeave + leave.getTotalLeavesApplied();
            }
        }

        employeeSalaryProcess.setPaidLeaves(earnLeave.size());
        employeeSalaryProcess.setCasualLeaves(casualLeave.size());
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.PRESENT, startDate, endDate);
        List<EmployeeAttendance> employeeHalfAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.HALF_DAY, startDate, endDate);
        double halfDays = employeeHalfAttendances.size();
        double totalPresentDays = (halfDays * 0.5) + employeeAttendances.size();
        List<EmployeeAttendance> noOfLeaves = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.LEAVE, startDate, endDate);
        List<EmployeeAttendance> noOfEls = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.EL, startDate, endDate);
        List<EmployeeAttendance> noOfMls = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.ML, startDate, endDate);
        List<EmployeeAttendance> noOfCls = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.CL, startDate, endDate);

        List<EmployeeAttendance> employeeAbsent = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.ABSENT, startDate, endDate);
        employeeSalaryProcess.setNoOfPresent(totalPresentDays);

        employeeAttendanceController.attendanceCompute(employeeSalaryProcess.getEmployee().getId(), startDate, endDate);
//        long sundays = getNumberofSundays(startDate, endDate);
        long sundays = 0;
        List<EmployeeAttendance> sundayOffs = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.SUNDAY, startDate, endDate);
        List<EmployeeAttendance> weekOffs = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.WO, startDate, endDate);
        List<EmployeeAttendance> employeeHolidays = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.HOLIDAY, startDate, endDate);
        List<EmployeeAttendance> employeePaidHolidays = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.PH, startDate, endDate);
        long holidays = employeeHolidays.size() + employeePaidHolidays.size();
        sundays = sundayOffs.size() + weekOffs.size();
//        long noOfholidays = getNumberOfHolidays(startDate, endDate);
        long duration = endDate.getTime() - startDate.getTime();
        long paymentDays = 0;
        if (duration > 0) {
            paymentDays = TimeUnit.MILLISECONDS.toDays(duration) + 1;
        } else {
            paymentDays = TimeUnit.MILLISECONDS.toDays(duration);
        }
//        YearMonth yearMonthObject = YearMonth.of(year, month);
//        paymentDays = yearMonthObject.lengthOfMonth();
        double presentDays = employeeSalaryProcess.getNoOfPresent();
        double ropBasic = employeeSalaryProcess.getBasicSalary().doubleValue() / paymentDays;
        double ropConveyanceAllowance = employeeSalaryProcess.getConveyanceAllowance().doubleValue() / paymentDays;
        double ropHRA = employeeSalaryProcess.getHouseRentAllowance().doubleValue() / paymentDays;
        double ropEducationAllowance = employeeSalaryProcess.getEducationalAllowance().doubleValue() / paymentDays;
        double ropMealAllowance = employeeSalaryProcess.getMealsAllowance().doubleValue() / paymentDays;
        double ropWashingAllowance = employeeSalaryProcess.getWashingAllowance().doubleValue() / paymentDays;
        double ropOtherAllowance = employeeSalaryProcess.getOtherAllowance().doubleValue() / paymentDays;
        double ropMisc = employeeSalaryProcess.getMiscellaneousAllowance().doubleValue() / paymentDays;
        double ropMobile = employeeSalaryProcess.getMobileAllowance().doubleValue() / paymentDays;

//        long totalNoOfLeaves = earnLeave.size() + MedicalLeave.size() + casualLeave.size();Set
        double totalNoOfLeaves = noOfLeaves.size() + noOfCls.size() + noOfEls.size() + noOfMls.size() + (halfDays * 0.5);
        if (presentDays == 0) {
            sundays = 0;
            holidays = 0;
            totalNoOfLeaves = 0;
        }
        double totalPaidDays = presentDays + sundays + holidays + totalNoOfLeaves;
        totalPaidDays = totalPaidDays - wopLeave.size();
        BigDecimal currentBasic = BigDecimal.valueOf(Math.round(ropBasic * totalPaidDays));
        BigDecimal currentConveyance = BigDecimal.valueOf(Math.round(ropConveyanceAllowance * totalPaidDays));
        BigDecimal currentHRA = BigDecimal.valueOf(Math.round(ropHRA * totalPaidDays));
        BigDecimal currentEducationAllowance = BigDecimal.valueOf(Math.round(ropEducationAllowance * totalPaidDays));
        BigDecimal currentMealAllowance = BigDecimal.valueOf(Math.round(ropMealAllowance * totalPaidDays));
        BigDecimal currentWashingAllowance = BigDecimal.valueOf(Math.round(ropWashingAllowance * totalPaidDays));
        BigDecimal currentOtherAllowance = BigDecimal.valueOf(Math.round(ropOtherAllowance * totalPaidDays));
        BigDecimal currentMiscellaneousAllowance = BigDecimal.valueOf(Math.round(ropMisc * totalPaidDays));
        BigDecimal currentMobileAllowance = BigDecimal.valueOf(Math.round(ropMobile * totalPaidDays));

        employeeSalaryProcess.setRateOfPay(BigDecimal.valueOf(Math.round(ropBasic)));
        // employeeSalaryProcess = checkLateEntryCount(employeeSalaryProcess);

        // TODO Late entry calculation later

       /* if (employeeSalaryProcess.getLateEntry() > 0) {
            System.out.println("Current Basic: "+currentBasic);
            BigDecimal lateEntryCharge = BigDecimal.valueOf(employeeSalaryProcess.getLateEntry() * (ropBasic / 2));
            employeeSalaryProcess.setLateEntryFine(lateEntryCharge);
            System.out.println("Late Entry charge : "+lateEntryCharge);
            currentBasic = currentBasic.subtract(lateEntryCharge);
            System.out.println("Current Basic: "+currentBasic);
        }*/
        employeeSalaryProcess.setCurrentBasic(currentBasic);
        employeeSalaryProcess.setConveyanceAllowance(currentConveyance);
        employeeSalaryProcess.setHouseRentAllowance(currentHRA);
        employeeSalaryProcess.setEducationalAllowance(currentEducationAllowance);
        employeeSalaryProcess.setMealsAllowance(currentMealAllowance);
        employeeSalaryProcess.setWashingAllowance(currentWashingAllowance);
        employeeSalaryProcess.setOtherAllowance(currentOtherAllowance);
        employeeSalaryProcess.setMiscellaneousAllowance(currentMiscellaneousAllowance);
        employeeSalaryProcess.setMobileAllowance(currentMobileAllowance);
        BigDecimal lossOfPay = employeeSalaryProcess.getBasicSalary().subtract(currentBasic);
        employeeSalaryProcess.setLossOfPay(lossOfPay);
        employeeSalaryProcess.setSundays(sundays);
        employeeSalaryProcess.setNoOfHolidays(holidays);
        employeeSalaryProcess.setPaymentDays(paymentDays);
        long workingDays = paymentDays - sundays - holidays;
        // 31-4-3
        employeeSalaryProcess.setWorkingDays(workingDays);
        employeeSalaryProcess.setUnpaidLeaves(wopLeave.size());
        //long leave = employeeSalaryProcess.getPaidLeaves() + employeeSalaryProcess.getUnpaidLeaves() + employeeSalaryProcess.getCasualLeaves();
        employeeSalaryProcess.setNoOfLeaves(totalNoOfLeaves);

        BigDecimal totalEarnings;
        totalEarnings = employeeSalaryProcess.getCurrentBasic();
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getVariableDearnessAllowance());
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getConveyanceAllowance());
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getHouseRentAllowance());
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getEducationalAllowance());
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getMealsAllowance());
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getWashingAllowance());
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getOtherAllowance());
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getMiscellaneousAllowance());
        totalEarnings = totalEarnings.add(employeeSalaryProcess.getMobileAllowance());
        System.out.println("Emp Id" + employeeSalaryProcess.getEmployee().getId());
        System.out.println("total earnings: " + totalEarnings);
//        totalEarnings = totalEarnings.add(employeeSalaryProcess.getEpfContribution());
//        totalEarnings = totalEarnings.add(employeeSalaryProcess.getGratuity());
        employeeSalaryProcess.setGrossSalary(totalEarnings);
        employeeSalaryProcess.setTotalEarning(totalEarnings);

        employeeSalaryProcess = getEmployeeSalaryDeductions(employeeSalaryProcess, employeeSalaryProcess.getEmployee().getId(), startDate, endDate);

        BigDecimal totalDeductions = BigDecimal.ZERO;

        BigDecimal roundOff = employeeSalaryProcess.getCurrentBasic().multiply(BigDecimal.valueOf(0.12));
        employeeSalaryProcess.setEpfContribution(roundOff.setScale(0, BigDecimal.ROUND_UP));
        double basic = employeeSalaryProcess.getCurrentBasic().doubleValue();
        double maxBasic = 15000;
        if (basic > maxBasic) {
            BigDecimal pf = BigDecimal.valueOf(maxBasic * 0.12);
            employeeSalaryProcess.setEpfContribution(pf);
        }
//        if (employeeSalaryProcess.getEmployee().isEpf()) {
//            employeeSalaryProcess.setEpfContribution(roundOff.setScale(0, BigDecimal.ROUND_UP));
//        } else {
//            employeeSalaryProcess.setEpfContribution(BigDecimal.ZERO);
//        }
        employeeSalaryProcess = calculateEmployeeEsicAndUpdateSalary(employeeSalaryProcess);
        totalDeductions = employeeSalaryProcess.getEpfContribution().add(employeeSalaryProcess.getEmployeeEsicContribution());
        totalDeductions = totalDeductions.add(employeeSalaryProcess.getProfessionalTax());
        if (employeeSalaryProcess != null) {
            totalDeductions = totalDeductions.add(employeeSalaryProcess.getTotalAdvanceDeduction());
            totalDeductions = totalDeductions.add(employeeSalaryProcess.getTotalIncomeTaxDeduction());
            totalDeductions = totalDeductions.add(employeeSalaryProcess.getTotalMealDeduction());
            totalDeductions = totalDeductions.add(employeeSalaryProcess.getTotalOthers());
            totalDeductions = totalDeductions.add(employeeSalaryProcess.getTotalOtherDeduction());
            totalDeductions.add(totalDeductions);
        }
        employeeSalaryProcess.setTotalDeduction(totalDeductions);
        employeeSalaryProcess.setNetPaid(totalEarnings.subtract(totalDeductions));
//        BigDecimal a = checkArrears(employeeSalaryProcess);
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal totalearnings = employeeSalaryProcess.getNetPaid().add(a);
        if (employeeSalaryProcess.getOtPay() != null) {
            totalearnings = totalearnings.add(employeeSalaryProcess.getOthers());
        }
        if (employeeSalaryProcess.getLateEntryLoss() != null) {
            totalearnings = totalearnings.subtract(employeeSalaryProcess.getLateEntryLoss());
        }
        if (employeeSalaryProcess.getEarlyOutLoss() != null) {
            totalearnings = totalearnings.subtract(employeeSalaryProcess.getEarlyOutLoss());
        }
        BigDecimal bonus = BigDecimal.valueOf(300);
        if (totalPaidDays == paymentDays) {
            totalearnings = totalearnings.add(bonus);
            employeeSalaryProcess.setBonus(bonus);
        }
        employeeSalaryProcess.setNetPaid(totalearnings);
        employeeSalaryProcess = generateEPF(employeeSalaryProcess);
        return employeeSalaryProcess;
    }

    private EmployeeSalaryProcess checkAttendancePresentCount(EmployeeSalaryProcess employeeSalaryProcess) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = 1;
        calendar.set(year, month, day);
        int numOfDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        Date endDate = calendar.getTime();
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.PRESENT, startDate, endDate);
        employeeSalaryProcess.setNoOfPresent(employeeAttendances.size());
        return employeeSalaryProcess;
    }

    public static long getNumberofSundays(Date d1, Date d2) throws Exception {
        Date date1 = d1;
        Date date2 = d2;

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        long sundays = 0;
        while (!c1.after(c2)) {
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                sundays++;
            }
            c1.add(Calendar.DATE, 1);
        }
        return sundays;
    }

    public long getNumberOfHolidays(Date d1, Date d2) throws Exception {
        long noOfHolidays = 0;
        List<Holiday> holidays = holidayService.getHolidayBetweenStartDateAndEndDate(d1, d2);
        if (holidays.size() > 0 && holidays != null) {
            for (Holiday holiday : holidays) {
                long noOfHoliday;
                noOfHolidays = holidays.size();
            }
        }
        return noOfHolidays;
    }

    @Autowired
    CompanyConfigureService companyConfigureService;

    private static DecimalFormat df = new DecimalFormat("0.00");

    public EmployeeSalaryProcess checkLateEntryCount(EmployeeSalaryProcess employeeSalaryProcess, Date sd, Date ed) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        int day = 1;
        int year = Integer.parseInt(employeeSalaryProcess.getSalaryYear());

        int month = Month.valueOf(employeeSalaryProcess.getSalaryMonth().toUpperCase()).getValue();
        YearMonth yearMonth = YearMonth.of(year, month);

        calendar.set(year, month - 1, day);
        int numOfDaysInMonth = yearMonth.lengthOfMonth();

        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        Date endDate = calendar.getTime();

        if (sd != null && ed != null) {
            startDate = sd;
            endDate = ed;
        }

        if (employeeSalaryProcess.getEmployee().getDateOfJoining().after(startDate)) {
            startDate = employeeSalaryProcess.getEmployee().getDateOfJoining();
        } else {
            startDate = startDate;
        }
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.PRESENT, startDate, endDate);

        double lateEntryCount = 0;
        double totalOTHrsOfEmployee = 0;
        double totalearlyOutHrs = 0;

        for (EmployeeAttendance attendance : employeeAttendances) {

            if (attendance.getEffectiveOverTime() != null) {
                totalOTHrsOfEmployee = totalOTHrsOfEmployee + Double.parseDouble(attendance.getEffectiveOverTime());
            }
            totalOTHrsOfEmployee = Double.valueOf(String.format("%.2f", totalOTHrsOfEmployee));


            if (attendance.getLateEntryNotFormated() != null) {
                lateEntryCount = lateEntryCount + Double.parseDouble(attendance.getLateEntryNotFormated());
                lateEntryCount = Double.parseDouble(df.format(lateEntryCount));
            }


            if (attendance.getEarlyOutNotFormated() != null) {
                totalearlyOutHrs = totalearlyOutHrs + Double.parseDouble(attendance.getEarlyOutNotFormated());
                totalearlyOutHrs = Double.parseDouble(df.format(totalearlyOutHrs));
            }
        }

        List<CompanyConfigure> companyConfigures = companyConfigureService.getCompany();
        CompanyConfigure companyConfigure = null;
        boolean lateEnable = false;
        boolean earlyEnable = false;
        if (!companyConfigures.isEmpty()) {
            Collections.reverse(companyConfigures);
            companyConfigure = companyConfigures.get(0);
            lateEnable = companyConfigure.isLateEntryLoss();
            earlyEnable = companyConfigure.isEarlyOutLoss();
        }

        if (employeeSalaryProcess.getEmployee().isOtRequired()) {
            employeeSalaryProcess = otPay(employeeSalaryProcess, totalOTHrsOfEmployee, year, month, sd, ed);
        }

        if (employeeSalaryProcess.getEmployee().isOtRequired() && lateEnable) {
            employeeSalaryProcess = lateEntryLoss(employeeSalaryProcess, lateEntryCount, year, month, sd, ed);
        }
        if (employeeSalaryProcess.getEmployee().isOtRequired() && earlyEnable) {
            employeeSalaryProcess = earlyOutLoss(employeeSalaryProcess, totalearlyOutHrs, year, month, sd, ed);
        }

        return employeeSalaryProcess;
    }

    public EmployeeSalaryProcess getEmployeeSalaryDeductions(EmployeeSalaryProcess employeeSalaryProcess, long employeeId, Date startDate, Date endDate) {
        BigDecimal totalAdvanceDeduction = BigDecimal.ZERO;
        BigDecimal totalIncomeTaxDeduction = BigDecimal.ZERO;
        BigDecimal totalMealDeduction = BigDecimal.ZERO;
        BigDecimal totalOthers = BigDecimal.ZERO;
        BigDecimal totalOtherDeduction = BigDecimal.ZERO;
        List<EmployeeSalaryDeduction> employeeSalaryDeductions = employeeSalaryDeductionService.getEmployeeSalaryDeductionsByEmployeeId(employeeId, startDate, endDate);
        if (employeeSalaryDeductions != null && employeeSalaryDeductions.size() > 0) {
            for (EmployeeSalaryDeduction deduction : employeeSalaryDeductions) {
                if (deduction.isStatus() == true) {
                    totalAdvanceDeduction = totalAdvanceDeduction.add(deduction.getAdvance());
                    totalIncomeTaxDeduction = totalIncomeTaxDeduction.add(deduction.getTdsIncometax());
                    totalMealDeduction = totalMealDeduction.add(deduction.getMeal());
                    totalOthers = totalOthers.add(deduction.getOther());
                    totalOtherDeduction = totalOtherDeduction.add(deduction.getOtherDeduction());
                }
            }
        }
        employeeSalaryProcess.setTotalAdvanceDeduction(totalAdvanceDeduction);
        employeeSalaryProcess.setTotalIncomeTaxDeduction(totalIncomeTaxDeduction);
        employeeSalaryProcess.setTotalMealDeduction(totalMealDeduction);
        employeeSalaryProcess.setTotalOthers(totalOthers);
        employeeSalaryProcess.setTotalOtherDeduction(totalOtherDeduction);
        return employeeSalaryProcess;
    }

    @PostMapping("employee-salary-process/get-employee-esi")
    public ResponseEntity<EmployeeSalaryProcess> calculateEmployeerContribution(@RequestBody EmployeeSalaryCalculationDto employeeSalaryCalculationDto) {
        BigDecimal employeerContribution = new BigDecimal("0");
        int checkEmployeeSalaryContribution = employeeSalaryCalculationDto.getEmployeeContribution().compareTo(BigDecimal.valueOf(0));
        if (checkEmployeeSalaryContribution == 1) {
//            employeerContribution = (employeeSalaryCalculationDto.getGross().multiply(BigDecimal.valueOf(0.0475)));
            employeerContribution = (employeeSalaryCalculationDto.getGross().multiply(BigDecimal.valueOf(0.0325)));
        } else {
            employeerContribution = new BigDecimal("0");
        }
        return new ResponseEntity(employeerContribution, HttpStatus.OK);
    }


    public EmployeeSalaryProcess calculateEmployeeEsicAndUpdateSalary(EmployeeSalaryProcess employeeSalaryProcess) {
        if (employeeSalaryProcess.getEmployeeEsicContribution() != null) {
            if (employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMonthlyCtc().doubleValue() <= 21000) {
                BigDecimal otPay = BigDecimal.ZERO;
                if (employeeSalaryProcess.getOtPay() != null) {
                    otPay = employeeSalaryProcess.getOtPay();
                }
                BigDecimal grossPlusOt = employeeSalaryProcess.getGrossSalary().add(otPay);
                BigDecimal employeeContribution = (grossPlusOt.multiply(BigDecimal.valueOf(0.0075)));
                employeeSalaryProcess.setEmployeeEsicContribution(employeeContribution.setScale(0, BigDecimal.ROUND_UP));
            } else {
                employeeSalaryProcess.setEmployeeEsicContribution(BigDecimal.ZERO);
            }
        } else {
            employeeSalaryProcess.setEmployeeEsicContribution(BigDecimal.ZERO);
        }
        return employeeSalaryProcess;
    }

    @PostMapping("employee-salary-process/get-employee-epf-contribution")
    public ResponseEntity<EmployeeSalaryProcess> calculateEPFContribution(@RequestBody EmployeeSalaryCalculationDto employeeSalaryCalculationDto) {
        BigDecimal fixedEPFWage = new BigDecimal(15000.00);
        employeeSalaryCalculationDto.setEpfWages(employeeSalaryCalculationDto.getBasic());
        int result = employeeSalaryCalculationDto.getEpfWages().compareTo(fixedEPFWage);
        if (result == -1) {
            employeeSalaryCalculationDto.setEpsWages(employeeSalaryCalculationDto.getEpfWages());
            employeeSalaryCalculationDto.setEdliWages(employeeSalaryCalculationDto.getEpfWages());

        } else if (result == 0) {
            employeeSalaryCalculationDto.setEpsWages(employeeSalaryCalculationDto.getEpfWages());
            employeeSalaryCalculationDto.setEdliWages(employeeSalaryCalculationDto.getEpfWages());

        } else if (result == 1) {
            employeeSalaryCalculationDto.setEpsWages(fixedEPFWage);
            employeeSalaryCalculationDto.setEdliWages(fixedEPFWage);
        }
        BigDecimal eeShareRemitted = employeeSalaryCalculationDto.getEpfWages().multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(100));
        BigDecimal epsContributionRemitted = employeeSalaryCalculationDto.getEpfWages().multiply(BigDecimal.valueOf(8.33)).divide(BigDecimal.valueOf(100));
        BigDecimal edliInsuranceFund = employeeSalaryCalculationDto.getEpfWages().multiply(BigDecimal.valueOf(0.5)).divide(BigDecimal.valueOf(100));

        eeShareRemitted = eeShareRemitted.setScale(0, BigDecimal.ROUND_UP);
        epsContributionRemitted = epsContributionRemitted.setScale(0, BigDecimal.ROUND_UP);
        edliInsuranceFund = edliInsuranceFund.setScale(0, BigDecimal.ROUND_UP);
        BigDecimal erShareRemitted = eeShareRemitted.subtract(epsContributionRemitted);

        employeeSalaryCalculationDto.setEeShareRemitted(eeShareRemitted);
        employeeSalaryCalculationDto.setEpsContributionRemitted(epsContributionRemitted);
        employeeSalaryCalculationDto.setErShareRemitted(erShareRemitted);
        employeeSalaryCalculationDto.setEdliInsuranceFund(edliInsuranceFund);

        return new ResponseEntity(employeeSalaryCalculationDto, HttpStatus.OK);
    }

    public EmployeeSalaryProcess getCurrentMonthEmployeeLeaveBalanceByEmployeeId(EmployeeSalaryProcess employeeSalaryProcess) {
        Optional<AcademicYear> academicYearObj = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear = academicYearObj.get();
        long currentMonth = Month.valueOf(employeeSalaryProcess.getSalaryMonth().toUpperCase()).getValue();
        //long currentMonth=Calendar.getInstance().get(Calendar.MONTH)+1;
        LocalDate today = LocalDate.now();
        Date monthBeginDate = java.sql.Date.valueOf(today.withDayOfMonth(1));
        Date monthEndDate = java.sql.Date.valueOf(today.plusMonths(1).withDayOfMonth(1).minusDays(1));
        List<Leave> medical = leaveService.getEmployeeLeaveDetails(LeaveStatus.APPROVED, LeaveType.MEDICAL_LEAVE, Integer.parseInt(String.valueOf(employeeSalaryProcess.getEmployee().getId())), "EMPLOYEE", academicYear, monthBeginDate, monthEndDate);
        List<Leave> casual = leaveService.getEmployeeLeaveDetails(LeaveStatus.APPROVED, LeaveType.CASUAL_LEAVE, Integer.parseInt(String.valueOf(employeeSalaryProcess.getEmployee().getId())), "EMPLOYEE", academicYear, monthBeginDate, monthEndDate);
        List<Leave> earn = leaveService.getEmployeeLeaveDetails(LeaveStatus.APPROVED, LeaveType.EARN_LEAVE, Integer.parseInt(String.valueOf(employeeSalaryProcess.getEmployee().getId())), "EMPLOYEE", academicYear, monthBeginDate, monthEndDate);
        BigDecimal totalMedicalLeave = BigDecimal.valueOf(0);
        BigDecimal totalCasualLeave = BigDecimal.valueOf(0);
        BigDecimal totalEarnLeave = BigDecimal.valueOf(0);
        if (medical != null && medical.size() > 0) {
            for (Leave medicalLeave : medical) {
                totalMedicalLeave = totalMedicalLeave.add(BigDecimal.valueOf(medicalLeave.getTotalLeavesApplied()));
            }
        }
        if (casual != null && casual.size() > 0) {
            for (Leave casualLeave : casual) {
                totalCasualLeave = totalCasualLeave.add(BigDecimal.valueOf(casualLeave.getTotalLeavesApplied()));
            }
        }
        if (earn != null && earn.size() > 0) {
            for (Leave earnLeave : earn) {
                totalEarnLeave = totalEarnLeave.add(BigDecimal.valueOf(earnLeave.getTotalLeavesApplied()));
            }
        }
        EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employeeSalaryProcess.getEmployee().getId(), academicYear.getId());

        BigDecimal medicalClosingLeave = employeeLeaveBalance.getClosingLeave().getMedicalLeave();
        BigDecimal casualClosingLeave = employeeLeaveBalance.getClosingLeave().getClearanceLeave();
        BigDecimal earnClosingLeave = employeeLeaveBalance.getClosingLeave().getEarnLeave();

        BigDecimal medicalOpeningLeave = medicalClosingLeave.add(totalMedicalLeave);
        BigDecimal casualOpeningLeave = casualClosingLeave.add(totalCasualLeave);
        BigDecimal earnOpeningLeave = earnClosingLeave.add(totalEarnLeave);

        employeeSalaryProcess.setOpeningEarnLeave(earnOpeningLeave);
        employeeSalaryProcess.setOpeningCasualLeave(casualOpeningLeave);
        employeeSalaryProcess.setOpeningMedicalLeave(medicalOpeningLeave);
        employeeSalaryProcess.setAvailEarnLeave(totalEarnLeave);
        employeeSalaryProcess.setAvailCasualLeave(totalCasualLeave);
        employeeSalaryProcess.setAvailMeadicalLeave(totalMedicalLeave);
        employeeSalaryProcess.setClosingEarnLeave(earnClosingLeave);
        employeeSalaryProcess.setClosingCasualLeave(casualClosingLeave);
        employeeSalaryProcess.setClosingMedicalLeave(medicalClosingLeave);

//        long totalLeave = medicalOpeningLeave.longValue() + casualClosingLeave.longValue() + earnClosingLeave.longValue();
//        employeeSalaryProcess.setNoOfLeaves(totalLeave);
        //employeeSalaryProcess.setNoOfLeaves(totalLeave);

        long totalLeave = totalEarnLeave.longValue() + totalCasualLeave.longValue() + totalMedicalLeave.longValue();
        return employeeSalaryProcess;
    }

    @GetMapping("employee-salary/view-salary-by-month/{year}/{month}")
    public ResponseEntity<List<EmployeeSalaryProcess>> viewSalaryByMonth(@PathVariable("year") int year, @PathVariable("month") int month) throws Exception {
        YearMonth yearMonth = YearMonth.of(year, month);  // January of 2015.
        LocalDate monthName = yearMonth.atDay(1);
        List<EmployeeSalaryProcess> employeeSalaryByMonth = employeeSalaryProcessService.getEmployeeSalaryByMonthAndYear(String.valueOf(monthName.getMonth()), String.valueOf(year), SalaryApprovalStatus.FINANCE_APPROVED);
        if (!(employeeSalaryByMonth != null && employeeSalaryByMonth.size() > 0)) {
            throw new EntityNotFoundException("FINANCE APPROVED Salary not found by month:" + monthName.getMonth() + " & year:" + year);
        }
        return new ResponseEntity(employeeSalaryByMonth, HttpStatus.OK);
    }


    public void attendanceCompute(EmployeeSalaryProcess employeeSalaryProcess, Date startDate, Date endDate) throws Exception {
        /*startDate= DateUtil.getYesterdayDate();
        endDate=DateUtil.getYesterdayDate();*/
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employeeSalaryProcess.getEmployee().getId(), AttendanceStatus.PRESENT, startDate, endDate);
        ArrayList allPresent = new ArrayList();
        ArrayList halfDayList = new ArrayList();
        long count = 0;
        for (EmployeeAttendance attendance : employeeAttendances) {
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            if (attendance.getInTime() == null) {
                /*attendance.setOutTime(attendance.getShift().getEndTime());
                employeeAttendanceService.createEmployeeAttendance(attendance);*/
                throw new Exception("No In-Time for " + attendance.getEmployee().getFirstName() + " On " + attendance.getMarkedOn());
            }
            String inTime = timeFormat.format(attendance.getInTime());
            if (attendance.getOutTime() == null) {
                /*attendance.setOutTime(attendance.getShift().getEndTime());
                employeeAttendanceService.createEmployeeAttendance(attendance);*/
                throw new Exception("No Out-Time for " + attendance.getEmployee().getFirstName() + " On " + attendance.getMarkedOn());
            }
            String outTime = timeFormat.format(attendance.getOutTime());
            long attendanceInTime = timeFormat.parse(inTime).getTime();
            long attendanceOutTime = timeFormat.parse(outTime).getTime();
            String shiftStartTime = timeFormat.format(attendance.getShift().getStartTime());
            long shiftStart = timeFormat.parse(shiftStartTime).getTime();
            String shiftEndTime = timeFormat.format(attendance.getShift().getEndTime());
            long shiftEnd = timeFormat.parse(shiftEndTime).getTime();
            if (attendanceInTime < shiftStart && attendanceOutTime > shiftEnd) {
                allPresent.add(attendance);
            } else if (attendanceInTime > shiftStart) {
                count++;
                long lateEntryTwoHours = shiftStart + 7200000;
                long earlyExitTwoHours = shiftEnd - 7200000;
                if (count > 2) {
                    if (attendanceInTime > lateEntryTwoHours || attendanceOutTime < earlyExitTwoHours) {
                        attendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
                        EmployeeAttendance employeeAttendance = employeeAttendanceService.createEmployeeAttendance(attendance, attendance.isDataProcessed());
                        halfDayList.add(employeeAttendance);
                    }
                } else if (attendanceOutTime < (shiftEnd - 7200000)) {
                    attendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
                    EmployeeAttendance employeeAttendance = employeeAttendanceService.createEmployeeAttendance(attendance, attendance.isDataProcessed());
                    halfDayList.add(employeeAttendance);
                }
            } else if (attendanceOutTime < shiftEnd) {
                if (attendanceInTime > (shiftStart + 7200000) || attendanceOutTime < (shiftEnd - 7200000)) {
                } else {
                    allPresent.add(attendance);
                }
            }
        }
    }


    @GetMapping("set-ctc-for-appraisal")
    public void updateCtcToSetEmpAndAffectedFrom() {
        List<EmployeeCTCData> employeeCTCData = employeeCtcDataRepo.findAll();
        List<Employee> employeeList = employeeService.findAll();
        for (Employee e : employeeList) {
            if (e.getEmployeeCTCData() != null) {
                long ctcId = e.getEmployeeCTCData().getId();

                EmployeeCTCData employeeCTCData1 = employeeCtcDataRepo.findOne(ctcId);
                employeeCTCData1.setEmpId(e.getId());
                employeeCTCData1.setAffectFrom(e.getDateOfJoining());
                employeeCtcDataRepo.save(employeeCTCData1);
            }
        }
    }

    public BigDecimal checkArrears(EmployeeSalaryProcess employeeSalaryProcess) {
        BigDecimal arrears = BigDecimal.ZERO;
        Optional<AcademicYear> a = academicYearService.getActiveAcademicYear();
        Date affectedFrom = employeeSalaryProcess.getEmployee().getEmployeeCTCData().getAffectFrom();
        Date announcedOn = employeeSalaryProcess.getEmployee().getEmployeeCTCData().getAnnouncedOn();

        List<EmployeeCTCData> employeeCTCData = employeeCtcDataRepo.findByEmpId(employeeSalaryProcess.getEmployee().getId());
        Collections.reverse(employeeCTCData);

        long affectedFromMonth = DateUtil.getMonthNumber(affectedFrom);
        long salaryMonth = DateUtil.getMonthNumber(employeeSalaryProcess.getSalaryMonth());
        System.out.println("salary month: " + salaryMonth + " affectedMonth " + affectedFromMonth);
        long diffOfMonth = salaryMonth - affectedFromMonth;

        long premonthNumber = salaryMonth - 1;

        for (long i = 0; i < diffOfMonth; i++) {

            String monthName = Month.of((int) premonthNumber).name();
            Optional<EmployeeSalaryProcess> previousMonthEmpSalaryProcess = employeeSalaryProcessService.getEmployeeSalaryProcessByMonth(employeeSalaryProcess.getEmployee().getId(), monthName, employeeSalaryProcess.getSalaryYear());

            //------previous Month sal process
            YearMonth yearMonthObjectPreviousMonth = YearMonth.of(Integer.parseInt(a.get().getYear()), (int) premonthNumber);
            int daysInMonthPrev = yearMonthObjectPreviousMonth.lengthOfMonth();
            if (previousMonthEmpSalaryProcess.isPresent()) {

                long previousroundBasic = previousMonthEmpSalaryProcess.get().getBasicSalary().longValue();
                long previousAllowanceTotal = previousMonthEmpSalaryProcess.get().getConveyanceAllowance().longValue() + previousMonthEmpSalaryProcess.get().getHouseRentAllowance().longValue() + previousMonthEmpSalaryProcess.get().getEducationalAllowance().longValue()
                        + previousMonthEmpSalaryProcess.get().getMealsAllowance().longValue() + previousMonthEmpSalaryProcess.get().getWashingAllowance().longValue()
                        + previousMonthEmpSalaryProcess.get().getOtherAllowance().longValue() + previousMonthEmpSalaryProcess.get().getMiscellaneousAllowance().longValue()
                        + previousMonthEmpSalaryProcess.get().getMobileAllowance().longValue();
                System.out.println("**************Total Prevous Allowance: " + previousAllowanceTotal);
                long previousMonthWorkingDays = (long) (previousMonthEmpSalaryProcess.get().getNoOfPresent() + previousMonthEmpSalaryProcess.get().getSundays() + previousMonthEmpSalaryProcess.get().getNoOfHolidays() + previousMonthEmpSalaryProcess.get().getNoOfLeaves());
//                System.out.println("previousBasic " + previousroundBasic + " working days" + previousMonthEmpSalaryProcess.get().getWorkingDays());
                long PreviousMonthEmpSalaryPerDay = ((previousroundBasic / previousMonthWorkingDays));


                //----------current month sal process
                YearMonth yearMonthObjectCurrentMonth = YearMonth.of(Integer.parseInt(a.get().getYear()), (int) salaryMonth);
                int daysInMonthCurrent = yearMonthObjectCurrentMonth.lengthOfMonth();
                long currentroundBasic = employeeSalaryProcess.getBasicSalary().longValue();
                long currentWorkingDays = (long) (employeeSalaryProcess.getNoOfPresent() + employeeSalaryProcess.getSundays() + employeeSalaryProcess.getNoOfHolidays() + employeeSalaryProcess.getNoOfLeaves());


                double allowanceTotal = employeeSalaryProcess.getTotalEarning().longValue() - currentroundBasic;
                System.out.println("total earning: " + employeeSalaryProcess.getTotalEarning().longValue());
                System.out.println("currentroundBasic: " + currentroundBasic);

                double paymentDays = previousMonthEmpSalaryProcess.get().getPaymentDays();
                double currentAllowance = allowanceTotal / paymentDays;
                System.out.println("getPaymentDays: " + previousMonthEmpSalaryProcess.get().getPaymentDays());
                System.out.println("per day: " + currentAllowance);
                System.out.println("previousMonthWorkingDays: " + previousMonthWorkingDays);
                currentAllowance = currentAllowance * previousMonthWorkingDays;
                System.out.println("currentAllowance: " + currentAllowance);

//                allowanceTotal = allowanceTotal / previousMonthEmpSalaryProcess.get().getPaymentDays();
//                allowanceTotal =  allowanceTotal *  previousMonthWorkingDays;
                System.out.println("**************Total Allowance: " + currentAllowance);

                long currentempPerDay = (long) (allowanceTotal / currentWorkingDays);


                long basicDiffPerDay = currentempPerDay - PreviousMonthEmpSalaryPerDay;


                if (basicDiffPerDay == 0) {
                    arrears = BigDecimal.ZERO;

                } else {
                    long diffAllowance = (long) (currentAllowance - previousAllowanceTotal);
                  /*  arrears = BigDecimal.valueOf(diffAllowance/previousMonthEmpSalaryProcess.get().getPaymentDays());
                    arrears = arrears.multiply(BigDecimal.valueOf(previousMonthWorkingDays));*/
                    System.out.println("******************** Arrears : ********************" + diffAllowance);
                    System.out.println("********************* current working days: " + previousMonthWorkingDays);
                    System.out.println("********************* PaymentDays: " + previousMonthEmpSalaryProcess.get().getPaymentDays());
                    arrears = arrears.add(BigDecimal.valueOf(diffAllowance));
                }

            }
            premonthNumber--;
        }

        return arrears;
    }


    //ARUN PAYSLIP
    @PostMapping("employee-salary-process/pay-slip1")
    public ResponseEntity<EmployeeSalaryProcess> getEmployeeSalaryPaySlipPDF1(@RequestBody EmployeeSalaryDto employeeSalaryDto, HttpServletRequest request, HttpServletResponse response) {
        Optional<EmployeeSalaryProcess> employeeSalaryObj = employeeSalaryProcessService.getEmployeeSalaryProcessByMonth(employeeSalaryDto.employeeId, employeeSalaryDto.inputMonth, employeeSalaryDto.inputYear);
        if (!employeeSalaryObj.isPresent()) {
            throw new EntityNotFoundException("employeeSalaryProcess-monthwise");
        }
        EmployeeSalaryProcess employeeSalary = employeeSalaryObj.get();
        try {
            Paragraph para = new Paragraph();
            Document document = new Document();
            response.setContentType("application/pdf");
            // PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); //to download
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //PDF to send email
            PdfWriter.getInstance(document, baos); // PDF to send email
            document.open();
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font font1 = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);

//            Image img1 = Image.getInstance("https://res.cloudinary.com/hanumanth-cloudinary/image/upload/v1557470801/weldomac_logo.jpg");
            Image img1 = Image.getInstance("");
            img1.scaleAbsolute(50f, 50f);
            img1.scaleToFit(70f, 50f);
            img1.setAbsolutePosition(90, 750);
            document.add(img1);

            para = new Paragraph("*** PVT LTD", fontHeader);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            para = new Paragraph("****", font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);

            para = new Paragraph("Pay-slip for the month of " + employeeSalary.getSalaryMonth() + " " + employeeSalary.getSalaryYear(), font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(6);
            table.setWidths(new int[]{2, 3, 2, 1, 1, 2});
            table.getDefaultCell().setBorder(0);
            table.setWidthPercentage(100);

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("Month:", FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
            PdfPCell pmonth = new PdfPCell(new Phrase("" + employeeSalary.getSalaryMonth(), FontFactory.getFont(FontFactory.COURIER, 10)));
            pmonth.setColspan(2);
            pmonth.setBorder(Rectangle.NO_BORDER);
            table.addCell(pmonth);


            table.addCell(new Phrase("Emp Id", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getEmployeeCode(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Empl Name", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getFirstName() + " " + employeeSalary.getEmployee().getLastName(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.addCell(new Phrase("Designation", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase(":" + employeeSalary.getEmployee().getDepartment().getName(), FontFactory.getFont(FontFactory.COURIER, 10)));


            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Gross ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getGrossSalary(), FontFactory.getFont(FontFactory.COURIER, 10)));


            table.getDefaultCell().setBorder(0);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Basic ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getCurrentBasic(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("HRA ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getHouseRentAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Conveyance ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getConveyanceAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Edu. Allow", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getEducationalAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Washing Allow", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getWashingAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Other Allow ", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getOtherAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("Spl Allow", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getMiscellaneousAllowance(), FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("PT", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getProfessionalTax(), FontFactory.getFont(FontFactory.COURIER, 10)));


            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("E.S.I.C", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" /*+ employeeSalary.getEmployeeContributionESIC()*/, FontFactory.getFont(FontFactory.COURIER, 10)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(new Phrase("P.F", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(new Phrase("" + employeeSalary.getEpfContribution(), FontFactory.getFont(FontFactory.COURIER, 10)));

            BigDecimal totEarning = BigDecimal.ZERO;
            totEarning = totEarning.add(employeeSalary.getCurrentBasic());
            totEarning = totEarning.add(employeeSalary.getConveyanceAllowance());
            totEarning = totEarning.add(employeeSalary.getHouseRentAllowance());
            totEarning = totEarning.add(employeeSalary.getEducationalAllowance());
            totEarning = totEarning.add(employeeSalary.getMealsAllowance());
            totEarning = totEarning.add(employeeSalary.getWashingAllowance());
            totEarning = totEarning.add(employeeSalary.getMobileAllowance());
            totEarning = totEarning.add(employeeSalary.getOtherAllowance());
            totEarning = totEarning.add(employeeSalary.getMiscellaneousAllowance());


            BigDecimal totDeduction = BigDecimal.ZERO;
            totDeduction = totDeduction.add(employeeSalary.getEpfContribution());
            //totDeduction = totDeduction.add(employeeSalary.getEmployeeContributionESIC());
            totDeduction = totDeduction.add(employeeSalary.getProfessionalTax());
            totDeduction = totDeduction.add(employeeSalary.getTotalAdvanceDeduction());
            totDeduction = totDeduction.add(employeeSalary.getTotalIncomeTaxDeduction());

            table.getDefaultCell().setBorderWidth(1);
            table.getDefaultCell().setBorder(PdfPCell.ANCHOR);
            table.addCell(new Phrase("Net Pay", FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));
            table.addCell(new Phrase("Rs " + totEarning.subtract(totDeduction), FontFactory.getFont(FontFactory.COURIER_BOLD, 12)));

            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            table.addCell(new Phrase("", FontFactory.getFont(FontFactory.COURIER, 10)));
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            //writer.setPageEvent(event);
            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);


            para = new Paragraph("*** This is a computer-generated document. No signature is required ***", font1);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.close();
            //Email Config
            try {
                Session mailSession = Session.getInstance(System.getProperties());
                Transport transport = new SMTPTransport(mailSession, new URLName("smtp.gmail.com"));
                transport = mailSession.getTransport("smtps");
                transport.connect("smtp.gmail.com", 465, "orileotest@gmail.com", "yakanna@123");

                MimeMessage m = new MimeMessage(mailSession);
                m.setFrom(new InternetAddress("orileotest@gmail.com"));
                Address[] toAddr = new InternetAddress[]{
                        new InternetAddress(employeeSalary.getEmployee().getEmployeeLogin().getEmail())
                };
                m.setRecipients(Message.RecipientType.TO, toAddr);
                m.setHeader("Content-Type", "multipart/mixed");
                m.setSubject("Pay Slip for " + employeeSalary.getEmployee().getFirstName() + " for " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear());
                m.setSentDate(new Date());

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Dear " + employeeSalary.getEmployee().getFirstName() + ",\nPlease find the attached payslip for month of " + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".\n  ***" +
                        "THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL ***");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(baos.toByteArray(), "application/pdf");
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName("PaySlip-" + employeeSalary.getSalaryMonth() + "-" + employeeSalary.getSalaryYear() + ".pdf");
                multipart.addBodyPart(messageBodyPart);
                m.setContent(multipart);
                transport.sendMessage(m, m.getAllRecipients());
                transport.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(employeeSalary, HttpStatus.OK);
    }

    public EmployeeSalaryProcess otPay(EmployeeSalaryProcess employeeSalaryProcess, double totalOtHrs, int y, int m, Date s, Date e) {
        double ot = 0;
        double otPay;
        double otFact = 1.5;
        double gross = employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMonthlyCtc().doubleValue();
        YearMonth yearMonthObject = YearMonth.of(y, m);
        long calenderDays = yearMonthObject.lengthOfMonth();
        if (s != null && e != null) {
            long duration = e.getTime() - s.getTime();
            calenderDays = 0;
            if (duration > 0) {
                calenderDays = TimeUnit.MILLISECONDS.toDays(duration) + 1;
            } else {
                calenderDays = TimeUnit.MILLISECONDS.toDays(duration);
            }
        }

        ot = (gross / (calenderDays * 8));
        ot = ot * otFact;
        otPay = ot * totalOtHrs;
        employeeSalaryProcess.setOthers(BigDecimal.valueOf(otPay));
        employeeSalaryProcess.setOtPay(BigDecimal.valueOf(otPay));
        employeeSalaryProcess.setOtHrs(totalOtHrs);
        return employeeSalaryProcess;
    }

    public EmployeeSalaryProcess lateEntryLoss(EmployeeSalaryProcess employeeSalaryProcess, double totalLateEntryHrs, int y, int m, Date s, Date e) {
        double ot = 0;
        double otPay;
        double gross = employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMonthlyCtc().doubleValue();
        Date date = new Date();
        YearMonth yearMonthObject = YearMonth.of(y, m);
        long calenderDays = yearMonthObject.lengthOfMonth();
        if (s != null && e != null) {
            long duration = e.getTime() - s.getTime();
            calenderDays = 0;
            if (duration > 0) {
                calenderDays = TimeUnit.MILLISECONDS.toDays(duration) + 1;
            } else {
                calenderDays = TimeUnit.MILLISECONDS.toDays(duration);
            }
        }
        ot = (gross / (calenderDays * 8));
        otPay = ot * totalLateEntryHrs;
        employeeSalaryProcess.setLateEntryLoss(BigDecimal.valueOf(otPay));
        employeeSalaryProcess.setTotalLateEntryHrs(totalLateEntryHrs);
        return employeeSalaryProcess;
    }

    public EmployeeSalaryProcess earlyOutLoss(EmployeeSalaryProcess employeeSalaryProcess, double totalEarlyOutHrs, int y, int m, Date s, Date e) {
        double ot = 0;
        double otPay;
        double gross = employeeSalaryProcess.getEmployee().getEmployeeCTCData().getMonthlyCtc().doubleValue();
        Date date = new Date();
        YearMonth yearMonthObject = YearMonth.of(y, m);
        long calenderDays = yearMonthObject.lengthOfMonth();
        if (s != null && e != null) {
            long duration = e.getTime() - s.getTime();
            calenderDays = 0;
            if (duration > 0) {
                calenderDays = TimeUnit.MILLISECONDS.toDays(duration) + 1;
            } else {
                calenderDays = TimeUnit.MILLISECONDS.toDays(duration);
            }
        }
        ot = (gross / (calenderDays * 8));
        otPay = ot * totalEarlyOutHrs;
        employeeSalaryProcess.setEarlyOutLoss(BigDecimal.valueOf(otPay));
        employeeSalaryProcess.setTotalEarlyOutHrs(totalEarlyOutHrs);
        return employeeSalaryProcess;
    }


    @GetMapping("ot-check")
    public void otPayCheck() {
        double ot = 0;
        double otPay;
        double gross = 25420;
        double totalOtHrs = 20.82;
        YearMonth yearMonthObject = YearMonth.of(2021, 1);
        int calenderDays = yearMonthObject.lengthOfMonth();
//        ot = ((gross / (calenderDays * 8)) * (150 / 100));
        double otFact = 1.5;
        ot = (gross / (calenderDays * 8));
        ot = ot * otFact;
        otPay = ot * totalOtHrs;
        System.out.println("Othrs = " + totalOtHrs + " and amount = " + otPay + " and calenderdays " + calenderDays + " otPerHour " + ot);
    }


    @GetMapping("employee-salary/check-salary-upto-date/{year}/{month}")
    public ResponseEntity<Boolean> checkSalaryProcessed(@PathVariable("year") int year, @PathVariable("month") int month) throws Exception {
//        List<EmployeeSalaryProcess> employeeSalaryProcessList = new ArrayList<>();
//        List<Employee> employeeList = employeeService.getEmployeeByType(EmployeeType.PERMANENT, true);
//        List<Employee> employeeListW = employeeService.getEmployeeByType(EmployeeType.PERMANENT_WORKER, true);
//        List<Employee> employeeListCont = employeeService.getEmployeeByType(EmployeeType.CONTRACT, true);
//        employeeList.addAll(employeeListW);
//        employeeList.addAll(employeeListCont);
//
//        if (employeeList != null && employeeList.size() > 0) {
//            for (Employee employee : employeeList) {
//                Optional<EmployeeSalaryProcess> employeeSalaryObj = employeeSalaryProcessService.getEmployeeSalaryCreatedByMonth(employee.getId(), Month.of(month).name(), String.valueOf(year));
//                if (employeeSalaryObj.isPresent()) {
////                    throw new EntityNotFoundException("Employee Salary Already Generated");
//                    employeeSalaryProcessService.delete(employeeSalaryObj.get().getId());
//                }
//                if (employee.getEmployeeCTCData() != null) {
//                    EmployeeCTCData employeeCTCData = employee.getEmployeeCTCData();
//                    EmployeeSalaryProcess employeeSalaryProcess = createSalaryProcess(employeeCTCData, year, month);
//                    employeeSalaryProcess.setRefId(employee.getId());
//                    //---------------------------------------------
//                    Employee employee1 = new Employee();
//                    employee1.setId(employee.getId());
//                    employee1.setFirstName(employee.getFirstName());
//                    employee1.setLastName(employee.getLastName());
//                    employee1.setEmployeeCode(employee.getEmployeeCode());
//                    employee1.setDateOfJoining(employee.getDateOfJoining());
//                    employee1.setEmployeeCTCData(employee.getEmployeeCTCData());
//                    employee1.setEmployeeType(employee.getEmployeeType());
//                    employee1.setOtRequired(employee.isOtRequired());
//                    //---------------------------------------------
//                    employeeSalaryProcess.setEmployee(employee1);
//                    System.out.println("Employee /in id: " + employee.getId());
//                    employeeSalaryProcess = checkLateEntryCount(employeeSalaryProcess);
//                    employeeSalaryProcess = checkLossOfPay(employeeSalaryProcess);
//                    employeeSalaryProcess = getCurrentMonthEmployeeLeaveBalanceByEmployeeId(employeeSalaryProcess);
////                    BigDecimal arrears = checkArrears(employeeSalaryProcess);
//                    employeeSalaryProcess.setCurrentBasic(employeeSalaryProcess.getCurrentBasic());
//                    employeeSalaryProcessService.createEmployeeSalaryProcess(employeeSalaryProcess);
//                    employeeSalaryProcessList.add(employeeSalaryProcess);
//                }
//            }
//        }
//        System.out.println("Total employees " + employeeList.size() + " salary list " + employeeSalaryProcessList.size());
        boolean processed = false;

        return new ResponseEntity(processed, HttpStatus.OK);
    }
}



