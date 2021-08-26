package com.dfq.coeffi.LossAnalysis.productionRatePopUpOpeningTime;

import java.util.List;
import java.util.Optional;

public interface ProductionRateEntryTimeService {

    ProductionRateEntryTime saveProductionRateEntryTime(ProductionRateEntryTime productionRateEntryTime);
    List<ProductionRateEntryTime> getAllProductionRateEntryTime();
    Optional<ProductionRateEntryTime> getProductionRateEntryTime(long id);
}
