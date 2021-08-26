package com.dfq.coeffi.Gate.Service;

import com.dfq.coeffi.Gate.Entity.EmployeeGateAssignment;
import com.dfq.coeffi.Gate.Repository.EmployeeGateAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeGateAssignmentServiceImpl implements EmployeeGateAssignmentService {
    @Autowired
    EmployeeGateAssignmentRepository employeeGateAssignmentRepository;
    @Override
    public EmployeeGateAssignment saveEmployeeGate(EmployeeGateAssignment employeeGateAssignment) {
        return employeeGateAssignmentRepository.save(employeeGateAssignment);
    }
}
