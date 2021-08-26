package com.dfq.coeffi.CanteenManagement.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeTypeDto {

    private String empType;
    private List<EmployeeDetailsDto> employeeDetailsDtos;
    private long totalEmployee;
}
