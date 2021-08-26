package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.visitor.Entities.VisitorCompany;
import com.dfq.coeffi.visitor.Repositories.VisitorCompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class VisitorCompanyController extends BaseController {
    @Autowired
    private VisitorCompanyRepository visitorCompanyRepository;

    @PostMapping("visitor-company")
    public ResponseEntity<VisitorCompany> save(@RequestBody VisitorCompany visitorCompany) {
        visitorCompany.setAllowedOrDenied(true);
        visitorCompanyRepository.save(visitorCompany);
        return new ResponseEntity<>(visitorCompany, HttpStatus.OK);
    }

    @PostMapping("visitor-company/{id}")
    public ResponseEntity<VisitorCompany> allowOrDenyCompany(@PathVariable long id) {
        VisitorCompany visitorCompany = visitorCompanyRepository.findOne(id);
        if (visitorCompany.isAllowedOrDenied()) {
            visitorCompany.setAllowedOrDenied(false);
            visitorCompanyRepository.save(visitorCompany);
        } else if (visitorCompany.isAllowedOrDenied() == false) {
            visitorCompany.setAllowedOrDenied(true);
            visitorCompanyRepository.save(visitorCompany);

        }
        return new ResponseEntity<>(visitorCompany, HttpStatus.OK);
    }

    @DeleteMapping("visitor-company/{id}")
    public void deleteCompany(@PathVariable long id) {
        visitorCompanyRepository.delete(id);
    }

    @GetMapping("visitor-companies")
    public ResponseEntity<List<VisitorCompany>> getAllCompanies() {
        List<VisitorCompany> visitorCompanies = visitorCompanyRepository.findAll();
        return new ResponseEntity<>(visitorCompanies, HttpStatus.OK);
    }

    @PostMapping("visitor-company/payment-toggle/{id}/{status}")
    public ResponseEntity<VisitorCompany> checkBoxPayment(@PathVariable long id, @PathVariable boolean status) {
        VisitorCompany visitorCompany = visitorCompanyRepository.findOne(id);
        visitorCompany.setPaymentApplicable(status);
        visitorCompanyRepository.save(visitorCompany);
        return new ResponseEntity<>(visitorCompany, HttpStatus.OK);
    }
}
