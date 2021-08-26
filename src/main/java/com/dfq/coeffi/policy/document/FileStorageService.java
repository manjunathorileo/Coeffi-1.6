package com.dfq.coeffi.policy.document;

import com.dfq.coeffi.entity.hr.employee.Employee;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {

    Document storeFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);

    DocumentCategory createDocumentCategory(DocumentCategory documentCategory);
    DocumentCategory getDocumentCategory(long id);
    List<DocumentCategory> getDocumentCategories();
    Document getDocument(long id);

    Document storePolicyDocument(MultipartFile file) throws IOException;

    Document storeEmployeeKycDocument(MultipartFile file, Employee employee, String prefix) throws IOException;

}