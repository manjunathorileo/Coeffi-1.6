package com.dfq.coeffi.preventiveMaintenance.user.service;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import com.dfq.coeffi.preventiveMaintenance.user.entity.SopStepsAssignedDocumentUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SopStepsAssignedDocumentUploadService
{
    SopStepsAssignedDocumentUpload saveDocument(MultipartFile file, SopType sopType, SopCategory SOPCategory);

    SopStepsAssignedDocumentUpload getDocumentFileById(long fileId);

    List<SopStepsAssignedDocumentUpload> getAllDocument();

    SopStepsAssignedDocumentUpload getWordBySopId(SopCategory SOPCategory);

    void deleteDocumentById(long id);

    SopStepsAssignedDocumentUpload saveDocuments(MultipartFile file);
}
