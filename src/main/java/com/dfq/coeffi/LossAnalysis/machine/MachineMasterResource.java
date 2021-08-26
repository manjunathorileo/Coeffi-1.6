package com.dfq.coeffi.LossAnalysis.machine;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterService;
import com.dfq.coeffi.controller.BaseController;
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
public class MachineMasterResource extends BaseController {

    private final MachineMasterService machineMasterService;
    private final ProductionLineMasterService productionLineMasterService;

    @Autowired
    public MachineMasterResource(MachineMasterService machineMasterService, ProductionLineMasterService productionLineMasterService) {
        this.machineMasterService = machineMasterService;
        this.productionLineMasterService = productionLineMasterService;
    }

    @PostMapping("/machine-master")
    public ResponseEntity<MachineMaster> createMachineMaster(@Valid @RequestBody MachineMaster machineMaster){
        Date toDay = new Date();
        machineMaster.setStatus(true);
        machineMaster.setCreatedOn(toDay);
        MachineMaster machineMasterObj = machineMasterService.createMachineMaster(machineMaster);
        return new ResponseEntity<>(machineMasterObj, HttpStatus.CREATED);
    }

    @GetMapping("/machine-master")
    public ResponseEntity<List<MachineMaster>> getAllMachineMaster(){
        List<MachineMaster> machineMaster = machineMasterService.getAllMachineMaster();
        if (machineMaster.isEmpty()){
            throw new EntityNotFoundException("There is No Machine Details.");
        }
        return new ResponseEntity<>(machineMaster, HttpStatus.OK);
    }

    @GetMapping("/machine-master/{id}")
    public ResponseEntity<MachineMaster> getMachineMaster(@PathVariable long id){
        Optional<MachineMaster> machineMaster = machineMasterService.getMachineMaster(id);
        return new ResponseEntity(machineMaster, HttpStatus.OK);
    }

    @DeleteMapping("/machine-master/{id}")
    public ResponseEntity<MachineMaster> deleteMachineMaster(@PathVariable long id){
        MachineMaster machineMaster = machineMasterService.deleteMachineMaster(id);
        return new ResponseEntity<>(machineMaster, HttpStatus.OK);
    }

    @GetMapping("/machine-master-by-production-line/{productionLineId}")
    public ResponseEntity<List<MachineMaster>> getMachineMasterByProductionLine(@PathVariable long productionLineId){
        Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(productionLineId);
        List<MachineMaster> machineMasters = machineMasterService.getMachineMasterByProductionLine(productionLineMasterOptional.get());
        if (machineMasters.isEmpty()){
            throw new EntityNotFoundException("There is no machinefor this production line.");
        }
        return new ResponseEntity<>(machineMasters, HttpStatus.OK);
    }
}