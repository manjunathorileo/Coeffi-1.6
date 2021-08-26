package com.dfq.coeffi.employeePerformanceManagement.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class EmployeePerformanceManagementDto {
    private long employeeId;
    private String goalStatus;
    private String appraisalStatus;
    private String finalRemarks;
    private long finalRatings;
    List<EmployeePerformanceManagement> employeePerformanceManagements;
}
