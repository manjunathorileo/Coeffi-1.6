package com.dfq.coeffi.SOPDetails.SopDocument.audio;

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
public class AudioServiceImpl implements AudioService
{

    @Autowired
    AudioRepository audioRepository;
    @Autowired
    SopCategoryRepository SOPCategoryRepository;
    @Autowired
    SopCategoryService SOPCategoryService;

    //Audio file save API
    @Override
    public Audio saveAudio(MultipartFile file, String desc, long did)
    {
        Audio document=new Audio();
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
            document.setAudioFileName(fileName);
            document.setAudioFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(document.getAudioFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setAudioFileDescription(desc);
            Optional<SopCategory> digitalSOP = SOPCategoryService.getSopCategory(did);
            document.setSopCategory(digitalSOP.get());
            document.setData(file.getBytes());
            Audio persistedDocument = audioRepository.save(document);
            return persistedDocument;
        }
        catch (IOException | FileStorageException ex)
        {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!",ex);
        }
    }

    //Audio file view by id
    @Override
    public Audio getAudioFileById(long fileId)
    {
        return audioRepository.findById(fileId).orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId));
    }

    //view all Audio files
    @Override
    public List<Audio> getAllAudio()
    {
        return audioRepository.findAll();
    }

    //get Audio file by sopid
    @Override
    public Audio getAudioBySopId(SopCategory SOPCategory)
    {
        return audioRepository.findBySopCategory(SOPCategory);
    }

    //delete audio file by id
    @Override
    public void deleteWAudioByid(long id)
    {
        audioRepository.deleteById(id);

    }
}
