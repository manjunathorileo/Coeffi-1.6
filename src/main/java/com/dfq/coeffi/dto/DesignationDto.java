package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.hr.Designation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class DesignationDto {

    private List<Designation> designationList;
}
