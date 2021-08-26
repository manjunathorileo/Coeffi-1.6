package com.dfq.coeffi.LossAnalysis.productionLine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductionLineMasterServiceImpl implements ProductionLineMasterService {

    @Autowired
    private ProductionLineMasterRepository productionLineMasterRepository;

    @Override
    public ProductionLineMaster createProductionLineMaster(ProductionLineMaster productionLineMaster) {
        return productionLineMasterRepository.save(productionLineMaster);
    }

    @Override
    public List<ProductionLineMaster> getAllProductionLineMaster() {
        List<ProductionLineMaster> productionLineMasters = new ArrayList<>();
        List<ProductionLineMaster> productionLineMasterList = productionLineMasterRepository.findAll();
        for (ProductionLineMaster productionLineMasterObj:productionLineMasterList) {
            if (productionLineMasterObj.getStatus().equals(true)){
                productionLineMasters.add(productionLineMasterObj);
            }
        }
        return productionLineMasters;
    }

    @Override
    public Optional<ProductionLineMaster> getProductionLineMaster(long id) {
        return productionLineMasterRepository.findById(id);
    }

    @Override
    public ProductionLineMaster deleteProductionLineMaster(long id) {
        ProductionLineMaster productionLineMaster = productionLineMasterRepository.findOne(id);
        productionLineMaster.setStatus(false);
        ProductionLineMaster deletedProductionLineMaster = productionLineMasterRepository.save(productionLineMaster);
        return deletedProductionLineMaster;
    }
}
