package com.dfq.coeffi.Oqc.User;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.Oqc.Admin.CheckListMaster;
import com.dfq.coeffi.Oqc.User.CheckListAssigned;
import com.dfq.coeffi.StoreManagement.Entity.ProductName;
import com.dfq.coeffi.StoreManagement.Entity.ProductionLineMasters;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class OqcUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private ProductName productName;
    @OneToOne
    private ProductionLineMasters productionLineMaster;
    @ManyToMany
    private List<CheckListAssigned> checkListAssigneds;
    private long noOfParameter;

    private long loggedById;

    private Boolean isSubmitted;
    @Temporal(TemporalType.DATE)
    private Date submittedOn;
    private long submittedById;
}
