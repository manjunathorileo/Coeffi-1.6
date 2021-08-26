package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.FoodImage;
import com.dfq.coeffi.CanteenManagement.Repository.FoodImageRepository;
import com.dfq.coeffi.SOPDetails.Exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Service
public class FoodImageServiceImpl implements FoodImageService {

    @Autowired
    FoodImageRepository foodImageRepository;

    @Override
    public FoodImage saveMenuFile(MultipartFile file) {
        FoodImage document=new FoodImage();
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
            FoodImage persistedDocument = foodImageRepository.save(document);
            return persistedDocument;
        }
        catch (IOException | FileStorageException ex)
        {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!",ex);
        }
    }

    @Override
    public FoodImage getByMenuFileId(long fileId) {
        return foodImageRepository.findOne(fileId);
    }
}
