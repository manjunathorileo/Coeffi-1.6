package com.dfq.coeffi.LossAnalysis.productionTrack;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.master.shift.Shift;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class ProductionTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private ProductionLineMaster productionLineMaster;

    @Temporal(TemporalType.DATE)
    private Date createdOn;

    @OneToOne
    private Shift shift;
    private long totalWorkingHrs;

    private long defaultProductionRate;
    private long defaultTotalItemProductionNo;
    private float defaultProductionPercent;
    private long currentProductionRate;
    private long currentTotalItemProductionNo;
    private float currentProductionPercent;

    private Boolean status;

    private long goodProduction;
    private long nonQualityProduction;

}
