package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Items {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String itemCategory;
    private long itemNumber;
    private String itemName;
    private long quantity;
    private String description;
    private long itemPrice;
    private boolean status;
    private long minQuantity;
    private long maxQuantity;
    private long reOrderQuantity;

}
