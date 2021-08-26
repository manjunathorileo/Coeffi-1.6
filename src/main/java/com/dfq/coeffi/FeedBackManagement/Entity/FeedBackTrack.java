package com.dfq.coeffi.FeedBackManagement.Entity;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackGrades;
import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackParameter;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class FeedBackTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long empId;
    private long counterId;
    @Temporal(TemporalType.DATE)
    private Date createdDate;
    @OneToOne
    CounterDetailsAdv counterDetailsAdv;
    @OneToOne
    FeedBackGrades feedBackGrades;
    @OneToOne
    FeedBackParameter feedBackParameter;
    @OneToOne
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private FeedBackStatus feedBackStatus;
}
