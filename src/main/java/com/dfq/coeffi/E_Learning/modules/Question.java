package com.dfq.coeffi.E_Learning.modules;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString

public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Product product;

    private String questionTitle;

    private String questionDescription;

    private String questionOption1;

    private String questionOption2;

    private String questionOption3;

    private String questionOption4;

    private String rightOption;

    private String selectedOption;

    private String qTitle;

    private String qdescription;

    private long level;

    private boolean valid;

    private boolean status;

    @CreationTimestamp
    private Date createdOn;


}

