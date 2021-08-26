package com.dfq.coeffi.employeePermanentContract.services;

import com.dfq.coeffi.employeePermanentContract.entities.ContractCompany;

import java.util.List;

public interface ContractCompanyService
{
    ContractCompany saveContractCompany(ContractCompany contractCompany);

    List<ContractCompany> getAllContractCompany();

    ContractCompany getContractCompanyById(long cmpId);

    void deleteContractCompany(long cumpId);

    ContractCompany getByName(String companyName);

    ContractCompany getByWorkOrderNumber(String workOrderNumber);
}
