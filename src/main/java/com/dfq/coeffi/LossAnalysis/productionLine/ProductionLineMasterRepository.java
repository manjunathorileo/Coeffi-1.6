package com.dfq.coeffi.LossAnalysis.productionLine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
public interface ProductionLineMasterRepository extends JpaRepository<ProductionLineMaster,Long> {
    Optional<ProductionLineMaster> findById(long id);
}
