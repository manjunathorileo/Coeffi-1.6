package com.dfq.coeffi.E_Learning.modules;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class TestMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String testName = "11";
    private String testTitle;
    private long testLevel;
    private long numberOfQuestions;
    private long timingInMinutes;
    private double passCriteriaMarks;
    @OneToOne
    private Product product;
}

