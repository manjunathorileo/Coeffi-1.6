package com.dfq.coeffi.policy.document;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.exception.FileStorageException;
import com.dfq.coeffi.exception.TPFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentCategoryRepository documentCategoryRepository;
    private final Path fileStorageLocation;

    @Value("${file.uploadDir}")
    private String uploadDir;

    @Value("${file.uploadDocumentDir}")
    private String uploadDocumentDir;

    @Value("${file.uploadCompanyPolicyDir}")
    private String uploadCompanyPolicyDir;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public Document storeFile(MultipartFile file) {

        Document document = new Document();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.getSize() > 200097152) {
            throw new EntityNotFoundException("Sorry! File size contains more than 200MB");
        }

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            document.setFileName(fileName);
            document.setSize(file.getSize());
            document.setFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/download/")
                    .path(document.getFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setData(file.getBytes());
            Document persistedDocument = documentRepository.save(document);
            return persistedDocument;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }


    @Override
    public Resource loadFileAsResource(String fileName) throws IOException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new TPFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new TPFileNotFoundException("File not found " + fileName, ex);
        }
    }

    @Override
    public DocumentCategory createDocumentCategory(DocumentCategory documentCategory) {
        return documentCategoryRepository.save(documentCategory);
    }

    @Override
    public DocumentCategory getDocumentCategory(long id) {
        return documentCategoryRepository.findOne(id);
    }

    @Override
    public List<DocumentCategory> getDocumentCategories() {
        return documentCategoryRepository.findAll();
    }

    @Override
    public Document getDocument(long id) {
        return documentRepository.findOne(id);
    }

    // private static String UPLOAD_FOLDER = "F:\\software\\coeffi\\ui\\html\\pdf\\";
    //private static String UPLOAD_FOLDER = "/home/orileo/Desktop/Orileo/build/co-effi/html/pdf/";

    @Override
    public Document storePolicyDocument(MultipartFile file) throws IOException {
        Document document = new Document();
        String tempFile = file.getOriginalFilename();
        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadCompanyPolicyDir + tempFile);
        Files.write(path, bytes);
        document.setFileDownloadUri(uploadCompanyPolicyDir);
        document.setFileName(tempFile);
        document.setSize(file.getSize());
        document.setData(file.getBytes());
        document.setFileType(file.getContentType());
        Document persistedDocument = documentRepository.save(document);
        return persistedDocument;
    }

//    @Override
//    public Document storeEmployeeKycDocument(MultipartFile file, Employee employee, String prefix) throws IOException {
//        Document document = new Document();
//        String tempFile = file.getOriginalFilename();
//        byte[] bytes = file.getBytes();
////        uploadDocumentDir= ServletUriComponentsBuilder.fromCurrentContextPath()
////                .path("/api/v1/download/")
////                .path(document.getFileName())
////                .toUriString();
//
//        Path path = Paths.get(uploadDocumentDir + prefix + "_" + employee.getEmployeeCode() + "_" + employee.getFirstName() + "." + tempFile.substring(tempFile.lastIndexOf(".") + 1));
//        Files.write(path, bytes);
//        document.setFileDownloadUri(uploadDocumentDir);
//        document.setFileName(employee.getEmployeeCode() + "_" + employee.getFirstName() + "." + tempFile.substring(tempFile.lastIndexOf(".") + 1));
//        document.setSize(file.getSize());
//        document.setData(file.getBytes());
//        document.setFileType(file.getContentType());
//        Document persistedDocument = documentRepository.save(document);
//        return persistedDocument;
//    }

    @Override
    public Document storeEmployeeKycDocument(MultipartFile file, Employee employee, String prifix) {

        Document document = new Document();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.getSize() > 2097152) {
            throw new EntityNotFoundException("Sorry! File size contains more than 2MB");
        }

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            document.setFileName(fileName);
//            document.setFileName(employee.getEmployeeCode() + "_" + employee.getFirstName() + "." + fileName.substring(fileName.lastIndexOf(".") + 1));

            document.setSize(file.getSize());
            document.setFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/download/")
                    .path(document.getFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);
            document.setData(file.getBytes());
            Document persistedDocument = documentRepository.save(document);
            return persistedDocument;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

}