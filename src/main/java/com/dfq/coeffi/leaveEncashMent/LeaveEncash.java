package com.dfq.coeffi.leaveEncashMent;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class LeaveEncash {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String employeeCode;
    private String employeeName;
    private String leaveType;
    private double balance;
    private double noOfLeaves;
    private Date encashDate;
    private String month;
    private String year;
    @CreationTimestamp
    private Date createdOn;

}
