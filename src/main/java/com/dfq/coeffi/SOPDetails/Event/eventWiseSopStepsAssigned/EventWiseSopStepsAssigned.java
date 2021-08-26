package com.dfq.coeffi.SOPDetails.Event.eventWiseSopStepsAssigned;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssignedDocumentUpload;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class EventWiseSopStepsAssigned {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String checkPart;
    private String checkPoint;
    private String description;
    private long standardValue;
    private Boolean checkPointStatus;
    private String remark;

    @Temporal(TemporalType.DATE)
    private Date submitedOn;

    @OneToOne
    private Employee submitedBy;

    @OneToOne
    private SopStepsAssignedDocumentUpload sopStepsAssignedDocumentUpload;

    private Boolean status;
}