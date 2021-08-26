package com.dfq.coeffi.E_Learning.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadResponse {

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadResponse(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }
}


