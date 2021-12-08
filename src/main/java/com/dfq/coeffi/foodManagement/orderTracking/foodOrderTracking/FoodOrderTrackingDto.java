package com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodOrderTrackingDto {

    private String foodType;
    private long employeeCount;
    private long visitorCount;
    private long contractCount;

    private List<FoodOrderTrackEmployeeDto> foodOrderTrackEmployeeDtos;


}
