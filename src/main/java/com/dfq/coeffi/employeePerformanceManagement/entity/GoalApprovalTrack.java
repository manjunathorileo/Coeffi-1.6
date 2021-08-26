package com.dfq.coeffi.employeePerformanceManagement.entity;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class GoalApprovalTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    @OneToOne
    private EmployeePerformanceManagement employeePerformanceManagement;

    @Enumerated(EnumType.STRING)
    private GoalStatusEnum goalApprovalStatus;

    @OneToOne
    private Employee approvedBy;

    private Boolean status;
}