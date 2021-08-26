package com.dfq.coeffi.Expenses.Repository;

import com.dfq.coeffi.Expenses.Entities.EmployeeExpenses;
import com.dfq.coeffi.Expenses.Entities.ExpensesEnum;
import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagement;
import com.dfq.coeffi.employeePerformanceManagement.entity.GoalStatusEnum;
import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeExpensesRepository extends JpaRepository<EmployeeExpenses,Long> {

    EmployeeExpenses findByEmployeeId(long employeeId);

}
