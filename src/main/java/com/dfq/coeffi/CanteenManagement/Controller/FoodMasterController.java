package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.FoodMaster;
import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.CanteenManagement.Service.FoodMasterService;
import com.dfq.coeffi.CanteenManagement.Service.FoodTimeService;
import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@Slf4j
public class FoodMasterController extends BaseController {

    private final FoodMasterService foodMasterService;
    private final FoodTimeService foodTimeService;

    @Autowired
    public FoodMasterController(FoodMasterService foodMasterService, FoodTimeService foodTimeService) {
        this.foodMasterService = foodMasterService;
        this.foodTimeService = foodTimeService;
    }

    @PostMapping("canteen/foodMaster")
    public ResponseEntity<FoodMaster> saveFoodMaster(@RequestBody FoodMaster foodMaster) {
        FoodTimeMasterAdv foodTimeMaster = foodTimeService.getFoodTime(foodMaster.getFoodType().getId());
        foodMaster.setFoodType(foodTimeMaster);
        List<FoodMaster> foodMasterList = foodMasterService.getAllFoodMaster();
        FoodMaster foodMasterObj =new FoodMaster();
        for (FoodMaster foodMasterObj1:foodMasterList) {
            if(foodMaster.getId() != foodMasterObj1.getId()) {
                if (foodMasterObj1.getFoodName().equals(foodMaster.getFoodName()) || foodMasterObj1.getFoodCode().equals(foodMaster.getFoodCode())) {
                    throw new EntityNotFoundException("This name or code already exit.");
                }
            }
        }
        foodMasterObj = foodMasterService.saveFoodMaster(foodMaster);
        return new ResponseEntity<>(foodMasterObj, HttpStatus.OK);
    }

    @PostMapping("canteen/foodMaster-update")
    public ResponseEntity<FoodMaster> updateFoodMaster(@RequestBody FoodMaster foodMaster) {
        FoodMaster foodMasterObj = foodMasterService.saveFoodMaster(foodMaster);
        return new ResponseEntity<>(foodMasterObj, HttpStatus.OK);
    }

    @GetMapping("canteen/foodMaster")
    public ResponseEntity<FoodMaster> getAllFoodMaster() {
        List<FoodMaster> foodMasters= foodMasterService.getAllFoodMaster();
        if (foodMasters.isEmpty()){
            throw new EntityNotFoundException("There is no food details");
        }
        return new ResponseEntity(foodMasters, HttpStatus.OK);
    }

    @GetMapping("canteen/foodMaster-by-FoodType/{foodTypeId}")
    public ResponseEntity<FoodMaster> getFoodMasterByFoodType(@PathVariable long foodTypeId) {
        FoodTimeMasterAdv foodTimeMasterAdv = foodTimeService.getFoodTime(foodTypeId);
        List<FoodMaster> foodMasters= foodMasterService.getFoodMasterByFoodType(foodTimeMasterAdv);
        if (foodMasters.isEmpty()){
            throw new EntityNotFoundException("There is no food details for " + foodTimeMasterAdv.getFoodType());
        }
        return new ResponseEntity(foodMasters, HttpStatus.OK);
    }

    @GetMapping("canteen/foodMaster/{id}")
    public ResponseEntity<FoodMaster> getFoodMaster(@PathVariable long id) {
        FoodMaster foodMasters= foodMasterService.getFoodMaster(id);
        return new ResponseEntity(foodMasters, HttpStatus.OK);
    }

    @DeleteMapping("canteen/foodMaster/{id}")
    public ResponseEntity<FoodMaster> deleteFoodMaster(@PathVariable long id) {
        FoodMaster foodMasters= foodMasterService.deleteFoodMaster(id);
        return new ResponseEntity(foodMasters, HttpStatus.OK);
    }
}
