package com.dfq.coeffi.employeePermanentContract.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EmployeePass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date StartDate;
    private Date endDate;
    private long empId;
    @OneToOne
    private EmpPermanentContract empPermanentContract;
    private boolean valid;
}
