package com.dfq.coeffi.dto;


import com.dfq.coeffi.entity.hr.Department;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class DepartmentDashboard {
    private String deprtmentName;
    private long noOfEmployee;

    private long permanentPresent;
    private long permanentAbsent;
    private long contractPresent;
    private long contractAbsent;
    private long departmentId;

}
