package com.dfq.coeffi.entity.payroll.payrollmaster;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PayHead implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8262106895178038351L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(length = 45)
	private String description;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 45)
	private TransactionType transactionType;
	
	@Column(length = 45)
	private int unit;

	@Column(length = 45)
	private String unitType;
	
	@Column(length = 45)
	private String calculatedOn;

	@Enumerated(EnumType.STRING)
	@Column(length = 45)
	private PercentageType percentageType;
}
