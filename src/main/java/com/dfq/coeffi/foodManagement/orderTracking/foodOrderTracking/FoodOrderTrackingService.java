package com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking;

import java.util.Date;
import java.util.List;

public interface FoodOrderTrackingService {

    FoodOrderTracking save(FoodOrderTracking foodOrderTracking);
    List<FoodOrderTracking> getAll();
    FoodOrderTracking getById(long id);
    List<FoodOrderTracking> getByfoodType(String foodType);
    List<FoodOrderTracking> getByEmployeeTypeByEmployeeCode(String employeeType, String employeeCode);
    List<FoodOrderTracking> getByEmployeeTypeByEmployeeCodeAndMarkedOn(String employeeType, String employeeCode,Date markedOn);
}
