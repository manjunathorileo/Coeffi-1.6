package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface IncomeRepository extends JpaRepository<Income, Long>
{
	@Query("SELECT i FROM Income i where i.createdOn between :startDate and :endDate")
	List<Income> getIncomeBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
