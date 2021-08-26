package com.dfq.coeffi.StoreManagement.Controller;

import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMaster;
import com.dfq.coeffi.LossAnalysis.productionLine.ProductionLineMasterRepository;
import com.dfq.coeffi.StoreManagement.Entity.*;
import com.dfq.coeffi.StoreManagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminConfigurationController {
  @Autowired
  FactroysRepository factroysRepository;
  @Autowired
  ProductionLineMastersRepository productionLineMasterRepository;
  @Autowired
  BatchMasterRepository  batchMasterRepository ;
  @Autowired
    ProductNameRepository productNameRepository;
  @Autowired
  ServiceDurationRepository serviceDurationRepository;
  @Autowired
  SupplierNameRepository supplierNameRepository;


    @PostMapping("fatory-save")
    public ResponseEntity<Factorys> saveFactory(@RequestBody Factorys factorys){
        factroysRepository.save(factorys);
        return new ResponseEntity<>(factorys,HttpStatus.CREATED);
    }
    @PostMapping("production-line-save")
    public ResponseEntity<ProductionLineMasters> productionLine(@RequestBody ProductionLineMasters productionLineMasters){
        productionLineMasterRepository.save(productionLineMasters);
        return new ResponseEntity<>(productionLineMasters,HttpStatus.CREATED);
    }
    @PostMapping("batch-save")
    public ResponseEntity<BatchMaster> saveBatch(@RequestBody BatchMaster batchMaster){
        batchMasterRepository.save(batchMaster);
        return new ResponseEntity<>(batchMaster,HttpStatus.CREATED);
    }
    @GetMapping("get-factory")
    public ResponseEntity<List<Factorys>> getProductionData(){
        List<Factorys> adminConfigurationList=factroysRepository.findAll();

        return new ResponseEntity<>(adminConfigurationList,HttpStatus.OK);
    }
    @GetMapping("get-production-line")
    public ResponseEntity<List<ProductionLineMasters>> getProductionDatas(){
        List<ProductionLineMasters> adminConfigurationList=productionLineMasterRepository.findAll();

        return new ResponseEntity<>(adminConfigurationList,HttpStatus.OK);
    }
    @GetMapping("get-batch")
    public ResponseEntity<List<BatchMaster>> getProductionDatass(){
        List<BatchMaster> adminConfigurationList=batchMasterRepository.findAll();

        return new ResponseEntity<>(adminConfigurationList,HttpStatus.OK);
    }

    @DeleteMapping("factory-delete/{id}")
    public void  factoryDelete(@PathVariable("id") long id){
        factroysRepository.delete(id);
    }
    @DeleteMapping("production-line-delete/{id}")
    public void productionLineDelete(@PathVariable("id") long id){
        productionLineMasterRepository.delete(id);
    }

    @DeleteMapping("Batch-delete/{id}")
    public void batchDelete(@PathVariable("id") long id){
        productionLineMasterRepository.delete(id);
    }

    @PostMapping("save-product-name")
    public ResponseEntity<ProductName> saveProduct(@RequestBody ProductName productName){
        productNameRepository.save(productName);
        return new ResponseEntity<>(productName,HttpStatus.CREATED);
    }
    @GetMapping("get-product-name")
    public ResponseEntity<List<ProductName>> getProductName(){
        List<ProductName> productNameList=productNameRepository.findAll();

        return new ResponseEntity<>(productNameList,HttpStatus.OK);
    }
    @DeleteMapping("delete-product-name/{id}")
    public void deleteProductName(@PathVariable("id")long id){
        productNameRepository.delete(id);
    }


    @PostMapping("save-supplier-name")
    public ResponseEntity<SupplierName> saveProduct(@RequestBody SupplierName supplierName){
        supplierNameRepository.save(supplierName);
        return new ResponseEntity<>(supplierName,HttpStatus.CREATED);
    }
    @GetMapping("get-supplier-name")
    public ResponseEntity<List<SupplierName>> getSupplierName(){
        List<SupplierName> productNameList=supplierNameRepository.findAll();

        return new ResponseEntity<>(productNameList,HttpStatus.OK);
    }
    @DeleteMapping("delete-supplier-name/{id}")
    public void deleteSupplierName(@PathVariable("id")long id){
        supplierNameRepository.delete(id);
    }

    @PostMapping("save-duration-name")
    public ResponseEntity<ServiceDuration> saveDuration(@RequestBody ServiceDuration  serviceDuration){
        serviceDurationRepository.save(serviceDuration);
        return new ResponseEntity<>(serviceDuration,HttpStatus.CREATED);
    }
    @GetMapping("get-duration-name")
    public ResponseEntity<List<ServiceDuration>> getDurationName(){
        List<ServiceDuration> productNameList=serviceDurationRepository.findAll();

        return new ResponseEntity<>(productNameList,HttpStatus.OK);
    }
    @DeleteMapping("delete-duration-name/{id}")
    public void deleteDurationName(@PathVariable("id")long id){
        serviceDurationRepository.delete(id);
    }





}
