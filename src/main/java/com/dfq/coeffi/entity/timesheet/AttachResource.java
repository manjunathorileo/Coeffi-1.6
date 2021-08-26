package com.dfq.coeffi.entity.timesheet;

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
public class AttachResource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @OneToOne(cascade=CascadeType.ALL)
    private Employee employee;

    @OneToOne(cascade=CascadeType.ALL)
    private Projects projects;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Tools> tools;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Activities> activities;
}
