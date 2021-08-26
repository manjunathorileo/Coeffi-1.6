package com.dfq.coeffi.entity.leave;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@ToString
@Entity
@Table(name = "leave_data")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Date leaveStartDate;

    @Column
    private Date leaveEndDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaveRequestOn;

    @Column
    private String leaveApprovedBy;

    @Column(length = 500)
    private String reason;

    @Column
    private double totalLeavesApplied;

    @Column
    private Integer refId;

    @Column
    private String firstName;

    @Column(length = 45)
    private String refName;

    @Column
    private String rejectedRemark;

    @Column
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column
    @Enumerated(EnumType.STRING)
    private LeaveStatus leaveStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private AcademicYear academicYear;

    private boolean halfDay;

    @OneToOne
    private Employee firstApprovalManager;

    @OneToOne
    private Employee secondApprovalManager;

    private String employeeCode;

    private String leaveHalfType;
}