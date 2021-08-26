package com.dfq.coeffi.FeedBackManagement.Services;

import com.dfq.coeffi.CanteenManagement.Entity.FoodImage;
import com.dfq.coeffi.FeedBackManagement.Entity.GradeImage;
import com.dfq.coeffi.FeedBackManagement.Repository.GradeImageRepository;
import com.dfq.coeffi.SOPDetails.Exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
@Service
public class GradeImageServiceImpl implements GradeImageService {

    @Autowired
    GradeImageRepository gradeImageRepository;

    @Override
    public GradeImage saveGradeFile(MultipartFile file) {
        GradeImage document=new GradeImage();
        String fileName= StringUtils.cleanPath(file.getOriginalFilename());
        if (file.getSize() > 16777216)
        {
            throw new EntityNotFoundException("Sorry! File size contains more than 16MB");
        }

        try
        {
            if (fileName.contains(".."))
            {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            document.setFileName(fileName) ;
            document.setFileType(file.getContentType()) ;
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(document.getFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setData(file.getBytes());
            GradeImage persistedDocument = gradeImageRepository.save(document);
            return persistedDocument;
        }
        catch (IOException | FileStorageException ex)
        {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!",ex);
        }
    }

    @Override
    public GradeImage getByGradeFileId(long fileId) {
        return gradeImageRepository.findOne(fileId);
    }
}
