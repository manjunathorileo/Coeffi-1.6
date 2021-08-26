package com.dfq.coeffi.Gate.Entity;

import com.dfq.coeffi.advanceFoodManagementExtra.EmployeeTypeAdvanced;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class EmployeeGateAssignmentDto {

    private List<Long> employeeIds;
    private EmployeeType employeeType;
    private List<Long> inGateIds;
    private List<Long> outGateIds;
    private long empId;


}
