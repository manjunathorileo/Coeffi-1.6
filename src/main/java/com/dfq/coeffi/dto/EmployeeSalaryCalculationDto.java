package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmployeeSalaryCalculationDto {
    private BigDecimal employeeContribution;
    private BigDecimal gross;
    private BigDecimal epfWages;
    private BigDecimal epsWages;
    private BigDecimal edliWages;
    private BigDecimal eeShareRemitted;
    private BigDecimal epsContributionRemitted;
    private BigDecimal edliInsuranceFund;
    private BigDecimal erShareRemitted;
    private  BigDecimal basic;
}
