package com.dfq.coeffi.Gate.Controller;

import com.dfq.coeffi.Gate.Entity.Gate;
import com.dfq.coeffi.Gate.Service.GateService;
import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GateController extends BaseController {
    @Autowired
    GateService gateService;

    @PostMapping("create-gate")
    public ResponseEntity<Gate> createGate(@RequestBody Gate gate){
        Gate gate1=gateService.saveGate(gate);

        return new ResponseEntity<>(gate1, HttpStatus.CREATED );
    }
    @GetMapping("gate-list")
    public ResponseEntity<List<Gate>> getGates(){
        List<Gate> gateList=gateService.getGates();

        return new ResponseEntity<>(gateList,HttpStatus.OK);
    }
    @GetMapping("get-gate/{gid}")
    public ResponseEntity<Gate> getGate(@PathVariable("gid") long gid){
        Gate gate=gateService.getGateById(gid);

        return new ResponseEntity<>(gate,HttpStatus.OK);
    }
    @DeleteMapping("delete-gate/{gid}")
    void deleteGate(@PathVariable("gid") long gid){
        gateService.deleteGate(gid);
    }




}
