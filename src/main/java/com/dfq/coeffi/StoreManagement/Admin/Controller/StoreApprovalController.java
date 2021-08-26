package com.dfq.coeffi.StoreManagement.Admin.Controller;

import com.dfq.coeffi.StoreManagement.Admin.Entity.ItemCategory;
import com.dfq.coeffi.StoreManagement.Admin.Entity.MinPercentage;
import com.dfq.coeffi.StoreManagement.Admin.Entity.ReasonCode;
import com.dfq.coeffi.StoreManagement.Admin.Entity.StoreApproval;
import com.dfq.coeffi.StoreManagement.Admin.Repository.ItemCategoryRepository;
import com.dfq.coeffi.StoreManagement.Admin.Repository.MinPercentageRepository;
import com.dfq.coeffi.StoreManagement.Admin.Repository.ReasonCodeRepository;
import com.dfq.coeffi.StoreManagement.Admin.Repository.StoreApprovalRepository;
import com.dfq.coeffi.StoreManagement.Entity.Items;
import com.dfq.coeffi.StoreManagement.Repository.ItemsRepository;
import com.dfq.coeffi.controller.BaseController;
import javafx.print.Collation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
public class StoreApprovalController extends BaseController {


    @Autowired
    MinPercentageRepository minPercentageRepository;

    @Autowired
    ReasonCodeRepository reasonCodeRepository;

    @Autowired
    StoreApprovalRepository storeApprovalRepository;

    @Autowired
    ItemsRepository itemsRepository;

    @Autowired
    ItemCategoryRepository itemCategoryRepository;

    @PostMapping("save-store-approval")
    public ResponseEntity<StoreApproval> saveApproval(@RequestBody StoreApproval storeApproval) {
        List<StoreApproval> storeApprovals = storeApprovalRepository.findAll();
        if (storeApprovals.isEmpty()){
            StoreApproval storeApproval1 = new StoreApproval();
            storeApprovalRepository.save(storeApproval);
        }else {
            Collections.reverse(storeApprovals);
            StoreApproval storeApprovalLatest = storeApprovals.get(0);
            storeApprovalLatest.setStoreApprove(storeApproval.getStoreApprove());
            storeApprovalRepository.save(storeApprovalLatest);
        }
        return new ResponseEntity<>(storeApproval, HttpStatus.CREATED);
    }

    @PostMapping("save-reason-code")
    public ResponseEntity<ReasonCode> saveReason(@RequestBody ReasonCode reasonCode) {
        reasonCodeRepository.save(reasonCode);

        return new ResponseEntity<>(reasonCode, HttpStatus.CREATED);
    }

    @PostMapping("save-min-percentage")
    public ResponseEntity<MinPercentage> saveMin(@RequestBody MinPercentage minPercentage) {
        minPercentageRepository.save(minPercentage);

        return new ResponseEntity<>(minPercentage, HttpStatus.CREATED);
    }

    @GetMapping("get-store-approval")
    public ResponseEntity<List<StoreApproval>> getStore() {
        List<StoreApproval> storeApprovalList = storeApprovalRepository.findAll();

        return new ResponseEntity<>(storeApprovalList, HttpStatus.OK);
    }

    @GetMapping("get-reason-code")
    public ResponseEntity<List<ReasonCode>> getReason() {
        List<ReasonCode> reasonCodeList = reasonCodeRepository.findAll();

        return new ResponseEntity<>(reasonCodeList, HttpStatus.OK);
    }

    @GetMapping("get-min-approval")
    public ResponseEntity<List<MinPercentage>> getMin() {
        List<MinPercentage> minPercentageList = minPercentageRepository.findAll();

        return new ResponseEntity<>(minPercentageList, HttpStatus.OK);
    }

    @DeleteMapping("delete-store-approval/{id}")
    public void storeDelete(@PathVariable("id") long id) {
        storeApprovalRepository.delete(id);
    }

    @DeleteMapping("delete-reason-code/{id}")
    public void reasonDelete(@PathVariable("id") long id) {
        reasonCodeRepository.delete(id);
    }

    @DeleteMapping("delete-min-approval/{id}")
    public void minDelete(@PathVariable("id") long id) {
        minPercentageRepository.delete(id);
    }

    @GetMapping("get-quality-indication/{id}")
    public boolean getLed(@PathVariable("id") long id) {
        MinPercentage minPercentage = minPercentageRepository.findOne((long) 1);
        boolean b;
        Items items = itemsRepository.findOne(id);
        if (items.getQuantity() < minPercentage.getMinPercentage()) {
            b = true;
        } else {
            b = false;
        }
        return b;
    }

    @PostMapping("save-item-category")
    public ResponseEntity<ItemCategory> saveItemCategory(@RequestBody ItemCategory itemCategory) {
        itemCategoryRepository.save(itemCategory);

        return new ResponseEntity<>(itemCategory, HttpStatus.CREATED);
    }

    @GetMapping("get-item-category")
    public ResponseEntity<List<ItemCategory>> getItemCategory() {
        List<ItemCategory> itemCategoryList = itemCategoryRepository.findAll();
        return new ResponseEntity<>(itemCategoryList, HttpStatus.OK);
    }

    @DeleteMapping("delete-item-category/{id}")
    public void itemDelete(@PathVariable("id") long id) {
        itemCategoryRepository.delete(id);
    }


}
