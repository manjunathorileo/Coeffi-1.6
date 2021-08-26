package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class BatchMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long batchNumber;
    private Date startDate;
    private Date createdOn;

//    @OneToMany(cascade = {CascadeType.ALL})
//    private List<ProductionMaster> productionMaster;
}
