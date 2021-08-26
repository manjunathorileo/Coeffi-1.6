package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class IqcInspectorItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String itemName;
    private String itemType;
    private String itemCategory;
    private double orderedCount;
    private double receivedCount;
    private double defectedCount;
    private double availableStock;
    private String remarks;
    private Date serviceDuration;
    private Date amcDueDate;
    private Date receivedDate;
    private Date inspectionDate;
    private String inspectorName;
    private String supplierName;
    @Enumerated(EnumType.STRING)
    private MaterialStatus  status;
}
