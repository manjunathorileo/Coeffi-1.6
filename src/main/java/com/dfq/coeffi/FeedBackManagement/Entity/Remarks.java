package com.dfq.coeffi.FeedBackManagement.Entity;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Remarks {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String ticketNumber;
    private String remarks;
    private long empId;
    private long counterId;
    private String adminRemarks;
    @Temporal(TemporalType.DATE)
    private Date createdDate;
    @Temporal(TemporalType.DATE)
    private Date adminDate;
    @OneToOne
    CounterDetailsAdv counterDetailsAdv;
    @OneToOne
    private Employee employee;
    @OneToOne
    private FeedBackGrades overAllRating;

    @ManyToMany
    private List<FeedBackTrack> feedBackTrackList;

    @Enumerated(EnumType.STRING)
    private FeedBackStatus feedBackStatus;
}
