package com.dfq.coeffi.CanteenManagement.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CounterDto {

    private long counterId;
    private String counterName;
    private long counterNo;
    private List<EmployeeTypeDto> employeeTypeDtos;
}
