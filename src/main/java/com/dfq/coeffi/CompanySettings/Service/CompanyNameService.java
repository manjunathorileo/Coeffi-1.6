package com.dfq.coeffi.CompanySettings.Service;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import jdk.nashorn.internal.runtime.options.Option;

import java.util.List;

public interface CompanyNameService {

    CompanyName saveCompany(CompanyName companyName );
    List<CompanyName> getCompany();
    CompanyName getCompanyById(long id);
    void deleteCompany(long id);
}
