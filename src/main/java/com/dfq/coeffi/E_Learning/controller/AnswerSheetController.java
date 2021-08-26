package com.dfq.coeffi.E_Learning.controller;

import com.dfq.coeffi.E_Learning.modules.AnswerSheet;
import com.dfq.coeffi.E_Learning.modules.UserTest;
import com.dfq.coeffi.E_Learning.service.AnswerSheetService;
import com.dfq.coeffi.E_Learning.service.UserTestService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class AnswerSheetController extends BaseController {

    @Autowired
    AnswerSheetService answerSheetService;

    @Autowired
    UserTestService userTestService;
    @Autowired
    EmployeeService employeeService;

    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;

    /**
     * @return create, save and update answer sheets
     */
    @PostMapping(value = "/answer-sheet/{userid}/{level}/{docId}")
    public ResponseEntity<List<AnswerSheet>> createAnswerSheet(@RequestBody List<AnswerSheet> answerSheets, @PathVariable("userid") long userid,
                                                               @PathVariable long level,@PathVariable long docId) throws Exception {
        long score = 0;
        for (AnswerSheet answerSheet : answerSheets) {
            if (answerSheet.getRightOption().equals(answerSheet.getSelectedOption())) {
                answerSheet.setValid(true);
                score++;
            } else {
                answerSheet.setValid(false);
            }
            answerSheet.setScore(score);
        }
        List<AnswerSheet> answerSheets1 = answerSheetService.saveAnswerSheet(answerSheets);
        UserTest userTest = new UserTest();
        userTest.setAnswerSheets(answerSheets1);
        Optional<Employee> employee = employeeService.getEmployee(userid);
        userTest.setEmployee(employee.get());
        userTest.setLevel(level);
        userTest.setTotalQuestions(answerSheets.size());
        userTest.setScore(score);
        double s = userTest.getScore();
        double t = userTest.getTotalQuestions();
        double marks = (s / t) * 100;
        System.out.println("Marks: " + marks + "Score: " + score + "TotalQuestions: " + userTest.getTotalQuestions());
        if (marks >= 35) {
            userTest.setFinalResult("PASS");
        } else {
            userTest.setFinalResult("FAIL");
        }
        userTest.setStatus(true);
        userTest.setProductId(answerSheets.get(0).getProduct().getId());
        userTest.setDocId(docId);
        userTestService.saveUpdateUserTest(userTest);
        if(employee.get().getFirstApprovalManager()!=null) {
            Optional<Employee> manager = employeeService.getEmployee(employee.get().getFirstApprovalManager().getId());
            Employee mgr = manager.get();
//            sendEmailResignatioStatus(mgr.getEmployeeLogin().getEmail(), mgr.getFirstName(), "Employee " + employee.get().getFirstName() + "has taken test");
        }
        return new ResponseEntity<>(answerSheets1, HttpStatus.CREATED);
    }


    public void sendEmailResignatioStatus(String email, String firstName, String content) {
        Date todayDate = new Date();
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = "Test_Submitted" + " on " + todayDate;
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, null);
        mailService.sendEmail(mailnew, "****");
    }

    /**
     * @return List<AnswerSheet>
     * return active list of answer sheets
     */
    @GetMapping(value = "/answer-sheet")
    public ResponseEntity<List<AnswerSheet>> getAnswerSheets() {
        List<AnswerSheet> answerSheets = answerSheetService.getAnswerSheet(true);
        return new ResponseEntity<>(answerSheets, HttpStatus.OK);
    }

    /**
     * @return AnswerSheet
     * return Answer sheet of particular id
     */
    @GetMapping(value = "/answer-sheet/{id}")
    public ResponseEntity<AnswerSheet> getAnswerSheet(@PathVariable long id) {
        Optional<AnswerSheet> answerSheetOptional = answerSheetService.getAnswerSheetById(id);
        if (!answerSheetOptional.isPresent()) {
            throw new EntityNotFoundException("No answer sheet found for id : " + id);
        }
        AnswerSheet answerSheet = answerSheetOptional.get();
        return new ResponseEntity<>(answerSheet, HttpStatus.OK);
    }

    /**
     * @return delete answer sheet of particular id
     */

    @DeleteMapping(value = "/answer-sheet/{id}")
    public ResponseEntity<AnswerSheet> deleteAnswerSheet(@PathVariable long id) {
        answerSheetService.deActivateById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/aaanswer-sheet/{userid}/{pid}")
    public ResponseEntity<ArrayList<AnswerSheet>> getTestTaken(@PathVariable long userid, @PathVariable("pid") long pid) {
        ArrayList<AnswerSheet> answerSheets = answerSheetService.getAnswerSheetByUserId(userid);
        List<AnswerSheet> answerSheets1 = new ArrayList<>();
        if (answerSheets.isEmpty()) {
            throw new EntityNotFoundException("Questions are not found");
        }
        for (AnswerSheet answerSheet : answerSheets) {
            if (pid == answerSheet.getProduct().getId()) {
                answerSheets1.add(answerSheet);
            }
        }
        return new ResponseEntity(answerSheets1, HttpStatus.OK);
    }


    @GetMapping("answer-sheet/view/{empid}/{pid}")
    public ResponseEntity<List<AnswerSheet>> getAnswerSheetOfUserForSelectedProduct(@PathVariable("empid") long empid, @PathVariable("pid") long pid) {
        List<AnswerSheet> answerSheets = answerSheetService.getAnswerSheetByUserId(empid);
        List<AnswerSheet> answerSheetList = new ArrayList<>();
        for (AnswerSheet answerSheet : answerSheets) {
            if (pid == answerSheet.getProduct().getId()) {
                answerSheetList.add(answerSheet);
            }
        }
        return new ResponseEntity<>(answerSheetList, HttpStatus.OK);
    }

}