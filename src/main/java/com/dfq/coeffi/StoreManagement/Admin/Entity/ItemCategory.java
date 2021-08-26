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
public class ItemCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String itemCode;
    private String itemCategory;
    private String itemDescription;
    private long itemPrice;
    private long quantity;
}
