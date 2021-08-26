package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class IqcInspector {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String po;
    private String grn;
    private String supplierName;
    @Temporal(TemporalType.DATE)
    private Date receivedDate;
    @Temporal(TemporalType.DATE)
    private Date inspectionDate;
    private String inspectorName;
    @Temporal(TemporalType.DATE)
    private Date markedOn;
    private String itemType;
    private String purchaseType;
    private String customerName;
    private long jobOrderNumber;
    private String productName;
    @Enumerated(EnumType.STRING)
    private MaterialsEnum  itemStatus;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<IqcInspectorItems> iqcInspectorItemsList;

}
