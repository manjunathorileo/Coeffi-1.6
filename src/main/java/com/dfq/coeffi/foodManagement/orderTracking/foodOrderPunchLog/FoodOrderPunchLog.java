package com.dfq.coeffi.foodManagement.orderTracking.foodOrderPunchLog;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class FoodOrderPunchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String deviceName;
    private String deviceId;
    private String location;
    private String employeeCode;
    private Date orderedOn;
    private Boolean inStatus;
    private Boolean akg;
}
