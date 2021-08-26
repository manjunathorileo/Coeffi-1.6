package com.dfq.coeffi.StoreManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class ProductionLineMasters {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String productionArea;
    private String productionLineName;
    private String description;
    private Date createdOn;
    private String productionEmployee;

//    @OneToMany(cascade = {CascadeType.ALL})
//    private List<BatchMaster> batchMaster;


}
