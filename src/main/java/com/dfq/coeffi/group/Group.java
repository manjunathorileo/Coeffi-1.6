package com.dfq.coeffi.group;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "communication_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    private String description;

    @OneToOne
    private Employee employeeGroupLead;

    private boolean status;

    @CreationTimestamp
    private Date createdOn;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Employee> groupEmployees;

}