package com.dfq.coeffi.entity.payroll;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "employee_ctc_data")
public class EmployeeCTCData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "salary_created_date",updatable = false)
    private Date salaryCreatedDate;

    private boolean status;

    @Column(length = 45)
    private BigDecimal basicSalary;

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

    @Column(length = 45)
    private BigDecimal medicalReimbursement;

    @Column(length = 45)
    private BigDecimal leaveTravelAllowance;

    @Column(length = 45)
    private BigDecimal royalty;

    @Column(length = 45)
    private BigDecimal employeeEsicContribution;

    @Column(length = 45)
    private BigDecimal employerContributionESIC;

    @Column(length = 45)
    private BigDecimal employeeContributionESIC;

    @Column(length = 45)
    private BigDecimal monthlyCtc;

    @Column(length = 45)
    private BigDecimal yearlyCtc;

    private long empId;

    private Date affectFrom;

    private Date announcedOn;

}
