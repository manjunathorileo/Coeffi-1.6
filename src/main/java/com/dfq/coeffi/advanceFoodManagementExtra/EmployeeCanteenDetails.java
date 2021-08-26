package com.dfq.coeffi.advanceFoodManagementExtra;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class EmployeeCanteenDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long empId;
    private String employeeCode;
    private String employeeType;
    private long counterId;
    private String counterName;
    private long foodTypeId;
    private String foodTypeName;
    private Date punchDate;
    @Temporal(TemporalType.DATE)
    private Date markedOn;
    private long foodRate;
    private String departmentName;
    private String employeeName;
    private long openingBalance;
    private long credited;
    private long closingBalance;
    private String caterer;
    private String companyName;
    private String locationName;
}
