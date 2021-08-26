package com.dfq.coeffi.employeePermanentContract.repositories;


import com.dfq.coeffi.employeePermanentContract.entities.ContractCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;

@EnableJpaRepositories
@Transactional
public interface ContractCompanyRepository extends JpaRepository<ContractCompany,Long> {

    ContractCompany findByCompanyName(String companyName);

    ContractCompany findByWorkOrderNumber(String workOrderNumber);
}
