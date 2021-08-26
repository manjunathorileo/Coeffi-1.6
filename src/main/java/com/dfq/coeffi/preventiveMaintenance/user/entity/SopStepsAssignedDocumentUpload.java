package com.dfq.coeffi.preventiveMaintenance.user.entity;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import com.dfq.coeffi.SOPDetails.SOPType.SopType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class SopStepsAssignedDocumentUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String documentFileName;
    private long documentFileSize;
    private String documentFileDescription;
    private String documentFileType;
    @Lob
    private byte[] data;
    private String fileDownloadUri;
    @OneToOne
    private SopCategory sopCategory;
    @OneToOne
    private SopType sopType;
}
