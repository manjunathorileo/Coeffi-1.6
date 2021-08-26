package com.dfq.coeffi.CanteenManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class FoodTrackerAdv {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String employeeCode;
    private String employeeName;
    private String companyName;
    private String departmentName;
    private String locationName;
    private String employeeType;
    private String foodType;
    private long foodTotalRate;
    @Temporal(TemporalType.DATE)
    private Date markedOn;
    private Date recordedOn;
    private String caterer;

}
