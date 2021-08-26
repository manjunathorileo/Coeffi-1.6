package com.dfq.coeffi.SOPDetails.SopDocument;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface SopDocumentUploadService
{
    Optional<SopDocumentUpload> getDocumentFileById(long fileId);

    SopDocumentUpload deleteDocumentFileById(long id);

    SopDocumentUpload saveDocument(MultipartFile file, SopDocumentUpload saveSopDocumentUpload);

    List<SopDocumentUpload> getDocBySopTypeBySopCategory(long sopTypeId, long sopCategoryId, SopDocumentType sopDocumentType);

    List<SopDocumentUpload> getWordBySopId(SopCategory SOPCategory);
}
