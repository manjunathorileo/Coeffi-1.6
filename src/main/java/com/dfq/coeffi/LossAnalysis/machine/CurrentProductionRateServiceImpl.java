//package com.dfq.coeffi.LossAnalysis.machine;
//
//import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@Transactional
//public class CurrentProductionRateServiceImpl implements CurrentProductionRateService {
//
//    @Autowired
//    private CurrentProductionRateRepository currentProductionRateRepository;
//
//    @Override
//    public CurrentProductionRate saveCurrentProductionRate(CurrentProductionRate currentProductionRate) {
//        return currentProductionRateRepository.save(currentProductionRate);
//    }
//
//    @Override
//    public List<CurrentProductionRate> getAllCurrentProductionRate() {
//        List<CurrentProductionRate> currentProductionRates = new ArrayList<>();
//        List<CurrentProductionRate> currentProductionRateList = currentProductionRateRepository.findAll();
//        for (CurrentProductionRate currentProductionRateObj:currentProductionRateList) {
//            if (currentProductionRateObj.getStatus().equals(true)){
//                currentProductionRates.add(currentProductionRateObj);
//            }
//        }
//        return currentProductionRates;
//    }
//
//    @Override
//    public Optional<CurrentProductionRate> getCurrentProductionRate(long id) {
//        return currentProductionRateRepository.findById(id);
//    }
//
//    @Override
//    public CurrentProductionRate deleteCurrentProductionRate(long id) {
//        CurrentProductionRate currentProductionRate = currentProductionRateRepository.findOne(id);
//        currentProductionRate.setStatus(false);
//        CurrentProductionRate currentProductionRateObj = currentProductionRateRepository.save(currentProductionRate);
//        return currentProductionRateObj;
//    }
//
//    @Override
//    public List<CurrentProductionRate> getCurrentProductionRateByProduction(ProductionLineMaster productionLineMaster) {
//        List<CurrentProductionRate> currentProductionRates = new ArrayList<>();
//        List<CurrentProductionRate> currentProductionRateList = currentProductionRateRepository.findByProductionLineMaster(productionLineMaster);
//        for (CurrentProductionRate currentProductionRateObj:currentProductionRateList) {
//            if (currentProductionRateObj.getStatus().equals(true)){
//                currentProductionRates.add(currentProductionRateObj);
//            }
//        }
//        return currentProductionRates;
//    }
//}
