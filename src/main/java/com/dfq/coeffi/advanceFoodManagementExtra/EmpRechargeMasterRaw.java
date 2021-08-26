package com.dfq.coeffi.advanceFoodManagementExtra;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EmpRechargeMasterRaw {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long nCode;
    private String employeeCode;
    private Date rechargeDate;
    private long rechargeAmount;
}
