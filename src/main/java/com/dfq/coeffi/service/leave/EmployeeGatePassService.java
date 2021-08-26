package com.dfq.coeffi.service.leave;

import com.dfq.coeffi.entity.leave.EmployeeGatePass;
import com.dfq.coeffi.entity.leave.GatePassStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeGatePassService {
    EmployeeGatePass createEmployeeGatePass(EmployeeGatePass employeeGatePass);
    List<EmployeeGatePass> getApprovedEmployeeGatePass(GatePassStatus gatePassStatus, Date today);
    List<EmployeeGatePass> getApprovedEmployeeGatePass(GatePassStatus gatePassStatus);
    Optional<EmployeeGatePass> getGatePassById(long id);
    List<EmployeeGatePass> getGatePassByEmployeeId(long employeeId);
    List<EmployeeGatePass> getEmployeeGatePassByEmployeeAndStatus(GatePassStatus gatePassStatus, long employeeId);

    List<EmployeeGatePass> getEmployeeGatePassByFirstApproverAndStatus(GatePassStatus gatePassStatus, long employeeId);
    List<EmployeeGatePass> getEmployeeGatePassBySecondApproverAndStatus(GatePassStatus gatePassStatus, long employeeId);
    List<EmployeeGatePass> getAllGatePassByApprover(long firstApprover, long secondApprover);

}
