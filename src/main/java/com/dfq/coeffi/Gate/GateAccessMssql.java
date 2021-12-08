package com.dfq.coeffi.Gate;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class GateAccessMssql {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String empId;
    private String controllerCode;
    private String doorId;

}
