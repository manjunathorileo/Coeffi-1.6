package com.dfq.coeffi.LossAnalysis;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.LossSubCategory.LossSubCategory;
import com.dfq.coeffi.LossAnalysis.machine.MachineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.master.shift.Shift;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class LossAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private Shift shift;

    @OneToOne
    private ProductionLineMaster productionLine;

    @OneToOne
    private MachineMaster machine;
    @OneToOne
    private LossCategory lossCategory;

    @OneToOne
    private LossSubCategory lossSubCategory;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "IST")
    @Temporal(TemporalType.TIME)
    private Date fromTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "IST")
    @Temporal(TemporalType.TIME)
    private Date toTime;

    private long lossTime;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    private Boolean status;

    private String productName;
}