package com.dfq.coeffi.CanteenManagement.employeeBalance;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class EmployeeBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long empId;
    private String empName;
    private String empType;
    @OneToOne
    private Employee employee;
    private long minimumBalanceAmount;
    private long actualBalance;
    private Boolean isBalanceLow;
    private long totalCredit;
    private long totaldebit;
}
