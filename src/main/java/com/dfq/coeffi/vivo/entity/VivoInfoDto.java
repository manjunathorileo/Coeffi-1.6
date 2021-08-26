package com.dfq.coeffi.vivo.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class VivoInfoDto {

    private long id;

    private String purpose;

    private long noOfPersons;

    private String entryTime;

    private String vehicleNumber;

    private long typeOfVehicleId;

    private String typeOfVehicle;

    private String driverDetails;

    private long workedHours;

    private long stayTime;

    private String route;

    private String exitTime;

    private long extraTime;

    private String remarksForExtraTime;

    private double payableAmount;

    private Date markedOn;

    private String companyName;

    private String companyType;

    private String BayNumber;

    private String slotNumber;

    private Date startDate;

    private Date endDate;

    private long imgId;

    private long rfid;

    private double grossWeight;

    private double tareWeight;

    private boolean allowOrDeny;

    private String dlNumber;

    private Date dlValidity;


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

    private long docId;
}
