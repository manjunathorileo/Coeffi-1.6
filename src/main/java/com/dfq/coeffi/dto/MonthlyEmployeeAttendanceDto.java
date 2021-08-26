package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class MonthlyEmployeeAttendanceDto {

    private String employeeName;
    private long employeeId;
    private String employeeCode;
    private long departmentId;
    private String departmentName;
    private long designationId;
    private String designationName;
    private List<MonthlyStatusDto> monthlyStatus;
    private String companyName;
    private String leaveHalfType;
    private String employeeType;
}
