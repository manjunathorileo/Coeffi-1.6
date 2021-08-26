package com.dfq.coeffi.entity.hr;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
public class DepartmentTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    private Date createdOn;

    private String departmentName;

    private String designation;

    private Date startDate;

    private Date endDate;
}
