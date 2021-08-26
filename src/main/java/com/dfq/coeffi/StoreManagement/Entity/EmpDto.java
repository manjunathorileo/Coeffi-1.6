package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class EmpDto {
    private String requestNumber;
    private String department;
    private String costCenter;
    private String purpose;
    private Date date;
    private String requestedBy;
    private String approvedBy;
    private Date approvedDate;
    private long employeeId;
    private long managerId;
    private long otp;
    private String otpValidation;
    private String employeeName;
    List<Materials> materialsList;
}
