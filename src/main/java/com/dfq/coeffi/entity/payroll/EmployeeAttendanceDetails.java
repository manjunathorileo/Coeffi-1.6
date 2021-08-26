package com.dfq.coeffi.entity.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther H Kapil Kumar on 17/5/18.
 * @Company Orileo Technologies
 */

@Entity
@Setter
@Getter
public class EmployeeAttendanceDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 45)
	private AttendanceStatus attendanceStatus;
	
	@ManyToOne
	private Employee employee;
}
