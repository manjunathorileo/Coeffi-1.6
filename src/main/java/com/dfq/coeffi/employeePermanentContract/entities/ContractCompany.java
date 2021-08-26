package com.dfq.coeffi.employeePermanentContract.entities;

import com.dfq.coeffi.controller.BaseController;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class ContractCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String companyName;

    private String Location;

    private String Address;

    private long phoneNumber;

    private String contactPerson;

    private Date contractStartDate;

    private Date contractEndDate;

    private String workOrderNumber;

    private Date dateOfOnBoarding;

    @Temporal(TemporalType.DATE)
    private Date dateOfTermination;

    private boolean paymentApplicable;

    private long stayTime;

    private long ratePerHour;

    private String licenseNumber;

    private Date licenseDate;

    private long noOfEmployees;

    private String licenseStatus;
}
