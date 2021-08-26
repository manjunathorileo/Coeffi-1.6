package com.dfq.coeffi.LossAnalysis.machine;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface MachineMasterRepository extends JpaRepository<MachineMaster,Long> {

    Optional<MachineMaster> findById(long id);

    List<MachineMaster> findByProductionLine(ProductionLineMaster productionLineMaster);
}
