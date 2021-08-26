package com.dfq.coeffi.entity.payroll.payrollmaster;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
public class PayHeadContract implements Serializable  {

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
