package com.dfq.coeffi.Expenses.Controllers;

import com.dfq.coeffi.Expenses.Entities.EmployeeExpenses;
import com.dfq.coeffi.Expenses.Entities.Expenses;
import com.dfq.coeffi.Expenses.Entities.ExpensesDto;
import com.dfq.coeffi.Expenses.Entities.ExpensesEnum;
import com.dfq.coeffi.Expenses.Services.EmployeeExpenseService;
import com.dfq.coeffi.Expenses.Services.ExpensesService;
import com.dfq.coeffi.SOPDetails.SopDocument.doc.Doc;
import com.dfq.coeffi.SOPDetails.SopDocument.doc.DocService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import jxl.write.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.CENTRE;

@RestController
public class ExpensesController extends BaseController {
    @Autowired
    ExpensesService expensesService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeExpenseService employeeExpenseService;
    @Autowired
    FileStorageService fileStorageService;
    @Autowired
    MailService mailService;
    @Autowired
    DocService docService;


//    @PostMapping("expenses/save")
//    public ResponseEntity<List<Expenses>> createExpenses(List<Expenses> expensesList) {
//        List<Expenses> expenses = new ArrayList<>();
//        for (Expenses expense : expensesList) {
//            Expenses exPersist = expensesService.save(expense);
//            expenses.add(expense);
//        }
//        return new ResponseEntity<>(expenses, HttpStatus.CREATED);
//    }


    @PostMapping("expenses-employee-save/{empid}")
    public ResponseEntity<List<Expenses>> createExitDetails(@PathVariable("empid") long empid, @RequestBody List<Expenses> expenses) {
        List<Expenses> detailsList = new ArrayList<>();
        Optional<Employee> employee = employeeService.getEmployee(empid);
        for (Expenses e : expenses) {
            e.setEmployeeId(empid);
            detailsList.add(e);
        }
        List<Expenses> exitDetails = expensesService.save(detailsList);
        if (exitDetails != null) {
            checkExistDetails(employee.get().getId());
        }

        return new ResponseEntity<>(exitDetails, HttpStatus.CREATED);
    }

    public void checkExistDetails(long id) {
        List<Expenses> expensesList = expensesService.getExpensesByEmployee(id);
        Optional<Employee> employee=employeeService.getEmployee(id);
        if (expensesList != null) {
            EmployeeExpenses employeeExpenses = employeeExpenseService.getByEmpId(id);
            if (employeeExpenses!=null) {
                employeeExpenses.setEmployeeId(id);
                employeeExpenses.setDesignation(employee.get().getDesignation().getName());
                employeeExpenses.setEmployeeName(employee.get().getFirstName()+" "+employee.get().getLastName());
                employeeExpenses.setExpenses(expensesList);
                employeeExpenses.setExpenseStatus(ExpensesEnum.New);
                EmployeeExpenses employeeExitObj = employeeExpenseService.createExit(employeeExpenses);
            } else {
                EmployeeExpenses employeeExpenses1 = new EmployeeExpenses();
                employeeExpenses1.setEmployeeId(id);
                employeeExpenses1.setDesignation(employee.get().getDesignation().getName());
                employeeExpenses1.setEmployeeName(employee.get().getFirstName()+" "+employee.get().getLastName());
                employeeExpenses1.setExpenses(expensesList);
                employeeExpenses1.setExpenseStatus(ExpensesEnum.New);
                EmployeeExpenses employeeExitObj = employeeExpenseService.createExit(employeeExpenses1);
            }
        }
    }

    @PostMapping("expenses-upload-document")
    public ResponseEntity<Doc> uploadNoDue(@RequestParam("file") MultipartFile file) throws IOException {
        Doc document1 =docService.saveDocuments(file);
        if(document1 ==null)
        {
            throw new EntityNotFoundException("**********");
        }
        return new ResponseEntity<>(document1, HttpStatus.ACCEPTED);
    }

    @GetMapping("download-expense-document/{fileId}")
    public ResponseEntity<Resource> getWordFileById(@PathVariable long fileId)
    {
        // Load file from database
        Doc document3 = docService.getDocumentFileById(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document3.getDocumentFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document3.getDocumentFileName() + "\"")
                .body(new ByteArrayResource(document3.getData()));
    }

    @PostMapping("expenses-submit")
    public ResponseEntity<Expenses> submit(@RequestBody ExpensesDto expensesDto) {
        Optional<Employee> employee = employeeService.getEmployee(expensesDto.getEmployeeId());
        Employee emp = employee.get();
        EmployeeExpenses employeeExpenses=employeeExpenseService.getByEmpId(emp.getId());


        employeeExpenses.setExpenseStatus(ExpensesEnum.Submitted);
        employeeExpenses.setTotalAmount(expensesDto.getTotalAmount());
        employeeExpenses.setManagerId(emp.getFirstApprovalManager().getId());
       employeeExpenseService.createExit(employeeExpenses);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("expenses-employee-view/{empid}")
    public ResponseEntity<List<EmployeeExpenses>> getAllExpensesEmployeeId(@PathVariable long empid)
    {
        //Optional<Employee> employee = employeeService.getEmployee(empid);
        List<EmployeeExpenses> employeeExpenses=employeeExpenseService.getAll();
        List<EmployeeExpenses> employeeExpensesList=new ArrayList<>();
        for (EmployeeExpenses e:employeeExpenses)
        {
            if(e.getEmployeeId()==empid)
            {
                employeeExpensesList.add(e);
            }

        }
        return new ResponseEntity<>(employeeExpensesList,HttpStatus.OK);
    }
    @GetMapping("expenses-manager-view/{mgrId}")
    public ResponseEntity<List<EmployeeExpenses>> getExpenseMgrView(@PathVariable long mgrId)
    {
        List<EmployeeExpenses> employeeExpenses=employeeExpenseService.getAll();
        EmployeeExpenses empExp=employeeExpenseService.getByEmpId(mgrId);
        List<EmployeeExpenses> employeeExpensesList=new ArrayList<>();
        for (EmployeeExpenses e:employeeExpenses)
        {
            if (e.getExpenseStatus().equals(ExpensesEnum.Submitted) && e.getManagerId()==mgrId)
            {
                employeeExpensesList.add(e);

            }
        }
        return new ResponseEntity<>(employeeExpensesList,HttpStatus.OK);
    }

    @GetMapping("expenses-manager-view-all-status/{mgrId}")
    public ResponseEntity<List<EmployeeExpenses>> getExpenseMgr(@PathVariable long mgrId)
    {
        List<EmployeeExpenses> employeeExpenses=employeeExpenseService.getAll();
        EmployeeExpenses empExp=employeeExpenseService.getByEmpId(mgrId);
        List<EmployeeExpenses> employeeExpensesList=new ArrayList<>();
        for (EmployeeExpenses e:employeeExpenses)
        {
            if (e.getManagerId()==mgrId)
            {
                employeeExpensesList.add(e);

            }
        }
        return new ResponseEntity<>(employeeExpensesList,HttpStatus.OK);
    }

    @PostMapping("expenses-manager-approve/{empid}/{remarks}")
    public ResponseEntity<EmployeeExpenses> getExpenseApproved(@PathVariable long empid,@PathVariable String remarks)
    {
        //Optional<Employee> firstManager=employeeService.getEmployeeByManagerId(firstmanager);
        EmployeeExpenses empExp=employeeExpenseService.getByEmpId(empid);
        System.out.println("empExp: "+empExp.getId());
        long mgrid=0;
        mgrid=empExp.getManagerId();
        Optional<Employee> employee1=employeeService.getEmployee(mgrid);
        Optional<Employee> employee=employeeService.getEmployee(empid);
            empExp.setExpenseStatus(ExpensesEnum.Approved);
            empExp.setReMarks(remarks);
            empExp.setApprovedBy(employee1.get().getFirstName()+" "+employee1.get().getLastName());
            employeeExpenseService.createExit(empExp);


        String message = "Your Expenses" + ExpensesEnum.Approved;
        sendEmailExpenseApproval(employee.get().getEmployeeLogin().getEmail(), employee.get().getFirstName(), message);


        return new ResponseEntity<>(empExp,HttpStatus.OK);
    }

    @PostMapping("expenses-manager-reject/{empid}/{remarks}")
    public ResponseEntity<EmployeeExpenses> getExpenseReject(@PathVariable long empid,@PathVariable String remarks)
    {
        //Optional<Employee> firstManager=employeeService.getEmployeeByManagerId(firstmanager);
        EmployeeExpenses empExp=employeeExpenseService.getByEmpId(empid);
        Optional<Employee> employee=employeeService.getEmployee(empid);

        empExp.setExpenseStatus(ExpensesEnum.Rejected);
        empExp.setReMarks(remarks);
        employeeExpenseService.createExit(empExp);


        String message = "Your Expenses" + ExpensesEnum.Rejected;
        sendEmailExpenseApproval(employee.get().getEmployeeLogin().getEmail(), employee.get().getFirstName(), message);


        return new ResponseEntity<>(empExp,HttpStatus.OK);
    }

    @GetMapping("expenses-finance-view")
    public ResponseEntity<List<EmployeeExpenses>> getApprovedEmployee()
    {
        List<EmployeeExpenses> employeeExpenses=employeeExpenseService.getAll();
        List<EmployeeExpenses> employeeExpensesList=new ArrayList<>();
        for (EmployeeExpenses e:employeeExpenses)
        {
            if(e.getExpenseStatus().equals(ExpensesEnum.Approved))
            {
                employeeExpensesList.add(e);

            }
        }

        return new ResponseEntity<>(employeeExpensesList,HttpStatus.OK);

    }

    @GetMapping("expenses-finance-all-view")
    public ResponseEntity<List<EmployeeExpenses>> getAAllExpenses()
    {
        List<EmployeeExpenses> employeeExpenses=employeeExpenseService.getAll();
        return new ResponseEntity<>(employeeExpenses,HttpStatus.OK);

    }
    @GetMapping("total-amount/{empid}")
    public ResponseEntity<Long> getTotal(@PathVariable long empid)
    {
        //List<EmployeeExpenses> employeeExpenses=employeeExpenseService.getAll();
        List<Expenses> expensesList=expensesService.getAll();
        long sum=0;
        //Optional<Employee> employee=employeeService.getEmployee(empid);

        for (Expenses e:expensesList)
        {
            if(e.getEmployeeId()==empid)
            {
                sum=sum+e.getAmount();

            }
        }

        return new ResponseEntity<>(sum,HttpStatus.OK);

    }

    @PostMapping("expenses-finance-process/{empid}/{remarks}")
    public ResponseEntity<EmployeeExpenses> getExpenseProcessed(@PathVariable long empid,@PathVariable String remarks)
    {
        //Optional<Employee> firstManager=employeeService.getEmployeeByManagerId(firstmanager);
        EmployeeExpenses empExp=employeeExpenseService.getByEmpId(empid);
        Optional<Employee> employee=employeeService.getEmployee(empid);

        empExp.setExpenseStatus(ExpensesEnum.Processed);
        empExp.setFinRemarks(remarks);
        changeCompleted(empid);
        employeeExpenseService.createExit(empExp);

        String message = "Your Expenses" + ExpensesEnum.Approved;
        sendEmailExpenseApproval(employee.get().getEmployeeLogin().getEmail(), employee.get().getFirstName(), message);


        return new ResponseEntity<>(empExp,HttpStatus.OK);
    }

    public void changeCompleted(long empid){
        EmployeeExpenses empExp=employeeExpenseService.getByEmpId(empid);
        Optional<Employee> employee=employeeService.getEmployee(empid);
        empExp.setExpenseStatus(ExpensesEnum.Completed);
        employeeExpenseService.createExit(empExp);
    }

    public void sendEmailExpenseApproval(String email, String firstName, String content)
    {
        Date todayDate = new Date();
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = "ExpenseStatus" + " on " + todayDate;
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, null);
        mailService.sendEmail(mailnew, "****");
    }

    @GetMapping("expenses-finance-approved-reports")
    public ResponseEntity<List<EmployeeExpenses>> getApprovedReports()
    {
        List<EmployeeExpenses> employeeExpenses=employeeExpenseService.getAll();
        List<EmployeeExpenses> employeeExpensesList=new ArrayList<>();
        for (EmployeeExpenses e:employeeExpenses)
        {
            if(e.getExpenseStatus().equals(ExpensesEnum.Approved))
            {
                employeeExpensesList.add(e);

            }
        }
        return new ResponseEntity<>(employeeExpensesList,HttpStatus.OK);

    }

    @GetMapping("expenses-excel-approved-reports")
    private void createCustomersDetails( HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        List<EmployeeExpenses> employeeExpenses=employeeExpenseService.getAll();
        List<EmployeeExpenses> employeeExpensesList=new ArrayList<>();
        for (EmployeeExpenses e:employeeExpenses)
        {
            if(e.getExpenseStatus().equals(ExpensesEnum.Approved))
            {
                employeeExpensesList.add(e);

            }
        }
        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= Reports.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try
        {
            writeToSheet(workbook, employeeExpensesList, response, 0);
            workbook.write();
            workbook.close();
        }
        catch (Exception e)
        {
            throw new ServletException("Exception in excel download", e);
        }
        finally
        {
            if (out != null)
                out.close();
        }
    }

    private WritableWorkbook writeToSheet(WritableWorkbook workbook, List<EmployeeExpenses> employeeExpenses, HttpServletResponse response, int index) throws IOException, WriteException
    {
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
        s.addCell(new Label(0, 0, "EmployeeId", headerFormat));
        s.addCell(new Label(1, 0, "Employee Name", headerFormat));
        s.addCell(new Label(2, 0, "Designation", headerFormat));
        s.addCell(new Label(3, 0, "Total Amount", headerFormat));
        s.addCell(new Label(4, 0, "Expense Status", headerFormat));


        int rownum = 1;
        for (EmployeeExpenses usr : employeeExpenses) {
            s.addCell(new Label(0, rownum, "" + usr.getEmployeeId()));
            s.addCell(new Label(1, rownum, "" + usr.getEmployeeName()));
            s.addCell(new Label(2, rownum, "" + usr.getDesignation()));
            s.addCell(new Label(3, rownum, "" + usr.getTotalAmount()));
            s.addCell(new Label(4, rownum, "" + usr.getExpenseStatus()));
            rownum++;
        }
        return workbook;
    }
}
