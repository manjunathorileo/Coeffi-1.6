package com.dfq.coeffi.superadmin.Services;

import com.dfq.coeffi.superadmin.Entity.CompanyLogo;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyLogoService
{
    CompanyLogo saveCompanyLogo(MultipartFile file);

}
