package com.dfq.coeffi.FeedBackManagement.Controller;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.FeedBackManagement.Entity.FeedBackParameter;
import com.dfq.coeffi.FeedBackManagement.Repository.FeedBackParameterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
public class FeedBackParameterController extends BaseController {

    @Autowired
    FeedBackParameterRepository feedBackParameterRepository;

    @PostMapping("save-feedback-parameter")
    public ResponseEntity<FeedBackParameter> saveParameter(@RequestBody FeedBackParameter feedBackParameter){
        List<FeedBackParameter> feedBackParameterList=feedBackParameterRepository.findAll();
        for (FeedBackParameter feed:feedBackParameterList) {
            if(feedBackParameter.getId()==0){
                if(feed.getParameterName().equals(feedBackParameter.getParameterName()) ){
                    throw new EntityNotFoundException("Already Parameter Name Exits");
                }
            }

        }
        if(feedBackParameter.getId()!=0){
            feedBackParameterRepository.save(feedBackParameter);
        }
        else{
            feedBackParameterRepository.save(feedBackParameter);
        }


        return new ResponseEntity<>(feedBackParameter, HttpStatus.CREATED);
    }

    @GetMapping("get-feedback-parameter")
    public ResponseEntity<List<FeedBackParameter>> getParameter(){
        List<FeedBackParameter> feedBackParameterList=feedBackParameterRepository.findAll();

        return new ResponseEntity<>(feedBackParameterList,HttpStatus.OK);
    }

    @DeleteMapping("delete-feedback-parameter/{id}")
    public void deleteParameter(@PathVariable("id") long id){
        feedBackParameterRepository.delete(id);
    }
}
