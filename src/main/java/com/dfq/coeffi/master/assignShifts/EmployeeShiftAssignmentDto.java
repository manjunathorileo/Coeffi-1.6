package com.dfq.coeffi.master.assignShifts;

import com.dfq.coeffi.master.shift.Shift;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Setter
@Getter
public class EmployeeShiftAssignmentDto {
    private List<Long> employeeIds;
    private long shiftId;
    private Date fromDate;
    private Date toDate;
    private Date generatedDate;
    private String reason;
}
