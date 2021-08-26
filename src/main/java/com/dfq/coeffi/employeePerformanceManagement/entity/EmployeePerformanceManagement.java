package com.dfq.coeffi.employeePerformanceManagement.entity;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class EmployeePerformanceManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToOne
    private Employee employee;

    private String goalName;

    private String goalDiscription;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum goalApprovalManager1;
    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum goalApprovalManager2;
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date approvalDate;

    private String selfAppraisal;
    private long selfRating;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date appraisalDate;

    private String managerGoalRemarks;

    private long managerGoalRating;

    private String managerRemark;

    private long managerRating;

    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum appraisalApprovalManager1;

    @Enumerated(EnumType.STRING)
    private ApprovalStatusEnum appraisalApprovalManager2;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date appraisalApprovalDate;

    @Enumerated(EnumType.STRING)
    private GoalStatusEnum goalStatus;

    private Boolean status;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToOne(cascade = CascadeType.ALL)
    private Employee firstManager;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToOne(cascade = CascadeType.ALL)
    private Employee secondManager;

    private String GoalApprovedBy;

    private String overAllGoalRemarks;

    private long overAllGoalRating;

    private String overAllAppraisalRemarks;

    private long overAllAppraisalRating;

}
