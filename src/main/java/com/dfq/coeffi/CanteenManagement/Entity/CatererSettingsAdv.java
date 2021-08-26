package com.dfq.coeffi.CanteenManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CatererSettingsAdv {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String foodType;
    private String employeeType;
    private long employeeRate;
    private long employerRate;
    private long catererTotal;
    @OneToOne
    private FoodTimeMasterAdv foodTimeMasterAdv;
    //(cascade = CascadeType.MERGE)
    @OneToOne(cascade = CascadeType.MERGE)
    private CounterDetailsAdv counterDetailsAdv;
    @OneToOne
    private BuildingDetails buildingDetails;
    private Boolean status;
}
