package com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance;

import com.dfq.coeffi.controller.leave.AccureLeave;
import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.OpeningLeave;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class EmployeeLeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private Employee employee;

    @OneToOne(cascade=CascadeType.MERGE)
    private OpeningLeave openingLeave;

    @OneToOne(cascade=CascadeType.MERGE)
    private AvailLeave availLeave;

    @OneToOne(cascade=CascadeType.MERGE)
    private ClosingLeave closingLeave;

    @OneToOne(cascade=CascadeType.ALL)
    private AccureLeave accureLeave;

    @OneToOne
    private AcademicYear academicYear;

    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    @JsonFormat(pattern ="yyyy-MM-dd")
    private Date lastUpdated;
    private long currentMonth;
    private Boolean status;
}
