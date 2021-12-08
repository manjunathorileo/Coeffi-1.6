package com.dfq.coeffi.LeaveSettings;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class LeaveSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String startMonth;
    private String endMonth;
    private boolean isEarnLeaveCarried;
    private boolean isCasualLeaveCarried;
    private boolean isSickLeaveCarried;
    private double ElmaxCarried;
    private double SlmaxCarried;
    private double ClmaxCarried;
    private long noOfDaysForOneEarnLeave;


}
