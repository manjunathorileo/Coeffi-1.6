package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.FoodTimeMasterAdv;
import com.dfq.coeffi.CanteenManagement.Service.FoodTimeService;
import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class FoodTimeAdvController extends BaseController {

    @Autowired
    private FoodTimeService foodTimeService;

    @PostMapping("canteen/food-times")
    public ResponseEntity<FoodTimeMasterAdv> saveFoodTimeMaster(@RequestBody FoodTimeMasterAdv foodTimeMasterAdv) {
        List<FoodTimeMasterAdv> foodTimeMasterAdvs = foodTimeService.getFoodTimes();
        List<FoodTimeMasterAdv> foodTimeMasterAdvsObj = new ArrayList<>();
        if (foodTimeMasterAdv.getId() > 0){
            for (FoodTimeMasterAdv foodTimeMaster:foodTimeMasterAdvs) {
                if (foodTimeMaster.getId() != foodTimeMasterAdv.getId()){
                    foodTimeMasterAdvsObj.add(foodTimeMaster);
                }
            }
        } else {
            foodTimeMasterAdvsObj = foodTimeMasterAdvs;
        }
        for (FoodTimeMasterAdv foodTimeMasterAdvObj:foodTimeMasterAdvsObj) {
            if (!foodTimeMasterAdvObj.getFoodType().equals(foodTimeMasterAdv.getFoodType())) {
                if ((foodTimeMasterAdvObj.getTimeFrom().getTime() <= foodTimeMasterAdv.getTimeFrom().getTime() && foodTimeMasterAdvObj.getTimeTo().getTime() >= foodTimeMasterAdv.getTimeFrom().getTime())
                        || foodTimeMasterAdvObj.getTimeFrom().getTime() <= foodTimeMasterAdv.getTimeTo().getTime() && foodTimeMasterAdvObj.getTimeTo().getTime() >= foodTimeMasterAdv.getTimeTo().getTime()) {
                    throw new EntityNotFoundException("This time is already allotted.");
                }
            } else {
                throw new EntityNotFoundException("This Food type already exist.");
            }
        }
        FoodTimeMasterAdv foodTimeMasterObjAdv = foodTimeService.createFoodTime(foodTimeMasterAdv);
        return new ResponseEntity<>(foodTimeMasterObjAdv, HttpStatus.OK);
    }

    @GetMapping("canteen/food-time")
    public ResponseEntity<List<FoodTimeMasterAdv>> getAllFoodTime() {
        List<FoodTimeMasterAdv> foodTimeMasterAdvs = foodTimeService.getFoodTimes();
        if (foodTimeMasterAdvs.isEmpty()){
            throw new EntityNotFoundException("There is no food timing details");
        }
        return new ResponseEntity<>(foodTimeMasterAdvs, HttpStatus.OK);
    }

    @GetMapping("canteen/food-time/{id}")
    public ResponseEntity<FoodTimeMasterAdv> getFoodTime(@PathVariable long id) {
        FoodTimeMasterAdv foodTimeMastersAdv = foodTimeService.getFoodTime(id);
        return new ResponseEntity(foodTimeMastersAdv, HttpStatus.OK);
    }

    @DeleteMapping("canteen/food-time/{id}")
    public void deleteFoodTime(@PathVariable long id) {
        foodTimeService.deleteFoodTime(id);
    }

    @GetMapping("canteen/food-time-current")
    public ResponseEntity<FoodTimeMasterAdv> getCurrentFoodTime() throws ParseException {
        FoodTimeMasterAdv foodTimeMastersAdv = getCurrentFoodTiming();
        return new ResponseEntity(foodTimeMastersAdv, HttpStatus.OK);
    }

    private FoodTimeMasterAdv getCurrentFoodTiming() throws ParseException {
        Date today = new Date();
        Time currentTime = getTimeFromString(today);
        List<FoodTimeMasterAdv> foodTimeMasterAdv = foodTimeService.getFoodTimes();
        FoodTimeMasterAdv foodTimeMasterAdvDetails = new FoodTimeMasterAdv();
        for (FoodTimeMasterAdv foodTimeMasterAdvObj : foodTimeMasterAdv) {
            Time fromTime = getTimeFromString(foodTimeMasterAdvObj.getTimeFrom());
            Time toTime = getTimeFromString(foodTimeMasterAdvObj.getTimeTo());
            if ((fromTime.before(currentTime) == true) && (toTime.after(currentTime))) {
                foodTimeMasterAdvDetails = foodTimeMasterAdvObj;
            }
        }
        return foodTimeMasterAdvDetails;
    }

    public Time getTimeFromString(Date dateValue) throws ParseException {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = timeFormat.format(dateValue);
        long formatedTime = timeFormat.parse(formattedDate).getTime();
        Time newTime = new Time(formatedTime);
        return newTime;
    }
}