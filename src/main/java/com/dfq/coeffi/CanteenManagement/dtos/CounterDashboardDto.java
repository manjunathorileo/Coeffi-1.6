package com.dfq.coeffi.CanteenManagement.dtos;

import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.advanceFoodManagementExtra.EmployeeTypeAdvanced;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CounterDashboardDto {

    private FoodTimeMasterAdv foodTimeMasterAdv;
    private CounterDetailsAdv counterDetailsAdv;
    private String employeeName;
    private long balance;
    private long minBalance;
    private String balanceStatus;
    private String foodStatus;
    private List<EmployeeTypeAdvanced> employeeTypes;
    private List<EmployeeTypeDto> employeeTypeDtos;
    private long grandTotal;

}
