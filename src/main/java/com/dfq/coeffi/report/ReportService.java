package com.dfq.coeffi.report;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;

import java.util.Date;
import java.util.List;

public interface ReportService {

    List<EmployeeAttendance> getTodayMarkedEmployeeAttendanceReport(Date todayDate, int monthName, long departmentId);

    List<EmployeeAttendance> getEmployeeAttendanceWeeklyReport(Date todayDate, Date toDate, EmployeeType employeeType, long departmentId);

    List<Employee> getEmployeesByJoiningMonth(Date todayDate, int monthName, EmployeeType employeeType, long departmentId);

    List<Employee> getEmployeesByLeavingMonth(Date todayDate, int monthName, EmployeeType employeeType, long departmentId);

    List<EmployeeAttendance> getEmployeesExtraHours(Date todayDate, int monthName, EmployeeType employeeType, long departmentId);

    List<EmployeeSalaryProcess> getMonthlyProfessionalTaxReport(String monthName, String year);

    List<Leave> getMonthlyLeaveReport(LeaveStatus leaveStatus, int inputMonth, int inputYear);

    List<EmployeeSalaryProcess> getEmployeeSalaryMonthWiseReport(String monthName, String year);

    List<EmployeeSalaryProcess> getEmployeeSalaryProcessMonthlyYearlyESIC(String monthName, String inputYear);

    List<EmployeeSalaryProcess> getEmployeeSalaryProcessMonthlyYearlyPaySlip(String inputMonth, String inputYear);

    List<EmployeeSalaryProcess> getEmployeeMonthlyEPFSatement(String inputMonth, String inputYear);

    List<Employee> getAdultEmployeeRegister();

    List<EmployeeAttendance> getTodayMarkedEmployeeAbsentReport(Date todayDate, int monthName, EmployeeType employeeType, long departmentId);

    List<EmployeeAttendance> getEmployeeAbsentWeeklyReport(Date todayDate, Date toDate, EmployeeType employeeType, long departmentId);
}
