package com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class FoodOrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String employeeId;
    private String employeeName;
    private String employeeCode;
    private String dept;
    private String employeeType;
    private String foodType;
    private Date orderedOn;
    private String companyName;
    private String location;
    private String shiftName;
    @Temporal(TemporalType.DATE)
    private Date markedOn;
}
