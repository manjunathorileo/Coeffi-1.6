package com.dfq.coeffi.Gate.Entity;

import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.OneToMany;
import java.util.List;

@Setter
@Getter
public class EmployeeGateDto {
    private long employeeNumber;
    private String employeeName;
    private EmployeeType  employeeType;
    private String Department;
    private String employeeCode;
    private List<Gate> inGates;
    private List<Gate> outGates;
    private String inGatesRef;
    private String outGatesRef;
}
