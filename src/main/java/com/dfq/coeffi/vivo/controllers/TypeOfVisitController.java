package com.dfq.coeffi.vivo.controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.vivo.entity.TypeOfVisit;
import com.dfq.coeffi.vivo.service.TypeOfVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TypeOfVisitController extends BaseController {
    @Autowired
    TypeOfVisitService typeOfVisitService;

    @PostMapping("type-of-visit")
    public ResponseEntity<List<TypeOfVisit>> saveVisitor(@RequestBody List<TypeOfVisit> typeOfVisitList) {
        for (TypeOfVisit typeOfVisit : typeOfVisitList) {
            typeOfVisit.setStatus(true);
            TypeOfVisit visitor1 = typeOfVisitService.saveVisitor(typeOfVisit);
        }
        return new ResponseEntity<>(typeOfVisitList, HttpStatus.OK);
    }

    @GetMapping("type-of-visits")
    public ResponseEntity<List<TypeOfVisit>> getAllVisitors() {
        List<TypeOfVisit> visits = typeOfVisitService.getAllVisitors();
        List<TypeOfVisit> visitList = new ArrayList<>();
        for (TypeOfVisit visit1:visits){
            if(visit1.isStatus()){
                visitList.add(visit1);
            }
        }
        return new ResponseEntity<>(visitList, HttpStatus.OK);
    }

    @GetMapping("type-of-visit/get/{id}")
    public ResponseEntity<Optional<TypeOfVisit>> getVisitorsById(@PathVariable long id) {
        Optional<TypeOfVisit> visitor3 = typeOfVisitService.getVisitorsById(id);
        return new ResponseEntity<>(visitor3, HttpStatus.OK);
    }

    @GetMapping("type-of-visit/delete/{id}")
    public void deleteVisitorsByid(@PathVariable long id) {
        Optional<TypeOfVisit> typeOfVisit=typeOfVisitService.getVisitorsById(id);
        typeOfVisit.get().setStatus(false);
        typeOfVisitService.saveVisitor(typeOfVisit.get());
    }
}
