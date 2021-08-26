package com.dfq.coeffi.Expenses.Services;

import com.dfq.coeffi.Expenses.Entities.EmployeeExpenses;
import com.dfq.coeffi.Expenses.Entities.ExpensesEnum;
import com.dfq.coeffi.Expenses.Repository.EmployeeExpensesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeExpensesServiceImpl implements EmployeeExpenseService {

    @Autowired
    EmployeeExpensesRepository employeeExpensesRepository;
    @Override
    public EmployeeExpenses createExit(EmployeeExpenses employeeExpenses) {
        return employeeExpensesRepository.save(employeeExpenses) ;
    }

    @Override
    public EmployeeExpenses get(long id) {
        return employeeExpensesRepository.findOne(id);
    }

    @Override
    public List<EmployeeExpenses> getAll() {
        return employeeExpensesRepository.findAll();
    }

    @Override
    public EmployeeExpenses getByEmpId(long employeeId)
    {
        return employeeExpensesRepository.findByEmployeeId(employeeId);
    }


}
