package com.dfq.coeffi.FeedBackManagement.Entity;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class FeedBackCounterSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String counterName;
    private long counterNo;
    @OneToOne
    private BuildingDetails buildingDetails;
    private Boolean status;
}
