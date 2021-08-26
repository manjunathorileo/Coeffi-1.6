package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.income.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@Transactional
@EnableJpaRepositories
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {

}
