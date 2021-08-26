package com.dfq.coeffi.entity.payroll;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.hibernate.annotations.CreationTimestamp;


import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Entity
@Table(name="employeeSalaryProcess")

public class EmployeeSalaryProcess implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5974069134701310617L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@Column(name = "salary_processing_date",updatable = false)
	private Date salaryProcessingDate;

	@Column(length = 45)
	private BigDecimal basicSalary;

	@Column(length = 45)
	private BigDecimal currentBasic;

	@Column(length = 45)
	private BigDecimal rateOfPay;

	@Column(length = 45)
	private BigDecimal variableDearnessAllowance;

	@Column(length = 45)
	private BigDecimal conveyanceAllowance;

	@Column(length = 45)
	private BigDecimal houseRentAllowance;

	@Column(length = 45)
	private BigDecimal educationalAllowance;

	@Column(length = 45)
	private BigDecimal mealsAllowance;

	@Column(length = 45)
	private BigDecimal washingAllowance;

	@Column(length = 45)
	private BigDecimal otherAllowance;

	@Column(length = 45)
	private BigDecimal miscellaneousAllowance;

	@Column(length = 45)
	private BigDecimal mobileAllowance;

	@Column(length = 45)
	private BigDecimal rla;

	@Column(length = 45)
	private BigDecimal tpt;

	@Column(length = 45)
	private BigDecimal uniformAllowance;

	@Column(length = 45)
	private BigDecimal shoeAllowance;

	@Column(length = 45)
	private BigDecimal epfContribution;

	@Column(length = 45)
	private BigDecimal bonus;

	@Column(length = 45)
	private BigDecimal gratuity;

	@Column(length = 45)
	private BigDecimal medicalPolicy;

	@Column(length = 45,name = "medical_reimbursement")
	private BigDecimal medical;

	@Column(length = 45)
	private BigDecimal leaveTravelAllowance;

	@Column(length = 45)
	private BigDecimal royalty;

	@Column(length = 45)
	private BigDecimal employeeEsicContribution;//assigned to UI

	@Column(length = 45)
	private BigDecimal employerContributionESIC;

	@Column(length = 45)
	private BigDecimal totalEarning;

	@Column(length = 45)
	private BigDecimal totalDeduction;

	@Column(length = 45)
	private BigDecimal professionalTax;

	@Column
	private Long refId;

	@Enumerated(EnumType.STRING)
	private SalaryApprovalStatus salaryApprovalStatus;

	@Column
	private String salaryMonth;

	@Column
	private String salaryYear;

	@Column
	private String rejectionNote;

	@Column(length=25)
	private BigDecimal netPaid;

	private BigDecimal lossOfPayDeduction;

	private BigDecimal epfWages;

	private BigDecimal epsWages;

	private BigDecimal edliWages;

	private BigDecimal eeShareRemitted;

	private BigDecimal epsContributionRemitted;

	private BigDecimal erShareRemitted;

	private BigDecimal edliInsuranceFund;

	private BigDecimal totalAdvanceDeduction ;

	private BigDecimal totalIncomeTaxDeduction ;

	private BigDecimal totalMealDeduction ;

	private BigDecimal totalOthers;

	private BigDecimal totalOtherDeduction;

	private BigDecimal lossOfPay;

	private BigDecimal otPay;

	private double otHrs;

	private BigDecimal others;

	@Column(length = 45)
	private BigDecimal grossSalary;

	private BigDecimal lateEntryFine;

	private double noOfPresent;

	private double unpaidLeaves;

	private long paidLeaves;

	private long casualLeaves;

	private long sundays;

	private long noOfHolidays;

	private long paymentDays;

	private long workingDays;

	private double noOfLeaves;

	private long lateEntry;

	private BigDecimal openingMedicalLeave;
	private BigDecimal openingCasualLeave;
	private BigDecimal openingEarnLeave;
	private BigDecimal availMeadicalLeave;
	private BigDecimal availCasualLeave;
	private BigDecimal availEarnLeave;
	private BigDecimal closingMedicalLeave;
	private BigDecimal closingCasualLeave;
	private BigDecimal closingEarnLeave;
	private boolean epf;

	@OneToOne
	private Employee employee;

	private BigDecimal lateEntryLoss;
	private double totalLateEntryHrs;

	private BigDecimal earlyOutLoss;
	private double totalEarlyOutHrs;
}