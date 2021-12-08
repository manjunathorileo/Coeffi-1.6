package com.dfq.coeffi.foodManagement.orderTracking.foodOrderPunchLog;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTracking;
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
public class FoodOrderPunchLogResource extends BaseController {

    @Autowired
    private FoodOrderPunchLogService foodOrderPunchLogService;

    @PostMapping("/food-order-punch-log")
    public ResponseEntity<FoodOrderPunchLog> saveFoodOrderPunchLog(@RequestBody FoodOrderPunchLog foodOrderPunchLog) {
        foodOrderPunchLog.setAkg(false);
        FoodOrderPunchLog foodOrderPunchLogObj = foodOrderPunchLogService.save(foodOrderPunchLog);
        return new ResponseEntity<>(foodOrderPunchLogObj, HttpStatus.CREATED);
    }

    @GetMapping("/food-order-punch-log")
    public ResponseEntity<FoodOrderPunchLog> getFoodOrderPunchLog() {
        List<FoodOrderPunchLog> foodOrderPunchLogObj = foodOrderPunchLogService.getAll();
        return new ResponseEntity(foodOrderPunchLogObj, HttpStatus.OK);
    }
}
