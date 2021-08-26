package com.dfq.coeffi.Oqc;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class MaterialInward {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String po;
    private String grn;
    private String department;
    private long quantity;
    private String supplierName;
    private String remarks;
    @Temporal(TemporalType.DATE)
    private Date receivedOn;
}
