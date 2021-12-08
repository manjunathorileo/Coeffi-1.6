package com.dfq.coeffi.preventiveMaintenance.user.controler;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssignedDocumentUpload;
import com.dfq.coeffi.preventiveMaintenance.user.service.SopStepsAssignedDocumentUploadService;
import com.dfq.coeffi.preventiveMaintenance.user.service.SopStepsAssignedService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class SopStepsAssignedDocumentUploadController extends BaseController {

    private final SopStepsAssignedDocumentUploadService sopStepsAssignedDocumentUploadService;
    private final SopCategoryService SOPCategoryService;
    private final SopTypeService sopTypeService;
    private final SopStepsAssignedService sopStepsAssignedService;

    @Autowired
    public SopStepsAssignedDocumentUploadController(SopStepsAssignedDocumentUploadService sopStepsAssignedDocumentUploadService, SopCategoryService SOPCategoryService, SopTypeService sopTypeService, SopStepsAssignedService sopStepsAssignedService) {
        this.sopStepsAssignedDocumentUploadService = sopStepsAssignedDocumentUploadService;
        this.SOPCategoryService = SOPCategoryService;
        this.sopTypeService = sopTypeService;
        this.sopStepsAssignedService = sopStepsAssignedService;
    }

    @PostMapping("sop-steps-document-upload/{sopTypeId}/{digitalSOPId}")
    public ResponseEntity<SopStepsAssignedDocumentUpload> saveWord(@RequestParam("file") MultipartFile file, @PathVariable long sopTypeId, @PathVariable long digitalSOPId) {
        Optional<SopType> sopType = sopTypeService.getSopTypeById(sopTypeId);
        Optional<SopCategory> sopCategory = SOPCategoryService.getSopCategory(digitalSOPId);
        SopCategory SopCategoryObj = sopCategory.get();
        SopStepsAssignedDocumentUpload documentObj = sopStepsAssignedDocumentUploadService.saveDocument(file, sopType.get(), SopCategoryObj);
        if(documentObj == null) {
            throw new EntityNotFoundException("Document not saved");
        }
        return new ResponseEntity<>(documentObj, HttpStatus.ACCEPTED);
    }

    @GetMapping("/sop-steps-document-view/{fileId}")
    public ResponseEntity<SopStepsAssignedDocumentUpload> viewSopFileById(@PathVariable long fileId) {
        SopStepsAssignedDocumentUpload documentObj = sopStepsAssignedDocumentUploadService.getDocumentFileById(fileId);
        return new ResponseEntity(documentObj,HttpStatus.OK);
    }

    @GetMapping("/sop-steps-document-download/{fileId}")
    public ResponseEntity<Resource> downloadDocumentFileById(@PathVariable long fileId)
    {
        SopStepsAssignedDocumentUpload documentObj = sopStepsAssignedDocumentUploadService.getDocumentFileById(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documentObj.getDocumentFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentObj.getDocumentFileName() + "\"")
                .body(new ByteArrayResource(documentObj.getData()));
    }

    @GetMapping("sop-steps-document-upload-view")
    public ResponseEntity<List<SopStepsAssignedDocumentUpload>> getAllWord()
    {
        List<SopStepsAssignedDocumentUpload> document2 = sopStepsAssignedDocumentUploadService.getAllDocument();
        return new ResponseEntity<>(document2,HttpStatus.OK);
    }

    @GetMapping("sop-steps-document-upload-by-SOPDetails/{digitalSopId}")
    public ResponseEntity<SopStepsAssignedDocumentUpload> getWordFileBySop(@PathVariable long digitalSopId)
    {
        Optional<SopCategory> digitalSOP= SOPCategoryService.getSopCategory(digitalSopId);
        SopStepsAssignedDocumentUpload document = sopStepsAssignedDocumentUploadService.getWordBySopId(digitalSOP.get());
        return new ResponseEntity(document,HttpStatus.OK);
    }
}
