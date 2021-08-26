package com.dfq.coeffi.Oqc.User;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class CheckListAssigned {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String parameter;
    private double parameterValue;
    private String description;
    private String grading;
    private String gradingValue;
    private String remarks;
    private Boolean isApplicable;

}
