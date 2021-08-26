package com.dfq.coeffi.CanteenManagement.Entity.pending;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class EmployeeRechargeDto {

    private Date rechargeDate;
    private long totalRecharge;
    private long minimumBalance;
    private long actualBalance;
}
