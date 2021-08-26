package com.dfq.coeffi.FeedBackManagement.Controller;

import com.dfq.coeffi.CanteenManagement.Entity.FoodImage;
import com.dfq.coeffi.FeedBackManagement.Entity.GradeImage;
import com.dfq.coeffi.FeedBackManagement.Services.GradeImageService;
import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@RestController
public class GradeImageController extends BaseController {

    @Autowired
    GradeImageService gradeImageService;

    @PostMapping("feedback/gradeImage")
    public ResponseEntity<GradeImage> saveWord(@RequestParam("file") MultipartFile file) {
        GradeImage document1 = gradeImageService .saveGradeFile(file) ;
        if(document1 ==null)
        {
            throw new EntityNotFoundException("**********");
        }
        return new ResponseEntity<>(document1, HttpStatus.ACCEPTED);
    }

    @GetMapping("feedback/gradeImage/{fileId}")
    public ResponseEntity<GradeImage> getMenuFileId(@PathVariable("fileId") long fileId){
        GradeImage gradeImage = gradeImageService .getByGradeFileId(fileId) ;
        return new ResponseEntity<>(gradeImage,HttpStatus.OK);
    }
}
