package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.payroll.SalaryApprovalStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class EmployeeSalaryDto {
	
	public List<Long> empIds;
	public List<Long> ids;
	public Long employeeId;
	public Long id;

	public String inputMonth;
	public String inputYear;
	public BigDecimal netSalary;

	public String employeeCode;
	public String employeeName;
	public String department;
	public String designation;
	public BigDecimal professionalTax;
	public String monthName;
	public String year;

	public SalaryApprovalStatus approvalStatus;
	public String rejectionNote;
	public boolean approve;

	public List<Long> approveIds;
	public List<FinanceRejection> financeRejections;

	public int monthNumber;
	long esicNumber;

	private double noOfPresent;
	private BigDecimal grossSalary;
	private BigDecimal employeeEsicContribution;
	private BigDecimal employerEsicContribution;
	private BigDecimal totalEsicContribution;
}
