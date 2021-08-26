package com.dfq.coeffi.foodManagement;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class FoodTimeMaster {

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


}
