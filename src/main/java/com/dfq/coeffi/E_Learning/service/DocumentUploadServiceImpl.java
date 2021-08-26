package com.dfq.coeffi.E_Learning.service;

import com.dfq.coeffi.E_Learning.modules.DocumentUpload;
import com.dfq.coeffi.E_Learning.modules.Product;
import com.dfq.coeffi.E_Learning.repository.DocumentUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.LimitExceededException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("DocUpload")
public class DocumentUploadServiceImpl implements DocumentUploadService  {

    @Autowired
    private DocumentUploadRepository documentUploadRepository;

    @Autowired
    private ProductService productService;

    @Override
    public DocumentUpload saveDocUpload(DocumentUpload documentUpload) {
        documentUpload.status = true;
        return documentUploadRepository.save(documentUpload);
    }

    @Override
    public List<DocumentUpload> getDocUpload(boolean status) {
        return documentUploadRepository.findByStatus(status);
    }

    @Override
    public List<DocumentUpload> getVideos() {
        return documentUploadRepository.findAllVideos();
    }

    @Override
    public Optional<DocumentUpload> getDocUploadById(long id) {
        return Optional.ofNullable(documentUploadRepository.findOne(id));
    }

    @Override
    public void deActivateById(long id) {
        documentUploadRepository.delete(id);
    }

    @Override
    public ArrayList<DocumentUpload> getdocumentsByProductId(long productId) {
        return documentUploadRepository.findAllDocuments(productId);
    }

    @Override
    public DocumentUpload storeFile(MultipartFile file, long pid, String title, String description) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            DocumentUpload documentUpload = new DocumentUpload(fileName, file.getContentType(), file.getBytes(), file.getSize());
            FileStorageException h;
            documentUpload.setStatus(true);
            Optional<Product> product = productService.getProductById(pid);
            documentUpload.setProduct(product.get());
            documentUpload.setTitle(title);
            documentUpload.setDescription(description);
            return documentUploadRepository.save(documentUpload);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public DocumentUpload storeCertificate(MultipartFile file) throws LimitExceededException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            DocumentUpload documentUpload = new DocumentUpload(fileName, file.getContentType(), file.getBytes(), file.getSize());
            FileStorageException h;
            documentUpload.setStatus(true);
            documentUpload.setTitle("-");
            documentUpload.setDescription("_");
            return documentUploadRepository.save(documentUpload);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public DocumentUpload getFile(long fileId) {
        return documentUploadRepository.findById(fileId)
                .orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }
}

