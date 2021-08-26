package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class AdminConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String factory;
    private String productionLine;
    private String batch;
}
