package com.dfq.coeffi.Exit;

import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.policy.document.Document;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class EmployeeExitDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private Department department;

    @OneToOne
    private Employee employee;

    private String asset;

    private Boolean isAccepted;

    @OneToOne
    private Document document;

    private String otherEmail;

    @OneToOne
    private Document resignationAccepted;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;

    private String hrRemark;

    private Boolean hrIsAccepted;

    @ManyToOne
    @JoinColumn
    private EmployeeExit employeeExit;
}