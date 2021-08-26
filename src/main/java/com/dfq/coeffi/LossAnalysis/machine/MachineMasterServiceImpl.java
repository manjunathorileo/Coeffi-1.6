package com.dfq.coeffi.LossAnalysis.machine;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MachineMasterServiceImpl implements MachineMasterService {

    @Autowired
    private MachineMasterRepository machineMasterRepository;

    @Override
    public MachineMaster createMachineMaster(MachineMaster machineMaster) {
        return machineMasterRepository.save(machineMaster);
    }

    @Override
    public List<MachineMaster> getAllMachineMaster() {
        List<MachineMaster> machineMasters = new ArrayList<>();
        List<MachineMaster> machineMasterList = machineMasterRepository.findAll();
        for (MachineMaster machineMasterObj:machineMasterList) {
            if (machineMasterObj.getStatus().equals(true) && machineMasterObj.getProductionLine().getStatus().equals(true)){
                machineMasters.add(machineMasterObj);
            }
        }
        return machineMasters;
    }

    @Override
    public Optional<MachineMaster> getMachineMaster(long id) {
        return machineMasterRepository.findById(id);
    }

    @Override
    public MachineMaster deleteMachineMaster(long id) {
        MachineMaster machineMaster = machineMasterRepository.findOne(id);
        machineMaster.setStatus(false);
        MachineMaster deletedMachineMaster = machineMasterRepository.save(machineMaster);
        return deletedMachineMaster;
    }

    @Override
    public List<MachineMaster> getMachineMasterByProductionLine(ProductionLineMaster productionLineMaster) {
        List<MachineMaster> machineMasters = new ArrayList<>();
        List<MachineMaster> machineMasterList = machineMasterRepository.findByProductionLine(productionLineMaster);
        for (MachineMaster machineMasterObj:machineMasterList) {
            if (machineMasterObj.getStatus().equals(true)){
                machineMasters.add(machineMasterObj);
            }
        }
        return machineMasters;
    }
}