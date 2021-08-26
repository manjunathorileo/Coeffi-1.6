package com.dfq.coeffi.CanteenManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class FoodManagementSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Boolean isAdvance;
}
