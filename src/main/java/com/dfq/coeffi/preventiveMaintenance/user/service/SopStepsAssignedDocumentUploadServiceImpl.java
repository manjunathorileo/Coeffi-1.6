package com.dfq.coeffi.preventiveMaintenance.user.service;

import com.dfq.coeffi.SOPDetails.Exceptions.FileStorageException;
import com.dfq.coeffi.SOPDetails.Exceptions.MyFileNotFoundException;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryRepository;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssignedDocumentUpload;
import com.dfq.coeffi.preventiveMaintenance.user.repository.SopStepsAssignedDocumentUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class SopStepsAssignedDocumentUploadServiceImpl implements SopStepsAssignedDocumentUploadService {
    @Autowired
    SopStepsAssignedDocumentUploadRepository sopStepsAssignedDocumentUploadRepository;

    @Override
    public SopStepsAssignedDocumentUpload saveDocument(MultipartFile file, SopType sopType, SopCategory SOPCategory){
        SopStepsAssignedDocumentUpload document = new SopStepsAssignedDocumentUpload();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.getSize() > 16777216) {
            throw new EntityNotFoundException("Sorry! File size contains more than 16MB");
        }
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            document.setDocumentFileName(fileName);
            document.setDocumentFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(document.getDocumentFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setSopCategory(SOPCategory);
            document.setData(file.getBytes());
            document.setSopType(sopType);
            SopStepsAssignedDocumentUpload persistedDocument = sopStepsAssignedDocumentUploadRepository.save(document);
            return persistedDocument;
        } catch (IOException | FileStorageException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public SopStepsAssignedDocumentUpload getDocumentFileById(long fileId) {
        return sopStepsAssignedDocumentUploadRepository.findById(fileId);
    }

    @Override
    public List<SopStepsAssignedDocumentUpload> getAllDocument() {
        return sopStepsAssignedDocumentUploadRepository.findAll();
    }

    @Override
    public SopStepsAssignedDocumentUpload getWordBySopId(SopCategory SOPCategory) {
        return sopStepsAssignedDocumentUploadRepository.findBySopCategory(SOPCategory);
    }

    @Override
    public void deleteDocumentById(long id) {
        sopStepsAssignedDocumentUploadRepository.delete(id);
    }

    @Override
    public SopStepsAssignedDocumentUpload saveDocuments(MultipartFile file) {
        SopStepsAssignedDocumentUpload document = new SopStepsAssignedDocumentUpload();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.getSize() > 10485760) {
            throw new EntityNotFoundException("Sorry! File size contains more than 16MB");
        }try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            document.setDocumentFileName(fileName);
            document.setDocumentFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(document.getDocumentFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setData(file.getBytes());
            SopStepsAssignedDocumentUpload persistedDocument = sopStepsAssignedDocumentUploadRepository.save(document);
            return persistedDocument;
        } catch (IOException | FileStorageException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

}
