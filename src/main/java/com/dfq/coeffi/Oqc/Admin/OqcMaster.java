package com.dfq.coeffi.Oqc.Admin;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class OqcMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long productId;
    private String product;
    private long productionLineId;
    private String productionLine;
    private long supervisorId;
    private String supervisor;
    private long noOfParameter;

    @OneToMany
    private List<CheckListMaster> checkListMasters;



}
