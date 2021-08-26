package com.dfq.coeffi.policy;


import java.util.List;
import java.util.Optional;

public interface CompanyPolicyService {
 CompanyPolicy saveCompanyPolicy(CompanyPolicy companyPolicy);
 List<CompanyPolicy> getAllPolicy();
 Optional<CompanyPolicy> getCompanyPolicyById(long id);
}
