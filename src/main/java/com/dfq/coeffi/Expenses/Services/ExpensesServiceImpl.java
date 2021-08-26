package com.dfq.coeffi.Expenses.Services;

import com.dfq.coeffi.Expenses.Entities.Expenses;
import com.dfq.coeffi.Expenses.Repository.ExpensesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpensesServiceImpl implements ExpensesService {
    @Autowired
    ExpensesRepository expensesRepository;

    @Override
    public Expenses save(Expenses expense) {
        return expensesRepository.save(expense);
    }

    @Override
    public Expenses getById(long id) {
        return expensesRepository.findOne(id);
    }

    @Override
    public List<Expenses> getAll() {
        return expensesRepository.findAll();
    }

    @Override
    public Expenses delete(long id) {
        return delete(id);
    }

    @Override
    public List<Expenses> save(List<Expenses> expenses) {
        return expensesRepository.save(expenses);
    }

    @Override
    public List<Expenses> getExpensesByEmployee(long employeeId) {
        return expensesRepository.findByEmployeeId(employeeId);
    }


}
