package com.dfq.coeffi.StoreManagement.Service;

import com.dfq.coeffi.StoreManagement.Entity.EmployeeRequest;
import com.dfq.coeffi.StoreManagement.Repository.EmployeeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeRequestServiceImpl implements EmployeeRequestService{

    @Autowired
    EmployeeRequestRepository employeeRequestRepository;
    @Override
    public EmployeeRequest saveRequest(EmployeeRequest employeeRequest) {
        return employeeRequestRepository.save(employeeRequest) ;
    }

    @Override
    public EmployeeRequest getRequest(long id) {
        return employeeRequestRepository.findOne(id) ;
    }

    @Override
    public List<EmployeeRequest> getRequests() {
        return employeeRequestRepository.findAll();
    }

    @Override
    public EmployeeRequest getByEmpId(long employeeId) {
        return employeeRequestRepository.findByEmployeeId(employeeId) ;
    }
}
