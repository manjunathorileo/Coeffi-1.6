package com.dfq.coeffi.DenialApps.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UninstalledAppsDto {
    private long employeeId;
    private String employeeName;
    private String designation;
    private String mobileNumber;
    private String department;
    private String uninstalledAPP;
    private Date uninstalledOn;
    private Date uninstalledTime;
}
