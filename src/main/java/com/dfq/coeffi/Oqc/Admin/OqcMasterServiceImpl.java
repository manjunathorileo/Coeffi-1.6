package com.dfq.coeffi.Oqc.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OqcMasterServiceImpl implements OqcMasterService {

    @Autowired
    private OqcMasterRepository oqcMasterRepository;

    @Override
    public OqcMaster createOqcMaster(OqcMaster oqcMaster) {
        return oqcMasterRepository.save(oqcMaster);
    }

    @Override
    public List<OqcMaster> getOqcMaster() {
        return oqcMasterRepository.findAll();
    }

    @Override
    public OqcMaster getOqcMaster(long id) {
        return oqcMasterRepository.findOne(id);
    }

    @Override
    public OqcMaster getOqcMasterByProductAndProductionLine(long productId, long productionLineId) {
        return oqcMasterRepository.findByProductAndProductionLine(productId, productionLineId);
    }
}
