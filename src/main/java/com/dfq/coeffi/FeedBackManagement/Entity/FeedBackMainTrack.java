package com.dfq.coeffi.FeedBackManagement.Entity;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackGrades;
import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackParameter;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class FeedBackMainTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String remarks;
    private String ticketNumber;
    private long empId;
    @OneToOne
    BuildingDetails buildingDetails;
    @OneToOne
    CounterDetailsAdv counterDetailsAdv;
    @ManyToMany
    List<FeedBackGrades> feedBackGradesList;
    @ManyToMany
    List<FeedBackParameter> feedBackParameterList;
    @OneToOne
    private Employee employee;
}
