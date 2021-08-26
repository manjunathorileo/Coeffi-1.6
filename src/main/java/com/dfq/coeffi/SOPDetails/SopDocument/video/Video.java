package com.dfq.coeffi.SOPDetails.SopDocument.video;

import com.dfq.coeffi.SOPDetails.SOPCategory.SopCategory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
public class Video{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String videoFileName;
    private long videoFileSize;
    private String videoFileDescription;
    private String videoFileType;
    @Lob
    private byte[] data;
    private String fileDownloadUri;

    @OneToOne
    private SopCategory sopCategory;



}
