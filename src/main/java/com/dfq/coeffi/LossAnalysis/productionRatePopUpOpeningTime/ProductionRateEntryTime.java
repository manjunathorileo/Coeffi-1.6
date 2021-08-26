package com.dfq.coeffi.LossAnalysis.productionRatePopUpOpeningTime;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class ProductionRateEntryTime {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long hourValue;

    @Temporal(TemporalType.DATE)
    private Date createdOn;

    @OneToOne
    private Employee createdBy;

    private Boolean status;
}
