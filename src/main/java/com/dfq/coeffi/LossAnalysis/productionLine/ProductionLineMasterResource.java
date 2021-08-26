package com.dfq.coeffi.LossAnalysis.productionLine;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductionLineMasterResource extends BaseController {

    private final ProductionLineMasterService productionLineMasterService;

    @Autowired
    public ProductionLineMasterResource(ProductionLineMasterService productionLineMasterService) {
        this.productionLineMasterService = productionLineMasterService;
    }

    @PostMapping("/production-line")
    public ResponseEntity<ProductionLineMaster> createProductionLineMaster(@Valid @RequestBody ProductionLineMaster productionLineMaster){
        Date today = new Date();
        productionLineMaster.setStatus(true);
        productionLineMaster.setCreatedOn(today);
        ProductionLineMaster productionLineMasterObj = productionLineMasterService.createProductionLineMaster(productionLineMaster);
        return new ResponseEntity<>(productionLineMasterObj, HttpStatus.CREATED);
    }

    @GetMapping("/production-line")
    public ResponseEntity<List<ProductionLineMaster>> getAllProductionLineMaster(){
        List<ProductionLineMaster> productionLineMasters = productionLineMasterService.getAllProductionLineMaster();
        if (productionLineMasters.isEmpty()){
            throw new EntityNotFoundException("There is No Production Line.");
        }
        return new ResponseEntity<>(productionLineMasters, HttpStatus.OK);
    }

    @GetMapping("/production-line/{id}")
    public ResponseEntity<ProductionLineMaster> getProductionLineMaster(@PathVariable long id){
        Optional<ProductionLineMaster> productionLineMasterOptional = productionLineMasterService.getProductionLineMaster(id);
        return new ResponseEntity(productionLineMasterOptional, HttpStatus.OK);
    }

    @DeleteMapping("/production-line/{id}")
    public ResponseEntity<ProductionLineMaster> deleteProductionLineMaster(@PathVariable long id){
        ProductionLineMaster productionLineMaster = productionLineMasterService.deleteProductionLineMaster(id);
        return new ResponseEntity<>(productionLineMaster, HttpStatus.OK);
    }

    @GetMapping("/production-line-by-logger/{loggerId}")
    public ResponseEntity<List<ProductionLineMaster>> getAllProductionLineMasterByLoggerId(@PathVariable long loggerId){
        List<ProductionLineMaster> productionLineMasters = new ArrayList<>();
        List<ProductionLineMaster> productionLineMasterList = productionLineMasterService.getAllProductionLineMaster();
        for (ProductionLineMaster productionLineMasterObj:productionLineMasterList) {
            List<Employee> assignToList = productionLineMasterObj.getAssignedTo();
            for (Employee assignTo:assignToList) {
                if (assignTo.getId() == loggerId){
                    productionLineMasters.add(productionLineMasterObj);
                }
            }
        }
        if (productionLineMasters.isEmpty()){
            throw new EntityNotFoundException("There is No Production Line.");
        }
        return new ResponseEntity<>(productionLineMasters, HttpStatus.OK);
    }
}