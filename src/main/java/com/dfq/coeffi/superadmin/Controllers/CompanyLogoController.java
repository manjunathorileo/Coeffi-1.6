package com.dfq.coeffi.superadmin.Controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.superadmin.Entity.CompanyLogo;
import com.dfq.coeffi.superadmin.Entity.LogoDto;
import com.dfq.coeffi.superadmin.Repository.CompanyLogoRepository;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import com.dfq.coeffi.superadmin.Services.CompanyLogoService;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.sql.Blob;

@RestController
public class CompanyLogoController extends BaseController
{
    @Autowired
    CompanyLogoService companyLogoService;

    @Autowired
    CompanyLogoRepository companyLogoRepository;

    @Autowired
    CompanyConfigureService companyConfigureService;

    @PostMapping("super-admin-logo-save")
    public ResponseEntity<CompanyLogo> saveCompany(@RequestParam("file") MultipartFile file)
    {
        CompanyLogo companyLogo =companyLogoService.saveCompanyLogo(file);
        if(companyLogo==null) {
            throw new EntityNotFoundException("No image");
        }
        return new ResponseEntity<>(companyLogo, HttpStatus.OK);
    }

    @GetMapping("super-admin-view-logo/{id}")
    public ResponseEntity<Blob> viewImage(@PathVariable("id") long id) throws IOException {
        CompanyLogo companyLogo= companyLogoRepository.findOne(id);
        LogoDto logoDto=new LogoDto();
        logoDto.setData1(companyLogo.getData());
        logoDto.setA(String.valueOf(companyLogo.getData()));
        Resource r= new ByteArrayResource(companyLogo.getData());

        System.out.println("** "+companyLogo.getData());
        byte[] encoded = Base64.encode((companyLogo.getData()));
        logoDto.setData(encoded);
        logoDto.setResource(r);
        return new ResponseEntity(r,HttpStatus.OK);
    }

}
