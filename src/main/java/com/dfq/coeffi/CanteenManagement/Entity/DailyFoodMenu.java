package com.dfq.coeffi.CanteenManagement.Entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class DailyFoodMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Temporal(TemporalType.DATE)
    private Date effectiveFrom;
    @Temporal(TemporalType.DATE)
    private Date effictiveTo;
    @CreationTimestamp
    private Date createdDate;
    @OneToOne
    private BuildingDetails buildingDetails;
    @OneToOne
    private CounterDetailsAdv counterDetailsAdv;
    @OneToOne
    private FoodTimeMasterAdv foodType;
    @ManyToMany
    private List<FoodMaster> foodList;
    private Boolean isDayWise;
    private long weekNo;
    private String dayName;
}