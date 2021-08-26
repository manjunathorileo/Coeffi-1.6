package com.dfq.coeffi.foodManagement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodTrackerDto {
    private String foodType;
    private long employeeCount;
    private long visitorCount;
    private long contractCount;

}
