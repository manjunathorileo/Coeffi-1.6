package com.dfq.coeffi.controller;

import com.dfq.coeffi.E_Learning.modules.DocumentUpload;
import com.dfq.coeffi.E_Learning.service.DocumentUploadService;
import com.dfq.coeffi.policy.document.FileStorageService;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class CoEffiOpenController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DocumentUploadService documentUploadService;

    @GetMapping("/file-view/{fileName:.+}")
    public String viewImage(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        File serverFile = new File(resource.getURI());
        byte[] encoded = Base64.encode(Files.readAllBytes(serverFile.toPath()));
        return new String(encoded);
    }

    @GetMapping("/file-download/{fileName:.+}")
    public byte[] downloadImage(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        File serverFile = new File(resource.getURI());
        return Files.readAllBytes(serverFile.toPath());
    }

    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable long fileId) {
        // Load file from database
        DocumentUpload documentUpload = documentUploadService.getFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documentUpload.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentUpload.getFileName() + "\"")
                .body(new ByteArrayResource(documentUpload.getData()));
    }
}