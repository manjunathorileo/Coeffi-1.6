package com.dfq.coeffi.SOPDetails.Event.eventCompletion;

import com.dfq.coeffi.SOPDetails.Event.eventMaster.EventMaster;
import com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned.EventWiseSopStepsAssigned;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class EventCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private SopType sopType;
    @OneToOne
    private SopCategory sopCategory;

    @OneToOne
    private EventMaster eventMaster;

    @OneToMany
    private List<EventWiseSopStepsAssigned> eventWiseSopStepsAssigneds;

    @Temporal(TemporalType.DATE)
    private Date submitedOn;
    private String remark;

    @OneToOne
    private Employee submitedBy;

    @CreationTimestamp
    private Date triggeredOn;

    private Boolean status;
}
