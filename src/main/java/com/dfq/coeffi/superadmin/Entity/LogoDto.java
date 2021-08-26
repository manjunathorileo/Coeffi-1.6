package com.dfq.coeffi.superadmin.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

import javax.persistence.Lob;

@Getter
@Setter
public class LogoDto
{
    @Lob
    private byte[] data1;
    private String a;
    private byte[] data;
    private Resource resource;
}
