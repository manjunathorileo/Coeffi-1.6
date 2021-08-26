package com.dfq.coeffi.FeedBackManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class FeedBackParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String parameterName;
    private String description;

}
