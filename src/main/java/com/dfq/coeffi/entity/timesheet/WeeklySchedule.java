package com.dfq.coeffi.entity.timesheet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@ToString
@Entity
public class WeeklySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String day;

    @Column
    private long hours;

    @Column
    private long totalWorkedHours;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Column
    private Long employeeId;

    @Column
    private Long projectId;

    @OneToOne(cascade=CascadeType.ALL)
    private Activities activities;
}
