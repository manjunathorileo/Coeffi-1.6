package com.dfq.coeffi.advanceFoodManagementExtra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EmployeeCanteenDetailsServiceImpl implements EmployeeCanteenDetailsService {
    @Autowired
    EmployeeCanteenDetailsRepository employeeCanteenDetailsRepository;


    @Override
    public void saveEmployeeCanteenDetails(EmployeeCanteenDetails employeeCanteenDetails) {
        employeeCanteenDetailsRepository.save(employeeCanteenDetails);
    }

    @Override
    public EmployeeCanteenDetails getEmployeeCanteenDetailsById(long id) {
        return employeeCanteenDetailsRepository.findOne(id);
    }

    @Override
    public EmployeeCanteenDetails getEmployeeCanteenDetailsByEmployeeIdAndFoodTypeAndDate(long employeeId, String foodType, Date date) {
        return employeeCanteenDetailsRepository.findByEmpIdAndFoodTypeNameAndMarkedOn(employeeId, foodType, date);
    }

    @Override
    public List<EmployeeCanteenDetails> getEmployeeCanteenDetailsByCounterId(long counterId) {
        return employeeCanteenDetailsRepository.findByCounterId(counterId);
    }

    @Override
    public List<EmployeeCanteenDetails> getEmployeeCanteenDetailsByCounterByFoodType(long counterId, long foodTypeId) {
        return employeeCanteenDetailsRepository.findByCounterIdAndFoodTypeId(counterId, foodTypeId);
    }

    @Override
    public EmployeeCanteenDetails findByMarkedOnAndEmployeeCodeAndFoodTypeName(Date markedOn, String employeeCode, String foodTypeName) {
        return employeeCanteenDetailsRepository.findByMarkedOnAndEmployeeCodeAndFoodTypeName(markedOn, employeeCode, foodTypeName);
    }

    @Override
    public List<EmployeeCanteenDetails> findByMarkedOnAndEmployeeCode(Date mk, String employeeCode) {
        return employeeCanteenDetailsRepository.findByMarkedOnAndEmployeeCode(mk,employeeCode);
    }
}
