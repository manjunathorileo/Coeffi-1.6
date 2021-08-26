package com.dfq.coeffi.visitor.Controllers;


import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.visitor.Entities.VisitorDocument;
import com.dfq.coeffi.visitor.Services.VisitorImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@RestController
@EnableAutoConfiguration
@Slf4j
public class VisitorImageController extends BaseController {

    @Autowired
    private final VisitorImageService visitorImageService;

    public VisitorImageController(VisitorImageService visitorImageService) {
        this.visitorImageService = visitorImageService;
    }

    @PostMapping("visitor/visitor-image-upload")
    public ResponseEntity<VisitorDocument> saveVisitorDoc(@RequestParam("file") MultipartFile file)
    {
        VisitorDocument visitorDocument =visitorImageService.saveImage(file);
        if(visitorDocument==null) {
            throw new EntityNotFoundException("No image");
        }
        return new ResponseEntity<>(visitorDocument, HttpStatus.OK);
    }
}
