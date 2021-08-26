package com.dfq.coeffi.LossAnalysis.lossAnalysisEntryTimeRule;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class LossAnalysisEntryTimeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long minValue;

    @Temporal(TemporalType.DATE)
    private Date createdOn;

    @OneToOne
    private Employee createdBy;

    private Boolean status;
}
