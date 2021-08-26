package com.dfq.coeffi.Oqc;

import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class MaterialInwardController extends BaseController {

    @Autowired
    MaterialInwardRepository materialInwardRepository;

    @PostMapping("save-material-inward")
    public ResponseEntity<MaterialInward> saveMaterialInward(@RequestBody MaterialInward materialInward){
        Date date=new Date();
        materialInward.setReceivedOn(date);
        materialInwardRepository.save(materialInward);

        return new ResponseEntity<>(materialInward, HttpStatus.OK);
    }

    @GetMapping("get-material-inward")
    public ResponseEntity<MaterialInward> getMaterialInward(){
        List<MaterialInward> materialInwardList=materialInwardRepository.findAll();

        return new ResponseEntity(materialInwardList,HttpStatus.OK);
    }
}
