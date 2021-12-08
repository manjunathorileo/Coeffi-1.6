package com.dfq.coeffi.aboutCoeffi;

import com.dfq.coeffi.policy.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AboutDto {

    private About about;
    private List<AboutDocumentsDto> aboutDocumentsDtos;

    private long id;
    private String productName;
    private String version;
    private Date releaseDate;
    private String description;
    private String modules;
    private List<Long> documentIds;

}
