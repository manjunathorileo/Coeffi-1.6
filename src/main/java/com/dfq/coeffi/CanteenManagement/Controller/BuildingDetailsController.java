package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.BuildingDetails;
import com.dfq.coeffi.CanteenManagement.Service.BuildingDetailsService;
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
public class BuildingDetailsController extends BaseController {

    @Autowired
    private BuildingDetailsService buildingDetailsService;

    @PostMapping("canteen/building")
    public ResponseEntity<BuildingDetails> saveBuildingDetails(@RequestBody BuildingDetails buildingDetails) {
        List<BuildingDetails> buildingDetailsList = buildingDetailsService.getAllBuildingDetails();
        BuildingDetails buildingDetailobj =new BuildingDetails();
        for (BuildingDetails buildingDetailsObj1:buildingDetailsList) {
            if(buildingDetails.getId()!=buildingDetailsObj1.getId()) {
                if (buildingDetailsObj1.getBuildingName().equals(buildingDetails.getBuildingName())) {
                    throw new EntityNotFoundException("This Building name already exist.");
                }
            }
        }
        if(buildingDetails.getId()!=0){
            buildingDetailobj = buildingDetailsService.createBuildingDetails(buildingDetails);
        }
        else{
            buildingDetailobj = buildingDetailsService.createBuildingDetails(buildingDetails);
        }

        return new ResponseEntity<>(buildingDetailobj, HttpStatus.OK);
    }

    @GetMapping("canteen/building")
    public ResponseEntity<BuildingDetails> getAllBuildingDetails() {
        List<BuildingDetails> buildingDetails = buildingDetailsService.getAllBuildingDetails();
        if (buildingDetails.isEmpty()){
            throw new EntityNotFoundException("There is no building details");
        }
        return new ResponseEntity(buildingDetails, HttpStatus.OK);
    }

    @GetMapping("canteen/building/{id}")
    public ResponseEntity<BuildingDetails> getBuildingDetails(@PathVariable long id) {
        BuildingDetails buildingDetailobj = buildingDetailsService.getBuildingDetails(id);
        return new ResponseEntity(buildingDetailobj, HttpStatus.OK);
    }

    @DeleteMapping("canteen/building/{id}")
    public ResponseEntity<BuildingDetails> deleteBuildingDetails(@PathVariable long id) {
        BuildingDetails buildingDetailobj = buildingDetailsService.deleteBuildingDetails(id);
        return new ResponseEntity(buildingDetailobj, HttpStatus.OK);
    }
}
