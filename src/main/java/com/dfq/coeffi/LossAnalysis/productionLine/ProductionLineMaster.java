package com.dfq.coeffi.LossAnalysis.productionLine;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class ProductionLineMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String lineName;
    private String description;
    private long defaultProductionRate;
    @Temporal(TemporalType.DATE)
    private Date createdOn;
    @OneToOne
    private Employee createdBy;
    private Boolean status;
    @OneToMany
    private List<Employee> assignedTo;
}
