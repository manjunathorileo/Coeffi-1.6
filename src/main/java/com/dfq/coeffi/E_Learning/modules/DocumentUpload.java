package com.dfq.coeffi.E_Learning.modules;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class DocumentUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String fileName;
    private Long fileSize;
    private String title;
    private String description;
    @Lob
    private byte[] data;
    @OneToOne
    private Product product;
    private String fileType;
    private String url;
    public boolean status;

    @CreationTimestamp
    private Date createdOn;

    public DocumentUpload() {
    }

    public DocumentUpload(String fileName, String fileType, byte[] data, long fileSize) {
        this.fileName = fileName;
        this.data = data;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}