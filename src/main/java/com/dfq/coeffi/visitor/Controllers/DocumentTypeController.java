package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.visitor.Entities.DocumentType;
import com.dfq.coeffi.visitor.Services.DocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@RestController
public class DocumentTypeController extends BaseController
{
    @Autowired
    DocumentTypeService documentTypeService;

    @PostMapping("visitor/visitor-documenttype-upload")
    public ResponseEntity<DocumentType> saveDocumentType(@RequestParam("file") MultipartFile file)
    {
        DocumentType documentType1 =documentTypeService.saveDocumentType(file);
        if(documentType1==null) {
            throw new EntityNotFoundException("No image");
        }
        return new ResponseEntity<>(documentType1, HttpStatus.OK);
    }
}
