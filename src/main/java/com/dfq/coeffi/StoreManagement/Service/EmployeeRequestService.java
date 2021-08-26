package com.dfq.coeffi.StoreManagement.Service;

import com.dfq.coeffi.StoreManagement.Entity.EmployeeRequest;

import java.util.List;

public interface EmployeeRequestService {

    EmployeeRequest saveRequest(EmployeeRequest employeeRequest);
    EmployeeRequest getRequest(long id);
    List<EmployeeRequest> getRequests();
    EmployeeRequest getByEmpId(long employeeId);
}
