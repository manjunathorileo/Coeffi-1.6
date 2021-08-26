package com.dfq.coeffi.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class EmployeeCTCDataDto {

    private BigDecimal basicSalary;
    private BigDecimal variableDearnessAllowance;
    private BigDecimal conveyanceAllowance;
    private BigDecimal houseRentAllowance;
    private BigDecimal educationalAllowance;
    private BigDecimal mealsAllowance;
    private BigDecimal washingAllowance;
    private BigDecimal otherAllowance;
    private BigDecimal miscellaneousAllowance;
    private BigDecimal mobileAllowance;
    private BigDecimal rla;
    private BigDecimal tpt;
    private BigDecimal uniformAllowance;
    private BigDecimal shoeAllowance;
    private BigDecimal epfContribution;
    private BigDecimal bonus;
    private BigDecimal gratuity;
    private BigDecimal medicalPolicy;
    private BigDecimal medicalReimbursement;
    private BigDecimal leaveTravelAllowance;
    private BigDecimal royalty;
    private BigDecimal employeeEsicContribution;
    private BigDecimal employerContributionESIC;
    private long employeeId;
    private long employeeCTCId;
    private BigDecimal professionalTax;
    private BigDecimal monthlyCtc;
    private BigDecimal yearlyCtc;
    private Date affectedFrom;

}
