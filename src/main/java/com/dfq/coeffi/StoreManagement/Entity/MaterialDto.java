package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
public class MaterialDto {
    private String requestNumber;
    private String department;
    private String costCenter;
    private String purpose;
    private Date date;
    private long employeeId;
    private String employeeName;
    private String otp;
    private String approvalEnum;
    private String purchaseType;
    private String customerName;
    private long jobOrderNumber;
    private String productName;
    private String materialType;
    private String reasonCode;
    private boolean requestIndication;
    private boolean qualityIndication;
    private List<Materials> materials;
}
