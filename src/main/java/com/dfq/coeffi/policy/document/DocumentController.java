package com.dfq.coeffi.policy.document;


import com.dfq.coeffi.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class DocumentController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/document-upload/{documentCategoryId}")
    public ResponseEntity<Document> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("documentCategoryId") long documentCategoryId) {
        Document document = fileStorageService.storeFile(file);
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

         ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/document-category")
    public ResponseEntity<List<DocumentCategory>> getDocumentCategories() {
        List<DocumentCategory> objects = fileStorageService.getDocumentCategories();
        if (CollectionUtils.isEmpty(objects)) {
            throw new EntityNotFoundException("documentCategories");
        }
        return new ResponseEntity<>(objects, HttpStatus.OK);

    }

    @PostMapping(value = "/document-category")
    public ResponseEntity<DocumentCategory> saveDocumentCategory(@Valid @RequestBody DocumentCategory documentCategory) {
        DocumentCategory persistedObject = fileStorageService.createDocumentCategory(documentCategory);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

}