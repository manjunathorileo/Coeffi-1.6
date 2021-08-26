package com.dfq.coeffi.visitor.Entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@Getter
@Setter
public class DocumentType
{
    @Id
    @GeneratedValue
    private long id;
    private String documentTypeName;
    private String documentType;
    @Lob
    private byte[] data;
    private String documentDownloadUri;
}
