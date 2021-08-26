package com.dfq.coeffi.employeePermanentContract.controllers;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractDepartmentDto {
    private String department;
    private long checkInCount;
    private long checkOutCount;
    private long employeeCount;
    private long totalInCount;
    private long totalOutCount;
}
