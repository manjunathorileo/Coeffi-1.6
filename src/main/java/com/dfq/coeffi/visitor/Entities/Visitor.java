package com.dfq.coeffi.visitor.Entities;

import com.dfq.coeffi.entity.hr.Department;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@Entity

public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String mobileNumber;

    private Date dateOfVisit;

    private long timeSlot;

    private String checkInTime;

    private String checkOutTime;

    private String itemCarried;

    private String personToVisit;

    private long docId;

    private Boolean status;

    private String extraTime;

    private double paymentAmt;

    private String registeredBy;

    private String kioskReg;

    private Boolean multiVisit;

    private long captureId;

    private String vehicleDetails;

    @Temporal(TemporalType.DATE)
    private Date loggedOn;

    private Date inTime;

    private Date outTime;

    //-----------new Req-----------

    private long emailAttachmentId;

    private String entryGateNumber;

    private String exitGateNumber;

    private long entryBodyTemperature;

    private long exitBodyTemperature;

    private boolean maskWearing;

    private String firstName;

    private String email;

    private String visitType;

    private String departmentName;

    @OneToOne
    VisitorPass visitorPass;

    private long showw;

    private long imgId;

    @UpdateTimestamp
    private Date recordedTime;

    private double totalHours;

}

