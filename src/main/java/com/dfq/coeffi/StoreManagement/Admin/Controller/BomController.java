package com.dfq.coeffi.StoreManagement.Admin.Controller;

import com.dfq.coeffi.StoreManagement.Admin.Entity.Bom;
import com.dfq.coeffi.StoreManagement.Admin.Entity.BomDto;
import com.dfq.coeffi.StoreManagement.Admin.Service.BomItemsService;
import com.dfq.coeffi.StoreManagement.Admin.Service.BomService;
import com.dfq.coeffi.StoreManagement.Entity.*;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class BomController extends BaseController {

    @Autowired
    BomService bomService;

    @Autowired
    BomItemsService bomItemsService;

    @PostMapping("save-admin-bom")
    public ResponseEntity<Bom> saveBom(@RequestBody BomDto bomDto) throws Exception {
        Date date=new Date();
        Bom bom=new Bom();
        bom.setBomName(bomDto.getBomName());
        bom.setBomNumber(bomDto.getBomNumber());
        bom.setCreationDate(date);
        bom.setBomItemsList(bomDto.getBomItemsList());
        bomService.saveBom(bom);

        return new ResponseEntity<>(bom, HttpStatus.CREATED);
    }

    @GetMapping("get-bom-list")
    public ResponseEntity<List<Bom>> getBomList(){
        List<Bom> bomList=bomService.getBom();

        return new ResponseEntity<>(bomList,HttpStatus.OK);
    }

    @DeleteMapping("delete-bom/{id}")
    void deleteBom(@PathVariable("id")long id){
        bomItemsService.deleteBomItems(id);
    }

    @GetMapping("bom-items/{id}")
    public ResponseEntity<Bom> getBomItems(@PathVariable("id") long id){
        Bom bom=bomService.getBomById(id);

        return new ResponseEntity<>(bom,HttpStatus.OK);
    }

    @GetMapping("bom-item-names")
    public ResponseEntity<List<BomDto>> getBomItemNames(){
        List<Bom> bomList=bomService.getBom();
        List<BomDto> bomDtoList=new ArrayList<>();
        for (Bom b:bomList) {
            BomDto bom=new BomDto();
            bom.setBomNumber(b.getBomNumber());
            bom.setBomName(b.getBomName());
            bomDtoList.add(bom);
        }
        return new ResponseEntity<>(bomDtoList,HttpStatus.OK);
    }
}
