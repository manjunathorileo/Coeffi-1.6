package com.dfq.coeffi.LossAnalysis.currentProductionRate;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class CurrentProductionRate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private ProductionLineMaster productionLineMaster;

    private long producationRate;

    @Temporal(TemporalType.DATE)
    private Date createdOn;
    @OneToOne
    private Employee createdBy;
    private Boolean status;
}
