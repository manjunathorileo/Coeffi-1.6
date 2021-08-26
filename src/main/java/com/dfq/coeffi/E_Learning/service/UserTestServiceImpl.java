package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.UserTest;
import com.dfq.coeffi.E_Learning.repository.UserTestRepository;
import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("UserTest")
public class UserTestServiceImpl implements UserTestService {
    @Autowired
    private UserTestRepository userTestRepository;

    @Override
    public UserTest saveUpdateUserTest(UserTest userTest) {
        return userTestRepository.save(userTest);
    }

    @Override
    public List<UserTest> getUserTest(boolean status) {
        return userTestRepository.findByStatus(status);
    }

    @Override
    public UserTest getUserTestById(long id) {
        return (userTestRepository.findOne(id));
    }

    @Override
    public void deActivateById(long id) {
        userTestRepository.deleteById(id);

    }
    @Override
    public List<UserTest> getUserTestByUserId(Employee employee) {
        return userTestRepository.findByEmployee(employee);
    }
}
