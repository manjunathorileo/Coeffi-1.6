package com.dfq.coeffi.SOPDetails.SOPCategory;

import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import com.dfq.coeffi.SOPDetails.SopDocument.SopDocumentType;
import com.dfq.coeffi.SOPDetails.SopDocument.SopDocumentUpload;
import com.dfq.coeffi.SOPDetails.SopDocument.SopDocumentUploadRepository;
import com.dfq.coeffi.SOPDetails.SopDocument.SopDocumentUploadService;
import com.dfq.coeffi.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class SopCategoryController extends BaseController {

    private final SopCategoryService SOPCategoryService;
    private final SopDocumentUploadService sopDocumentUploadService;
    private final SopDocumentUploadRepository docRepository;
    private final SopCategoryRepository SOPCategoryRepository;
    private final SopTypeService sopTypeService;

    @Autowired
    public SopCategoryController(SopCategoryService SOPCategoryService, SopDocumentUploadService sopDocumentUploadService, SopDocumentUploadRepository docRepository, SopCategoryRepository SOPCategoryRepository, SopTypeService sopTypeService) {
        this.SOPCategoryService = SOPCategoryService;
        this.sopDocumentUploadService = sopDocumentUploadService;
        this.docRepository = docRepository;
        this.SOPCategoryRepository = SOPCategoryRepository;
        this.sopTypeService = sopTypeService;
    }

    @PostMapping("sop-category")
    public ResponseEntity<SopCategory> saveDigitalSOP(@RequestBody SopCategory SOPCategory) {
        Date today = new Date();
        SOPCategory.setCreatedOn(today);
        SOPCategory.setStatus(true);
        SopCategory sopCategoryObj = SOPCategoryService.saveDigitalSOP(SOPCategory);
        return new ResponseEntity<>(sopCategoryObj, HttpStatus.OK);
    }

    @GetMapping("sop-category")
    public ResponseEntity<List<SopCategory>> getDigitalSOP() {
        List<SopCategory> sopCategories = SOPCategoryService.getDigitalSOP();
        if (sopCategories.isEmpty()){
            throw new EntityNotFoundException("There is no SOP Categpry");
        }
        return new ResponseEntity<>(sopCategories, HttpStatus.OK);
    }

    @GetMapping("sop-category/{id}")
    public ResponseEntity<Optional<SopCategory>> getSopCategoryById(@PathVariable long id) {
        Optional<SopCategory> sopCategory = SOPCategoryService.getSopCategory(id);
        return new ResponseEntity<>(sopCategory, HttpStatus.OK);
    }

    @DeleteMapping("sop-category/{id}")
    public ResponseEntity<SopCategory> deleteDigitalSOPByid(@PathVariable long id) {
        SopCategory sopCategory = SOPCategoryService.deleteSopCategory(id);
        return new ResponseEntity<>(sopCategory, HttpStatus.OK);
    }

    @GetMapping("sop-category-by-sop-type/{sopTypeId}")
    public ResponseEntity<List<SopCategory>> getSopCategoryBySopType(@PathVariable long sopTypeId) {
        Optional<SopType> sopTypeOptional = sopTypeService.getSopTypeById(sopTypeId);
        List<SopCategory> SopCategoryList = SOPCategoryService.getSopCategoryBySopType(sopTypeOptional.get());
        if (SopCategoryList.isEmpty()){
            throw new EntityNotFoundException("There is No Sop Category for this Sop Type.");
        }
        return new ResponseEntity<>(SopCategoryList, HttpStatus.OK);
    }

    @GetMapping("doc-list/{id}/{sid}")
    public ResponseEntity<List<SopDocumentUpload>> getDocuments(@PathVariable("id") long sopId, @PathVariable("sid") long sopTypeId) {
        List<SopDocumentUpload> sopDocumentUploads = new ArrayList<>();
        List<SopDocumentUpload> sopDocumentUploadList = docRepository.getDocByTwoIds(sopId, sopTypeId);
        for (SopDocumentUpload sopDocumentUpload : sopDocumentUploadList) {
            if (sopDocumentUpload.getSopDocumentType().equals(SopDocumentType.Document)) {
                sopDocumentUploads.add(sopDocumentUpload);
            }
        }
        return new ResponseEntity<>(sopDocumentUploads, HttpStatus.OK);
    }

    @GetMapping("audio-list/{id}/{sid}")
    public ResponseEntity<List<SopDocumentUpload>> getAudios(@PathVariable("id") long sopId, @PathVariable("sid") long sopTypeId) {
        List<SopDocumentUpload> audios = new ArrayList<>();
        List<SopDocumentUpload> audioList = docRepository.getDocByTwoIds(sopId, sopTypeId);
        for (SopDocumentUpload audio : audioList) {
            if (audio.getSopDocumentType().equals(SopDocumentType.Audio)) {
                audios.add(audio);
            }
        }
        return new ResponseEntity<>(audios, HttpStatus.OK);
    }

    @GetMapping("video-list/{id}/{sid}")
    public ResponseEntity<List<SopDocumentUpload>> getVideosList(@PathVariable("id") long sopId, @PathVariable("sid") long sopTypeId) {
        List<SopDocumentUpload> audios = new ArrayList<>();
        List<SopDocumentUpload> audioList = docRepository.getDocByTwoIds(sopId, sopTypeId);
        for (SopDocumentUpload audio : audioList) {
            if (audio.getSopDocumentType().equals(SopDocumentType.Video)) {
                audios.add(audio);
            }
        }
        return new ResponseEntity<>(audios, HttpStatus.OK);
    }

    @GetMapping("user-digital/{id}/{sid}")
    public ResponseEntity<List<SopDocumentUpload>> getVideos(@PathVariable("id") long sopId, @PathVariable("sid") long sopTypeId) {
        List<SopDocumentUpload> sopDocumentUploads = new ArrayList<>();
        List<SopDocumentUpload> sopDocumentUploadList = docRepository.getDocByTwoIds(sopId, sopTypeId);
        for (SopDocumentUpload sopDocumentUpload : sopDocumentUploadList) {
            if (sopDocumentUpload.getSopDocumentType().equals(SopDocumentType.Document)) {
                sopDocumentUploads.add(sopDocumentUpload);
            }
        }
        for (SopDocumentUpload video : sopDocumentUploadList) {
            if (video.getSopDocumentType().equals(SopDocumentType.Audio)) {
                sopDocumentUploads.add(video);
            }
        }
        for (SopDocumentUpload video : sopDocumentUploadList) {
            if (video.getSopDocumentType().equals(SopDocumentType.Video)) {
                sopDocumentUploads.add(video);
            }
        }
        return new ResponseEntity<>(sopDocumentUploads, HttpStatus.OK);
    }
}
