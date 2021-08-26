package com.dfq.coeffi.employeePermanentContract.entities;

import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class PermanentContractAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date markedOn;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private AttendanceStatus attendanceStatus;

    private String employeeCode;

    private Date recordedTime;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "IST")
    private Date inTime;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "IST")
    private Date outTime;

    private String workedHours;

    private String overTime;

    @OneToOne
    private EmpPermanentContract empPermanentContract;

    private long empId;

    private double extraTime;

    private double payableAmount;

    private String employeeName;

    private String companyName;

    private String validation;

    private double payment;

    private long imgId;

    private long entryBodyTemperature;

    private long exitBodyTemperature;

    private String entryGateNumber;

    private String exitGateNumber;

    private boolean maskWearing;

    private String employeeType;

}
