package com.dfq.coeffi.policy.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@ToString
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String fileName;
    @Lob
    private byte[] data;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    @CreationTimestamp
    private Date createdOn;
}