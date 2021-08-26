package com.dfq.coeffi.StoreManagement.Entity;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.master.shift.Shift;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class ProductionItemQualityCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Temporal(TemporalType.DATE)
    private Date createdOn;
    private long serialNumber;
    @Enumerated(EnumType.STRING)
    private MaterialStatus lqcStatus;
    private String lqcBy;
    @Temporal(TemporalType.DATE)
    private Date lqcOn;
    private String lqcRejectedBy;
    private Date lqcRejectedOn;
    @Enumerated(EnumType.STRING)
    private MaterialStatus fqcStatus;
    private String fqcBy;
    @Temporal(TemporalType.DATE)
    private Date fqcOn;
    private String fqcRejectedBy;
    private Date fqcRejectedOn;
    private String productionEmployee;

    @OneToOne(cascade = {CascadeType.ALL})
    private ProductionLineMasters productionLineMasters;

    @OneToOne(cascade = {CascadeType.ALL})
    private BatchMaster batchMasters;

    @OneToOne(cascade = {CascadeType.ALL})
    private Factorys factorys ;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<ProductionMaster> productionMasters;

    @OneToOne(cascade = {CascadeType.ALL})
    private Shift shifts;



}
