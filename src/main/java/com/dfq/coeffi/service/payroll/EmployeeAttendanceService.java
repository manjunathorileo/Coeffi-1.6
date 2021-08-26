package com.dfq.coeffi.service.payroll;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;

public interface EmployeeAttendanceService {
	
	EmployeeAttendance createEmployeeAttendance(EmployeeAttendance employeeAttendance,boolean processed);
	
	Optional<EmployeeAttendance> getEmployeeAttendance(long id);

    List<EmployeeAttendance> getTodayMarkedEmployeeAttendance(Date todayDate);

    List<EmployeeAttendance> getAllEmployeeAttendance();

	void deleteEmployeeAttendance(long id);

	List<EmployeeAttendance> getEmployeetWeekAttendance(Date startDate, Date endDate);

	List<EmployeeAttendance> getEmployeeMontlyAttendanceByEmployeeId(Date startDate, Date endDate, long employeeId);

	List<EmployeeAttendance> getTodayMarkedEmployeeAttendanceByDeptIdAndDesgId(Date todayDate, long departmentId, long designationId);

	List<EmployeeAttendance> getEmployeeAttendanceByEmployeeIdAndStatus(long employeeId, AttendanceStatus attendanceStatus, Date startDate, Date endDate);

	EmployeeAttendance getEmployeeAttendanceByEmployeeIdAndStatus(long employeeId, AttendanceStatus attendanceStatus, Date todayDate);

	EmployeeAttendance getEmployeeAttendanceByEmployeeId(Date startDate, long employeeId);

	List<EmployeeAttendance> getEmployeeAttendanceByDepartment(Date startDate, long departmentId);

}
