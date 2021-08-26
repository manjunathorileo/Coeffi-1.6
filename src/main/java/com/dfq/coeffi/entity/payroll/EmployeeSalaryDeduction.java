package com.dfq.coeffi.entity.payroll;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
public class EmployeeSalaryDeduction {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "createdOn",updatable = false)
    private Date createdOn;

    @Temporal(TemporalType.DATE)
    @Column(name = "recordedOn")
    private Date recordedOn;

    @Column(length = 45)
    private BigDecimal advance;

    @Column(length = 45)
    private BigDecimal tdsIncometax;

    @Column(length = 45)
    private BigDecimal meal;

    @Column(length = 45)
    private BigDecimal other;

    @Column(length = 45)
    private BigDecimal otherDeduction;

    private String remarks;

    @OneToOne
    private Employee employee;

    private  boolean status;

}
