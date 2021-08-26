package com.dfq.coeffi.Gate.Entity;

import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserManagementDto {
    private long employeeId;
    private String employeeName;
    private EmployeeType employeeType;
    private String department;
    private String userName;
    private String password;
    private String employeeCode;
}
