package com.dfq.coeffi.entity.hr;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class DepartmentTrackerDto {

    private Date designationStartDate;

    private Date designationEndDate;

    private int firstWeekOff;

    private int secondWeekOff;
}
