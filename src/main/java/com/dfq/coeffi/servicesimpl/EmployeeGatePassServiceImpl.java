package com.dfq.coeffi.servicesimpl;

import com.dfq.coeffi.entity.leave.EmployeeGatePass;
import com.dfq.coeffi.entity.leave.GatePassStatus;
import com.dfq.coeffi.repository.leave.EmployeeGatePassRepository;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.EmployeeGatePassService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeGatePassServiceImpl implements EmployeeGatePassService {

    private final EmployeeGatePassRepository employeeGatePassRepository;
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeGatePassServiceImpl(EmployeeGatePassRepository employeeGatePassRepository, EmployeeService employeeService){
        this.employeeGatePassRepository = employeeGatePassRepository;
        this.employeeService = employeeService;
    }

    @Override
    public EmployeeGatePass createEmployeeGatePass(EmployeeGatePass employeeGatePass) {
        return employeeGatePassRepository.save(employeeGatePass);
    }

    @Override
    public List<EmployeeGatePass> getApprovedEmployeeGatePass(GatePassStatus gatePassStatus, Date today) {
        return employeeGatePassRepository.getApprovedEmployeeGatePass(gatePassStatus, DateUtil.getTodayDate());
    }

    @Override
    public List<EmployeeGatePass> getApprovedEmployeeGatePass(GatePassStatus gatePassStatus) {
        return employeeGatePassRepository.getApprovedEmployeeGatePass(gatePassStatus);
    }

    @Override
    public Optional<EmployeeGatePass> getGatePassById(long id) {
        return Optional.ofNullable(employeeGatePassRepository.findOne(id));
    }

    @Override
    public List<EmployeeGatePass> getGatePassByEmployeeId(long employeeId) {
        return employeeGatePassRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<EmployeeGatePass> getEmployeeGatePassByEmployeeAndStatus(GatePassStatus gatePassStatus, long employeeId) {
        return employeeGatePassRepository.findByGatePassStatusAndEmployeeId(gatePassStatus,employeeId);
    }

    @Override
    public List<EmployeeGatePass> getEmployeeGatePassByFirstApproverAndStatus(GatePassStatus gatePassStatus, long employeeId) {
        return employeeGatePassRepository.findByGatePassStatusAndFirstApprover(gatePassStatus,employeeId);
    }

    @Override
    public List<EmployeeGatePass> getEmployeeGatePassBySecondApproverAndStatus(GatePassStatus gatePassStatus, long employeeId) {
        return employeeGatePassRepository.findByGatePassStatusAndSecondApprover(gatePassStatus,employeeId);
    }

    @Override
    public List<EmployeeGatePass> getAllGatePassByApprover(long firstApprover, long secondApprover) {
        return employeeGatePassRepository.getAllGatePassByApprover(firstApprover,secondApprover);
    }

}
