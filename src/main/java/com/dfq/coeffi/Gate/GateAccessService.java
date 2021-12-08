package com.dfq.coeffi.Gate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GateAccessService {
    @Autowired
    GateAccessRepository gateAccessRepository;

    public void createGateAccess(GateAccessMssql gateAccessMssql){
        gateAccessRepository.save(gateAccessMssql);
    }


    public List<GateAccessMssql> getByEmployeeAndController(String employeeCode, String gateNumber) {
        List<GateAccessMssql> gateAccessMssqls = gateAccessRepository.findByEmpIdAndControllerCode(employeeCode,gateNumber);
        return gateAccessMssqls;
    }
}
