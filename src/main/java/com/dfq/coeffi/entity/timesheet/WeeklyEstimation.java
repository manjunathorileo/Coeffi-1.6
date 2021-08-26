package com.dfq.coeffi.entity.timesheet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
public class WeeklyEstimation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade=CascadeType.ALL)
    private Activities activities;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<WeeklySchedule> weeklySchedules;

    @Column
    private Long employeeId;

    @Column
    private Long projectId;
}
