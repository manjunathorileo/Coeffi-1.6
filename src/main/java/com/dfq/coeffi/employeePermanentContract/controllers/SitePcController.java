package com.dfq.coeffi.employeePermanentContract.controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.SitePc;
import com.dfq.coeffi.employeePermanentContract.repositories.SitePcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class SitePcController extends BaseController {
    @Autowired
    private SitePcRepository sitePcRepository;

    @PostMapping("sitePc")
    public ResponseEntity<SitePc> save(@RequestBody SitePc sitePc) {
        sitePcRepository.save(sitePc);
        return new ResponseEntity<>(sitePc, HttpStatus.CREATED);
    }

    @GetMapping("sitePcs")
    public ResponseEntity<List<SitePc>> getAllSites() {
        List<SitePc> sitePcs = sitePcRepository.findAll();
        return new ResponseEntity<>(sitePcs, HttpStatus.OK);
    }

    @GetMapping("sitePc/{id}")
    public ResponseEntity<SitePc> getById(@PathVariable long id) {
        SitePc sitePc = sitePcRepository.findOne(id);
        return new ResponseEntity<>(sitePc, HttpStatus.OK);
    }

    @DeleteMapping("sitePc/{id}")
    public void deleteSite(@PathVariable long id) {
        sitePcRepository.delete(id);
    }
}
