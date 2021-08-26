package com.dfq.coeffi.entity.timesheet;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
public class Timesheet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Date createdDate;

    @OneToOne(cascade=CascadeType.ALL)
    private Employee employee;

    @OneToOne(cascade=CascadeType.ALL)
    private Projects projects;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<ActivityDetails> activityDetails;


}
