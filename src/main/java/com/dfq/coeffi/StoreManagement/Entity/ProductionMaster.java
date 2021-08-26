package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ProductionMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String productionName;
    private double serialNumber;
    private String clientName;
    private Date createdOn;
    @Enumerated(EnumType.STRING)
    private MaterialStatus productionLqcItemStatus;
    @Enumerated(EnumType.STRING)
    private MaterialStatus productionFqcItemStatus;

}
