package com.dfq.coeffi.superadmin.Services;

import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Repository.CompanyConfigureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyConfigureImpl implements CompanyConfigureService {
    @Autowired
    CompanyConfigureRepository configureRepository;

    @Override
    public CompanyConfigure saveCompany(CompanyConfigure companyConfigure) {
        return configureRepository.save(companyConfigure);
    }

    @Override
    public List<CompanyConfigure> getCompany() {
        return configureRepository.findAll();
    }

    @Override
    public void deleteCompany(long id) {
        configureRepository.delete(id);
    }

    @Override
    public CompanyConfigure getCompanyById(long id) {
        return configureRepository.findOne(id);
    }
}
