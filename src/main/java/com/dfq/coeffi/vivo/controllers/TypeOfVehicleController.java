package com.dfq.coeffi.vivo.controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.vivo.entity.TypeOfVehicle;
import com.dfq.coeffi.vivo.entity.TypeOfVehicleDto;
import com.dfq.coeffi.vivo.service.TypeOfVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TypeOfVehicleController extends BaseController {
    @Autowired
    TypeOfVehicleService typeOfVehicleService;

    @PostMapping("type-of-vehicle")
    public ResponseEntity<List<TypeOfVehicle>> saveVehicle(@RequestBody List<TypeOfVehicle> typeOfVehicleList) {
        for (TypeOfVehicle vehicle : typeOfVehicleList) {
            vehicle.setStatus(true);
            TypeOfVehicle vehicle1 = typeOfVehicleService.saveVehicle(vehicle);
        }
        if (typeOfVehicleList.isEmpty()) {
            throw new EntityNotFoundException("No Vehicle Type");
        }
        return new ResponseEntity<>(typeOfVehicleList, HttpStatus.OK);
    }

    @PostMapping("type-of-vehicle-payment")
    public ResponseEntity<TypeOfVehicle> saveVehicle(@RequestBody TypeOfVehicle typeOfVehicle) {
        Optional<TypeOfVehicle> vehicle1 = typeOfVehicleService.getVehicleById(typeOfVehicle.getId());
        if (vehicle1.isPresent()) {
            TypeOfVehicle v=vehicle1.get();
            v.setRate(typeOfVehicle.getRate());
            v.setCurrency(typeOfVehicle.getCurrency());
            typeOfVehicleService.saveVehicle(vehicle1.get());
        }

        return new ResponseEntity<>(vehicle1.get(), HttpStatus.OK);
    }

    @GetMapping("type-of-vehicles")
    public ResponseEntity<List<TypeOfVehicle>> getAllVehicles() {
        List<TypeOfVehicle> vehicle = typeOfVehicleService.getAllVehicles();
        List<TypeOfVehicle> vehicleList = new ArrayList<>();
        for (TypeOfVehicle typeOfVehicle : vehicle) {
            if (typeOfVehicle.isStatus()) {
                vehicleList.add(typeOfVehicle);
            }
        }

        return new ResponseEntity<>(vehicleList, HttpStatus.OK);
    }

    @GetMapping("type-of-vehicle/{id}")
    public ResponseEntity<Optional<TypeOfVehicle>> getVehicleById(@PathVariable long id) {
        Optional<TypeOfVehicle> vehicle3 = typeOfVehicleService.getVehicleById(id);
        return new ResponseEntity<>(vehicle3, HttpStatus.OK);
    }

    @GetMapping("typeofvehicle-delete/{id}")
    public void deleteVehicleByid(@PathVariable long id) {
        Optional<TypeOfVehicle> typeOfVehicle = typeOfVehicleService.getVehicleById(id);
        typeOfVehicle.get().setStatus(false);
        typeOfVehicleService.saveVehicle(typeOfVehicle.get());
    }

    @PostMapping("report/typeofvehicle")
    public ResponseEntity<List<TypeOfVehicle>> getVehicleTypeByFilter(@RequestBody TypeOfVehicleDto typeOfVehicleDto) {
        List<TypeOfVehicle> vt = typeOfVehicleService.getAllVehicles();
        List<TypeOfVehicle> vtl = new ArrayList<>();
        for (TypeOfVehicle v : vt) {
            if (typeOfVehicleDto.getTypeOfVehicle().equals(v.getTypeOfVehicle())) {
                vtl.add(v);
            }
        }
        return new ResponseEntity<>(vtl, HttpStatus.OK);
    }


}
