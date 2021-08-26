package com.dfq.coeffi.foodManagement;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class CatereDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String address;
    private String location;
    private Date validFrom;
    private Date validTo;
    @CreationTimestamp
    private Date createdOn;
    private Date status;
    private String workOrderNumber;


}
