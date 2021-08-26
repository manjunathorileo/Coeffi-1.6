package com.dfq.coeffi.CompanySettings.Controller;

import com.dfq.coeffi.CompanySettings.Entity.Location;
import com.dfq.coeffi.CompanySettings.Service.LocationService;
import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LocationController extends BaseController {

    @Autowired
    LocationService locationService;

//    @PostMapping("save-location")
    ResponseEntity<Location> saveLocation(@RequestBody Location location){
        locationService.saveLocation(location);
        return new ResponseEntity<>(location, HttpStatus.CREATED);
    }

    @GetMapping("get-location")
    ResponseEntity<List<Location>> getLocation(){
        List<Location> locationList=locationService.getLocation();
        return new ResponseEntity<>(locationList,HttpStatus.OK);
    }

    @GetMapping("get-location-id/{id}")
    ResponseEntity<Location> getLocationById(@PathVariable("id")long id){
        Location location=locationService.getLocationById(id);
        return new ResponseEntity<>(location,HttpStatus.OK);
    }

    @DeleteMapping("delete-location")
    void deleteCompany(@PathVariable("id")long id){
        locationService.delete(id);
    }
}
