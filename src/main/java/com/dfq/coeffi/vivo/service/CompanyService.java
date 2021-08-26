package com.dfq.coeffi.vivo.service;

import com.dfq.coeffi.vivo.entity.Company;

import java.util.List;

public interface CompanyService {
     Company save(Company company);
     Company get(long id);
     List<Company> getAll();

}
