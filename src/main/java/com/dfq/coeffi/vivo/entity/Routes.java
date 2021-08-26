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
public class Routes
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String routes;
    private String remarks;
    private boolean status;
}
