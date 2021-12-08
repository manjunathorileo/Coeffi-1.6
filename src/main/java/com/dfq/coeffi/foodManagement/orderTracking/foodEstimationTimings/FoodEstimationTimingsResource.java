package com.dfq.coeffi.foodManagement.orderTracking.foodEstimationTimings;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.*;

@RestController
@Slf4j
public class FoodEstimationTimingsResource extends BaseController {

    @Autowired
    private FoodEstimationTimingsService foodEstimationTimingsService;

    @PostMapping("/food-estimation-timing")
    public ResponseEntity<FoodEstimationTimings> saveFoodUsageTimings(@RequestBody FoodEstimationTimings foodEstimationTimings) {
        FoodEstimationTimings foodEstimationTimingsOverLap = getCurrentFoodEstimate(foodEstimationTimings);
        if (foodEstimationTimingsOverLap!=null){
            throw new EntityExistsException("Estimation already exists for this timings");
        }
        FoodEstimationTimings foodEstimationTimingsObj = foodEstimationTimingsService.save(foodEstimationTimings);
        return new ResponseEntity<>(foodEstimationTimingsObj, HttpStatus.CREATED);
    }

    @GetMapping("/food-estimation-timing")
    public ResponseEntity<List<FoodEstimationTimings>> getAllFoodUsageTimings() {
        List<FoodEstimationTimings> foodEstimationTimings = foodEstimationTimingsService.getAll();
        if (foodEstimationTimings.isEmpty()) {
            throw new EntityNotFoundException("There is no food usage timings.");
        }
        return new ResponseEntity<>(foodEstimationTimings, HttpStatus.OK);
    }

    @GetMapping("/food-estimation-timing/{id}")
    public ResponseEntity<FoodEstimationTimings> getById(@PathVariable long id) {
        FoodEstimationTimings foodEstimationTimings = foodEstimationTimingsService.getById(id);
        return new ResponseEntity<>(foodEstimationTimings, HttpStatus.OK);
    }

    @DeleteMapping("/food-estimation-timing/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        foodEstimationTimingsService.delete(id);
        String msg = "Deleted successfully";
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping("food-estimation-timing/current-live")
    public ResponseEntity<FoodEstimationTimings> getCurrentFoodEstimate() {
        FoodEstimationTimings foodEstimationTimingsObj = new FoodEstimationTimings();
        int orderHour = DateUtil.getRunningHour(new Date());
        List<FoodEstimationTimings> foodEstimationTimingsList = foodEstimationTimingsService.getAll();
        List<FoodEstimationTimings> foodEstimationTimingsList1 = new ArrayList<>();
        for (FoodEstimationTimings foodEstimationTimings : foodEstimationTimingsList) {
            int startHour = DateUtil.getRunningHour(foodEstimationTimings.getTimeFrom());
            int endHour = DateUtil.getRunningHour(foodEstimationTimings.getTimeTo());
            if (orderHour >= startHour && orderHour <= endHour) {
                foodEstimationTimingsList1.add(foodEstimationTimings);
            }
        }
        if (!foodEstimationTimingsList1.isEmpty()) {
            Collections.reverse(foodEstimationTimingsList1);
            foodEstimationTimingsObj = foodEstimationTimingsList1.get(0);
        }
        return new ResponseEntity<>(foodEstimationTimingsObj, HttpStatus.OK);
    }

    public FoodEstimationTimings getCurrentFoodEstimate(FoodEstimationTimings foodEstimationTimingsPayload) {
        FoodEstimationTimings foodEstimationTimingsObj = null;
        int orderHour = DateUtil.getRunningHour(foodEstimationTimingsPayload.getTimeFrom());
        List<FoodEstimationTimings> foodEstimationTimingsList = foodEstimationTimingsService.getAll();
        List<FoodEstimationTimings> foodEstimationTimingsList1 = new ArrayList<>();
        for (FoodEstimationTimings foodEstimationTimings : foodEstimationTimingsList) {
            int startHour = DateUtil.getRunningHour(foodEstimationTimings.getTimeFrom());
            int endHour = DateUtil.getRunningHour(foodEstimationTimings.getTimeTo());
            if (orderHour >= startHour && orderHour <= endHour) {
                foodEstimationTimingsList1.add(foodEstimationTimings);
            }
        }
        if (!foodEstimationTimingsList1.isEmpty()) {
            Collections.reverse(foodEstimationTimingsList1);
            foodEstimationTimingsObj = foodEstimationTimingsList1.get(0);
        }
        return foodEstimationTimingsObj;
    }
}