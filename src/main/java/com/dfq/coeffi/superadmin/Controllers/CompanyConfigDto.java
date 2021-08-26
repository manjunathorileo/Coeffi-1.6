package com.dfq.coeffi.superadmin.Controllers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyConfigDto {
    private long id;
    private boolean attendanceBonus;
    private double bonusAmount;
    private boolean lateEntryLoss;
    private boolean earlyOutLoss;
    private boolean salaryRegenerate;

}
