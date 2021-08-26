package com.dfq.coeffi.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CompanyPolicyDescriptionServiceImpl implements CompanyPolicyDescriptionService {
    @Autowired
    CompanyPolicyDescriptionRepository companyPolicyDescriptionRepository;

    @Override
    public CompanyPolicyDescription createCompanyPolicy(CompanyPolicyDescription companyPolicyDescription) {
        return companyPolicyDescriptionRepository.save(companyPolicyDescription);
    }

    @Override
    public List<CompanyPolicyDescription> getAllCompanyPolicyDescription() {
        return companyPolicyDescriptionRepository.findAll();
    }

    @Override
    public CompanyPolicyDescription listCompanyPolicyDescription(long id) {
        return companyPolicyDescriptionRepository.findOne(id);
    }

    @Override
    public void deleteCompanyPolicyDescription(long id) {
        companyPolicyDescriptionRepository.deleteCompanyPolicyDescription(id);
    }
}
