package com.dfq.coeffi.Exit;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.policy.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
public class EmployeeExit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private Employee employee;

    @OneToMany
    private List<EmployeeExitDetails> employeeExitDetails;

    private String hrFinalRemark;

    private Boolean hrFinalStatus;

    private String emailId;

    private BigDecimal totalNoticePeriod;

    private BigDecimal leaveBalance;

    private BigDecimal assignedNoticePeriod;

    private Boolean status;

    @OneToOne
    private Document resignationFile;

    private boolean hrReviewCompleted;
}