package com.dfq.coeffi.superadmin.Services;

import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;

import java.util.List;

public interface CompanyConfigureService {
    CompanyConfigure saveCompany(CompanyConfigure companyConfigure);

    List<CompanyConfigure> getCompany();

    void deleteCompany(long id);

    CompanyConfigure getCompanyById(long id);
}
