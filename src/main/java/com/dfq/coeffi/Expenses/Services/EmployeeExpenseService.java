package com.dfq.coeffi.Expenses.Services;

import com.dfq.coeffi.Expenses.Entities.EmployeeExpenses;
import com.dfq.coeffi.Expenses.Entities.ExpensesEnum;

import java.util.List;

public interface EmployeeExpenseService {

    EmployeeExpenses createExit(EmployeeExpenses employeeExpenses);
    EmployeeExpenses get(long id);
    List<EmployeeExpenses> getAll();
    EmployeeExpenses getByEmpId(long employeeId);


}
