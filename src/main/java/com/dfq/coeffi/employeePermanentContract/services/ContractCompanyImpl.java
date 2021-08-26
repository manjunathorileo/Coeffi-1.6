package com.dfq.coeffi.employeePermanentContract.services;

import com.dfq.coeffi.employeePermanentContract.entities.ContractCompany;
import com.dfq.coeffi.employeePermanentContract.repositories.ContractCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ContractCompanyImpl implements ContractCompanyService {
    @Autowired
    ContractCompanyRepository contractCompanyRepository;

    @Override
    public ContractCompany saveContractCompany(ContractCompany contractCompany) {
        return contractCompanyRepository.save(contractCompany);
    }

    @Override
    public List<ContractCompany> getAllContractCompany() {
        return contractCompanyRepository.findAll();
    }

    @Override
    public ContractCompany getContractCompanyById(long cmpId) {
        return contractCompanyRepository.findOne(cmpId);
    }

    @Override
    public void deleteContractCompany(long cumpId) {
        contractCompanyRepository.delete(cumpId);

    }

    @Override
    public ContractCompany getByName(String companyName) {
        return contractCompanyRepository.findByCompanyName(companyName);
    }

    @Override
    public ContractCompany getByWorkOrderNumber(String workOrderNumber) {
        return contractCompanyRepository.findByWorkOrderNumber(workOrderNumber);
    }
}
