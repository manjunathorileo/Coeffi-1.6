package com.dfq.coeffi.group;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GroupDto {

    private long id;
    private long groupEmployeeLeadId;
    private List<Long> employeeIds;

    private String title;
    private String description;

}