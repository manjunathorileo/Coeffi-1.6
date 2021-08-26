package com.dfq.coeffi.SOPDetails.SopDocument.doc;

import com.dfq.coeffi.SOPDetails.Exceptions.FileStorageException;
import com.dfq.coeffi.SOPDetails.Exceptions.MyFileNotFoundException;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryRepository;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DocServiceImpl implements DocService {
    @Autowired
    DocRepository docRepository;
    @Autowired
    SopCategoryRepository SOPCategoryRepository;
    @Autowired
    SopCategoryService SOPCategoryService;

    //Document file save API
    @Override
    public Doc saveDocument(MultipartFile file, String desc, long did) {
        Doc document = new Doc();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.getSize() > 16777216) {
            throw new EntityNotFoundException("Sorry! File size contains more than 16MB");
        }

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            document.setDocumentFileName(fileName);
            document.setDocumentFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(document.getDocumentFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setDocumentFileDescription(desc);
            Optional<SopCategory> digitalSOP = SOPCategoryService.getSopCategory(did);
            document.setSopCategory(digitalSOP.get());
            document.setData(file.getBytes());
            Doc persistedDocument = docRepository.save(document);
            return persistedDocument;
        } catch (IOException | FileStorageException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    //Document file view by id
    @Override
    public Doc getDocumentFileById(long fileId) {
        return docRepository.findById(fileId).orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }

    //view all Document files
    @Override
    public List<Doc> getAllDocument() {
        return docRepository.findAll();
    }

    // Get Document file by sopid
    @Override
    public Doc getWordBySopId(SopCategory SOPCategory) {
        return docRepository.findBySopCategory(SOPCategory);
    }

    //delete Document file by id
    @Override
    public void deleteDocumentById(long id) {
        docRepository.delete(id);

    }

    @Override
    public Doc saveDocuments(MultipartFile file) {
        Doc document = new Doc();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.getSize() > 10485760) {
            throw new EntityNotFoundException("Sorry! File size contains more than 16MB");
        }try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            document.setDocumentFileName(fileName);
            document.setDocumentFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(document.getDocumentFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setData(file.getBytes());
            Doc persistedDocument = docRepository.save(document);
            return persistedDocument;
        } catch (IOException | FileStorageException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

}
