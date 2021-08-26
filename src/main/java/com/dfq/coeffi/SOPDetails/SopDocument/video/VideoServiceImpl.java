package com.dfq.coeffi.SOPDetails.SopDocument.video;

import com.dfq.coeffi.SOPDetails.Exceptions.FileStorageException;
import com.dfq.coeffi.SOPDetails.Exceptions.MyFileNotFoundException;
import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
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
public class VideoServiceImpl implements VideoService
{
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    SopCategoryService SOPCategoryService;

    @Override
    public Video saveVideo(MultipartFile file, String desc, long did)
    {
        Video document=new Video();
        String fileName= StringUtils.cleanPath(file.getOriginalFilename());
        if (file.getSize() > 16777216) {
            throw new EntityNotFoundException("Sorry! File size contains more than 16MB");
        }

        try
        {
            if (fileName.contains(".."))
            {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            document.setVideoFileName(fileName);
            document.setVideoFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(document.getVideoFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setVideoFileDescription(desc);
            Optional<SopCategory> digitalSOP = SOPCategoryService.getSopCategory(did);
            document.setSopCategory(digitalSOP.get());
            document.setData(file.getBytes());
            Video persistedDocument = videoRepository.save(document);
            return persistedDocument;
        }
        catch (IOException | FileStorageException ex)
        {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!",ex);
        }
    }

    @Override
    public Video getVideoFileById(long fileId) {
        return videoRepository.findById(fileId).orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }

    @Override
    public List<Video> getAllVideo()
    {
        return videoRepository.findAll();
    }

    @Override
    public Video getVideoBySopId(SopCategory SOPCategory) {
        return videoRepository.findBySopCategory(SOPCategory);
    }

    @Override
    public void deleteVideoByid(long id)
    {
        videoRepository.delete(id);

    }
}
