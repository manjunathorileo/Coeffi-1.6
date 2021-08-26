package com.dfq.coeffi.visitor.Services;

import com.dfq.coeffi.exception.FileStorageException;
import com.dfq.coeffi.visitor.Entities.VisitorDocument;
import com.dfq.coeffi.visitor.Repositories.VisitorImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;


@Service
public class VisitorImageServiceImplementation implements VisitorImageService {
    @Autowired
    private final VisitorImageRepository visitorImageRepository;

    public VisitorImageServiceImplementation(VisitorImageRepository visitorImageRepository) {
        this.visitorImageRepository = visitorImageRepository;
    }

    @Override
    public VisitorDocument saveImage(MultipartFile file) {
        VisitorDocument visitorDocument = new VisitorDocument();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            visitorDocument.setVisitorImgDoc(fileName);
            visitorDocument.setVisitorImgType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/download/")
                    .path(visitorDocument.getVisitorImgDoc())
                    .toUriString();
            visitorDocument.setImgDownloadUri(fileDownloadUri);
            visitorDocument.setData(file.getBytes());
            VisitorDocument persistedDocument = visitorImageRepository.save(visitorDocument);
            return persistedDocument;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
