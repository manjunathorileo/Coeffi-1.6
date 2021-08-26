package com.dfq.coeffi.preventiveMaintenance.admin.entity;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity

public class PreventiveMaintenanceMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private SopType sopType;

    @OneToOne
    private SopCategory sopCategory;

    @OneToMany(fetch = FetchType.EAGER)
    private List<SopStepsMaster> sopStepsMasters;

    private String checklistFileName;
    private String checkListFileUrl;
    private long checkListSlNo;

    @OneToOne
    private Employee uploadedBy;

    @Temporal(TemporalType.DATE)
    private Date createdOn;
    @Temporal(TemporalType.DATE)
    private Date lastUpdatedOn;

    @OneToOne
    private DurationType durationType;

    private long durationValue;

    @OneToMany
    private List<Employee> employee;
}
