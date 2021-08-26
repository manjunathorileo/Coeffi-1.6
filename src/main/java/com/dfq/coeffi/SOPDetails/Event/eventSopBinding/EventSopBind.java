package com.dfq.coeffi.SOPDetails.Event.eventSopBinding;

import com.dfq.coeffi.SOPDetails.Event.eventMaster.EventMaster;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EventSopBind {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private SopType sopType;
    @OneToOne
    private SopCategory sopCategory;
    @OneToOne
    private EventMaster eventMaster;
    @Temporal(TemporalType.DATE)
    private Date createdOn;
    @OneToOne
    private Employee createdBy;
    private Boolean status;
}
