package com.dfq.coeffi.Oqc.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OqcMasterRepository extends JpaRepository<OqcMaster,Long> {

    @Query("SELECT oqc FROM OqcMaster oqc where oqc.productId = :productId AND oqc.productionLineId = :productionLineId ")
    OqcMaster findByProductAndProductionLine(@Param("productId") long productId, @Param("productionLineId") long productionLineId);
}
