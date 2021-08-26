package com.dfq.coeffi.vivo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class PaymentRules {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private boolean applicable;
    private long rate;
    private Date markedOn;
    private String currency;
}
