package com.dfq.coeffi.Expenses.Entities;

import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ExpensesDto {

    private long employeeId;
    private String expensesStatus;
    private long totalAmount;
    List<Expenses> expenses;
}
