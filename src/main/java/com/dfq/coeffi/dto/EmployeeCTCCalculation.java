package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmployeeCTCCalculation {

    private BigDecimal totalEarningsPerMonth;

    private BigDecimal totalEarningsPerYear;

    private BigDecimal bonus;

    private BigDecimal employeeEsicContribution;

}
