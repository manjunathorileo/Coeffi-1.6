package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class MonthlyStatusDto {

    private String day;
    private String employeeName;
    private AttendanceStatus attendanceStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date markedOn;

    private Date inTime;
    private Date outTime;
    private String workedHours;
    private String lateEntry;

    private String extraHrs;
    private double noOfPresentDays;
    private long noOfHolidays;
    private long noOfSudays;
    private long noOfAbsent;
    private long noOfHalfDays;
    private long noOfCompOffs;
    private double totalOtHours;
    private double totalHours;
    private double totalLateEntry;
    private double totalEarlyCheckOut;
    private double totalExactWorkedHrs;
    private String earlyOut;
    private String leaveHalfType;

    private long id;

    public double noOfLeaves;

    private long singleEntryBf;
    private long singleEntryLaunch;
    private long singleEntrySnaks;
    private long singleEntryDinner;
    private boolean weekOffPresent;
}
