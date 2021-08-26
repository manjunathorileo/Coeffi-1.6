package com.dfq.coeffi.advanceFoodManagementExtra;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EmployeeCanteenAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String cardId;
    private Date punchDateTime;
    @Temporal(TemporalType.DATE)
    private Date markedOn;
    private String deviceLogId;
    private String direction;
    private String counter;

}
