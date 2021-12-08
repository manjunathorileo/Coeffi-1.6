package com.dfq.coeffi.policy;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class CompanyPolicyController extends BaseController {

    @Autowired
    private CompanyPolicyService companyPolicyService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("company-policy")
    public ResponseEntity<CompanyPolicy> listAllCompanyPolicy() {
        List<CompanyPolicy> allPolicy = companyPolicyService.getAllPolicy();
        List<CompanyPolicy> activePolicy = new ArrayList<>();
        for(CompanyPolicy c:allPolicy){
            if(c.isActive()){
                activePolicy.add(c);
            }
        }
        return new ResponseEntity(activePolicy, HttpStatus.OK);
    }

    @PostMapping("company-policy/create/{policyName}")
    public ResponseEntity<CompanyPolicy> createCompanyPolicy(@RequestParam("file") MultipartFile file,@PathVariable("policyName")String policyName) throws IOException {
        CompanyPolicy companyPolicy = new CompanyPolicy();
        //Document document = fileStorageService.storeFile(file);
        Document document = fileStorageService.storePolicyDocument(file);
        companyPolicy.setDocument(document);
        companyPolicy.setPolicyName(policyName);
        companyPolicy.setActive(true);
        companyPolicyService.saveCompanyPolicy(companyPolicy);
        return new ResponseEntity<>(companyPolicy,HttpStatus.CREATED);
    }

    @GetMapping("company-policy/{id}")
    public ResponseEntity<CompanyPolicy> getCompanyPolicyById(@PathVariable long id) {
        Optional<CompanyPolicy> companyPolicyObj = companyPolicyService.getCompanyPolicyById(id);
        if(!companyPolicyObj.isPresent()){
            throw new EntityNotFoundException("CompanyPolicy not found for id : "+id);
        }
        CompanyPolicy companyPolicy = companyPolicyObj.get();
        return new ResponseEntity(companyPolicy,HttpStatus.OK);
    }

    @PostMapping("company-policy-update/{id}")
    public CompanyPolicy updateCompanyPolicy(@PathVariable long id, @Valid @RequestBody CompanyPolicy companyPolicy) {
        Optional<CompanyPolicy> presentCompanyPolicy = companyPolicyService.getCompanyPolicyById(id);
        companyPolicy.setId(id);
        companyPolicyService.saveCompanyPolicy(companyPolicy);
        return companyPolicy;
    }

    @GetMapping("company-policy/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("company-policy/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable long fileId) {
        // Load file from database
        Document documentUpload = fileStorageService.getDocument(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documentUpload.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentUpload.getFileName() + "\"")
                .body(new ByteArrayResource(documentUpload.getData()));
    }


    @DeleteMapping("company-policy/{id}")
    public void deleteCompanyPolicy(@PathVariable("id") long id){
        Optional<CompanyPolicy> companyPolicy=companyPolicyService.getCompanyPolicyById(id);
        companyPolicy.get().setActive(false);
        companyPolicyService.saveCompanyPolicy(companyPolicy.get());
    }
}
