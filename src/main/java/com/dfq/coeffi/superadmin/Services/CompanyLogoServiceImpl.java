package com.dfq.coeffi.superadmin.Services;

import com.dfq.coeffi.exception.FileStorageException;
import com.dfq.coeffi.superadmin.Entity.CompanyLogo;
import com.dfq.coeffi.superadmin.Repository.CompanyLogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@Service
public class CompanyLogoServiceImpl implements CompanyLogoService
{
    @Autowired
    CompanyLogoRepository companyLogoRepository;

    @Override
    public CompanyLogo saveCompanyLogo(MultipartFile file)
    {
        CompanyLogo companyLogo= new CompanyLogo();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            companyLogo.setDocumentTypeName(fileName);
            companyLogo.setDocumentType(file.getContentType());

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/download/")
                    .path(companyLogo.getDocumentTypeName())
                    .toUriString();
            companyLogo.setDocumentDownloadUri(fileDownloadUri);
            companyLogo.setData(file.getBytes());
            CompanyLogo persistedDocument = companyLogoRepository.save(companyLogo);
            return persistedDocument;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
