package com.dfq.coeffi.employeePermanentContract.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class SitePc {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String siteName;
    private long productionCapacity;
    private String code;
    private String remarks;
    private long noOfEmp;
    private boolean available;
}
