package com.dfq.coeffi.preventiveMaintenance.admin.repository;


import com.dfq.coeffi.preventiveMaintenance.admin.entity.PreventiveMaintenanceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface PreventiveMaintenanceMasterRepository extends JpaRepository<PreventiveMaintenanceMaster,Long> {

    @Query("SELECT pm FROM PreventiveMaintenanceMaster pm WHERE pm.sopType.id = :sopTypeId AND pm.sopCategory.id = :digitalSOPId AND pm.durationType.id = :durationTypeId AND pm.durationValue = :durationValue")
    PreventiveMaintenanceMaster findByAssemblyLineByStagesByDurationTypeByDurationValue(@Param("sopTypeId") long sopTypeId, @Param("digitalSOPId") long digitalSOPId, @Param("durationTypeId") long durationTypeId, @Param("durationValue") long durationValue);

    Optional<PreventiveMaintenanceMaster> findById(long id);

    @Query("SELECT pm FROM PreventiveMaintenanceMaster pm WHERE pm.sopType.id = :sopTypeId AND pm.sopCategory.id = :digitalSopId ")
    List<PreventiveMaintenanceMaster> findBySopTypeByDigitalSop(@Param("sopTypeId") long sopTypeId, @Param("digitalSopId") long digitalSopId);
}
