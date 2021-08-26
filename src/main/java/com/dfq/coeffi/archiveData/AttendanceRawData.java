package com.dfq.coeffi.archiveData;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class AttendanceRawData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String employeeCode;

    private Date transactionDate;

    @Temporal(TemporalType.DATE)
    private Date markedOn;

    private Date logDate;

    private String direction;

    private long deviceLogId;
}
