package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.visitor.Entities.VisitorCompany;
import com.dfq.coeffi.visitor.Entities.VisitorSite;
import com.dfq.coeffi.visitor.Repositories.VisitorSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class VisitorSiteController extends BaseController {

    @Autowired
    VisitorSiteRepository visitorSiteRepository;

    @PostMapping("visitor-site")
    public ResponseEntity<VisitorSite> save(@RequestBody VisitorSite visitorSite) {
        visitorSiteRepository.save(visitorSite);
        return new ResponseEntity<>(visitorSite, HttpStatus.OK);
    }

    @GetMapping("visitor-sites")
    public ResponseEntity<List<VisitorSite>> getaAllSites() {
        List<VisitorSite> visitorSites = visitorSiteRepository.findAll();
        return new ResponseEntity<>(visitorSites, HttpStatus.OK);
    }

    @DeleteMapping("visitor-site/{id}")
    public void deleteSite(@PathVariable long id){
        visitorSiteRepository.delete(id);
    }


}
