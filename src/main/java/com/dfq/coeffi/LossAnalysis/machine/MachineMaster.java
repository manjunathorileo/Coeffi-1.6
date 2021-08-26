package com.dfq.coeffi.LossAnalysis.machine;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class MachineMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String machineName;
    private String description;
    private long defaultMachineProductionRate;
    @OneToOne
    private ProductionLineMaster productionLine;
    @OneToMany
    private List<Employee> assignedTo;
    @Temporal(TemporalType.DATE)
    private Date createdOn;
    @OneToOne
    private Employee createdBy;
    private Boolean status;
}