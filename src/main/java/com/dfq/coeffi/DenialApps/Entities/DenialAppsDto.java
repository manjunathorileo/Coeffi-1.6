package com.dfq.coeffi.DenialApps.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class DenialAppsDto {
    private long employeeId;
    private String employeeName;
    private String designation;
    private String mobileNumber;
    private String department;
    private String usedApp;
    private Date usedOn;
    private Date usedTime;
}
