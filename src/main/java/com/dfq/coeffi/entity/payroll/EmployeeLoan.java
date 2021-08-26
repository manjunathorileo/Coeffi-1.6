package com.dfq.coeffi.entity.payroll;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EmployeeLoan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2192582489435985416L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(length = 45)
	private BigDecimal amount;
	
	@Column
	private boolean status;
		
	@Column(length = 45)
	private int loanInterest;
	
	@Column(length = 45)
	private int loanDuration;
	
	@Column(length = 45)
	private BigDecimal emiPay;
	
	@Column(length = 45)
	private BigDecimal optionalExtraPay;
	
	@Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "loan_issue_date",updatable = false)
	private Date loanIssueDate;
	
	@Column(name = "due_date")
	//@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dueDate;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Employee employee;
	
	@OneToOne(cascade = CascadeType.ALL)
	private AcademicYear academicYear;
	
	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "employeeLoan")
	private List<LoanPlanner> loanPlanners;

}
