package com.dfq.coeffi.LossAnalysis;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.machine.MachineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.master.shift.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
public interface LossAnalysisRepository extends JpaRepository<LossAnalysis,Long> {

    @Query("SELECT lossAnalysis FROM LossAnalysis lossAnalysis WHERE lossAnalysis.productionLine = :productionLineMaster AND lossAnalysis.machine = :machineMaster")
    List<LossAnalysis> findByProductionLineByMachine(@Param("productionLineMaster") ProductionLineMaster productionLineMaster, @Param("machineMaster") MachineMaster machineMaster);

    List<LossAnalysis> findByLossCategory(LossCategory lossCategory);

    List<LossAnalysis> findByProductionLine(ProductionLineMaster productionLineMaster);

    List<LossAnalysis> findByShift(Shift shift);

    Optional<LossAnalysis> findById(long id);

    @Query("SELECT lossAnalysis FROM LossAnalysis lossAnalysis WHERE lossAnalysis.productionLine.id = :productionLineId AND lossAnalysis.shift.id = :shiftId AND lossAnalysis.createdOn = :createOn")
    List<LossAnalysis> findByCreatedOnByShiftByProductionLine(@Param("createOn") Date createOn, @Param("shiftId") long shiftId, @Param("productionLineId") long productionLineId);

    @Query("SELECT lossAnalysis FROM LossAnalysis lossAnalysis WHERE lossAnalysis.productionLine.id = :productionLineId AND lossAnalysis.shift.id = :shiftId AND lossAnalysis.createdOn >= :fromDate AND lossAnalysis.createdOn <= :toDate ORDER BY lossTime DESC")
    List<LossAnalysis> findByFromDateByToDateByShiftByProductionLine(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("shiftId") long shiftId, @Param("productionLineId") long productionLineId);

    @Query("SELECT lossAnalysis FROM LossAnalysis lossAnalysis WHERE lossAnalysis.productionLine.id = :productionLineId AND lossAnalysis.shift.id = :shiftId AND lossAnalysis.createdOn >= :fromDate AND lossAnalysis.createdOn <= :toDate AND lossAnalysis.lossCategory.id = :lossCategoryId ORDER BY lossTime DESC")
    List<LossAnalysis> findByfromDateByToDateByShiftByProductionLineByLossCategory(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("shiftId") long shiftId, @Param("productionLineId") long productionLineId, @Param("lossCategoryId") long lossCategoryId);

    @Query("SELECT lossAnalysis FROM LossAnalysis lossAnalysis WHERE lossAnalysis.machine.id = :machineId AND lossAnalysis.shift.id = :shiftId AND lossAnalysis.createdOn >= :fromDate AND lossAnalysis.createdOn <= :toDate ORDER BY lossTime DESC")
    List<LossAnalysis> findByFromDateByToDateByShiftByMachine(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("shiftId") long shiftId, @Param("machineId") long machineId);

    @Query("SELECT lossAnalysis FROM LossAnalysis lossAnalysis WHERE lossAnalysis.machine.id = :machineId AND lossAnalysis.shift.id = :shiftId AND lossAnalysis.createdOn >= :fromDate AND lossAnalysis.createdOn <= :toDate AND lossAnalysis.lossCategory.id = :lossCategoryId ORDER BY lossTime DESC")
    List<LossAnalysis> findByfromDateByToDateByShiftByMachineByLossCategory(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("shiftId") long shiftId, @Param("machineId") long machineId, @Param("lossCategoryId") long lossCategoryId);
}
