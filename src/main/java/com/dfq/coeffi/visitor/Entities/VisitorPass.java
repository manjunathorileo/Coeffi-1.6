package com.dfq.coeffi.visitor.Entities;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class VisitorPass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    private String email;

    private Date dateOfBirth;

    private String gender;

    private String visitType;

    private String visitorType;

    private String visitorOrganization;

    private String visitorLocation;

    private Date dateOfVisit;

    private String checkInTime;

    private String checkOutTime;

    private String departmentName;

    private String itemCarried;

    private String personToVisit;

    private String idProofType;

    private long idProofNumber;

    private long docId;

    private String docName;

    private Boolean status;

    private long imgId;

    private String imgName;

    private String extraTime;

    private double paymentAmt;

    private String registeredBy;

    private String kioskReg;

    private Boolean multiVisit;

    private long captureId;

    @CreationTimestamp
    private Date createdOn;

    private String purpose;

    //-----------new Req-----------

    private String vehicleDetails;

    private long idProofDocId;

    private long emailAttachmentId;

    private  String companyName;

    private String siteName;

    private String contactAddress;

    private String passPortNumber;

    private long passPortDocId;

    private String validTill;

    private String visaNumber;

    private String visaValidTill;

    private long visaDocId;

    private String visaType;

    private String authorisedBy;

    //---------pass----------------

    private String entryGateNumber;

    private String exitGateNumber;

    private long entryBodyTemperature;

    private long exitBodyTemperature;

    private boolean maskWearing;

    private Date startDate;

    private Date endDate;

    private long timeSlot;

    private String approvedCompany;

    private String rfid;

    private boolean allowedOrDenied;

}
