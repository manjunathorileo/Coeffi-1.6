package com.dfq.coeffi.evacuationApi;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


@Entity
@Getter
@Setter
public class EvacuationTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long permanentCount;
    private long contractCount;
    private long permanentContractCount;
    private long visitorCount;
    private long tempCount;
    private long vehicleCount;
    private Date recordedOn;

}
