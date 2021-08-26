package com.dfq.coeffi.FeedBackManagement.Entity;

import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateDto {
    private CounterDetailsAdv counterDetailsAdv;
    private FoodTimeMasterAdv foodTimeMasterAdv;
    private boolean validate;
    private long empId;
    private Employee employee;
}
