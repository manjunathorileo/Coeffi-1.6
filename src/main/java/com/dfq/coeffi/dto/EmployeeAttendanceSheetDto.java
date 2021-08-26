package com.dfq.coeffi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class EmployeeAttendanceSheetDto {

    private String employeeName;
    private String monStatus;
    private String tueStatus;
    private String wedStatus;
    private String thuStatus;
    private String friStatus;
    private String satStatus;
    private long employeeId;
    private String week;
    private long departmentId;
    private String departmentName;
    private long designationId;
    private String designationName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date markedOn;

    private String monWorkedHours;
    private String tueWorkedHours;
    private String wedWorkedHours;
    private String thuWorkedHours;
    private String friWorkedHours;
    private String satWorkedHours;

    private Date monInTime;
    private Date tueInTime;
    private Date wedInTime;
    private Date thuInTime;
    private Date friInTime;
    private Date satInTime;

    private Date monOutTime;
    private Date tueOutTime;
    private Date wedOutTime;
    private Date thuOutTime;
    private Date friOutTime;
    private Date satOutTime;

    private long monId;
    private long tueId;
    private long wedId;
    private long thuId;
    private long friId;
    private long satId;

}
