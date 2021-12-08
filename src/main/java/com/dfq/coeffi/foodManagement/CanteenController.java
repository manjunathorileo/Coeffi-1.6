package com.dfq.coeffi.foodManagement;

import com.dfq.coeffi.advanceFoodManagementExtra.EmployeeCanteenAttendanceService;
import com.dfq.coeffi.advanceFoodManagementExtra.EmployeeCanteenDetails;
import com.dfq.coeffi.advanceFoodManagementExtra.EmployeeCanteenDetailsService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.foodManagement.orderTracking.foodEstimationTimings.FoodEstimationTimings;
import com.dfq.coeffi.foodManagement.orderTracking.foodOrderTracking.FoodOrderTrackingService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class CanteenController extends BaseController {
    @Autowired
    CanteenService canteenService;

    @PostMapping("canteen/food-time")
    public void saveFoodTimeMaster(@RequestBody FoodTimeMaster foodTimeMaster) {
        FoodTimeMaster foodTimingsOverLap = getCurrentFoodEstimate(foodTimeMaster);
        if (foodTimingsOverLap != null) {
            throw new EntityExistsException("Food usage  already exists for this timings");
        }
        canteenService.createFoodTime(foodTimeMaster);
    }

    @GetMapping("canteen/food-times")
    public ResponseEntity<List<FoodTimeMaster>> getFoodTime() {
        List<FoodTimeMaster> foodTimeMasters = canteenService.getFoodTimes();
        return new ResponseEntity<>(foodTimeMasters, HttpStatus.OK);
    }

    @GetMapping("canteen/caterer-settings")
    public ResponseEntity<List<CatererSettings>> getCatererSettings() {
        List<FoodTimeMaster> foodTimeMasters = canteenService.getFoodTimes();
        if (foodTimeMasters.isEmpty()) {
            throw new EntityNotFoundException("Create Time master first");
        }
        for (FoodTimeMaster foodTimeMaster : foodTimeMasters) {
            CatererSettings catererSettingEmployee = canteenService.getCatererSetting(foodTimeMaster.getFoodType(), "EMPLOYEE");
            CatererSettings catererSettingVisitor = canteenService.getCatererSetting(foodTimeMaster.getFoodType(), "VISITOR");
            CatererSettings catererSettingContract = canteenService.getCatererSetting(foodTimeMaster.getFoodType(), "CONTRACT");
            if (catererSettingEmployee == null) {
                catererSettingEmployee = new CatererSettings();
                catererSettingEmployee.setFoodType(foodTimeMaster.getFoodType());
                catererSettingEmployee.setEmployeeType("EMPLOYEE");
                catererSettingEmployee.setEmployeeRate(0);
                catererSettingEmployee.setEmployerRate(0);
                catererSettingEmployee.setCatererTotal(0);
                catererSettingEmployee.setFoodTimeMaster(foodTimeMaster);
                canteenService.createCatererSettings(catererSettingEmployee);
            }
            if (catererSettingVisitor == null) {
                catererSettingVisitor = new CatererSettings();
                catererSettingVisitor.setFoodType(foodTimeMaster.getFoodType());
                catererSettingVisitor.setEmployeeType("VISITOR");
                catererSettingVisitor.setEmployeeRate(0);
                catererSettingVisitor.setEmployerRate(0);
                catererSettingVisitor.setCatererTotal(0);
                catererSettingVisitor.setFoodTimeMaster(foodTimeMaster);
                canteenService.createCatererSettings(catererSettingVisitor);
            }
            if (catererSettingContract == null) {
                catererSettingContract = new CatererSettings();
                catererSettingContract.setFoodType(foodTimeMaster.getFoodType());
                catererSettingContract.setEmployeeType("CONTRACT");
                catererSettingContract.setEmployeeRate(0);
                catererSettingContract.setEmployerRate(0);
                catererSettingContract.setCatererTotal(0);
                catererSettingContract.setFoodTimeMaster(foodTimeMaster);
                canteenService.createCatererSettings(catererSettingContract);
            }
        }
        List<CatererSettings> catererSettings = canteenService.getCatererSettings();
        return new ResponseEntity<>(catererSettings, HttpStatus.OK);
    }

    @PostMapping("canteen/caterer-settings")
    public void allocateRateforETypeAndFType(@RequestBody CatererSettings catererSettings) {
        canteenService.createCatererSettings(catererSettings);
    }

    @PostMapping("canteen/caterer-details")
    public void saveCatererDetails(@RequestBody CatereDetails catereDetails) {
        canteenService.createCatererDetails(catereDetails);
    }

    @GetMapping("canteen/caterer-details")
    public ResponseEntity<List<CatereDetails>> getCatererDetails() {
        List<CatereDetails> catereDetailsList = canteenService.getCatererDetails();
        return new ResponseEntity<>(catereDetailsList, HttpStatus.OK);
    }

    @DeleteMapping("canteen/caterer-details/{id}")
    public void deletecat(@PathVariable long id) {
        canteenService.deleteCatererDetail(id);
    }

    @Autowired
    EmployeeCanteenDetailsService employeeCanteenDetailsService;
    @Autowired
    EmployeeCanteenAttendanceService employeeCanteenAttendanceService;


    @GetMapping("food-usage-timing/current-live")
    public ResponseEntity<FoodTimeMaster> getCurrentFoodEstimate() {
        FoodTimeMaster foodEstimationTimingsObj = new FoodTimeMaster();
        int orderHour = DateUtil.getRunningHour(new Date());
        List<FoodTimeMaster> foodEstimationTimingsList = canteenService.getFoodTimes();
        List<FoodTimeMaster> foodEstimationTimingsList1 = new ArrayList<>();
        for (FoodTimeMaster foodEstimationTimings : foodEstimationTimingsList) {
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

    public FoodTimeMaster getCurrentFoodEstimate(FoodTimeMaster foodTimeMasterPayload) {
        FoodTimeMaster foodEstimationTimingsObj = null;
        int orderHour = DateUtil.getRunningHour(foodTimeMasterPayload.getTimeFrom());
        List<FoodTimeMaster> foodEstimationTimingsList = canteenService.getFoodTimes();
        List<FoodTimeMaster> foodEstimationTimingsList1 = new ArrayList<>();
        for (FoodTimeMaster foodEstimationTimings : foodEstimationTimingsList) {
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


    @Autowired
    FoodTrackerRepository foodTrackerRepository;

    @GetMapping("canteen/latest-food-punch")
    public ResponseEntity<FoodTracker> getLatest() {
        List<FoodTracker> foodTrackers = foodTrackerRepository.findByMarkedOn(new Date());
        Collections.reverse(foodTrackers);
        FoodTracker foodTracker = new FoodTracker();
        if (!foodTrackers.isEmpty()) {
            foodTracker = foodTrackers.get(0);
        }
        return new ResponseEntity<>(foodTracker, HttpStatus.OK);
    }


}
