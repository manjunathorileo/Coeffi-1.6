package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.CatererDetailsAdv;
import com.dfq.coeffi.CanteenManagement.Service.CatererDetailsService;
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
public class CatererDetailController extends BaseController {

    @Autowired
    CatererDetailsService catererDetailsService;

    @PostMapping("canteen/caterer-detail")
    public void saveCatererDetails(@RequestBody CatererDetailsAdv catererDetailsAdv) {
        catererDetailsService.createCatererDetails(catererDetailsAdv);
    }

    @GetMapping("canteen/caterer-detail")
    public ResponseEntity<List<CatererDetailsAdv>> getCatererDetails() {
        List<CatererDetailsAdv> catererDetailsAdvList = catererDetailsService.getCatererDetails();
        if (catererDetailsAdvList.isEmpty()){
            throw new EntityNotFoundException("There is no cantere details");
        }
        return new ResponseEntity<>(catererDetailsAdvList, HttpStatus.OK);
    }

    @DeleteMapping("canteen/caterer-detail/{id}")
    public void deletecat(@PathVariable long id) {
        catererDetailsService.deleteCatererDetail(id);
    }
}