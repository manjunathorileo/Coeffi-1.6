package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @Auther H Kapil Kumar on 20/3/18.
 * @Company Orileo Technologies
 */

public interface ExpenseRepository extends JpaRepository<Expense, Long>
{
	@Query("SELECT e FROM Expense e where e.createdOn between :startDate and :endDate")
	List<Expense> getExpenseBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}