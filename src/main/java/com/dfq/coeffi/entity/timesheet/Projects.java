package com.dfq.coeffi.entity.timesheet;

/**
 * Created by ADMIN on 8/4/2018.
 */
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 55)
    private String name;

    @Column(length = 55)
    private String clientName;

    @Column(length = 15)
    private Long clientId;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date expectedEndDate;

    @Temporal(TemporalType.DATE)
    private Date actualEndDate;

    @Column(length = 255)
    private String description;

    @Temporal(TemporalType.DATE)
    private Date createdOn;

    @Column(length = 15)
    private String createdBy;

    @Temporal(TemporalType.DATE)
    private Date modifiedOn;

    @Column(length = 15)
    private String modifiedBy;

    @OneToOne(cascade=CascadeType.ALL)
    private Employee employee;
}
