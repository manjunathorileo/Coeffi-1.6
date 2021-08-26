package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.expense.Liability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface LiabilityRepository extends JpaRepository<Liability, Long> {

	
	@Query("SELECT l FROM Liability l where l.createdOn between :startDate and :endDate")
	List<Liability> getLiabilityBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
