package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Setter
@Getter
public class EmployeeAttendanceDto {

    private long id;
    private long employeeId;
    private String employeeName;
    private String session;
    private long departmentId;
    private String departmentName;
    private long designationId;
    private String designationName;
    private AttendanceStatus attendanceStatus;
    private String day;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date markedOn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inputDate;

    private EmployeeType employeeType;

    private int monthNumber;
    private String reportFrequency;

    private int inputMonth;

    private String workedHours;

    private long permanentPresent;
    private long permanentAbsent;
    private long contractPresent;
    private long contractAbsent;

    private Date inTime;
    private Date outTime;

    private int inputYear;

    private String monthName;
    private String year;

    private long shiftId;
    private String empCode;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date in;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date out;

    private Date date;
    private long permanentCount;
    private long contractCount;
    private long permanentContractCount;



}
