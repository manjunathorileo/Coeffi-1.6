package com.dfq.coeffi.DenialApps.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class UninstalledMustAppsTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long employeeId;
    private String uninstalledApp;
    private Date uninstalledOn;
    private Date uninstalledTime;
}
