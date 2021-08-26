package com.dfq.coeffi.CompanySettings.Service;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Repository.CompanyNameRepository;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class CompanyNameServiceImpl implements CompanyNameService  {

    @Autowired
    CompanyNameRepository companyNameRepository;


    @Override
    public CompanyName saveCompany(CompanyName companyName) {
        return companyNameRepository.save(companyName) ;
    }

    @Override
    public List<CompanyName> getCompany() {
        List<CompanyName> companyNameList=companyNameRepository.findAll();
        return companyNameList;
    }

    @Override
    public CompanyName getCompanyById(long id) {
        CompanyName companyName=companyNameRepository.findOne(id);
        return companyName ;
    }

    @Override
    public void deleteCompany(long id) {
        companyNameRepository.delete(id);
    }
}
