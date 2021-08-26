package com.dfq.coeffi.CanteenManagement.dtos;

import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.advanceFoodManagementExtra.EmployeeTypeAdvanced;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CatererDashboardDto {

    private FoodTimeMasterAdv foodTimeMasterAdv;
    private List<EmployeeTypeAdvanced> employeeTypes;
    private List<CounterDto> counterDtos;
    private long grandTotal;


}
