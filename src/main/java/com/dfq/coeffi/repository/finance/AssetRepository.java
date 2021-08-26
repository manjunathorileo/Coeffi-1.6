package com.dfq.coeffi.repository.finance;

import com.dfq.coeffi.entity.finance.expense.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface AssetRepository extends JpaRepository<Asset,Long>
{
	@Query("SELECT a FROM Asset a where a.createdOn between :startDate and :endDate")
	List<Asset> getAssetBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);



}
