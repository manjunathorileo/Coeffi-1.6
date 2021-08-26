package com.dfq.coeffi.CanteenManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.FoodImage;
import com.dfq.coeffi.CanteenManagement.Service.FoodImageService;
import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@RestController
@Slf4j
public class FoodImageController extends BaseController {

    @Autowired
    FoodImageService foodImageService;

    @PostMapping("canteen/foodImage")
    public ResponseEntity<FoodImage> saveWord(@RequestParam("file") MultipartFile file) {
        FoodImage document1 = foodImageService.saveMenuFile(file);
        if(document1 ==null)
        {
            throw new EntityNotFoundException("**********");
        }
        return new ResponseEntity<>(document1, HttpStatus.ACCEPTED);
    }

    @GetMapping("canteen/foodImage/{fileId}")
    public ResponseEntity<FoodImage> getMenuFileId(@PathVariable("fileId") long fileId){
        FoodImage foodImage = foodImageService.getByMenuFileId(fileId);
        return new ResponseEntity<>(foodImage,HttpStatus.OK);
    }
}
