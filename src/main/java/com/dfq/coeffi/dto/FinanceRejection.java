package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.payroll.SalaryApprovalStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FinanceRejection {
    public long id;
    public String rejectionNote;
}
