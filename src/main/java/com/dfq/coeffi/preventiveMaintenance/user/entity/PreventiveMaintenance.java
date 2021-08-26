package com.dfq.coeffi.preventiveMaintenance.user.entity;


import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.preventiveMaintenance.durationType.DurationType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class PreventiveMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private Date dateOfService;

    @OneToOne
    private SopType sopType;

    @OneToOne
    private SopCategory sopCategory;

    @OneToMany(fetch = FetchType.EAGER)
    private List<SopStepsAssigned> sopStepsAssigned;

    private Boolean allCheckListCompleted;

    @OneToOne
    private DurationType durationType;

    private long durationValue;

    private long submitedYear;

    @Temporal(TemporalType.DATE)
    private Date submitedOn;

    private String submitedBy;

    private Boolean isAutoSubmit;

    private String pdfUrl;
    private String pdfName;
    private boolean submitedBySupervisor;

    @CreationTimestamp
    private Date assignedOn;

    @OneToMany
    private List<Employee> employee;
}