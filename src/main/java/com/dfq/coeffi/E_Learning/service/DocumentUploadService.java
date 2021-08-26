package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.DocumentUpload;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface DocumentUploadService {

    DocumentUpload saveDocUpload(DocumentUpload documentUpload);

    List<DocumentUpload> getDocUpload(boolean status);

    List<DocumentUpload> getVideos();

    Optional<DocumentUpload> getDocUploadById(long id);

    void deActivateById(long id);

    ArrayList<DocumentUpload> getdocumentsByProductId(long productId);

    DocumentUpload storeFile(MultipartFile file, long pid, String title, String description) throws LimitExceededException;

    DocumentUpload storeCertificate(MultipartFile file) throws LimitExceededException;

    DocumentUpload getFile(long fileId);
}
