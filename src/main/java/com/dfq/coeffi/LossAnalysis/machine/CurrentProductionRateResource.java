//package com.dfq.coeffi.LossAnalysis.machine;
//
//import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
//import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
//import com.dfq.coeffi.controller.BaseController;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.persistence.EntityNotFoundException;
//import javax.validation.Valid;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//public class CurrentProductionRateResource extends BaseController {
//
//    private final CurrentProductionRateService currentProductionRateService;
//    private final MachineMasterService machineMasterService;
//    private final ProductionLineMasterService productionLineMasterService;
//
//    @Autowired
//    public CurrentProductionRateResource(CurrentProductionRateService currentProductionRateService, MachineMasterService machineMasterService, ProductionLineMasterService productionLineMasterService) {
//        this.currentProductionRateService = currentProductionRateService;
//        this.machineMasterService = machineMasterService;
//        this.productionLineMasterService = productionLineMasterService;
//    }
//
//    @PostMapping("/current-production-rate")
//    public ResponseEntity<CurrentProductionRate> saveCurrentProductionRate(@Valid @RequestBody CurrentProductionRate currentProductionRate){
//        Date today = new Date();
//        currentProductionRate.setCreatedOn(today);
//        currentProductionRate.setStatus(true);
//        CurrentProductionRate currentProductionRateObj = currentProductionRateService.saveCurrentProductionRate(currentProductionRate);
//        return new ResponseEntity<>(currentProductionRateObj, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/current-production-rate")
//    public ResponseEntity<List<CurrentProductionRate>> getAllCurrentProductionRate(){
//        List<CurrentProductionRate> currentProductionRates = currentProductionRateService.getAllCurrentProductionRate();
//        if (currentProductionRates.isEmpty()){
//            throw new EntityNotFoundException("There is no current production rate.");
//        }
//        return new ResponseEntity<>(currentProductionRates, HttpStatus.OK);
//    }
//
//    @GetMapping("/todays-current-production-rate/{productionLineId}")
//    public ResponseEntity<CurrentProductionRate> getTodaysCurrentProductionRateByMachin(@PathVariable long productionLineId){
//        Date toDay = new Date();
//        Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(productionLineId);
//        CurrentProductionRate currentProductionRate = new CurrentProductionRate();
//        List<CurrentProductionRate> currentProductionRates = currentProductionRateService.getCurrentProductionRateByProduction(productionLineMasterOptional.get());
//        for (CurrentProductionRate currentProductionRateObj:currentProductionRates) {
//            if (currentProductionRateObj.getCreatedOn().getDate() == toDay.getDate()){
//                currentProductionRate = currentProductionRateObj;
//            }
//        }
//        return new ResponseEntity<>(currentProductionRate, HttpStatus.OK);
//    }
//}
//
