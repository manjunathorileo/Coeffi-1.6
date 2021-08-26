package com.dfq.coeffi.SOPDetails.SopDocument.doc;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
public class Doc
{

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
}
