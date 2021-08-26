package com.dfq.coeffi.vivo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class VivoPass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String purpose;

    private long noOfPersons;

    private String entryTime;

    private String driverEntryTime;

    private String vehicleNumber;

    @OneToOne
    private TypeOfVehicle vehicleType;

    private String TypeOfVehicle;

    private String driverDetails;

    private String workedHours;

    private long stayTime;

    private String route;

    private String exitTime;

    private String driverExitTime;

    private String extraTime;

    private String driverExtraTime;

    private String remarksForExtraTime;

    private double payableAmount;

    private double driverPayableAmount;

    @CreationTimestamp
    private Date markedOn;

    @Temporal(TemporalType.DATE)
    private Date loggedOn;

    private String companyName;

    private String companyType;

    private String bayNumber;

    private String slotNumber;

    private String bayArchiveNumber;

    private String slotArchiveNumber;

    private boolean active;

    private long showw;

    private Date startDate;

    private Date endDate;

    private long imgId;

    private long rfid;

    private boolean allowOrDeny;

    private String dlNumber;

    private Date dlValidity;

    @OneToOne
    private VivoInfo vivoInfo;

    //GEBE Emp table prototype

    private long empId;

    private long CardId;

    private String empname;

    private String emppin;

    private String sitecode;

    private String MachineName;

    private String newCardid;

    private String employeeType;

    private String mobileNumber;

    private boolean terminate;

    private long docId;


}
