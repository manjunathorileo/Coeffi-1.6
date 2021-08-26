package com.dfq.coeffi.visitor.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class VisitorDto {

    private long id;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    private String email;

    private Date dateOfBirth;

    private String gender;

    private String visitType;

    private String visitorOrganization;

    private String visitorLocation;

    private Date dateOfVisit;

    private long timeSlot;

    private String checkInTime;

    private String checkOutTime;

    private String departmentName;

    private String itemCarried;

    private String personToVisit;

    private String idProofType;

    private long idProofNumber;

    private Boolean status;

    private long imgId;

    private String imgName;

    private long docId;

    private String docName;

    private String extraTime;

    private double paymentAmt;

    private long officialCount;

    private long nonOfficial;

    private long casual;

    private Date passStartDate;

    private Date passEndDate;

    private long entryBodyTemperature;

    private long exitBodyTemperature;

    private boolean maskWearing;

    private String entryGateNumber;

    private String exitGateNumber;

    private String rfid;


}
