package com.dfq.coeffi.LossAnalysis.currentProductionRate;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.LossAnalysis.productionTrack.ProductionTrack;
import com.dfq.coeffi.LossAnalysis.productionTrack.ProductionTrackService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class CurrentProductionRateResource extends BaseController {

    private final CurrentProductionRateService currentProductionRateService;
    private final ShiftController shiftController;
    private final ProductionLineMasterService productionLineMasterService;
    private final ProductionTrackService productionTrackService;

    @Autowired
    public CurrentProductionRateResource(CurrentProductionRateService currentProductionRateService, ShiftController shiftController, ProductionLineMasterService productionLineMasterService, ProductionTrackService productionTrackService) {
        this.currentProductionRateService = currentProductionRateService;
        this.shiftController = shiftController;
        this.productionLineMasterService = productionLineMasterService;
        this.productionTrackService = productionTrackService;
    }

    @PostMapping("/current-production-rate")
    public ResponseEntity<CurrentProductionRate> saveCurrentProductionRate(@Valid @RequestBody CurrentProductionRate currentProductionRate){
        Date today = new Date();
        currentProductionRate.setCreatedOn(today);
        currentProductionRate.setStatus(true);
        CurrentProductionRate currentProductionRateObj = currentProductionRateService.saveCurrentProductionRate(currentProductionRate);

        ResponseEntity<Shift> shiftResponseEntity = shiftController.getCurrentShift();
        if (shiftResponseEntity == null){
            throw new EntityNotFoundException("There is no running shift");
        }
        Shift shift = shiftResponseEntity.getBody();
        List <ProductionTrack> productionTracks = productionTrackService.getProductionTrackByDateByShiftByProduction(today, shift.getId(), currentProductionRateObj.getProductionLineMaster().getId());
        for (ProductionTrack productionTrack:productionTracks) {
            long totalCurrentProduction = currentProductionRate.getProducationRate() * productionTrack.getTotalWorkingHrs();
            float completeCurrentProduction = Float.valueOf(totalCurrentProduction)/Float.valueOf(currentProductionRate.getProducationRate() * productionTrack.getTotalWorkingHrs());
            float currentProductionPer = completeCurrentProduction * 100;
            productionTrack.setCurrentProductionRate(currentProductionRate.getProducationRate());
            productionTrack.setCurrentTotalItemProductionNo(totalCurrentProduction);
            productionTrack.setCurrentProductionPercent(currentProductionPer);
            ProductionTrack productionTrackObj = productionTrackService.createProductionTrack(productionTrack);
        }
        return new ResponseEntity<>(currentProductionRateObj, HttpStatus.CREATED);
    }

    @GetMapping("/current-production-rate")
    public ResponseEntity<List<CurrentProductionRate>> getAllCurrentProductionRate(){
        List<CurrentProductionRate> currentProductionRates = currentProductionRateService.getAllCurrentProductionRate();
        if (currentProductionRates.isEmpty()){
            throw new EntityNotFoundException("There is no current production rate.");
        }
        return new ResponseEntity<>(currentProductionRates, HttpStatus.OK);
    }

    @GetMapping("/todays-current-production-rate/{productionLineId}")
    public ResponseEntity<CurrentProductionRate> getTodaysCurrentProductionRateByMachin(@PathVariable long productionLineId){
        Date toDay = new Date();
        Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(productionLineId);
        CurrentProductionRate currentProductionRate = new CurrentProductionRate();
        List<CurrentProductionRate> currentProductionRates = currentProductionRateService.getCurrentProductionRateByProduction(productionLineMasterOptional.get());
        for (CurrentProductionRate currentProductionRateObj:currentProductionRates) {
            if (currentProductionRateObj.getCreatedOn().getDate() == toDay.getDate()){
                currentProductionRate = currentProductionRateObj;
            }
        }
        if (currentProductionRate.getProducationRate() < 1){
            currentProductionRate.setProductionLineMaster(productionLineMasterOptional.get());
            currentProductionRate.setProducationRate(productionLineMasterOptional.get().getDefaultProductionRate());
        }
        return new ResponseEntity<>(currentProductionRate, HttpStatus.OK);
    }
}

