package com.dfq.coeffi.visitor.Services;


import com.dfq.coeffi.visitor.Entities.VisitorDocument;
import org.springframework.web.multipart.MultipartFile;

public interface VisitorImageService {


    VisitorDocument saveImage(MultipartFile file);




}
