package com.dfq.coeffi.Expenses.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class EmployeeExpenses {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String employeeName;
    private long employeeId;
    private long totalAmount;
    private String designation;
    private String approvedBy;

    @Enumerated(EnumType.STRING)
    private ExpensesEnum ExpenseStatus;
    private long managerId;
    private String reMarks;
    private String finRemarks;
    @OneToMany
    private List<Expenses> expenses;
}
