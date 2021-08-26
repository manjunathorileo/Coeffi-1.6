package com.dfq.coeffi.resource;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class EmployeeAttendanceResource {

	private long departmentId;
	private long designationId;
	private Date attendanceDate;
 	private int presentCount;
	private int absentCount;
	private int onLeaveCount;
    private int onEventCount;
    private String session;

 	private List<EmployeeAttendanceSheet> employeeAttendanceReport;
}
