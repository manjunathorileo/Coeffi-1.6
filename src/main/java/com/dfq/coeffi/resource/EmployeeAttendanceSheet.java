package com.dfq.coeffi.resource;

import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Setter
@Getter
public class EmployeeAttendanceSheet {
	
	private long employeeId;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 45)
	private AttendanceStatus markedStatus;
}
