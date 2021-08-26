package com.dfq.coeffi.visitor.Entities;

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
public class VisitorCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String remarks;

    private boolean allowedOrDenied;

    @CreationTimestamp
    private Date markedOn;

    private boolean paymentApplicable;

    private String regNumber;


}
