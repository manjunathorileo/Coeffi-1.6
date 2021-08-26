package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.UserTest;
import com.dfq.coeffi.entity.hr.employee.Employee;

import java.util.List;

public interface UserTestService {
    UserTest saveUpdateUserTest(UserTest userTest);

    List<UserTest> getUserTest(boolean status);

    UserTest getUserTestById(long id);

    void deActivateById(long id);

    List<UserTest> getUserTestByUserId(Employee employee);
}
