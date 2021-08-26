package com.dfq.coeffi.Expenses.Repository;

import com.dfq.coeffi.Expenses.Entities.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpensesRepository extends JpaRepository<Expenses,Long> {
    List<Expenses> findByEmployeeId(long employeeId);
}
