package com.dfq.coeffi.aboutCoeffi;

import com.dfq.coeffi.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AboutController extends BaseController {

    @Autowired
    AboutService aboutService;

    @PostMapping("about/save")
    public void saveAbout(@RequestBody AboutDto about){
        aboutService.saveAbout(about);
    }

    @GetMapping("about")
    public ResponseEntity<AboutDto> getLatestAbout(){
        AboutDto aboutDto = aboutService.getLatestAbout();
        return new ResponseEntity<>(aboutDto, HttpStatus.OK);
    }






}
