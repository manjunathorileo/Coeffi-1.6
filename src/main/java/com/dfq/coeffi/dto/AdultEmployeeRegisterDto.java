package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class AdultEmployeeRegisterDto {
    private long departmentId;
    private String nameAddress;
    private String age;
    private String fatherName;
    private String jobTitle;
    private String departmentName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfJoining;

    private EmployeeType employeeType;
}
