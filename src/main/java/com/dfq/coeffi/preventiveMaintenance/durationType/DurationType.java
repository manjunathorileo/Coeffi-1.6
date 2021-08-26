package com.dfq.coeffi.preventiveMaintenance.durationType;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity

public class DurationType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String durationType;

    private long lowLimit;
    private long highLimit;

    private Boolean status;
}
