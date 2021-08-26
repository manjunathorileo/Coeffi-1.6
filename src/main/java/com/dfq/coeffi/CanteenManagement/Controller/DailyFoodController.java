package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.*;
import com.dfq.coeffi.CanteenManagement.Service.*;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.util.DateUtil;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class DailyFoodController extends BaseController {

    private final DailyFoodMenuService dailyFoodMenuService;
    private final FoodTimeService foodTimeService;
    private final FoodMasterService foodMasterService;
    private final CounterDetailsService counterDetailsService;
    private final BuildingDetailsService buildingDetailsService;

    @Autowired
    public DailyFoodController(DailyFoodMenuService dailyFoodMenuService, FoodTimeService foodTimeService, FoodMasterService foodMasterService, CounterDetailsService counterDetailsService, BuildingDetailsService buildingDetailsService) {
        this.dailyFoodMenuService = dailyFoodMenuService;
        this.foodTimeService = foodTimeService;
        this.foodMasterService = foodMasterService;
        this.counterDetailsService = counterDetailsService;
        this.buildingDetailsService = buildingDetailsService;
    }

    @PostMapping("canteen/dailyfoodmenu")
    public ResponseEntity<DailyFoodMenu> saveConfiguration(@RequestBody DailyFoodMenu dailyFoodMenu) {
        Date toDay = new Date();
        dailyFoodMenu.setCreatedDate(toDay);
        FoodTimeMasterAdv foodTimeMasterAdv = foodTimeService.getFoodTime(dailyFoodMenu.getFoodType().getId());
        BuildingDetails buildingDetails = buildingDetailsService.getBuildingDetails(dailyFoodMenu.getBuildingDetails().getId());
        List<CounterDetailsAdv> counterDetailsAdvs = counterDetailsService.getCounterDetailsAdvByBuilding(buildingDetails);

        List<DailyFoodMenu> dailyFoodMenuList = dailyFoodMenuService.getDailyFoodMenuByFoodType(foodTimeMasterAdv);
        for (DailyFoodMenu dailyFoodMenuObj : dailyFoodMenuList) {
            if (dailyFoodMenuObj.getId() != dailyFoodMenu.getId()) {
                if (dailyFoodMenuObj.getEffectiveFrom().equals(dailyFoodMenu.getEffectiveFrom()) && dailyFoodMenuObj.getEffictiveTo().equals(dailyFoodMenu.getEffictiveTo())) {
                    if (dailyFoodMenuObj.getWeekNo() == dailyFoodMenu.getWeekNo() && dailyFoodMenuObj.getDayName().equals(dailyFoodMenu.getDayName())) {
                        if (dailyFoodMenu.getCounterDetailsAdv().getId() == 0) {
                            for (CounterDetailsAdv counterDetailsAdv : counterDetailsAdvs) {
                                if (dailyFoodMenuObj.getCounterDetailsAdv().getId() == counterDetailsAdv.getId()) {
                                    throw new EntityNotFoundException("Menu already present for this counter.");
                                }
                            }
                        } else {
                            if (dailyFoodMenuObj.getCounterDetailsAdv().getId() == dailyFoodMenu.getCounterDetailsAdv().getId()) {
                                throw new EntityNotFoundException("Menu already present for this counter.");
                            }
                        }
                    }
                }
            }
        }
        List<DailyFoodMenu> dailyFoodMenuObj = new ArrayList<>();
        if (dailyFoodMenu.getCounterDetailsAdv().getId() == 0) {
            for (CounterDetailsAdv counterDetailsAdv : counterDetailsAdvs) {
                DailyFoodMenu dailyFoodMenu1 = new DailyFoodMenu();
                dailyFoodMenu1.setEffectiveFrom(dailyFoodMenu.getEffectiveFrom());
                dailyFoodMenu1.setEffictiveTo(dailyFoodMenu.getEffictiveTo());
                dailyFoodMenu1.setCreatedDate(dailyFoodMenu.getCreatedDate());
                dailyFoodMenu1.setBuildingDetails(dailyFoodMenu.getBuildingDetails());
                dailyFoodMenu1.setCounterDetailsAdv(counterDetailsAdv);
                dailyFoodMenu1.setFoodType(dailyFoodMenu.getFoodType());
                dailyFoodMenu1.setIsDayWise(dailyFoodMenu.getIsDayWise());
                dailyFoodMenu1.setWeekNo(dailyFoodMenu.getWeekNo());
                dailyFoodMenu1.setDayName(dailyFoodMenu.getDayName());
                dailyFoodMenu1.setFoodList(dailyFoodMenu.getFoodList());
                dailyFoodMenuObj.add(dailyFoodMenuService.saveDailyFood(dailyFoodMenu1));
            }
        } else {
            dailyFoodMenuObj.add(dailyFoodMenuService.saveDailyFood(dailyFoodMenu));
        }
        return new ResponseEntity(dailyFoodMenuObj, HttpStatus.CREATED);
    }

    @GetMapping("canteen/dailyfoodmenu")
    public ResponseEntity<List<DailyFoodMenu>> getAllFoodMenu() {
        List<DailyFoodMenu> dailyFoodMenuList = dailyFoodMenuService.getDailyFood();
        if (dailyFoodMenuList.isEmpty()) {
            throw new EntityNotFoundException("There is no food menu present");
        }
        return new ResponseEntity<>(dailyFoodMenuList, HttpStatus.OK);
    }

    @GetMapping("canteen/dailyfoodmenu/{id}")
    public ResponseEntity<List<DailyFoodMenu>> getDailyFoodMenu(@PathVariable long id) {
        DailyFoodMenu dailyFoodMenu = dailyFoodMenuService.getDailyFoodMenu(id);
        return new ResponseEntity(dailyFoodMenu, HttpStatus.OK);
    }

    @GetMapping("canteen/dailyfoodmenu-by-counter/{counterId}")
    public ResponseEntity<List<DailyFoodMenu>> getDailyFoodMenuByCounter(@PathVariable long counterId) {
        CounterDetailsAdv counterDetailsAdv = counterDetailsService.getCounterDetails(counterId);
        List<DailyFoodMenu> dailyFoodMenu = dailyFoodMenuService.getDailyFoodMenuByCounterDetails(counterDetailsAdv);
        if (dailyFoodMenu.isEmpty()) {
            throw new EntityNotFoundException("There is no food menu for this counter.");
        }
        return new ResponseEntity(dailyFoodMenu, HttpStatus.OK);
    }

    @GetMapping("canteen/dailyfoodmenu-by-counter-by-foodtype/{counterId}/{foodTypeId}")
    public ResponseEntity<List<DailyFoodMenu>> getDailyFoodMenuByCounterByFoodType(@PathVariable long counterId, @PathVariable long foodTypeId) {
        CounterDetailsAdv counterDetailsAdv = counterDetailsService.getCounterDetails(counterId);
        FoodTimeMasterAdv foodTimeMasterAdv = foodTimeService.getFoodTime(foodTypeId);
        List<DailyFoodMenu> dailyFoodMenu = dailyFoodMenuService.getDailyFoodMenuByCounterByFoodType(counterDetailsAdv, foodTimeMasterAdv);
        if (dailyFoodMenu.isEmpty()) {
            throw new EntityNotFoundException("There is no food menu for this counter.");
        }
        return new ResponseEntity(dailyFoodMenu, HttpStatus.OK);
    }

    @GetMapping("canteen/dailyfoodmenu-by-foodtype/{foodTypeId}")
    public ResponseEntity<List<DailyFoodMenu>> getDailyFoodMenuByFoodType(@PathVariable long foodTypeId) {
        FoodTimeMasterAdv foodTimeMasterAdv = foodTimeService.getFoodTime(foodTypeId);
        List<DailyFoodMenu> dailyFoodMenu = dailyFoodMenuService.getDailyFoodMenuByFoodType(foodTimeMasterAdv);
        if (dailyFoodMenu.isEmpty()) {
            throw new EntityNotFoundException("There is no food menu for this food type.");
        }
        return new ResponseEntity(dailyFoodMenu, HttpStatus.OK);
    }

    @GetMapping("canteen/dailyfoodmenu-today")
    public ResponseEntity<List<DailyFoodMenu>> getTodaysDailyFoodMenu() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        Date todate = new Date();
        Date today = DateUtil.mySqlFormatDate(todate);
        Calendar calendarObj = Calendar.getInstance();
        int week = calendarObj.get(Calendar.WEEK_OF_MONTH);
        String day = formatter.format(todate);
        FoodTimeMasterAdv foodTimeMasterAdv = getCurrentFoodTiming();
        List<DailyFoodMenu> dailyFoodMenu = new ArrayList<>();
        List<DailyFoodMenu> dailyFoodMenuList = dailyFoodMenuService.getTodaysDailyFoodMenuByFoodtype(today, foodTimeMasterAdv.getId());
        if (dailyFoodMenuList.isEmpty()) {
            throw new EntityNotFoundException("There is no food menu for today");
        }
        for (DailyFoodMenu dailyFoodMenuObj : dailyFoodMenuList) {
            if (dailyFoodMenuObj.getWeekNo() == week && dailyFoodMenuObj.getDayName().equals(day)) {
                dailyFoodMenu.add(dailyFoodMenuObj);
            }
        }
        /*if (dailyFoodMenu.size() == 0) {
            dailyFoodMenu.dailyFoodMenuList.get(0);
        }*/
        return new ResponseEntity(dailyFoodMenu, HttpStatus.OK);
    }

    @GetMapping("canteen/dailyfoodmenu-today/{counterId}")
    public ResponseEntity<List<DailyFoodMenu>> getTodaysDailyFoodMenu(@PathVariable long counterId) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        Date todate = new Date();
        Date today = DateUtil.mySqlFormatDate(todate);
        Calendar calendarObj = Calendar.getInstance();
        int week = calendarObj.get(Calendar.WEEK_OF_MONTH);
        String day = formatter.format(todate);
        FoodTimeMasterAdv foodTimeMasterAdv = getCurrentFoodTiming();
        DailyFoodMenu dailyFoodMenu = new DailyFoodMenu();
        List<DailyFoodMenu> dailyFoodMenuList = dailyFoodMenuService.getTodaysDailyFoodMenu(today, foodTimeMasterAdv.getId(), counterId);
        if (dailyFoodMenuList.isEmpty()) {
            throw new EntityNotFoundException("There is no food menu for today");
        }
        for (DailyFoodMenu dailyFoodMenuObj : dailyFoodMenuList) {
            if (dailyFoodMenuObj.getWeekNo() == week && dailyFoodMenuObj.getDayName().equals(day)) {
                dailyFoodMenu = dailyFoodMenuObj;
            }
        }
        if (dailyFoodMenu.getId() == 0) {
            dailyFoodMenu = dailyFoodMenuList.get(0);
        }
        return new ResponseEntity(dailyFoodMenu, HttpStatus.OK);
    }

    @DeleteMapping("canteen/dailyfoodmenu/{id}")
    public void deleteMenu(@PathVariable("id") long id) {
        dailyFoodMenuService.deleteDailyFoodMenu(id);
    }

    @GetMapping("canteen/dailyfoodmenu-foodlist-edit-by-foodtype/{id}/{foodTypeId}")
    public ResponseEntity<List<DailyFoodMenu>> getTodaysDailyFoodMenu(@PathVariable long id, @PathVariable long foodTypeId) {
        FoodTimeMasterAdv foodTimeMaster = foodTimeService.getFoodTime(foodTypeId);
        List<FoodMaster> allFoodList = foodMasterService.getFoodMasterByFoodType(foodTimeMaster);
        DailyFoodMenu dailyFoodMenus = dailyFoodMenuService.getDailyFoodMenu(id);
        List<FoodMaster> selectedFoodList = dailyFoodMenus.getFoodList();
        List<FoodListDto> foodListDtos = new ArrayList<>();
        for (FoodMaster foodMasterObj : allFoodList) {
            int isChecked = 0;
            FoodListDto foodListDto = new FoodListDto();
            foodListDto.setId(foodMasterObj.getId());
            foodListDto.setFoodName(foodMasterObj.getFoodName());
            foodListDto.setFoodCode(foodMasterObj.getFoodCode());
            foodListDto.setUnit(foodMasterObj.getUnit());
            for (FoodMaster foodMaster : selectedFoodList) {
                if (foodMasterObj.getId() == foodMaster.getId()) {
                    isChecked = 1;
                }
            }
            if (isChecked == 1) {
                foodListDto.setIsSelected(true);
            } else {
                foodListDto.setIsSelected(false);
            }
            foodListDtos.add(foodListDto);
        }
        return new ResponseEntity(foodListDtos, HttpStatus.OK);
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