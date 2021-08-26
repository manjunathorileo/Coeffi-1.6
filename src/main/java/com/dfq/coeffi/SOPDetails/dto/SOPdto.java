package com.dfq.coeffi.SOPDetails.dto;

import com.dfq.coeffi.SOPDetails.SopDocument.SopDocumentUpload;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SOPdto
{

    private SopDocumentUpload document;
    private SopDocumentUpload audio;
    private SopDocumentUpload video;
}
