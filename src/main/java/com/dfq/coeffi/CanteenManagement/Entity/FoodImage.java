package com.dfq.coeffi.CanteenManagement.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class FoodImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String FileName;
    private long FileSize;
    private String FileDescription;
    private String FileType;
    @Lob
    private byte[] data;
    private String fileDownloadUri;
}
