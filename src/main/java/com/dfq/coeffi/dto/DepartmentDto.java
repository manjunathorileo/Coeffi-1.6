package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.hr.Department;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class DepartmentDto {

    private List<Department> departmentList;

}
