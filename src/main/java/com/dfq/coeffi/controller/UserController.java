package com.dfq.coeffi.controller;

import com.dfq.coeffi.dto.UserDto;
import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.user.LoggedUser;
import com.dfq.coeffi.entity.user.Role;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.repository.UserRepository;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.communication.EmailLogService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

/**
 * @author H Kapil Kumar
 */

@RestController
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    EmailLogService emaillogservice;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    MailService mailService;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public Principal user(Principal user) {
        return user;
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> loadUsers() {

        return userService.getUsers();
    }

    @GetMapping("/user-roles")
    public ResponseEntity<List<Role>> getRole() {
        List<Role> roles = userService.getRoles();
        if (CollectionUtils.isEmpty(roles)) {
            throw new EntityNotFoundException("roles");
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/user-roles-admin")
    public ResponseEntity<List<Role>> getRoleForAdminConfig() {
        List<Role> roles = userService.getRoles();
        List<Role> roleList = new ArrayList<>();
        for (Role role : roles) {
            if (role.getName().equalsIgnoreCase("ADMIN") ||
                    role.getName().equalsIgnoreCase("IT_ADMIN") ||
                    role.getName().equalsIgnoreCase("LD_ADMIN") ||
                    role.getName().equalsIgnoreCase("SOP_ADMIN") ||
                    role.getName().equalsIgnoreCase("PRODUCTION_ADMIN")) {
                roleList.add(role);
            }
        }
        if (CollectionUtils.isEmpty(roles)) {
            throw new EntityNotFoundException("roles");
        }
        return new ResponseEntity<>(roleList, HttpStatus.OK);
    }

    @GetMapping("/user-roles-super-admin")
    public ResponseEntity<List<Role>> getRoleForITAdminConfig() {
        List<Role> roles = userService.getRoles();
        List<Role> roleList = new ArrayList<>();
        for (Role role : roles) {
            if (role.getName().equalsIgnoreCase("IT_ADMIN")) {
                roleList.add(role);
            }
        }
        if (CollectionUtils.isEmpty(roles)) {
            throw new EntityNotFoundException("roles");
        }
        return new ResponseEntity<>(roleList, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        User user = userService.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException("User not found for id " + id);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PostMapping("/user/logged-user")
    public ResponseEntity<LoggedUser> getLoggedUser(@Valid @RequestBody LoggedUser loggedUser) {
        LoggedUser persistedLoggedUser = userService.saveLoggedUser(loggedUser);
        return new ResponseEntity<>(persistedLoggedUser, HttpStatus.CREATED);
    }

    @PostMapping("/user/send-password-to-email")
    public ResponseEntity<User> sendPasswordToEmail(@Valid @RequestBody UserDto userDto) {
        Optional<User> user = userService.sendPasswordToEmail(userDto.getEmail());
        if (!user.isPresent()) {
            throw new EntityNotFoundException("User not found for id " + user.get().getEmail());
        }
        sendEmailAndSaveLog(user.get().getPassword(), user.get().getEmail(), user.get().getFirstName());
        return new ResponseEntity(user.get(), HttpStatus.OK);
    }

    public void sendEmailAndSaveLog(String password, String email, String firstName) {
        Map<String, Object> model = new HashMap<String, Object>();
//        model.put("name", "Hi " + firstName + ",\n You recently requested to reset your password for your Edupod account.\n Your password is :" + password);
        EmailConfig emailConfig = new EmailConfig();
        String content = ("Hi" + firstName + ",You recently requested to reset your password for your account.\n Your password is :" + password);
        Mail newMail = emailConfig.setMailCredentials(email, "Forget Password for Co-effi Application", content, model);
        mailService.sendEmail(newMail, "SampleMail.txt");
        EmailLog emails = new EmailLog();
        emails.setRecipient(email);
        emails.setStudentName(firstName);
        emails.setSubject("Forget Password");
        emails.setMessage("Password Sent to Email");
        emails.setDate(DateUtil.getTodayDate());
        emaillogservice.saveEmailLog(emails);
    }

    @GetMapping("/logged-user")
    public Principal getUserLoggedUser(Principal user) {
        return user;
    }

    @PostMapping("employee/reset-password")
    public ResponseEntity<User> resetPassword(@Valid @RequestBody UserDto userDto) {
        Optional<Employee> employeeObj = employeeService.getEmployeeByIdAndPassword(userDto.getEmployeeId(), userDto.getPassword());
        if (!(employeeObj.isPresent())) {
            throw new EntityNotFoundException("The old password you have entered is incorrect");
        }
        Employee employee = employeeObj.get();
        User user = employee.getEmployeeLogin();
        user.setPassword(userDto.getNewPassword());
        employee.setEmployeeLogin(user);
        employeeService.save(employee);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/user-admin")
    public ResponseEntity<List<User>> getUsersForAdminConfig() {
        List<User> users = userService.getUsers();
        List<User> userList = new ArrayList<>();
        for (User user : users) {
            List<Role> roles = user.getRoles();
            List<Role> roleList = new ArrayList<>();
            for (Role role : roles) {
                if (role.getName().equalsIgnoreCase("ADMIN") ||
                        role.getName().equalsIgnoreCase("IT_ADMIN") ||
                        role.getName().equalsIgnoreCase("LD_ADMIN") ||
                        role.getName().equalsIgnoreCase("SOP_ADMIN") ||
                        role.getName().equalsIgnoreCase("PRODUCTION_ADMIN")) {
                    roleList.add(role);
                    if (user.isActive()) {
                        userList.add(user);
                    }
                }
            }

        }
        if (CollectionUtils.isEmpty(users)) {
            throw new EntityNotFoundException("roles");
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @DeleteMapping("user/{id}")
    public void deleteUser(@PathVariable long id) {
        User user = userService.getUser(id);
        user.setActive(false);
        userService.saveUser(user);
    }


    @GetMapping("user-itadmin")
    public ResponseEntity<User> getItAdminDetails() {
        List<User> users = userService.getUsers();
        List<User> userList = new ArrayList<>();
        for (User user : users) {
            List<Role> roles = user.getRoles();
            List<Role> roleList = new ArrayList<>();
            for (Role role : roles) {
                if (role.getName().equalsIgnoreCase("IT_ADMIN")) {
                    roleList.add(role);
                    userList.add(user);
                }
            }

        }
        return new ResponseEntity(userList, HttpStatus.OK);
    }


}