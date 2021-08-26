package com.dfq.coeffi.resource.timesheet;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class TimesheetResource {

    private long employeeId;
    private long projectId;
    private List<Integer> activityDetailIds;

}
