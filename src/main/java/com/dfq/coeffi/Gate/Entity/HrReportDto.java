package com.dfq.coeffi.Gate.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HrReportDto {
    private long employeeId;
    private String employeeName;
    private String department;
    private long age;
    private String Gender;
    private long mCount;
    private long fcount;
}
