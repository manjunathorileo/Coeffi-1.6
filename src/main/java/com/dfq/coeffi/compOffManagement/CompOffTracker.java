package com.dfq.coeffi.compOffManagement;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class CompOffTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long employeeId;
    private String employeeName;
    private String month;
    private String year;
    @OneToMany
    private List<DatesForCompOff> generatedDates;
    private long compOffGeneratedDays;
    private long compOffAvailedDays;
    private long balance;
    private String employeeCode;

}
