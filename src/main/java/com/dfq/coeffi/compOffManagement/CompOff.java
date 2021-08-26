package com.dfq.coeffi.compOffManagement;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class CompOff {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Date compOffGenDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date compOffRequestOn;

    @Column
    private String compOffApprovedBy;

    @Column(length = 500)
    private String reason;

    @Column
    private long employeeId;

    @Column
    private String firstName;

    @Column
    private String rejectedRemark;

    @Column
    @Enumerated(EnumType.STRING)
    private LeaveStatus compOffStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private AcademicYear academicYear;

    private boolean halfDay;

    private long firstMgrId;

    private long secondMgrId;

    @OneToOne
    private Employee firstApprovalManager;

    @OneToOne
    private Employee secondApprovalManager;

    private String employeeCode;

}
