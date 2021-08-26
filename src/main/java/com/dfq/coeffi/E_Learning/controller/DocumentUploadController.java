package com.dfq.coeffi.E_Learning.controller;

import com.dfq.coeffi.E_Learning.modules.DocumentUpload;
import com.dfq.coeffi.E_Learning.modules.UploadResponse;
import com.dfq.coeffi.E_Learning.repository.DocumentUploadRepository;
import com.dfq.coeffi.E_Learning.service.DocumentUploadService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.naming.LimitExceededException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class DocumentUploadController extends BaseController {

    @Autowired
    DocumentUploadService documentUploadService;
    @Autowired
    DocumentUploadRepository documentUploadRepository;
    @Autowired
    CompanyConfigureService companyConfigureService;

    private static final Logger logger = LoggerFactory.getLogger(DocumentUploadController.class);

    /**
     * @return create, save, and update document
     */

    @PostMapping(value = "/document-upload")
    public ResponseEntity<DocumentUpload> create(@RequestBody DocumentUpload documentUpload) {
        DocumentUpload documentUpload1 = documentUploadService.saveDocUpload(documentUpload);
        return new ResponseEntity<>(documentUpload1, HttpStatus.CREATED);
    }

    /**
     * @return List<documentUpload>
     * return list od document upload
     */


    @GetMapping("document/videos/{pid}")
    public ResponseEntity<List<DocumentUpload>> getVideosForProduct(@PathVariable("pid") long pid) {
        List<DocumentUpload> documentUploads = documentUploadService.getVideos();
        List<DocumentUpload> documentUploadList = new ArrayList<>();
        for (DocumentUpload d : documentUploads) {
            if (pid == d.getProduct().getId()) {
                d.setData(null);
                documentUploadList.add(d);
            }
        }
        return new ResponseEntity<>(documentUploadList, HttpStatus.OK);
    }

    /**
     * @return return document upload od particular id
     */

    @GetMapping(value = "/document-upload/{id}")
    public ResponseEntity<DocumentUpload> getDocUpload(@PathVariable long id) {
        Optional<DocumentUpload> docUploadOptional = documentUploadService.getDocUploadById(id);
        if (!docUploadOptional.isPresent()) {
            throw new EntityNotFoundException("not found");
        }
        DocumentUpload upload = docUploadOptional.get();
        return new ResponseEntity<>(upload, HttpStatus.OK);
    }

    @GetMapping(value = "/document-upload/product/{productId}")
    public ResponseEntity<List<DocumentUpload>> getdocumentsByProductId(@PathVariable long productId) {
        List<DocumentUpload> documentList = documentUploadService.getdocumentsByProductId(productId);
        List<DocumentUpload> documentListLight = new ArrayList<>();

        for (DocumentUpload documentUpload:documentList){
            documentUpload.setData(null);
            documentListLight.add(documentUpload);
        }
        return new ResponseEntity<>(documentListLight, HttpStatus.OK);
    }

    /**
     * @return delete document upload of  particular id
     */

    @DeleteMapping(value = "/document-upload/{id}")
    public ResponseEntity<DocumentUpload> deleteDocUploadById(@PathVariable long id) {
        documentUploadService.deActivateById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     *
     */
    @PostMapping("/uploadFile/{pid}/{title}/{description}")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("pid") long pid, @PathVariable("title") String title, @PathVariable("description") String description) throws Exception {

        if (file.getSize() > 4194304) {
            throw new LimitExceededException("Limit exceeded.. file size should be less than 4MB");
        }
        DocumentUpload documentUpload = documentUploadService.storeFile(file, pid, title, description);

        String fileExtension = getExtensionOfFile(file);
        if (fileExtension.equalsIgnoreCase("mp4")) {
            throw new Exception("Please Upload a File");
        }
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(String.valueOf(documentUpload.getId()))
                .toUriString();
        documentUpload.setUrl(fileDownloadUri);
        UploadResponse uploadResponse = new UploadResponse(documentUpload.getFileName(), fileDownloadUri,
                file.getContentType(), file.getSize());
        documentUploadRepository.save(documentUpload);

        return new ResponseEntity<>(uploadResponse, HttpStatus.OK);
    }

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload-video/{pid}/{title}/{description}")
    public ResponseEntity<UploadResponse> uploadVideo(@RequestParam("file") MultipartFile file, @PathVariable("pid") long pid, @PathVariable("title") String title, @PathVariable("description") String description) throws Exception {

        if (file.getSize() > 400194304) {
            throw new LimitExceededException("Limit exceeded.. file size should be less than 4MB");
        }
        String fileExtension = getExtensionOfFile(file);
        if (!fileExtension.equalsIgnoreCase("mp4")) {
            throw new Exception("Please Upload Video Of mp4 Format");
        }

        fileStorageService.storeFile(file);

        DocumentUpload documentUpload = documentUploadService.storeFile(file, pid, title, description);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(String.valueOf(documentUpload.getId()))
                .toUriString();
        UploadResponse uploadResponse = new UploadResponse(documentUpload.getFileName(), fileDownloadUri,
                file.getContentType(), file.getSize());
        documentUpload.setUrl(fileDownloadUri);
        documentUploadRepository.save(documentUpload);

        return new ResponseEntity<>(uploadResponse, HttpStatus.OK);
    }

    public static String getExtensionOfFile(MultipartFile file) {
        String fileExtension = "";
        // Get file Name first
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        System.out.println("FileName: " + fileName);

        // If fileName do not contain "." or starts with "." then it is not a valid file
        if (fileName.contains(".") && fileName.lastIndexOf(".") != 0) {
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        System.out.println("File Extension: " + fileExtension);
        return fileExtension;
    }

    /*@PostMapping("/uploadMultipleFiles")
    public List<UploadResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }*/


    @PostMapping("/upload-certificate")
    public ResponseEntity<UploadResponse> uploadCertificate(@RequestParam("file") MultipartFile file) throws Exception {

        if (file.getSize() > 4194304) {
            throw new LimitExceededException("Limit exceeded.. file size should be less than 4MB");
        }
        DocumentUpload documentUpload = documentUploadService.storeCertificate(file);

        String fileExtension = getExtensionOfFile(file);
        if (fileExtension.equalsIgnoreCase("mp4")) {
            throw new Exception("Please Upload a File");
        }
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(String.valueOf(documentUpload.getId()))
                .toUriString();
        documentUpload.setUrl(fileDownloadUri);
        UploadResponse uploadResponse = new UploadResponse(documentUpload.getFileName(), fileDownloadUri,
                file.getContentType(), file.getSize());
        documentUploadRepository.save(documentUpload);
        CompanyConfigure companyConfigure = companyConfigureService.getCompanyById(1);
        companyConfigure.setData(file.getBytes());
        companyConfigureService.saveCompany(companyConfigure);

        return new ResponseEntity<>(uploadResponse, HttpStatus.OK);
    }


}


