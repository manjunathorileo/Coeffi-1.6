package com.dfq.coeffi.preventiveMaintenance.user.repository;

import com.dfq.coeffi.preventiveMaintenance.user.entity.PreventiveMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


@Transactional
@EnableJpaRepositories
public interface PreventiveMaintenanceRepository extends JpaRepository<PreventiveMaintenance, Long> {

    @Query("SELECT pm FROM PreventiveMaintenance pm WHERE pm.sopType.id = :sopTypeId AND pm.sopCategory.id = :digitalSOPId AND pm.durationType.id = :durationTypeId AND pm.durationValue = :durationValue")
    List<PreventiveMaintenance> findByAssemblyLineByStagesByDurationTypeByDurationValue(@Param("sopTypeId") long sopTypeId, @Param("digitalSOPId") long digitalSOPId, @Param("durationTypeId") long durationTypeId, @Param("durationValue") long durationValue);

    @Query("SELECT pm FROM PreventiveMaintenance pm WHERE pm.durationType.id = :durationTypeId AND pm.durationValue = :durationValue")
    List<PreventiveMaintenance> findByDurationTypeByDurationValue(@Param("durationTypeId") long durationTypeId, @Param("durationValue") long durationValue);

    Optional<PreventiveMaintenance> findById(long id);

    @Query("SELECT pm FROM PreventiveMaintenance pm WHERE pm.sopType.id = :sopTypeId AND pm.sopCategory.id = :sopCategoryId ")
    List<PreventiveMaintenance> findBySopTypeBySopCategory(@Param("sopTypeId") long sopTypeId, @Param("sopCategoryId") long sopCategoryId);
}
