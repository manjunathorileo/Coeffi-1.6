package com.dfq.coeffi.SOPDetails.SopDocument.audio;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
public class Audio{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String audioFileName;
    private long audioFileSize;
    private String audioFileDescription;
    private String audioFileType;
    @Lob
    private byte[] data;
    private String fileDownloadUri;

    @OneToOne
    private SopCategory sopCategory;



}
