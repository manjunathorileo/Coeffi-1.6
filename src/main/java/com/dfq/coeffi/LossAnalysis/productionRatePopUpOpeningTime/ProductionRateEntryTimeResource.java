package com.dfq.coeffi.LossAnalysis.productionRatePopUpOpeningTime;

import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class ProductionRateEntryTimeResource extends BaseController {

    private final ProductionRateEntryTimeService productionRateEntryTimeService;

    @Autowired
    public ProductionRateEntryTimeResource(ProductionRateEntryTimeService productionRateEntryTimeService) {
        this.productionRateEntryTimeService = productionRateEntryTimeService;
    }

    @PostMapping("production-rate-entry-time")
    private ResponseEntity<ProductionRateEntryTime> createProductionRateEntryTime(@Valid @RequestBody ProductionRateEntryTime productionRateEntryTime) {
        Date today = new Date();
        productionRateEntryTime.setCreatedOn(today);
        productionRateEntryTime.setStatus(true);
        List<ProductionRateEntryTime> productionRateEntryTimes = productionRateEntryTimeService.getAllProductionRateEntryTime();
        ProductionRateEntryTime productionRateEntryTimeObj = new ProductionRateEntryTime();
        if (productionRateEntryTimes.isEmpty()){
            productionRateEntryTimeObj = productionRateEntryTimeService.saveProductionRateEntryTime(productionRateEntryTime);
        } else {
            for (ProductionRateEntryTime productionRateEntryTimeLoop:productionRateEntryTimes) {
                productionRateEntryTimeLoop.setCreatedOn(today);
                productionRateEntryTimeLoop.setCreatedBy(productionRateEntryTime.getCreatedBy());
                productionRateEntryTimeLoop.setHourValue(productionRateEntryTime.getHourValue());
                productionRateEntryTimeObj = productionRateEntryTimeService.saveProductionRateEntryTime(productionRateEntryTimeLoop);
            }
        }
        return new ResponseEntity<>(productionRateEntryTimeObj, HttpStatus.OK);
    }

    @GetMapping("/production-rate-entry-time")
    private ResponseEntity<ProductionRateEntryTime> getAllProductionRateEntryTime() {
        List<ProductionRateEntryTime> productionRateEntryTimes = productionRateEntryTimeService.getAllProductionRateEntryTime();
        if (productionRateEntryTimes.isEmpty()){
            throw new EntityNotFoundException("There is no time for Production Rate entry.");
        }
        return new ResponseEntity(productionRateEntryTimes, HttpStatus.OK);
    }
}
