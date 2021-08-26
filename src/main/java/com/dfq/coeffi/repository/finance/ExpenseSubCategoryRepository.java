package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.expense.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Auther H Kapil Kumar on 26/3/18.
 * @Company Orileo Technologies
 */

public interface ExpenseSubCategoryRepository extends JpaRepository<SubCategory, Long>
{
    List<SubCategory> findSubCategoryByCategoryId(long id);
}