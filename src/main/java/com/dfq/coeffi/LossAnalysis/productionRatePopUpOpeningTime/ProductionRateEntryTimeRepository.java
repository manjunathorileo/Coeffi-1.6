package com.dfq.coeffi.LossAnalysis.productionRatePopUpOpeningTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface ProductionRateEntryTimeRepository extends JpaRepository<ProductionRateEntryTime,Long> {
    Optional<ProductionRateEntryTime> findById(long id);
}
