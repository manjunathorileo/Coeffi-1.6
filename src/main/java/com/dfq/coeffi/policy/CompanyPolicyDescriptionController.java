package com.dfq.coeffi.policy;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class CompanyPolicyDescriptionController extends BaseController {
    @Autowired
    private CompanyPolicyDescriptionService companyPolicyDescriptionService;

    @Autowired
    private CompanyPolicyService companyPolicyService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("company-policy-description")
    public ResponseEntity<List<CompanyPolicyDescription>> listAllCompanyPolicyDescription() {
        List<CompanyPolicyDescription> descriptionList = companyPolicyDescriptionService.getAllCompanyPolicyDescription();
        return new ResponseEntity<>(descriptionList, HttpStatus.OK);
    }


    @PostMapping("company-policy-description/create")
    public ResponseEntity<CompanyPolicyDescription> createCompanyPolicyDescription(@Valid @RequestBody CompanyPolicyDescription companyPolicyDescription) {
        long id = companyPolicyDescription.getCompanyPolicy().getId();
        Optional<CompanyPolicy> companyPolicyObj = companyPolicyService.getCompanyPolicyById(id);
        CompanyPolicy companyPolicy = companyPolicyObj.get();
        companyPolicyDescription.setCompanyPolicy(companyPolicy);
        CompanyPolicyDescription policyDescription = companyPolicyDescriptionService.createCompanyPolicy(companyPolicyDescription);
        return new ResponseEntity<>(policyDescription, HttpStatus.CREATED);
    }

    @PostMapping("company-policy-description/{id}/{subPolicyName}/{description}")
    public ResponseEntity<CompanyPolicyDescription> createCompanyPolicyDescription(@RequestParam("file") MultipartFile file, @PathVariable("id") long id,@PathVariable("subPolicyName")String subPolicyName,@PathVariable("description")String description) {
        CompanyPolicyDescription companyPolicyDescription = new CompanyPolicyDescription();
        Optional<CompanyPolicy> companyPolicyObj = companyPolicyService.getCompanyPolicyById(id);
        CompanyPolicy companyPolicy = companyPolicyObj.get();
        companyPolicyDescription.setCompanyPolicy(companyPolicy);
        companyPolicyDescription.setSubPolicyName(subPolicyName);
        companyPolicyDescription.setDescription(description);
        Document document = fileStorageService.storeFile(file);
        companyPolicyDescription.setDocument(document);
        CompanyPolicyDescription policyDescription = companyPolicyDescriptionService.createCompanyPolicy(companyPolicyDescription);
        return new ResponseEntity<>(policyDescription, HttpStatus.CREATED);
    }

    @GetMapping("company-policy-description/{id}")
    public CompanyPolicyDescription getDescriptionsById(@PathVariable long id) {
        return companyPolicyDescriptionService.listCompanyPolicyDescription(id);
    }

    @DeleteMapping("company-policy-description/{id}")
    public void deleteCompanyPolicyDescription(@PathVariable long id) {
        companyPolicyDescriptionService.deleteCompanyPolicyDescription(id);
    }

    @PostMapping("company-policy-description/{id}")
    public CompanyPolicyDescription updateCompanyPolicyDescription(@PathVariable long id, @Valid @RequestBody CompanyPolicyDescription companyPolicyDescription) {
        CompanyPolicyDescription presentCompanyPolicyDescription = companyPolicyDescriptionService.listCompanyPolicyDescription(id);
        companyPolicyDescription.setId(id);
        companyPolicyDescriptionService.createCompanyPolicy(companyPolicyDescription);
        return companyPolicyDescription;
    }
}
