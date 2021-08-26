package com.dfq.coeffi.DenialApps.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class DenialApps {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String appCategory;
    private String denialAppName;
    private String description;
    private Date createdOn;
    private boolean status;
}
