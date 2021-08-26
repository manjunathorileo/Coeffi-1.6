package com.dfq.coeffi.SOPDetails.SopDocument;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadDto {

    private String description;
    private long sopTypeId;
    private long sopCategoryId;
    private long loggerId;
    private long oldDocId;
}
