package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Entity.CatererSettingsAdv;
import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Service.BuildingDetailsService;
import com.dfq.coeffi.CanteenManagement.Service.CatererSettingsService;
import com.dfq.coeffi.CanteenManagement.Service.CounterDetailsService;
import com.dfq.coeffi.CanteenManagement.Service.FoodTimeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class CatererSettingController extends BaseController {

    private final CatererSettingsService catererSettingsService;
    private final FoodTimeService foodTimeService;
    private final CounterDetailsService counterDetailsService;
    private final BuildingDetailsService buildingDetailsService;

    @Autowired
    public CatererSettingController(CatererSettingsService catererSettingsService, FoodTimeService foodTimeService, CounterDetailsService counterDetailsService, BuildingDetailsService buildingDetailsService) {
        this.catererSettingsService = catererSettingsService;
        this.foodTimeService = foodTimeService;
        this.counterDetailsService = counterDetailsService;
        this.buildingDetailsService = buildingDetailsService;
    }

    @GetMapping("employee/employee-type")
    public ResponseEntity<List<EmployeeType>> getEmployeeType() {
        List<EmployeeType> employeeTypes = Arrays.asList(EmployeeType.values());
        return new ResponseEntity<>(employeeTypes, HttpStatus.OK);
    }

    @GetMapping("canteen/caterer-setting")
    public ResponseEntity<List<CatererSettingsAdv>> getCatererSettings() {
        List<CatererSettingsAdv> catererSettingAdvs = catererSettingsService.getCatererSettings();
        if (catererSettingAdvs.isEmpty()) {
            throw new EntityNotFoundException("There is setting details");
        }
        return new ResponseEntity<>(catererSettingAdvs, HttpStatus.OK);
    }

    /*@GetMapping("canteen/caterer-setting")
    public ResponseEntity<List<CatererSettingsAdv>> getCatererSettings() {
        List<FoodTimeMasterAdv> foodTimeMasterAdvs = foodTimeService.getFoodTimes();
        if (foodTimeMasterAdvs.isEmpty()) {
            throw new EntityNotFoundException("Create Time master first");
        }
        List<EmployeeType> employeeTypes = Arrays.asList(EmployeeType.values());
        for (FoodTimeMasterAdv foodTimeMasterAdv : foodTimeMasterAdvs) {
            for (EmployeeType employeeType:employeeTypes) {
                CatererSettingsAdv catererSettingsAdv = catererSettingsService.getCatererSetting(foodTimeMasterAdv.getFoodType(), employeeType.name());
                if (catererSettingsAdv == null) {
                    catererSettingsAdv = new CatererSettingsAdv();
                    catererSettingsAdv.setFoodType(foodTimeMasterAdv.getFoodType());
                    catererSettingsAdv.setEmployeeType(employeeType.name());
                    catererSettingsAdv.setEmployeeRate(0);
                    catererSettingsAdv.setEmployerRate(0);
                    catererSettingsAdv.setCatererTotal(0);
                    catererSettingsAdv.setFoodTimeMasterAdv(foodTimeMasterAdv);
                    catererSettingsService.createCatererSettings(catererSettingsAdv);
                }
            }
        }
        List<CatererSettingsAdv> catererSettingAdvs = catererSettingsService.getCatererSettings();
        return new ResponseEntity<>(catererSettingAdvs, HttpStatus.OK);
    }*/

    @PostMapping("canteen/caterer-setting")
    public ResponseEntity<CatererSettingsAdv> allocateRateforETypeAndFType(@RequestBody CatererSettingsAdv catererSettingsAdv) {
        List<CatererSettingsAdv> catererSettingsAdvList = catererSettingsService.getCatererSettings();
        BuildingDetails buildingDetails = buildingDetailsService.getBuildingDetails(catererSettingsAdv.getBuildingDetails().getId());
        List<CounterDetailsAdv> counterDetailsAdvs = counterDetailsService.getCounterDetailsAdvByBuilding(buildingDetails);
        for (CatererSettingsAdv catererSettingsAdvObj : catererSettingsAdvList) {
            if (catererSettingsAdvObj.getFoodTimeMasterAdv().getId() == catererSettingsAdv.getFoodTimeMasterAdv().getId()) {
                if (catererSettingsAdvObj.getId() != catererSettingsAdv.getId()) {
                    if (catererSettingsAdv.getCounterDetailsAdv().getId() == 0) {
                        for (CounterDetailsAdv counterDetailsAdv : counterDetailsAdvs) {
                            if (catererSettingsAdvObj.getEmployeeType().equals(catererSettingsAdv.getEmployeeType()) && catererSettingsAdvObj.getCounterDetailsAdv().getId() == counterDetailsAdv.getId()) {
                                throw new EntityNotFoundException("Already there is setting for this employee type in this counter.");
                            }
                        }
                    } else {
                        if (catererSettingsAdvObj.getEmployeeType().equals(catererSettingsAdv.getEmployeeType()) && catererSettingsAdvObj.getCounterDetailsAdv().getId() == catererSettingsAdv.getCounterDetailsAdv().getId()) {
                            throw new EntityNotFoundException("Already there is setting for this employee type in this counter.");
                        }
                    }
                }
            }
        }
        List<CatererSettingsAdv> catererSettingsAdvs = new ArrayList<>();
        if (catererSettingsAdv.getCounterDetailsAdv().getId() == 0) {
            for (CounterDetailsAdv counterDetailsAdv : counterDetailsAdvs) {
                CatererSettingsAdv catererSettingsAdv1 = new CatererSettingsAdv();
                catererSettingsAdv1.setEmployeeType(catererSettingsAdv.getEmployeeType());
                catererSettingsAdv1.setFoodTimeMasterAdv(catererSettingsAdv.getFoodTimeMasterAdv());
                catererSettingsAdv1.setBuildingDetails(buildingDetails);
                catererSettingsAdv1.setCounterDetailsAdv(counterDetailsAdv);
                catererSettingsAdv1.setFoodType(catererSettingsAdv.getFoodTimeMasterAdv().getFoodType());
                catererSettingsAdv1.setStatus(catererSettingsAdv.getStatus());
                catererSettingsAdv1.setEmployeeRate(catererSettingsAdv.getEmployeeRate());
                catererSettingsAdv1.setEmployerRate(catererSettingsAdv.getEmployerRate());
                catererSettingsAdv1.setCatererTotal(catererSettingsAdv.getCatererTotal());
                catererSettingsAdvs.add(catererSettingsService.createCatererSettings(catererSettingsAdv1));
            }
        } else {
            catererSettingsAdv.setFoodType(catererSettingsAdv.getFoodTimeMasterAdv().getFoodType());
            catererSettingsAdvs.add(catererSettingsService.createCatererSettings(catererSettingsAdv));
        }
        return new ResponseEntity<>(catererSettingsAdv, HttpStatus.OK);
    }

    @DeleteMapping("canteen/caterer-settings/{id}")
    public ResponseEntity<CatererSettingsAdv> getCatererSettingsDelete(@PathVariable long id) {
        CatererSettingsAdv catererSettingsAdv = catererSettingsService.deleteCatererSettings(id);
        return new ResponseEntity<>(catererSettingsAdv, HttpStatus.OK);
    }
}