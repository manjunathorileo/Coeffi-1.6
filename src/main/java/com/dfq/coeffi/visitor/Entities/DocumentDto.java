package com.dfq.coeffi.visitor.Entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

import javax.persistence.Lob;

@Getter
@Setter
public class DocumentDto {

    @Lob
    private byte[] data1;
    @Lob
    private String a;
    @Lob
    private byte[] data;
    @Lob
    private Resource resource;


}
