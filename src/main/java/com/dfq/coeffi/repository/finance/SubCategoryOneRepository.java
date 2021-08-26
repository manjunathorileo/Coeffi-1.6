package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.expense.SubCategoryOne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Auther H Kapil Kumar on 26/3/18.
 * @Company Orileo Technologies
 */

public interface SubCategoryOneRepository extends JpaRepository<SubCategoryOne, Long>
{
    List<SubCategoryOne> findSubCategoryOneBySubCategoryId(long id);

}