package com.dfq.coeffi.CanteenManagement.Entity;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EmployeeRecharge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @CreationTimestamp
    private Date rechargeDate;
    private String transactionRefNumber;
    private String paymentRefNumber;
    private String employeeCategory;
    private long empId;
    private long rechargeAmount;
    private long totalRechargeAmount;
    private long minimumBalanceAmount;
    private long actualBalance;

    @OneToOne
    private Employee employee;
    private Boolean isNew;
}