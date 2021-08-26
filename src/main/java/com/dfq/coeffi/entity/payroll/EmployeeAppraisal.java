package com.dfq.coeffi.entity.payroll;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id; 
import javax.persistence.OneToOne;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class EmployeeAppraisal implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1708827987272025831L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(length = 45)
	private int kpiPoints;
	
	@Column
	private boolean status;

	@Column(length = 45)
	private String newDesignation;
	
	@Column(length = 45)
	private BigDecimal salaryIncrement;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Employee employee;
	
	@OneToOne(cascade = CascadeType.ALL)
	private AcademicYear academicYear;
}
