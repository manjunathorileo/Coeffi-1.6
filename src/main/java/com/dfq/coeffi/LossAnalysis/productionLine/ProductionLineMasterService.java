package com.dfq.coeffi.LossAnalysis.productionLine;

import java.util.List;
import java.util.Optional;

public interface ProductionLineMasterService {

    ProductionLineMaster createProductionLineMaster(ProductionLineMaster productionLineMaster);
    List<ProductionLineMaster> getAllProductionLineMaster();
    Optional<ProductionLineMaster> getProductionLineMaster(long id);
    ProductionLineMaster deleteProductionLineMaster(long id);
}