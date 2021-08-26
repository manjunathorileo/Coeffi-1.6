package com.dfq.coeffi.vivo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class TimeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long limitInHours;
    private boolean status;
}
