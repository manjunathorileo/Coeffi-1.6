package com.dfq.coeffi.SOPDetails.SopDocument;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.controller.BaseController;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class SopVideoUploadController extends BaseController {

    private final SopCategoryService SOPCategoryService;
    private final SopDocumentUploadService sopDocumentUploadService;

    @Autowired
    public SopVideoUploadController(SopCategoryService SOPCategoryService, SopDocumentUploadService sopDocumentUploadService) {
        this.SOPCategoryService = SOPCategoryService;
        this.sopDocumentUploadService = sopDocumentUploadService;
    }

    /*@PostMapping("video/save/{desc}/{sid}/{did}")
    public ResponseEntity<SopDocumentUpload> saveVideo(@RequestParam("file") MultipartFile file, @PathVariable("desc") String description, @PathVariable("sid") long sid, @PathVariable("did") long did)
    {
        SopDocumentType sopDocumentType = SopDocumentType.Video;
        SopDocumentUpload video1= sopDocumentUploadService.saveDocument(file,description,sid,did, sopDocumentType);
        if(video1==null){
            throw new EntityNotFoundException("**********");
        }
        return new ResponseEntity<>(video1, HttpStatus.ACCEPTED);
    }

    @GetMapping("/downloadVideoFile/{fileId}")
    public ResponseEntity<Resource> getVideoFileById(@PathVariable long fileId)
    {
        // Load file from database
        SopDocumentUpload video3 = sopDocumentUploadService.getDocumentFileById(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(video3.getDocumentFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + video3.getDocumentFileName() + "\"")
                .body(new ByteArrayResource(video3.getData()));
    }

    @GetMapping("video/view")
    public ResponseEntity<List<SopDocumentUpload>> getAllVideo()
    {
        List<SopDocumentUpload> document2 = sopDocumentUploadService.getAllDocument();
        List<SopDocumentUpload> documentUploadList=new ArrayList<>();
        for (SopDocumentUpload d:document2) {
            if(d.getSopDocumentType().equals(SopDocumentType.Video)){
                documentUploadList.add(d);
            }

        }
        return new ResponseEntity<>(documentUploadList,HttpStatus.OK);
    }

    @GetMapping("video/by-sop/{id}")
    public ResponseEntity<SopDocumentUpload> getVideoFileBySop(@PathVariable("id") long sopId)
    {
        Optional<SopCategory> digitalSOP= SOPCategoryService.getSopCategory(sopId);
        SopDocumentUpload video= sopDocumentUploadService.getWordBySopId(digitalSOP.get());
        return new ResponseEntity<>(video ,HttpStatus.OK );
    }

    @DeleteMapping("video/delete/{id}")
    public void deleteVideoByid(@PathVariable("id") long id)
    {
        sopDocumentUploadService.deleteDocumentById(id);
    }*/
}
