package com.dfq.coeffi.resource.timesheet;

import com.dfq.coeffi.entity.timesheet.AttachResource;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProjectResource {

    private long employeeId;
    private long projectId;
    private List<Integer> activityIds;
    private List<Integer> toolIds;
}
