package com.dfq.coeffi.LossAnalysis.currentProductionRate;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CurrentProductionRateServiceImpl implements CurrentProductionRateService {

    @Autowired
    private CurrentProductionRateRepository currentProductionRateRepository;

    @Override
    public CurrentProductionRate saveCurrentProductionRate(CurrentProductionRate currentProductionRate) {
        return currentProductionRateRepository.save(currentProductionRate);
    }

    @Override
    public List<CurrentProductionRate> getAllCurrentProductionRate() {
        List<CurrentProductionRate> currentProductionRates = new ArrayList<>();
        List<CurrentProductionRate> currentProductionRateList = currentProductionRateRepository.findAll();
        for (CurrentProductionRate currentProductionRateObj:currentProductionRateList) {
            if (currentProductionRateObj.getStatus().equals(true) && currentProductionRateObj.getProductionLineMaster().getStatus().equals(true)){
                currentProductionRates.add(currentProductionRateObj);
            }
        }
        return currentProductionRates;
    }

    @Override
    public Optional<CurrentProductionRate> getCurrentProductionRate(long id) {
        return currentProductionRateRepository.findById(id);
    }

    @Override
    public CurrentProductionRate deleteCurrentProductionRate(long id) {
        CurrentProductionRate currentProductionRate = currentProductionRateRepository.findOne(id);
        currentProductionRate.setStatus(false);
        CurrentProductionRate currentProductionRateObj = currentProductionRateRepository.save(currentProductionRate);
        return currentProductionRateObj;
    }

    @Override
    public List<CurrentProductionRate> getCurrentProductionRateByProduction(ProductionLineMaster productionLineMaster) {
        List<CurrentProductionRate> currentProductionRates = new ArrayList<>();
        List<CurrentProductionRate> currentProductionRateList = currentProductionRateRepository.findByProductionLineMaster(productionLineMaster);
        for (CurrentProductionRate currentProductionRateObj:currentProductionRateList) {
            if (currentProductionRateObj.getStatus().equals(true)){
                currentProductionRates.add(currentProductionRateObj);
            }
        }
        return currentProductionRates;
    }

    @Override
    public CurrentProductionRate getCurrentProductionRateByDateByProductionLine(Date today, long productionLineId) {
        CurrentProductionRate currentProductionRate = new CurrentProductionRate();
        List<CurrentProductionRate> currentProductionRateList = currentProductionRateRepository.findByCreatedOnByProductionLine(today, productionLineId);
        for (CurrentProductionRate currentProductionRateObj:currentProductionRateList) {
            if (currentProductionRateObj.getStatus().equals(true)){
                currentProductionRate = currentProductionRateObj;
            }
        }
        return currentProductionRate;
    }
}
