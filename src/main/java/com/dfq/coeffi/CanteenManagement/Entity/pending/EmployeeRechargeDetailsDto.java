package com.dfq.coeffi.CanteenManagement.Entity.pending;

import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EmployeeRechargeDetailsDto {
    private String employeeCode;
    private EmployeeType employeeCategory;
    private String employeeName;
    private String department;
    private Date joiningDate;
    private Date leavingDate;
    private String transactionRefNumber;
    private String paymentRefNumber;
    private long totalRechargeAmount;
    private long minimumBalanceAmount;
    private long actualBalance;
}
