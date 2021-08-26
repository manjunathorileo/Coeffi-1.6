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
public class ActivityDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade=CascadeType.ALL)
    private WeeklyEstimation weeklyEstimation;
}
