package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Company;
import com.dfq.coeffi.vivo.repository.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImp implements CompanyService {
    @Autowired
    CompanyRepo companyRepo;
    @Override
    public Company save(Company company) {
        return companyRepo.save(company);
    }

    @Override
    public Company get(long id) {
        return companyRepo.findOne(id);
    }

    @Override
    public List<Company> getAll() {
        return companyRepo.findAll();
    }
}
