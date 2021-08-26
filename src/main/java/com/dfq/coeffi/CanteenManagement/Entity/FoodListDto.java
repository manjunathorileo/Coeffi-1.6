package com.dfq.coeffi.CanteenManagement.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodListDto {

    private long id;
    private String foodName;
    private String foodCode;
    private String unit;
    private Boolean isSelected;
}
