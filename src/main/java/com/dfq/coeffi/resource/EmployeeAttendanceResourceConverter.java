package com.dfq.coeffi.resource;

import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeAttendanceResourceConverter {

	@Autowired
	private EmployeeService employeeService;


    public EmployeeAttendance toEntity(EmployeeAttendanceResource employeeResource, EmployeeAttendanceSheet attendanceSheet) {
        EmployeeAttendance employeeAttendance = new EmployeeAttendance();
        employeeAttendance.setAttendanceStatus(attendanceSheet.getMarkedStatus());
        employeeAttendance.setEmployee(employeeService.getEmployee(attendanceSheet.getEmployeeId()).get());
        employeeAttendance.setMarkedOn(DateUtil.convertDateToFormat(employeeResource.getAttendanceDate()));
        return employeeAttendance;
    }


}
