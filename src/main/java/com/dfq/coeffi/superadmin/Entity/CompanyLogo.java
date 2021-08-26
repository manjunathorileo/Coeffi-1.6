package com.dfq.coeffi.superadmin.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CompanyLogo
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String documentTypeName;
    private String documentType;
    @Lob
    private byte[] data;
    private String documentDownloadUri;


}
