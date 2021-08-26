package com.dfq.coeffi.entity.payroll;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name ="loanPlanner")
public class LoanPlanner implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8193753386130584180L;


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column
	private boolean status;
	
	@Column(length = 45)
	private BigDecimal emiPay;
	
	@Column(name = "emi_date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date emiDate;
	
	 @JsonBackReference
	 @ManyToOne
	 @JoinColumn(name = "employeeLoan_id")
	 private EmployeeLoan employeeLoan;
}
