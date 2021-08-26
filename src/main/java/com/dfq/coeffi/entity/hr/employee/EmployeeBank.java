package com.dfq.coeffi.entity.hr.employee;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name="employee_bank")
public class EmployeeBank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=100)
    private String bankName;

    @Column(length=25)
    private String branch;

    @Column(length=100)
    private String accountNumber;

    @Column(length=100)
    private String accountName;

    @Column(length=25)
    private String ifscCode;

    @Column(length=20)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
}
