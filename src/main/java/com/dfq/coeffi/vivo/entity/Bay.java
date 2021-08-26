package com.dfq.coeffi.vivo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Bay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String bayNumber;

    private String bayVehicleType;

    @CreationTimestamp
    private Date createdOn;

    private boolean status;

    private long numberOfSlots;

    @OneToMany
    private List<Slot> slots;






}
