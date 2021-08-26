package com.dfq.coeffi.LossAnalysis.currentProductionRate;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface CurrentProductionRateRepository extends JpaRepository<CurrentProductionRate,Long> {

    Optional<CurrentProductionRate> findById(long id);

    List<CurrentProductionRate> findByProductionLineMaster(ProductionLineMaster productionLineMaster);

    @Query("SELECT currentProductionRate FROM CurrentProductionRate currentProductionRate WHERE currentProductionRate.productionLineMaster.id = :productionLineId AND currentProductionRate.createdOn = :today")
    List<CurrentProductionRate> findByCreatedOnByProductionLine(@Param("today") Date today, @Param("productionLineId") long productionLineId);
}