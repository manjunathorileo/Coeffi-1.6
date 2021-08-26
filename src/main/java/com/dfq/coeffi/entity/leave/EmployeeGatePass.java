package com.dfq.coeffi.entity.leave;


import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@ToString
@Entity
public class EmployeeGatePass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
//    @JsonFormat(pattern ="yyyy-MM-dd")
    @CreationTimestamp
    private Date gatePassRequestOn;

    @Enumerated(EnumType.STRING)
    private GatePassType gatePassType;

    @Column
    private long employeeId;

    private String employeeName;

    private String fromTime;

    private String toTime;

    private String reason;

    private String description;

    @Enumerated(EnumType.STRING)
    private GatePassStatus gatePassStatus;

    @OneToOne
    private Employee employeeObject;

    @OneToOne(cascade=CascadeType.ALL)
    private AcademicYear academicYear;

    private long firstApprover;

    private long secondApprover;

    private String actualExitTime;
    private String actualEntryTime;


}
