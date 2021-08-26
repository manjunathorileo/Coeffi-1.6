package com.dfq.coeffi.employeePermanentContract.controllers;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.ContractCompany;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import com.dfq.coeffi.employeePermanentContract.services.ContractCompanyService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class ContractCompanyController extends BaseController {

    @Autowired
    ContractCompanyService contractCompanyService;

    @Autowired
    PermanentContractRepo permanentContractService;


    @PostMapping("contract-company-save")
    public ResponseEntity<ContractCompany> saveCompany(@RequestBody ContractCompany contractCompany) {
        ContractCompany contractCompany1 = contractCompanyService.saveContractCompany(contractCompany);
        List<EmpPermanentContract> empPermanentContract = permanentContractService.findByContractCompany(contractCompany1.getCompanyName());
        for (EmpPermanentContract empPermanentContract1 : empPermanentContract) {
            empPermanentContract1.setContractCompany(contractCompany.getCompanyName());
            permanentContractService.save(empPermanentContract1);
        }
        return new ResponseEntity<>(contractCompany1, HttpStatus.CREATED);
    }

    @GetMapping("get-contract-companies")
    public ResponseEntity<List<ContractCompany>> getCompany() {
        List<ContractCompany> companyList = contractCompanyService.getAllContractCompany();
        List<ContractCompany> contractCompanyList = new ArrayList<>();
        for (ContractCompany contractCompany : companyList) {
            List<EmpPermanentContract> employee = permanentContractService.findByContractCompany(contractCompany.getCompanyName());
            if (!employee.isEmpty()) {
                contractCompany.setNoOfEmployees(employee.size());
            }
            Date date = new Date();
            if (contractCompany.getLicenseDate() != null) {
                if (DateUtil.getDifferenceDays(date, contractCompany.getLicenseDate()) <= 29) {
                    contractCompany.setLicenseStatus("Pass Expiring soon..");
                } else if (date.after(contractCompany.getLicenseDate())) {
                    contractCompany.setLicenseStatus("Pass Expired");
                }
            }
            contractCompanyList.add(contractCompany);
        }
        return new ResponseEntity<>(contractCompanyList, HttpStatus.OK);
    }

    @GetMapping("get-company-by-id/{cmpid}")
    public ResponseEntity<ContractCompany> getcompanyId(@PathVariable long cmpid) {
        ContractCompany contractCompany = contractCompanyService.getContractCompanyById(cmpid);
        return new ResponseEntity<>(contractCompany, HttpStatus.OK);
    }

    @DeleteMapping("delete-contract-company/{cmpid}")
    public void deleteCompany(@PathVariable long cmpid) {
        contractCompanyService.deleteContractCompany(cmpid);
    }

    @GetMapping("contract-company/{cmpId}/{status}")
    public ResponseEntity<ContractCompany> setPaymentToCompany(@PathVariable long cmpId, @PathVariable boolean status) {
        ContractCompany contractCompany = contractCompanyService.getContractCompanyById(cmpId);
        contractCompany.setPaymentApplicable(status);
        contractCompanyService.saveContractCompany(contractCompany);
        return new ResponseEntity<>(contractCompany, HttpStatus.OK);
    }

    @GetMapping("contract-company/check-exists/{workOrderNumber}/{id}")
    public ResponseEntity<ContractCompany> workOrderNumberExists(@PathVariable String workOrderNumber, @PathVariable long id) {
        ContractCompany contractCompany = contractCompanyService.getByWorkOrderNumber(workOrderNumber);
        if (contractCompany != null) {
            if (contractCompany.getId() == id) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
                throw new EntityNotFoundException("Enter another workOrderNumber");
            }
        } else {
            return new ResponseEntity(HttpStatus.OK);
        }
    }


}
