package com.dfq.coeffi.vivo.entity;

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
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String slotNumber;

    @CreationTimestamp
    private Date createdOn;

    private boolean status;

    private boolean available;

}
