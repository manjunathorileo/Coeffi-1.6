package com.dfq.coeffi.CompanySettings.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class CompanyName {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String companyName;
    private Date createdOn;
    private String address;
    private String licenseNumber;
    private boolean status;
    @OneToMany
    private List<Location> locations;
}
