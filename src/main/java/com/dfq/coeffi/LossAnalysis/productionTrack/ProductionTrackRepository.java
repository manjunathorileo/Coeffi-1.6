package com.dfq.coeffi.LossAnalysis.productionTrack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
public interface ProductionTrackRepository extends JpaRepository<ProductionTrack,Long> {

    Optional<ProductionTrack> findById(long id);

    @Query("SELECT productionTrack FROM ProductionTrack productionTrack WHERE productionTrack.productionLineMaster.id = :productionLineId AND productionTrack.shift.id = :shiftId AND productionTrack.createdOn = :today")
    List<ProductionTrack> findByDateByShiftByProductionLine(@Param("today") Date today, @Param("shiftId") long shiftId, @Param("productionLineId") long productionLineId);
}
