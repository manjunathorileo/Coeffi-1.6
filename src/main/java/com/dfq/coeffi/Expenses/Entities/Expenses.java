package com.dfq.coeffi.Expenses.Entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String purpose;
    private long amount;
    private String placeOfVisit;
    private Date fromDate;
    private Date toDate;
    private String recepitId;
    private String recepitName;
    @CreationTimestamp
    private Date submittedDate;
    private Date paymentDate;
    private long employeeId;


}
