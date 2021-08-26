package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class EmployeeRequestDto {

    private String employeeName;
    private String department;
    private Date date;
}
