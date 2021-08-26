package com.dfq.coeffi.LossAnalysis.productionTrack;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductionTrackService {

    ProductionTrack createProductionTrack(ProductionTrack productionTrack);
    List<ProductionTrack> getAllProductionTrack();
    Optional<ProductionTrack> getProductionTrack(long id);
    List<ProductionTrack> getProductionTrackByDateByShiftByProduction(Date today, long shiftId, long productionLineId);
}
