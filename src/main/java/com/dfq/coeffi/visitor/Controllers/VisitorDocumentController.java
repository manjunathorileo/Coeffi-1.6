package com.dfq.coeffi.visitor.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.visitor.Entities.VisitorDocAdmin;
import com.dfq.coeffi.visitor.Services.VisitorDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class VisitorDocumentController extends BaseController {
    @Autowired
    VisitorDocumentService visitorDocumentService;
    @Autowired
    FileStorageService fileStorageService;


    @PostMapping("visitor/document-save")
    public ResponseEntity<VisitorDocAdmin> saveDocument(@RequestBody VisitorDocAdmin visitorDocAdmin) {
        VisitorDocAdmin visitorDocAdmin1 = visitorDocumentService.saveDocument(visitorDocAdmin);
        return new ResponseEntity<>(visitorDocAdmin1, HttpStatus.OK);
    }

    @GetMapping("visitor/document-view")
    public ResponseEntity<List<VisitorDocAdmin>> getAllDocument() {
        List<VisitorDocAdmin> visitorDocAdmin2 = visitorDocumentService.getAllDocument();
        return new ResponseEntity<>(visitorDocAdmin2, HttpStatus.OK);

    }

    @GetMapping("visitor/document/viewbyid/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable long id) {
        Document document = fileStorageService.getDocument(id);
        return new ResponseEntity<>(document, HttpStatus.OK);

    }

    @DeleteMapping("visitor/document-delete-by-id/{id}")
    public void deleteDocumentByid(@PathVariable long id) {
        visitorDocumentService.deleteDocumentById(id);

    }


}
