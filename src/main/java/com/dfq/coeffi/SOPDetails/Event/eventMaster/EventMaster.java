package com.dfq.coeffi.SOPDetails.Event.eventMaster;

import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EventMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String eventName;
    private String description;
    @OneToOne
    private Department department;
    @Enumerated(EnumType.STRING)
    private AttachType attachType;
    @OneToOne
    private Employee attachedTo;
    @Temporal(TemporalType.DATE)
    private Date createdOn;
    @OneToOne
    private Employee createdBy;
    private Boolean status;
}
