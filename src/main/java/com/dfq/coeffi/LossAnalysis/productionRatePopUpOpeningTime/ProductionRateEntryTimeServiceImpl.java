package com.dfq.coeffi.LossAnalysis.productionRatePopUpOpeningTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductionRateEntryTimeServiceImpl implements ProductionRateEntryTimeService {

    @Autowired
    private ProductionRateEntryTimeRepository productionRateEntryTimeRepository;

    @Override
    public ProductionRateEntryTime saveProductionRateEntryTime(ProductionRateEntryTime productionRateEntryTime) {
        return productionRateEntryTimeRepository.save(productionRateEntryTime);
    }

    @Override
    public List<ProductionRateEntryTime> getAllProductionRateEntryTime() {
        List<ProductionRateEntryTime> productionRateEntryTimes = new ArrayList<>();
        List<ProductionRateEntryTime> productionRateEntryTimeList = productionRateEntryTimeRepository.findAll();
        for (ProductionRateEntryTime productionRateEntryTime:productionRateEntryTimeList) {
            if (productionRateEntryTime.getStatus() == true){
                productionRateEntryTimes.add(productionRateEntryTime);
            }
        }
        return productionRateEntryTimes;
    }

    @Override
    public Optional<ProductionRateEntryTime> getProductionRateEntryTime(long id) {
        return productionRateEntryTimeRepository.findById(id);
    }
}
