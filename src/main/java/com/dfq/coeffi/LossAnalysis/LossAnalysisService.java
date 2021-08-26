package com.dfq.coeffi.LossAnalysis;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import com.dfq.coeffi.LossAnalysis.machine.MachineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.master.shift.Shift;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LossAnalysisService {

    LossAnalysis createLossAnalysis(LossAnalysis lossAnalysis);

    List<LossAnalysis> getAllLossAnalysis();

    Optional<LossAnalysis> getLossAnalysisById(long id);

    List<LossAnalysis> getLossAnalysisByAssemblyLineByStage(ProductionLineMaster productionLineMaster, MachineMaster machineMaster);

    List<LossAnalysis> getLossAnalysisByAssemblyLine(ProductionLineMaster productionLineMaster);

    List<LossAnalysis> getLossAnalysisByShift(Shift shift);

    List<LossAnalysis> getLossAnalysisByLossCategory(LossCategory lossCategory);

    void deleteLossAnalysis(long id);

    List<LossAnalysis> getLossAnalysisByShiftByDateByProductionLine(Date createOn, long shiftId, long productionLineId);

    List<LossAnalysis> getLossAnalysisByShiftByFromDateByToDateByProductionLine(Date fromDate, Date toDate, long shiftId, long productionLineId);

    List<LossAnalysis> getLossAnalysisByFromDateByToDateByShiftByProductionLineByLossCategory(Date fromDate, Date toDate, long shiftId, long productionLineId, long lossCategoryId);

    List<LossAnalysis> getLossAnalysisByShiftByFromDateByToDateByMachine(Date fromDate, Date toDate, long shiftId, long machineId);

    List<LossAnalysis> getLossAnalysisByFromDateByToDateByShiftByMachineByLossCategory(Date fromDate, Date toDate, long shiftId, long machineId, long lossCategoryId);
}
