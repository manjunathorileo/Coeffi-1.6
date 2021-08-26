package com.dfq.coeffi.employeePerformanceManagement.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class EmployeeAppraisalRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long monthGap;
    private Boolean status;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;
}