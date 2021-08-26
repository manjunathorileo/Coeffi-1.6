package com.dfq.coeffi.SOPDetails.SopDocument.doc;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocService
{
    //Document file save API
    Doc saveDocument(MultipartFile file, String desc, long did);

    //Document file view by id
    Doc getDocumentFileById(long fileId);

    //view all Document files
    List<Doc> getAllDocument();

    // Get Document file by sopid
    Doc getWordBySopId(SopCategory SOPCategory);

    //delete Document file by id
    void deleteDocumentById(long id);


    Doc saveDocuments(MultipartFile file);
}
