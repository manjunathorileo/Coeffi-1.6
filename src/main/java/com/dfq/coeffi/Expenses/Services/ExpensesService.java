package com.dfq.coeffi.Expenses.Services;

import com.dfq.coeffi.Expenses.Entities.Expenses;

import java.util.List;

public interface ExpensesService {
    Expenses save(Expenses expenses);
    Expenses getById(long id);
    List<Expenses> getAll();
    Expenses delete(long id);
    List<Expenses> save(List<Expenses> expenses);
    List<Expenses> getExpensesByEmployee(long employeeId);
}
