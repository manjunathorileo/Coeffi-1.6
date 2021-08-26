package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.expense.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Auther H Kapil Kumar on 20/3/18.
 * @Company Orileo Technologies
 */

public interface ExpenseCategoryRepository extends JpaRepository<Category, Long>
{

}