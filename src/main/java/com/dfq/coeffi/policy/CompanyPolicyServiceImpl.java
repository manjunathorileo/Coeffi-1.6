package com.dfq.coeffi.policy;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CompanyPolicyServiceImpl implements CompanyPolicyService {

    private CompanyPolicyRepository companyPolicyRepository;

    @Autowired
    public CompanyPolicyServiceImpl(CompanyPolicyRepository companyPolicyRepository){
        this.companyPolicyRepository = companyPolicyRepository;
    }

    @Override
    public CompanyPolicy saveCompanyPolicy(CompanyPolicy companyPolicy) {
        return companyPolicyRepository.save(companyPolicy);
    }

    @Override
    public List<CompanyPolicy> getAllPolicy() {
        return companyPolicyRepository.findAll();
    }

    @Override
    public Optional<CompanyPolicy> getCompanyPolicyById(long id) {
        return companyPolicyRepository.findById(id);
    }
}
