package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.FoodManagementSetting;
import com.dfq.coeffi.CanteenManagement.Service.FoodManagementSettingService;
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
import java.util.List;

@RestController
@Slf4j
public class FoodManagementSettingController extends BaseController {

    @Autowired
    private FoodManagementSettingService foodManagementSettingService;

    @PostMapping("canteen/FoodManagementSetting")
    public ResponseEntity<FoodManagementSetting> saveFoodManagementSetting(@RequestBody FoodManagementSetting foodManagementSetting) {
        FoodManagementSetting foodManagementSettingObj = foodManagementSettingService.createFoodManagementSetting(foodManagementSetting);
        return new ResponseEntity<>(foodManagementSettingObj, HttpStatus.OK);
    }

    @GetMapping("canteen/FoodManagementSetting")
    public ResponseEntity<FoodManagementSetting> getAllFoodManagementSetting() {
        List<FoodManagementSetting> foodManagementSettings = foodManagementSettingService.getFoodManagementSetting();
        if (foodManagementSettings.isEmpty()){
            throw new EntityNotFoundException("There is no building details");
        }
        return new ResponseEntity(foodManagementSettings, HttpStatus.OK);
    }
}