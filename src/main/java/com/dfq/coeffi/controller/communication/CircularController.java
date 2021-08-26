package com.dfq.coeffi.controller.communication;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.CircularDto;
import com.dfq.coeffi.entity.communication.Circular;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.service.communication.CircularService;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.communication.EmailLogService;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import com.dfq.coeffi.service.communication.SMSLogService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.notification.messages.TextMessageService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.sql.rowset.serial.SerialBlob;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;

@RestController
public class CircularController extends BaseController {

    @Autowired
    CircularService circularService;

    @Autowired
    SMSLogService smsLogService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmailLogService emaillogservice;

    @Autowired
    MailService mailService;

    @Autowired
    TextMessageService textMessageService;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    private JavaMailSender javaMailSender;

    List<Long> employeesId;


    // Update circular By Id
    @PutMapping("circular/{id}")
    public ResponseEntity<Circular> updatecircular(@PathVariable long id, @Valid @RequestBody Circular circular) {
        Circular persistedObject = circularService.getCircularById(id);
        if (persistedObject == null) {
            throw new EntityNotFoundException(Circular.class.getSimpleName());
        }
        // persistedObject = circularService.saveCircular(circular);
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    //Delete by id
    @DeleteMapping("circular/{id}")
    public ResponseEntity<Circular> deletecircular(@PathVariable Long id) {
        Circular persistenceObject = circularService.getCircularById(id);
        if (persistenceObject == null) {
            throw new EntityNotFoundException(Circular.class.getName());
        }
        circularService.deleteCircular(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("circular/{id}")
    public ResponseEntity<Circular> getcircularById(@PathVariable Long id) {
        Circular persistenceObject = circularService.getCircularById(id);
        if (persistenceObject == null) {
            throw new EntityNotFoundException(Circular.class.getName());
        }
        return new ResponseEntity<Circular>(persistenceObject, HttpStatus.OK);
    }

    /**
     * To create Circular and Send emails to student or Teachers
     *
     * @param circularDto
     * @return
     * @throws MessagingException
     */
    @PostMapping("circular/email")
    public ResponseEntity<Mail> sendMail(@RequestBody CircularDto circularDto, Principal principal) {
        employeesId = circularDto.empIds;
        ArrayList employees = new ArrayList();
        for (Long employeeIds : employeesId) {
            Optional<Employee> employeeId = employeeService.getEmployee(employeeIds);
            if (!employeeId.isPresent()) {
                throw new EntityNotFoundException("employee");
            }
            Employee employee = employeeId.get();
            employees.add(employee);
        }
        Circular circular = saveCircular(circularDto, employees);
        String userId = principal.getName();
        Optional<Employee> loggedEmployeeObj = employeeService.getEmployeeByLogin(Long.valueOf(userId));
        return new ResponseEntity(circular, HttpStatus.CREATED);
    }

    public Circular saveCircular(CircularDto circularDto, List<Employee> employeeList) {
        ArrayList listEmployeeIds = new ArrayList();
        Circular circular = new Circular();
        circular.setMessage(circularDto.message);
        circular.setTitle(circularDto.title);
        circular.setDate(circularDto.circularDate);
        circular.setEmail(circularDto.isEmail);
        circular.setSMS(circularDto.isSMS);
        circular.setEmployeeForCirculars(employeeList);
        circular.setApproveStatus(false);
        circular.setDocument(fileStorageService.getDocument(circularDto.documentId));
        circularService.saveCircular(circular);
        return circular;
    }

    public void sendEmailAndSaveLog(Circular circularMessageDto, String email, String firstName) throws IOException, SQLException {
        ArrayList<EmailLog> emaillogs = new ArrayList<EmailLog>();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", circularMessageDto.getMessage());
        //    circulardto.filename
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = circularMessageDto.getTitle() + " on " + circularMessageDto.getCreatedOn();
        String content = "Dear All, Please find the below circular." + circularMessageDto.getTitle();
       /* Message message = new MimeMessage();
         messageBodyPart = new MimeBodyPart();
            String filename = "D:/test.PDF";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);
            // Send the complete message parts
            message.setContent(multipart);
            // Send message
            Transport.send(message);
*/
        //FileSystemResource file = new FileSystemResource("C:\\log.txt");
        /**
         *          MimeMessage message = javaMailSender.createMimeMessage();
         *          MimeMessageHelper helper = new MimeMessageHelper(message, true);
         *         // helper.setTo("xyz@gmail.com");
         *          //helper.setText("<html><body><h1>hello Welcome!</h1><body></html>", true);
         *          FileSystemResource file  = new FileSystemResource(new File(path));
         *          helper.addAttachment("testfile", file);
         *          helper.addAttachment("D:/test.PDF", new ClassPathResource("test.jpeg"));
         *
         *          javaMailSender.send(message);
         *    }
         */
        //  Document pdf=circularMessageDto.getDocument();
        // MimeMessage message = javaMailSender.createMimeMessage();
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, model);

        byte[] byteData = circularMessageDto.getDocument().getData();
        File file = convertUsingJavaNIO(byteData);
        Path path = Paths.get(file.getAbsolutePath());
        Files.write(path, byteData);

        mailService.sendEmailWithAttachment(mailnew, "SampleMail.txt", file);
        //Saving Email log
        EmailLog emails = new EmailLog();
        emails.setRecipient(email);
        emails.setStudentName(firstName);
        emails.setSubject(circularMessageDto.getTitle());
        emails.setMessage(circularMessageDto.getMessage());
        emails.setDate(circularMessageDto.getDate());

        emaillogs.add(emails);
        emaillogservice.saveAllEmailLogs(emaillogs);
    }

    public static File convertUsingJavaNIO(byte[] fileBytes) {
        File f = new File("Cirular_Document.pdf");
        try {
            Files.write(f.toPath(), fileBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return f;
    }


    public void sendEmailAndSaveLogShare(Circular circular, String email, String firstName) {
        ArrayList<EmailLog> emaillogs = new ArrayList<EmailLog>();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", circular.getMessage());
        EmailConfig emailConfignew = new EmailConfig();
        String content = circular.getMessage();
        Mail mailnew = emailConfignew.setMailCredentials(email, circular.getTitle(), content, model);
        mailService.sendEmail(mailnew, "SampleMail.txt");
        EmailLog emails = new EmailLog();
        emails.setRecipient(email);
        emails.setStudentName(firstName);
        emails.setSubject(circular.getTitle());
        emails.setMessage(circular.getMessage());
        emails.setDate(circular.getDate());
        emaillogs.add(emails);
        emaillogservice.saveAllEmailLogs(emaillogs);
    }

    @PostMapping("circular/share/{id}")
    public ResponseEntity<Mail> shareCircular(@PathVariable long id, @RequestBody CircularDto circularMessageDto) throws MessagingException {
        Circular persistenceObject = circularService.getCircularById(id);
        ArrayList bl = new ArrayList();
        for (Long employeeIds : circularMessageDto.empIds) {
            Optional<Employee> employeeId = employeeService.getEmployee(employeeIds);
            Employee employee = employeeId.get();
            if (!employeeId.isPresent()) {
                throw new EntityNotFoundException("employee");
            }
            if (persistenceObject.isEmail()) {
                sendEmailAndSaveLogShare(persistenceObject, employee.getEmployeeLogin().getEmail(), employee.getFirstName());
                persistenceObject.setEmail(persistenceObject.isEmail());
            } else if (persistenceObject.isSMS()) {
                textMessageService.sendTextMessage(employee.getPhoneNumber(), circularMessageDto.message);
                persistenceObject.setSMS(persistenceObject.isSMS());
            }
            bl.add(employeeIds);
            //persistenceObject.setEmployeeId(bl);
            circularService.saveCircular(persistenceObject);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("circular/circularList")
    public ResponseEntity<List<Circular>> listAllCircular() {
        List<Circular> persistenceObject = circularService.listAllCirculars();
        List<Circular> circularList = new ArrayList<>();
        for (Circular circular : persistenceObject) {
            List<Employee> employeesOfCircular = circular.getEmployeeForCirculars();
            List<Employee> employeeList = new ArrayList<>();
            for (Employee employee : employeesOfCircular) {
                Employee employee1 = new Employee();
                employee1.setId(employee.getId());
                employee1.setFirstName(employee.getFirstName());
                employee1.setLastName(employee.getLastName());
                employee1.setEmployeeCode(employee.getEmployeeCode());
                employeeList.add(employee1);
            }
            circular.setEmployeeForCirculars(employeeList);
            circularList.add(circular);
        }
        if (persistenceObject == null) {
            throw new EntityNotFoundException(Circular.class.getName());
        }
        return new ResponseEntity<>(circularList, HttpStatus.OK);
    }

    @GetMapping("circular/by-employee/{employeeId}")
    public ResponseEntity<List<Circular>> listOfCircularsByEmployeeId(@PathVariable("employeeId") long employeeId) {
        List<Circular> circulars = circularService.listAllCirculars();

        ArrayList circulars1 = new ArrayList();
        for (Circular circular : circulars) {
            List<Employee> employees = circular.getEmployeeForCirculars();
            for (Employee employee : employees) {
                if (employee.getId() == employeeId) {
                    Circular circular1=new Circular();
                    circular1.setId(circular.getId());
                    circular1.setTitle(circular.getTitle());
                    circular1.setCreatedOn(circular.getCreatedOn());
                    circular1.setMessage(circular.getMessage());
                    circular1.setDate(circular.getDate());
                    circulars1.add(circular1);
                }
            }
        }
        if (CollectionUtils.isEmpty(circulars)) {
            throw new EntityNotFoundException("circulars not found for employee : " + employeeId);
        }
        return new ResponseEntity<>(circulars1, HttpStatus.OK);
    }

    MultipartFile multipartFile;

    @PostMapping("circular/upload-document")
    public ResponseEntity<Document> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        Document document;
        if (file.isEmpty()) {
            throw new FileNotFoundException();
        } else {
            multipartFile = file;
            document = fileStorageService.storePolicyDocument(file);
        }
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @GetMapping("circular/un-approval-list")
    public ResponseEntity<List<Circular>> listOfUnapprovalCirculars() {
        List<Circular> circulars = circularService.getAllCircularsByStatus(false);
        List<Circular> circularList = new ArrayList<>();
        for (Circular circular : circulars) {
            List<Employee> employeesOfCircular = circular.getEmployeeForCirculars();
            List<Employee> employeeList = new ArrayList<>();
            for (Employee employee : employeesOfCircular) {
                Employee employee1 = new Employee();
                employee1.setId(employee.getId());
                employee1.setFirstName(employee.getFirstName());
                employee1.setLastName(employee.getLastName());
                employee1.setEmployeeCode(employee.getEmployeeCode());
                employeeList.add(employee1);
            }
            circular.setEmployeeForCirculars(employeeList);
            circularList.add(circular);
        }
        if (CollectionUtils.isEmpty(circulars)) {
            throw new EntityNotFoundException("circulars not found ");
        }
        return new ResponseEntity<>(circularList, HttpStatus.OK);
    }

    @GetMapping("circular/approval/{id}")
    public ResponseEntity<Circular> getcircular(@PathVariable Long id) throws IOException, SQLException {
        Circular circulars = circularService.getCircularById(id);
        List<Employee> employees = circulars.getEmployeeForCirculars();
        for (Employee employee : employees) {
            if (circulars.isEmail()) {
                sendEmailAndSaveLog(circulars, employee.getEmployeeLogin().getEmail(), employee.getFirstName());
            } else if (circulars.isSMS()) {
                textMessageService.sendTextMessage(employee.getPhoneNumber(), circulars.getMessage());
            }
        }
        circulars.setApproveStatus(true);
        circulars.setStatus("Approved");
        circularService.saveCircular(circulars);
        return new ResponseEntity<>(circulars, HttpStatus.OK);
    }

    @GetMapping("circular/approved-by-employee/{employeeId}")
    public ResponseEntity<List<Circular>> listOfApprovedCircularsByEmployeeId(@PathVariable("employeeId") long employeeId) {
        ArrayList circulars1 = new ArrayList();
        List<Circular> circulars = circularService.getAllCircularByApprovedStatus(true);
        for (Circular circular : circulars) {
            List<Employee> employees = circular.getEmployeeForCirculars();
            List<Employee> employeeList=new ArrayList<>();
            for (Employee employee : employees) {
                if (employee.getId() == employeeId) {
                    Circular circular1=new Circular();
                    circular1.setId(circular.getId());
                    circular1.setTitle(circular.getTitle());
                    circular1.setCreatedOn(circular.getCreatedOn());
                    circular1.setMessage(circular.getMessage());
                    circular1.setDate(circular.getDate());
                    circulars1.add(circular1);
                }
            }
        }
        if (CollectionUtils.isEmpty(circulars)) {
            throw new EntityNotFoundException("circulars not found for employee : " + employeeId);
        }
        return new ResponseEntity<>(circulars1, HttpStatus.OK);
    }


}
