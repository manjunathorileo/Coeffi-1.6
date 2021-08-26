package com.dfq.coeffi.dto.timesheetDto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WeekTimesheetDto {

    private long employeeId;
    private String activityName;
    private long projectId;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String totalWorkedHours;
}
