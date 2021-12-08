package com.dfq.coeffi.preventiveMaintenance.admin.entity;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
public class SopStepsMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String checkPart;
    private String checkPoint;
    private String description;
    private long standardValue;
    private Boolean status;
    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;
}