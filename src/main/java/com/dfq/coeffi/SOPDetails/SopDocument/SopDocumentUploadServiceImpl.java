package com.dfq.coeffi.SOPDetails.SopDocument;

import com.dfq.coeffi.SOPDetails.Exceptions.FileStorageException;
import com.dfq.coeffi.SOPDetails.Exceptions.MyFileNotFoundException;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryRepository;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.SOPDetails.SOPType.SopTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SopDocumentUploadServiceImpl implements SopDocumentUploadService {

    @Autowired
    SopDocumentUploadRepository docRepository;

    @Override
    public SopDocumentUpload saveDocument(MultipartFile file, SopDocumentUpload saveSopDocumentUpload) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename()) + ".V" + saveSopDocumentUpload.getDocVersion();
        if (file.getSize() > 16777216) {
            throw new EntityNotFoundException("Sorry! File size contains more than 16MB");
        }
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            saveSopDocumentUpload.setDocumentFileName(fileName);
            saveSopDocumentUpload.setDocumentFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(saveSopDocumentUpload.getDocumentFileName())
                    .toUriString();
            saveSopDocumentUpload.setFileDownloadUri(fileDownloadUri);
            saveSopDocumentUpload.setData(file.getBytes());
            SopDocumentUpload persistedDocument = docRepository.save(saveSopDocumentUpload);
            return persistedDocument;
        } catch (IOException | FileStorageException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Optional<SopDocumentUpload> getDocumentFileById(long id) {
        return docRepository.findById(id);
    }

    @Override
    public List<SopDocumentUpload> getDocBySopTypeBySopCategory(long sopTypeId, long sopCategoryId, SopDocumentType sopDocumentType) {
        List<SopDocumentUpload> sopDocumentUploads = new ArrayList<>();
        List<SopDocumentUpload> sopDocumentUploadList = docRepository.findBySopTypeBySopCategory(sopTypeId, sopCategoryId, sopDocumentType);
        for (SopDocumentUpload sopDocumentUpload: sopDocumentUploadList) {
            if (sopDocumentUpload.getStatus().equals(true)){
                sopDocumentUploads.add(sopDocumentUpload);
            }
        }
        return sopDocumentUploads;
    }

    @Override
    public List<SopDocumentUpload> getWordBySopId(SopCategory SOPCategory) {
        List<SopDocumentUpload> sopDocumentUploads = new ArrayList<>();
        List<SopDocumentUpload> sopDocumentUploadList = docRepository.findBySopCategory(SOPCategory);
        for (SopDocumentUpload sopDocumentUpload: sopDocumentUploadList) {
            if (sopDocumentUpload.getStatus().equals(true)){
                sopDocumentUploads.add(sopDocumentUpload);
            }
        }
        return sopDocumentUploads;
    }

    @Override
    public SopDocumentUpload deleteDocumentFileById(long id) {
        Optional<SopDocumentUpload> sopDocumentUploadOptional = docRepository.findById(id);
        SopDocumentUpload sopDocumentUpload = sopDocumentUploadOptional.get();
        sopDocumentUpload.setStatus(false);
        SopDocumentUpload deletedSopDocument = docRepository.save(sopDocumentUpload);
        return deletedSopDocument;
    }
}
