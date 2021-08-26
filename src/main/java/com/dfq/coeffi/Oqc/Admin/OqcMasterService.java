package com.dfq.coeffi.Oqc.Admin;

import com.dfq.coeffi.Oqc.Admin.OqcMaster;

import java.util.List;

public interface OqcMasterService {

    OqcMaster createOqcMaster(OqcMaster oqcMaster);
    List<OqcMaster> getOqcMaster();
    OqcMaster getOqcMaster(long id);
    OqcMaster getOqcMasterByProductAndProductionLine(long productId, long productionLineId);
}
