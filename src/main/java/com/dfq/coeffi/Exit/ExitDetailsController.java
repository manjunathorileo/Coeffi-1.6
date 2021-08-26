package com.dfq.coeffi.Exit;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ExitDetailsController extends BaseController {
    @Autowired
    EmployeeExitDetailsService employeeExitDetailsService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeExitService employeeExitService;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    EmployeeLeaveBalanceService employeeLeaveBalanceService;
    @Autowired
    MailService mailService;


    @PostMapping("exit-detail/{empid}")
    public ResponseEntity<EmployeeExitDetails> createExitDetail(@PathVariable("empid") long empid, @RequestBody EmployeeExitDetails employeeExitDetails) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        employeeExitDetails.setEmployee(employee.get());
        EmployeeExitDetails employeeExitDetails1 = employeeExitDetailsService.createExitDetail(employeeExitDetails);
        return new ResponseEntity<>(employeeExitDetails, HttpStatus.CREATED);
    }

    @PostMapping("/exit-details/{empid}")
    public ResponseEntity<List<EmployeeExitDetails>> createExitDetails(@PathVariable("empid") long empid, @RequestBody List<EmployeeExitDetails> employeeExitDetails) {
        List<EmployeeExitDetails> detailsList = new ArrayList<>();
        Optional<Employee> employee = employeeService.getEmployee(empid);
        for (EmployeeExitDetails employeeExitDetails1 : employeeExitDetails) {
            employeeExitDetails1.setEmployee(employee.get());
            detailsList.add(employeeExitDetails1);
        }
        List<EmployeeExitDetails> exitDetails = employeeExitDetailsService.createExitDetails(detailsList);
        if (exitDetails != null) {
            checkExistDetails(employee.get().getId());
        }
        return new ResponseEntity<>(exitDetails, HttpStatus.CREATED);
    }

    @GetMapping("exit-details/{id}")
    public ResponseEntity<Optional<EmployeeExitDetails>> getExitDetailsById(@PathVariable long id) {
        Optional<EmployeeExitDetails> employeeExitDetails = employeeExitDetailsService.getExitDetailsById(id);

        return new ResponseEntity<>(employeeExitDetails, HttpStatus.OK);
    }

    @GetMapping("exit-details")
    public ResponseEntity<List<EmployeeExitDetails>> getAllExitDetails() {
        List<EmployeeExitDetails> employeeExitDetails = employeeExitDetailsService.getAllExitDetails();
        return new ResponseEntity<>(employeeExitDetails, HttpStatus.OK);
    }


    @PostMapping("exit-details/upload-document/{id}")
    public ResponseEntity<EmployeeExitDetails> uploadProfilePicture(@PathVariable("id") long id, @RequestParam("file") MultipartFile file) throws IOException {
        EmployeeExitDetails persistedEmployee = null;
        if (file.isEmpty()) {
            throw new FileNotFoundException();

        } else {
            Optional<EmployeeExitDetails> employeeExitDetails = employeeExitDetailsService.getExitDetailsById(id);

            EmployeeExitDetails employeeExitDetails1 = employeeExitDetails.get();
            Document document = fileStorageService.storeFile(file);
            employeeExitDetails1.setDocument(document);
            persistedEmployee = employeeExitDetailsService.createExitDetail(employeeExitDetails1);
        }
        return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
    }


    @PostMapping("exit-details/submit")
    public ResponseEntity<EmployeeExit> submit(@RequestBody ExitDto exitDto) {
        Optional<Employee> employee = employeeService.getEmployee(exitDto.getEmployeeId());
        Employee emp = employee.get();
        Optional<EmployeeExit> employeeExit = employeeExitService.getExitByEmpl(emp);
        EmployeeExit empExit = employeeExit.get();
        empExit.setStatus(true);
        empExit.setEmailId(exitDto.getAlternateEmail());
        Document document = fileStorageService.getDocument(exitDto.getDocumentId());
        empExit.setResignationFile(document);
        EmployeeExit employeeExit1 = employeeExitService.createExit(empExit);
        return new ResponseEntity<>(employeeExit1, HttpStatus.OK);
    }

    @PostMapping("exit-details/hr-final-review")
    public ResponseEntity<EmployeeExit> hrFinalReview(@RequestBody EmployeeExit employeeExit) {
        EmployeeExit employeeExit1 = employeeExitService.createExit(employeeExit);
        if (employeeExit1.isHrReviewCompleted()) {
            boolean content = employeeExit1.getHrFinalStatus();
            String message;
            if (content) {
                message = "ACCEPTED";
            } else {
                message = "REJECTED";
            }
            sendEmailResignatioStatus(employeeExit1.getEmailId(), employeeExit1.getEmployee().getFirstName(), "Your Resignation is " + message);
        }
        return new ResponseEntity<>(employeeExit1, HttpStatus.OK);
    }

    public void sendEmailResignatioStatus(String email, String firstName, String content) {
        Date todayDate = new Date();
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = "Resignation_Status" + " on " + todayDate;
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, null);
        mailService.sendEmail(mailnew, "****");
    }

    @PostMapping("exit-details/upload-document")
    public ResponseEntity<Document> uploadNoDue(@RequestParam("file") MultipartFile file) throws IOException {
        Document document = null;
        if (file.isEmpty()) {
            throw new FileNotFoundException();

        } else {
            document = fileStorageService.storeFile(file);
        }
        return new ResponseEntity<>(document, HttpStatus.OK);
    }


    @GetMapping("exit-details/hr")
    public ResponseEntity<List<EmployeeExit>> hrView() {
        List<EmployeeExit> employeeExit = employeeExitService.getAllExit();
        List<EmployeeExit> employeeExitList = new ArrayList<>();

        for (EmployeeExit employeeExit1 : employeeExit) {
            if (employeeExit1.getStatus() == true && employeeExit1.isHrReviewCompleted() == false) {
                employeeExitList.add(employeeExit1);
            }
        }
        if (employeeExitList.isEmpty()) {
            throw new EntityNotFoundException("No Details for approval");
        }
        return new ResponseEntity<>(employeeExitList, HttpStatus.OK);
    }

    public void checkExistDetails(long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        List<EmployeeExitDetails> employeeExitDetails = employeeExitDetailsService.getExitDetailsByEmpl(employee.get());
        if (employeeExitDetails != null) {
            Optional<EmployeeExit> employeeExit = employeeExitService.getExitByEmpl(employee.get());
            if (employeeExit.isPresent()) {
                Optional<EmployeeLeaveBalance> employeeLeaveBalanceOptional = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeId(employee.get().getId());
                BigDecimal totalNoticePeriod = employee.get().getTotalNoticePeriod();
                BigDecimal leaveBalance = employeeLeaveBalanceOptional.get().getClosingLeave().getTotalLeave();
                employeeExit.get().setTotalNoticePeriod(totalNoticePeriod);
                employeeExit.get().setLeaveBalance(leaveBalance);
                employeeExit.get().setStatus(true);
                employeeExit.get().setEmployee(employee.get());
                employeeExit.get().setEmployeeExitDetails(employeeExitDetails);
                EmployeeExit employeeExitObj = employeeExitService.createExit(employeeExit.get());
            } else {
                EmployeeExit employeeExit1 = new EmployeeExit();
                Optional<EmployeeLeaveBalance> employeeLeaveBalanceOptional = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeId(employee.get().getId());
                BigDecimal totalNoticePeriod = employee.get().getTotalNoticePeriod();
                BigDecimal leaveBalance = employeeLeaveBalanceOptional.get().getClosingLeave().getTotalLeave();
                employeeExit1.setTotalNoticePeriod(totalNoticePeriod);
                employeeExit1.setLeaveBalance(leaveBalance);
                employeeExit1.setStatus(true);
                employeeExit1.setEmployee(employee.get());
                employeeExit1.setEmployeeExitDetails(employeeExitDetails);
                EmployeeExit employeeExitObj = employeeExitService.createExit(employeeExit1);
            }
        }
    }
}
