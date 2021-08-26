package com.dfq.coeffi.SOPDetails.SopDocument;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class SopDocumentUploadController extends BaseController {

    private final SopTypeService sopTypeService;
    private final SopCategoryService sopCategoryService;
    private final EmployeeService employeeService;
    private final SopDocumentUploadService sopDocumentUploadService;

    private SopDocumentUploadRepository sopDocumentUploadRepository;
    @Autowired
    public SopDocumentUploadController(SopTypeService sopTypeService, SopCategoryService sopCategoryService, EmployeeService employeeService, SopDocumentUploadService sopDocumentUploadService) {
        this.sopTypeService = sopTypeService;
        this.sopCategoryService = sopCategoryService;
        this.employeeService = employeeService;
        this.sopDocumentUploadService = sopDocumentUploadService;
    }

    @GetMapping("doc/{id}")
    public ResponseEntity<SopDocumentUpload> getAnyDocumentById(@PathVariable long id) {
        Optional<SopDocumentUpload> sopDocumentUpload = sopDocumentUploadService.getDocumentFileById(id);
        return new ResponseEntity(sopDocumentUpload, HttpStatus.OK);
    }

    @GetMapping("/downloadDocFile/{id}")
    public ResponseEntity<Resource> getDownloadAnyDocFileById(@PathVariable long id) {
        Optional<SopDocumentUpload> sopDocumentUpload = sopDocumentUploadService.getDocumentFileById(id);
        SopDocumentUpload sopDocumentUploadObj = sopDocumentUpload.get();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(sopDocumentUploadObj.getDocumentFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sopDocumentUploadObj.getDocumentFileName() + "\"")
                .body(new ByteArrayResource(sopDocumentUploadObj.getData()));
    }

    @DeleteMapping("doc-delete/{id}")
    public ResponseEntity<SopDocumentUpload> deleteAnyDocFileByid(@PathVariable("id") long id) {
        SopDocumentUpload sopDocumentUpload = sopDocumentUploadService.deleteDocumentFileById(id);
        return new ResponseEntity(sopDocumentUpload, HttpStatus.OK);
    }

    @PostMapping("audio-save/{description}/{sopTypeId}/{sopCategoryId}/{loggerId}")
    public ResponseEntity<SopDocumentUpload> saveAudio(@RequestParam("file") MultipartFile file, @PathVariable String description, @PathVariable long sopTypeId, @PathVariable long sopCategoryId, @PathVariable long loggerId) {
        Date today = new Date();
        Optional<SopType> sopTypeOptional = sopTypeService.getSopTypeById(sopTypeId);
        Optional<SopCategory> sopCategoryOptional = sopCategoryService.getSopCategory(sopCategoryId);
        Optional<Employee> employeeOptional = employeeService.getEmployee(loggerId);
        SopDocumentUpload saveSopDocumentUpload = new SopDocumentUpload();
        saveSopDocumentUpload.setStatus(true);
        saveSopDocumentUpload.setCreatedOn(today);
        saveSopDocumentUpload.setSopType(sopTypeOptional.get());
        saveSopDocumentUpload.setSopCategory(sopCategoryOptional.get());
        saveSopDocumentUpload.setCreatedBy(employeeOptional.get());
        saveSopDocumentUpload.setSopDocumentType(SopDocumentType.Audio);
        saveSopDocumentUpload.setDocumentFileDescription(description);
        saveSopDocumentUpload.setDocVersion(1);
        SopDocumentUpload sopDocumentUpload = sopDocumentUploadService.saveDocument(file, saveSopDocumentUpload);
        return new ResponseEntity(sopDocumentUpload, HttpStatus.ACCEPTED);
    }

    @PostMapping("audio-by-sopType-sopCategory")
    public ResponseEntity<SopDocumentUpload> getAudioFileBySop(@RequestBody FileUploadDto fileUploadDto) {
        SopDocumentType sopDocumentType = SopDocumentType.Audio;
        List<SopDocumentUpload> audio = sopDocumentUploadService.getDocBySopTypeBySopCategory(fileUploadDto.getSopTypeId(), fileUploadDto.getSopCategoryId(), sopDocumentType);
        return new ResponseEntity(audio, HttpStatus.OK);
    }

    @PostMapping("audio-update/{oldDocId}/{loggerId}")
    public ResponseEntity<SopDocumentUpload> updateAudio(@RequestParam("file") MultipartFile file, @PathVariable long oldDocId, @PathVariable long loggerId) {
        Date today = new Date();
        Optional<Employee> employeeOptional = employeeService.getEmployee(loggerId);
        Optional<SopDocumentUpload> sopDocumentUploadOptional = sopDocumentUploadService.getDocumentFileById(oldDocId);
        SopDocumentUpload sopDocumentUpload = sopDocumentUploadOptional.get();
        sopDocumentUpload.setCreatedOn(today);
        sopDocumentUpload.setCreatedBy(employeeOptional.get());
        sopDocumentUpload.setSopDocumentType(SopDocumentType.Audio);
        sopDocumentUpload.setDocVersion(sopDocumentUpload.getDocVersion() + 1);
        SopDocumentUpload updatedSopDocumentUpload = sopDocumentUploadService.saveDocument(file, sopDocumentUpload);
        return new ResponseEntity(updatedSopDocumentUpload, HttpStatus.ACCEPTED);
    }

    @PostMapping("video-save/{description}/{sopTypeId}/{sopCategoryId}/{loggerId}")
    public ResponseEntity<SopDocumentUpload> saveVideo(@RequestParam("file") MultipartFile file, @PathVariable String description, @PathVariable long sopTypeId, @PathVariable long sopCategoryId, @PathVariable long loggerId) {
        Date today = new Date();
        Optional<SopType> sopTypeOptional = sopTypeService.getSopTypeById(sopTypeId);
        Optional<SopCategory> sopCategoryOptional = sopCategoryService.getSopCategory(sopCategoryId);
        Optional<Employee> employeeOptional = employeeService.getEmployee(loggerId);
        SopDocumentUpload saveSopDocumentUpload = new SopDocumentUpload();
        saveSopDocumentUpload.setStatus(true);
        saveSopDocumentUpload.setCreatedOn(today);
        saveSopDocumentUpload.setSopType(sopTypeOptional.get());
        saveSopDocumentUpload.setSopCategory(sopCategoryOptional.get());
        saveSopDocumentUpload.setCreatedBy(employeeOptional.get());
        saveSopDocumentUpload.setSopDocumentType(SopDocumentType.Video);
        saveSopDocumentUpload.setDocumentFileDescription(description);
        saveSopDocumentUpload.setDocVersion(1);
        SopDocumentUpload sopDocumentUpload = sopDocumentUploadService.saveDocument(file, saveSopDocumentUpload);
        return new ResponseEntity(sopDocumentUpload, HttpStatus.ACCEPTED);
    }

    @PostMapping("video-by-sopType-sopCategory")
    public ResponseEntity<SopDocumentUpload> getVideoFileBySop(@RequestBody FileUploadDto fileUploadDto) {
        SopDocumentType sopDocumentType = SopDocumentType.Video;
        List<SopDocumentUpload> video = sopDocumentUploadService.getDocBySopTypeBySopCategory(fileUploadDto.getSopTypeId(), fileUploadDto.getSopCategoryId(), sopDocumentType);
        return new ResponseEntity(video, HttpStatus.OK);
    }

    @PostMapping("video-update/{oldDocId}/{loggerId}")
    public ResponseEntity<SopDocumentUpload> updateVideo(@RequestParam("file") MultipartFile file, @PathVariable long oldDocId, @PathVariable long loggerId) {
        Date today = new Date();
        Optional<Employee> employeeOptional = employeeService.getEmployee(loggerId);
        Optional<SopDocumentUpload> sopDocumentUploadOptional = sopDocumentUploadService.getDocumentFileById(oldDocId);
        SopDocumentUpload sopDocumentUpload = sopDocumentUploadOptional.get();
        sopDocumentUpload.setCreatedOn(today);
        sopDocumentUpload.setCreatedBy(employeeOptional.get());
        sopDocumentUpload.setSopDocumentType(SopDocumentType.Video);
        sopDocumentUpload.setDocVersion(sopDocumentUpload.getDocVersion() + 1);
        SopDocumentUpload updatedSopDocumentUpload = sopDocumentUploadService.saveDocument(file, sopDocumentUpload);
        return new ResponseEntity(updatedSopDocumentUpload, HttpStatus.ACCEPTED);
    }

    @PostMapping("word-save/{description}/{sopTypeId}/{sopCategoryId}/{loggerId}")
    public ResponseEntity<SopDocumentUpload> saveWord(@RequestParam("file") MultipartFile file, @PathVariable String description, @PathVariable long sopTypeId, @PathVariable long sopCategoryId, @PathVariable long loggerId) {
        Date today = new Date();
        Optional<SopType> sopTypeOptional = sopTypeService.getSopTypeById(sopTypeId);
        Optional<SopCategory> sopCategoryOptional = sopCategoryService.getSopCategory(sopCategoryId);
        Optional<Employee> employeeOptional = employeeService.getEmployee(loggerId);
        SopDocumentUpload saveSopDocumentUpload = new SopDocumentUpload();
        saveSopDocumentUpload.setStatus(true);
        saveSopDocumentUpload.setCreatedOn(today);
        saveSopDocumentUpload.setSopType(sopTypeOptional.get());
        saveSopDocumentUpload.setSopCategory(sopCategoryOptional.get());
        saveSopDocumentUpload.setCreatedBy(employeeOptional.get());
        saveSopDocumentUpload.setSopDocumentType(SopDocumentType.Document);
        saveSopDocumentUpload.setDocumentFileDescription(description);
        saveSopDocumentUpload.setDocVersion(1);
        SopDocumentUpload sopDocumentUpload = sopDocumentUploadService.saveDocument(file, saveSopDocumentUpload);
        return new ResponseEntity(sopDocumentUpload, HttpStatus.ACCEPTED);
    }

    @PostMapping("word-by-sopType-sopCategory")
    public ResponseEntity<SopDocumentUpload> getWordFileBySop(@RequestBody FileUploadDto fileUploadDto) {
        SopDocumentType sopDocumentType = SopDocumentType.Document;
        List<SopDocumentUpload> doc = sopDocumentUploadService.getDocBySopTypeBySopCategory(fileUploadDto.getSopTypeId(), fileUploadDto.getSopCategoryId(), sopDocumentType);
        return new ResponseEntity(doc, HttpStatus.OK);
    }

    @PostMapping("word-update/{oldDocId}/{loggerId}")
    public ResponseEntity<SopDocumentUpload> updateWord(@RequestParam("file") MultipartFile file, @PathVariable long oldDocId, @PathVariable long loggerId) {
        Date today = new Date();
        Optional<Employee> employeeOptional = employeeService.getEmployee(loggerId);
        Optional<SopDocumentUpload> sopDocumentUploadOptional = sopDocumentUploadService.getDocumentFileById(oldDocId);
        SopDocumentUpload sopDocumentUpload = sopDocumentUploadOptional.get();
        sopDocumentUpload.setCreatedOn(today);
        sopDocumentUpload.setCreatedBy(employeeOptional.get());
        sopDocumentUpload.setSopDocumentType(SopDocumentType.Document);
        sopDocumentUpload.setDocVersion(sopDocumentUpload.getDocVersion() + 1);
        SopDocumentUpload updatedSopDocumentUpload = sopDocumentUploadService.saveDocument(file, sopDocumentUpload);
        return new ResponseEntity(updatedSopDocumentUpload, HttpStatus.ACCEPTED);
    }
}