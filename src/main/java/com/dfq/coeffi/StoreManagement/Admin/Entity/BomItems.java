package com.dfq.coeffi.StoreManagement.Admin.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class BomItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String itemName;
    private long itemCode;
    private long itemPrice;
    private long totalAmount;
    private String description;
    private long quantity;



}
