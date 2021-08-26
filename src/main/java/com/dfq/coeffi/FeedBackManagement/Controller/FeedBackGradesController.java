package com.dfq.coeffi.FeedBackManagement.Controller;


import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackGrades;
import com.dfq.coeffi.FeedBackManagement.Entity.GradeImage;
import com.dfq.coeffi.FeedBackManagement.Repository.FeedBackGradesRepository;
import com.dfq.coeffi.FeedBackManagement.Services.GradeImageService;
import com.dfq.coeffi.controller.BaseController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Parameter;
import java.util.List;

@RestController
@Slf4j
public class FeedBackGradesController extends BaseController {

    @Autowired
    FeedBackGradesRepository feedBackGradesRepository;

    @Autowired
    GradeImageService gradeImageService;

    @PostMapping("save-feedback-grades")
    public ResponseEntity<FeedBackGrades> saveGrades(@RequestBody FeedBackGrades feedBackGrades){
        List<FeedBackGrades> feedBackGradesList=feedBackGradesRepository.findAll();
        for (FeedBackGrades feed:feedBackGradesList) {
            if(feedBackGrades.getId()==0){
                if(feed.getGradingName().equals(feedBackGrades.getGradingName()) ){
                    throw new EntityNotFoundException("Already Grading Name Exits");
                }
            }

        }
        if(feedBackGrades.getId()!=0){
            GradeImage gradeImage=gradeImageService.getByGradeFileId(feedBackGrades.getFileId());
            feedBackGrades.setGradeImages(gradeImage);
            feedBackGradesRepository.save(feedBackGrades);
        }
        else{
            GradeImage gradeImage=gradeImageService.getByGradeFileId(feedBackGrades.getFileId());
            feedBackGrades.setGradeImages(gradeImage);
            feedBackGradesRepository.save(feedBackGrades);
        }

        return new ResponseEntity<>(feedBackGrades , HttpStatus.CREATED);
    }

    @GetMapping("get-feedback-grade")
    public ResponseEntity<List<FeedBackGrades>> saveFeedBack(){
        List<FeedBackGrades> feedBackGradesList=feedBackGradesRepository.findAll();

        return new ResponseEntity<>(feedBackGradesList,HttpStatus.OK);
    }

    @DeleteMapping("delete-feedback-grade/{id}")
    public void deleteFeedBack(@PathVariable("id") long id){
        feedBackGradesRepository.delete(id);
    }
}
