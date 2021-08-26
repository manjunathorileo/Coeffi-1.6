package com.dfq.coeffi.CompanySettings.Controller;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Entity.Location;
import com.dfq.coeffi.CompanySettings.Repository.LocationRepository;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.CompanySettings.Service.LocationService;
import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompanyNameController extends BaseController {

    @Autowired
    CompanyNameService companyNameService;
    @Autowired
    LocationService locationService;
    @Autowired
    LocationRepository locationRepository;

    @PostMapping("save-company")
    ResponseEntity<CompanyName> saveCompany(@RequestBody CompanyName companyName){
        companyNameService.saveCompany(companyName);
        return new ResponseEntity<>(companyName, HttpStatus.CREATED);
    }

    @PostMapping("save-location")
    ResponseEntity<CompanyName> saveCompany(@RequestBody CompanyDto companyDto){
        CompanyName companyName = companyNameService.getCompanyById(companyDto.getCompanyId());
        locationRepository.save(companyDto.getLocations());
        companyName.setLocations(companyDto.getLocations());
        companyNameService.saveCompany(companyName);
        return new ResponseEntity<>(companyName, HttpStatus.CREATED);
    }

    @GetMapping("get-companys")
    ResponseEntity<List<CompanyName>> getCompany(){
        List<CompanyName> companyNameList=companyNameService.getCompany();

        return new ResponseEntity<>(companyNameList,HttpStatus.OK);
    }
    @GetMapping("get-company-id/{id}")
    ResponseEntity<CompanyName> getCompanyById(@PathVariable("id") long id){
       CompanyName companyName=companyNameService.getCompanyById(id);

       return new ResponseEntity<>(companyName,HttpStatus.OK);
    }

    @DeleteMapping("delete-company/{id}")
    void deleteCompany(@PathVariable("id") long id){
        companyNameService.deleteCompany(id);
    }


}
