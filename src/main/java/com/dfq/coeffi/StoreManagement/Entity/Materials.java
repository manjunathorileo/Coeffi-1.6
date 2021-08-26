package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class Materials {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long itemNumber;
    private String itemCategory;
    private String itemName;
    private String description;
    private long quantity;
    private String remarks;
    private long itemPrice;
    private long minQuantity;
    private long maxQuantity;
    private long reOrderQuantity;
    private long employeeId;
    @Temporal(TemporalType.DATE)
    private Date markedOn;

    @Enumerated(EnumType.STRING)
    private MaterialsEnum  materialsStatus;
}
