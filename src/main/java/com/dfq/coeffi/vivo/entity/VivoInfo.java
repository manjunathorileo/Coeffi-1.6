package com.dfq.coeffi.vivo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class VivoInfo {

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

    private double grossWeight;

    private double tareWeight;

    private String description;

    @OneToOne
    private VivoPass vivoPass;

    //GEBE Emp table prototype

    private long empId;

    private long CardId;

    private String empname;

    private String emppin;

    private String sitecode;

    private String MachineName;

    private String newCardid;

    private Date checkedIn;

    private Date checkedOut;

    private String gateNo;

    private String employeeType;

}
