package com.dfq.coeffi.EvacuationDashboard;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Setter
@Getter
public class EvacuationDto {
    private long employeeId;
    private String employeeName;
    private String employeeCode;
    private String department;
    private String companyName;
    private Date date;
    private Date inTime;
    private Date outTime;
    private String totalHours;
    private String employeeType;
    private String entryGate;
    private String exitGate;
}
