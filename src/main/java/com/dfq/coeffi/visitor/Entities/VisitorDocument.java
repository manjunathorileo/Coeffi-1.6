package com.dfq.coeffi.visitor.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class VisitorDocument {
    @Id
    @GeneratedValue
    private long id;
    private String visitorImgDoc;
    private String visitorImgType;
    @Lob
    private byte[] data;
    private String imgDownloadUri;


}
