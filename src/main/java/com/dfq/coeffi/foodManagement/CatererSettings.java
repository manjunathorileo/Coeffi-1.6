package com.dfq.coeffi.foodManagement;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CatererSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String foodType;
    private String employeeType;
    private long employeeRate;
    private long employerRate;
    private long catererTotal;
    @OneToOne
    private FoodTimeMaster foodTimeMaster;

}
