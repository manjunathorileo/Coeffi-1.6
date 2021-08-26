package com.dfq.coeffi.CanteenManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class FoodMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne
    private FoodTimeMasterAdv foodType;
    private String foodName;
    private String foodCode;
    private String unit;
    @OneToMany
    List<FoodImage> foodImages;
    private Boolean status;
}