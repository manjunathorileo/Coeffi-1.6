package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.CounterDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Service.CounterDetailsService;
import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class CounterDetailsController extends BaseController {

    @Autowired
    private CounterDetailsService counterDetailsService;

    @PostMapping("canteen/counter")
    public ResponseEntity<CounterDetailsAdv> saveCounterDetails(@RequestBody CounterDetailsAdv counterDetailsAdv) {
        //if (counterDetailsAdv.getId()<1 ) {
            List<CounterDetailsAdv> counterDetailsAdvList = counterDetailsService.getAllCounterDetails();
            for (CounterDetailsAdv counterDetailsAdvObj : counterDetailsAdvList) {
                if(counterDetailsAdvObj.getId() != counterDetailsAdv.getId()) {
                    if (counterDetailsAdvObj.getCounterNo() == counterDetailsAdv.getCounterNo()) {
                        throw new EntityNotFoundException("This counter number already exist");
                    }
                }
            }
        //}
        CounterDetailsAdv counterDetailsobjAdv = counterDetailsService.createCounterDetails(counterDetailsAdv);
        return new ResponseEntity<>(counterDetailsobjAdv, HttpStatus.OK);
    }

    @GetMapping("canteen/counter")
    public ResponseEntity<CounterDetailsAdv> getAllCounterDetails() {
        List<CounterDetailsAdv> counterDetailsobjAdv = counterDetailsService.getAllCounterDetails();
        if (counterDetailsobjAdv.isEmpty()){
            throw new EntityNotFoundException("There is no counter");
        }
        return new ResponseEntity(counterDetailsobjAdv, HttpStatus.OK);
    }

    @GetMapping("canteen/counter/{id}")
    public ResponseEntity<CounterDetailsAdv> getCounterDetails(@PathVariable long id) {
        CounterDetailsAdv counterDetailsobjAdv = counterDetailsService.getCounterDetails(id);
        return new ResponseEntity(counterDetailsobjAdv, HttpStatus.OK);
    }

    @DeleteMapping("canteen/counter/{id}")
    public ResponseEntity<CounterDetailsAdv> deleteCounterDetails(@PathVariable long id) {
        CounterDetailsAdv counterDetailsobjAdv = counterDetailsService.deleteCounterDetails(id);
        return new ResponseEntity(counterDetailsobjAdv, HttpStatus.OK);
    }

    @GetMapping("canteen/counter-by-building/{buildingId}")
    public ResponseEntity<CounterDetailsAdv> getCounterDetailsByBuilding(@PathVariable long buildingId) {
        List<CounterDetailsAdv> counterDetailsAdvs = new ArrayList<>();
        List<CounterDetailsAdv> counterDetailsobjAdv = counterDetailsService.getAllCounterDetails();
        for (CounterDetailsAdv counterDetailsAdvObj:counterDetailsobjAdv) {
            if (counterDetailsAdvObj.getBuildingDetails().getId() == buildingId){
                counterDetailsAdvs.add(counterDetailsAdvObj);
            }
        }
        if (counterDetailsAdvs.isEmpty()){
            throw new EntityNotFoundException("There is no counter for this building.");
        }
        return new ResponseEntity(counterDetailsAdvs, HttpStatus.OK);
    }
}