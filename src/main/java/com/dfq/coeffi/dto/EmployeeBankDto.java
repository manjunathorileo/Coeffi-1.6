package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.hr.employee.AccountType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Setter
@Getter
public class EmployeeBankDto {

    private long employeeId;
    private String bankName;
    private String branch;
    private String accountNumber;
    private String accountName;
    private String ifscCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private AccountType accountType;
}