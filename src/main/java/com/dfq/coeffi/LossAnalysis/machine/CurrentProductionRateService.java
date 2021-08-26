package com.dfq.coeffi.LossAnalysis.machine;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;

import java.util.List;
import java.util.Optional;

public interface CurrentProductionRateService {

    CurrentProductionRate saveCurrentProductionRate(CurrentProductionRate currentProductionRate);
    List<CurrentProductionRate> getAllCurrentProductionRate();
    Optional<CurrentProductionRate> getCurrentProductionRate(long id);
    CurrentProductionRate deleteCurrentProductionRate(long id);
    List<CurrentProductionRate> getCurrentProductionRateByProduction(ProductionLineMaster productionLineMaster);
}