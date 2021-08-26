package com.dfq.coeffi.LossAnalysis.machine;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;

import java.util.List;
import java.util.Optional;

public interface MachineMasterService {

    MachineMaster createMachineMaster(MachineMaster machineMaster);
    List<MachineMaster> getAllMachineMaster();
    Optional<MachineMaster> getMachineMaster(long id);
    MachineMaster deleteMachineMaster(long id);
    List<MachineMaster> getMachineMasterByProductionLine(ProductionLineMaster productionLineMaster);
}
