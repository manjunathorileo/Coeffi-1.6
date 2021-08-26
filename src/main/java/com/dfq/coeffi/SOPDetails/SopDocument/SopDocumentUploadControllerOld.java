package com.dfq.coeffi.SOPDetails.SopDocument;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
public class SopDocumentUploadControllerOld extends BaseController {

    private final SopDocumentUploadService sopDocumentUploadService;
    private final SopCategoryService SOPCategoryService;

    @Autowired
    public SopDocumentUploadControllerOld(SopDocumentUploadService sopDocumentUploadService, SopCategoryService SOPCategoryService) {
        this.sopDocumentUploadService = sopDocumentUploadService;
        this.SOPCategoryService = SOPCategoryService;
    }

   /* @ApiOperation(value="word file save",response=Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully saved word file"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )


    @PostMapping("word/save/{desc}/{sid}/{did}")
    public ResponseEntity<SopDocumentUpload> saveWord(@RequestParam("file") MultipartFile file, @PathVariable("desc") String description, @PathVariable("sid") long sid, @PathVariable("did") long did)
    {
        SopDocumentType sopDocumentType = SopDocumentType.Document;
        SopDocumentUpload document1 = sopDocumentUploadService.saveDocument(file,description,sid,did,sopDocumentType);
        if(document1 ==null)
        {
            throw new EntityNotFoundException("**********");
        }
        return new ResponseEntity<>(document1, HttpStatus.ACCEPTED);
    }

    *//**
     * download word file from database by id
     * @param fileId
     * @return word file
     *//*

    @ApiOperation(value="download word file by id",response=Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully downloaded word file"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )

    @GetMapping("/downloadWordFile/{fileId}")
    public ResponseEntity<Resource> getWordFileById(@PathVariable long fileId)
    {
        // Load file from database
        SopDocumentUpload document3 = sopDocumentUploadService.getDocumentFileById(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document3.getDocumentFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document3.getDocumentFileName() + "\"")
                .body(new ByteArrayResource(document3.getData()));
    }

    *//**
     * view all word files from database
     * @return all word files from database
     *//*

    @ApiOperation(value="get all word files from database",response=Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrived all word files from database"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )

    @GetMapping("word/view")
    public ResponseEntity<List<SopDocumentUpload>> getAllWord()
    {
        List<SopDocumentUpload> document2 = sopDocumentUploadService.getAllDocument();
        List<SopDocumentUpload> documentUploadList=new ArrayList<>();
        for (SopDocumentUpload d:document2) {
            if(d.getSopDocumentType().equals(SopDocumentType.Document)){
             documentUploadList.add(d);
            }

        }
        return new ResponseEntity<>(documentUploadList,HttpStatus.OK);
    }





    *//**
     * view word file from database by id
     * @param :digital sop id
     * @return word file which is attached to digital sop id
     *//*

    @ApiOperation(value="get word file by digital sop id",response=Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrived word file"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )

    @GetMapping("word/by-sop/{id}")
    public ResponseEntity<SopDocumentUpload> getWordFileBySop(@PathVariable("id") long sopId)
    {
        Optional<SopCategory> digitalSOP= SOPCategoryService.getSopCategory(sopId);
        SopDocumentUpload document = sopDocumentUploadService.getWordBySopId(digitalSOP.get());
        return new ResponseEntity<>(document,HttpStatus.OK);
    }

    *//**
     * delete word file from database by id
     * @param :word file id
     *//*

    @ApiOperation(value="delete word file by id",response=Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )

    @DeleteMapping("word/delete/{id}")
    public void deleteWordByid(@PathVariable("id") long id)
    {
        sopDocumentUploadService.deleteDocumentById(id);

    }*/
}
