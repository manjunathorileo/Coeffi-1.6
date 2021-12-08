package com.dfq.coeffi.foodManagement.orderTracking.foodEstimationTimings;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class FoodEstimationTimings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String foodType;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date timeFrom;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date timeTo;
    @CreationTimestamp
    private Date createdOn;
    private String shiftName;
}