package com.dfq.coeffi.SOPDetails.SOPType;

import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class SopTypeController extends BaseController {
    @Autowired
    SopTypeService sopTypeService;

    @PostMapping("save-sop-type")
    public ResponseEntity<SopType> saveSop(@RequestBody SopType sopType) {
        Date today = new Date();
        sopType.setCreatedOn(today);
        sopType.setStatus(true);
        SopType sopTypeObj = sopTypeService.saveSop(sopType);
        return new ResponseEntity<>(sopTypeObj, HttpStatus.CREATED);
    }

    @GetMapping("get-sop-type")
    public ResponseEntity<List<SopType>> getAllSop() {
        List<SopType> sopTypeList = sopTypeService.getSopList();
        if (sopTypeList.isEmpty()) {
            throw new EntityNotFoundException("There is No SOp Type.");
        }
        return new ResponseEntity<>(sopTypeList, HttpStatus.OK);
    }

    @GetMapping("/sop-type/{id}")
    public ResponseEntity<SopType> getSop(@PathVariable long id) {
        Optional<SopType> sopType = sopTypeService.getSopTypeById(id);
        return new ResponseEntity(sopType, HttpStatus.OK);
    }

    @DeleteMapping("/sop-type/{id}")
    public ResponseEntity<SopType> deleteSopType(@PathVariable long id) {
        SopType sopType = sopTypeService.deleteSopType(id);
        return new ResponseEntity(sopType, HttpStatus.OK);
    }

}
