package com.dfq.coeffi.foodManagement.orderTracking.foodOrderReport;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class FoodOrderReportDto {

    private Date fromDate;
    private Date toDate;
    private String fromDateStr;
    private String toDateStr;
    private String employeeType;
    private String companyName;
    private String dept;

    private String employeeCode;
    private String employeeName;
    private String location;

    private long totalBfEstimate;
    private long totalLunchEstimate;
    private long totalDinnerEstimate;
    private long totalSnacksEstimate;
    private long totalMidnightSnackEstimate;

    private long totalBfUsage;
    private long totalLunchUsage;
    private long totalDinnerUsage;
    private long totalSnacksUsage;
    private long totalMidnightSnacksUsage;
}