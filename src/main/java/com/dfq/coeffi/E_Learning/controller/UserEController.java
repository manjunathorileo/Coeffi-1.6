package com.dfq.coeffi.E_Learning.controller;

import com.dfq.coeffi.E_Learning.modules.UserDto;
import com.dfq.coeffi.E_Learning.modules.UserImport;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class UserEController extends BaseController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User persistedObject = userService.saveUser(user);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @GetMapping(value = "users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> getUsers = userService.getUsers();
        return new ResponseEntity<>(getUsers, HttpStatus.OK);
    }

    @GetMapping(value = "/user-e/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        User userOptional = userService.getUser(id);
        User user = userOptional;
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PostMapping("/user-import/u")
    public ResponseEntity<List<UserDto>> userImport(@RequestParam("file") MultipartFile file) {
        List<UserDto> dto = UserImport.userImport(file);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


}
