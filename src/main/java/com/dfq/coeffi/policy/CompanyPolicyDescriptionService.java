package com.dfq.coeffi.policy;


import java.util.List;

public interface CompanyPolicyDescriptionService {

    CompanyPolicyDescription createCompanyPolicy(CompanyPolicyDescription companyPolicyDescription);

    List<CompanyPolicyDescription> getAllCompanyPolicyDescription();

    CompanyPolicyDescription listCompanyPolicyDescription(long id);

    void deleteCompanyPolicyDescription(long id);
}
