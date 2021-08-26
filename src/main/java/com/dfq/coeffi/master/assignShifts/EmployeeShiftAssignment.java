package com.dfq.coeffi.master.assignShifts;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.master.shift.Shift;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class EmployeeShiftAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private Employee employee;

    @OneToOne
    private Shift shift;

    private int weekNo;

    @CreationTimestamp
    @JsonFormat(pattern ="yyyy-MM-dd")
    private Date createdOn;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="IST")
    private Date fromDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="IST")
    private Date toDate;
}
