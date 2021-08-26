package com.dfq.coeffi.entity.payroll;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.hibernate.annotations.CreationTimestamp;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EmployeeSalary implements Serializable
{
	
	private static final long serialVersionUID = 2389438032073938690L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "salary_Generation_Date",updatable = false)
	private Date salaryGenerationDate;
	
	@Column(length = 45)
	private BigDecimal basicSalary;
	
	@Column(length = 45)
	private BigDecimal houseRentAllowance;
	
	@Column(length = 45)
	private BigDecimal medicalAllowance;
	
	@Column(length = 45)
	private BigDecimal travelAllowance;
	
	@Column(length = 45)
	private BigDecimal specialAllowance;
	
	@Column(length = 45)
	private BigDecimal employeesPF;
	
	@Column(length = 45)
	private BigDecimal employeersPF;

	@Column(length = 45)
	private BigDecimal professionalTax;	
	
	@Column(length = 45)
	private BigDecimal netSalaryPayble;

	@Column(length = 45)
	private BigDecimal totalEarning;
	
	@Column(length = 45)
	private BigDecimal totalDeduction;
	
	@Column
	private boolean approve;
	
	@Column(length = 45)
	private BigDecimal employeeEmi;
	
	@Column(length = 45)
	private BigDecimal tds;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Employee employee;
}
