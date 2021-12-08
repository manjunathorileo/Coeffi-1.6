package com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FoodOrderTrackingServiceImpl implements FoodOrderTrackingService {

    @Autowired
    private FoodOrderTrackingRepository foodOrderTrackingRepository;

    @Override
    public FoodOrderTracking save(FoodOrderTracking foodOrderTracking) {
        FoodOrderTracking foodOrderTrackingObj = foodOrderTrackingRepository.save(foodOrderTracking);
        return foodOrderTrackingObj;
    }

    @Override
    public List<FoodOrderTracking> getAll() {
        List<FoodOrderTracking> foodOrderTrackings = foodOrderTrackingRepository.findAll();
        return foodOrderTrackings;
    }

    @Override
    public FoodOrderTracking getById(long id) {
        FoodOrderTracking foodOrderTracking = foodOrderTrackingRepository.findOne(id);
        return foodOrderTracking;
    }

    @Override
    public List<FoodOrderTracking> getByfoodType(String foodType) {
        List<FoodOrderTracking> foodOrderTrackings = foodOrderTrackingRepository.findByFoodType(foodType);
        return foodOrderTrackings;
    }

    @Override
    public List<FoodOrderTracking> getByEmployeeTypeByEmployeeCode(String employeeType,String employeeCode) {
        List<FoodOrderTracking> foodOrderTrackings = foodOrderTrackingRepository.findByEmployeeTypeByEmplCode(employeeType, employeeCode);
        return foodOrderTrackings;
    }

    @Override
    public List<FoodOrderTracking> getByEmployeeTypeByEmployeeCodeAndMarkedOn(String employeeType, String employeeCode,Date markedOn) {
        List<FoodOrderTracking> foodOrderTrackings = foodOrderTrackingRepository.findByEmployeeTypeAndEmployeeCodeAndMarkedOn(employeeType, employeeCode,markedOn);
        return foodOrderTrackings;
    }
}
