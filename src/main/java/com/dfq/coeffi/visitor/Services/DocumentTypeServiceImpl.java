package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.exception.FileStorageException;
import com.dfq.coeffi.visitor.Entities.DocumentType;
import com.dfq.coeffi.visitor.Entities.VisitorDocument;
import com.dfq.coeffi.visitor.Repositories.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
@Service
public class DocumentTypeServiceImpl implements DocumentTypeService
{
    @Autowired
    DocumentTypeRepository documentTypeRepository;

    @Override
    public DocumentType saveDocumentType(MultipartFile file) {
        DocumentType documentType= new DocumentType();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            documentType.setDocumentTypeName(fileName);
            documentType.setDocumentType(file.getContentType());

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/download/")
                    .path(documentType.getDocumentTypeName())
                    .toUriString();
            documentType.setDocumentDownloadUri(fileDownloadUri);
            documentType.setData(file.getBytes());
            DocumentType persistedDocument = documentTypeRepository.save(documentType);
            return persistedDocument;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }




}
