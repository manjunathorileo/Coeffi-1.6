package com.dfq.coeffi.CanteenManagement.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class FoodTimeMasterAdv {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private CatererDetailsAdv catererDetailsAdv;
    private String foodType;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date timeFrom;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="IST")
    private Date timeTo;
    @CreationTimestamp
    private Date createdOn;
    private Boolean status;
}