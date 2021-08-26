package com.dfq.coeffi.report;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<EmployeeAttendance> getTodayMarkedEmployeeAttendanceReport(Date todayDate,int monthName,long departmentId) {
        return reportRepository.getTodayMarkedEmployeeAttendanceReport(todayDate, monthName, departmentId);
    }

    @Override
    public List<EmployeeAttendance> getEmployeeAttendanceWeeklyReport(Date todayDate,Date toDate,EmployeeType employeeType, long departmentId) {
        return reportRepository.getEmployeeAttendanceWeeklyReport(todayDate,toDate,employeeType,departmentId);
    }

    @Override
    public List<Employee> getEmployeesByJoiningMonth(Date todayDate, int monthName, EmployeeType employeeType, long departmentId) {
        return reportRepository.getEmployeesByJoiningMonth(todayDate, monthName, employeeType, departmentId);
    }

    @Override
    public List<Employee> getEmployeesByLeavingMonth(Date todayDate, int monthName, EmployeeType employeeType, long departmentId) {
        return reportRepository.getEmployeesByLeavingMonth(todayDate, monthName, employeeType, departmentId);
    }

    @Override
    public List<EmployeeAttendance> getEmployeesExtraHours(Date todayDate, int monthName, EmployeeType employeeType, long departmentId) {
        return reportRepository.getEmployeesExtraHours(todayDate, monthName, employeeType, departmentId);
    }

    @Override
    public List<EmployeeSalaryProcess> getMonthlyProfessionalTaxReport(String monthName, String year) {
        return reportRepository.getMonthlyProfessionalTaxReport(monthName,year);
    }

    @Override
    public List<Leave> getMonthlyLeaveReport(LeaveStatus leaveStatus, int inputMonth,int year) {
        return reportRepository.getMonthlyLeaveReport(leaveStatus,inputMonth,year);
    }

    @Override
    public List<EmployeeSalaryProcess> getEmployeeSalaryMonthWiseReport(String monthName,String year) {
        return reportRepository.getEmployeeSalaryMonthWiseReport(monthName,year);
    }

    @Override
    public List<EmployeeSalaryProcess> getEmployeeSalaryProcessMonthlyYearlyESIC(String monthName, String inputYear) {
        return reportRepository.getEmployeeSalaryProcessMonthlyYearlyESIC(monthName, inputYear);
    }

    @Override
    public List<EmployeeSalaryProcess> getEmployeeSalaryProcessMonthlyYearlyPaySlip(String inputMonth, String inputYear) {
        return reportRepository.getEmployeeSalaryProcessMonthlyYearlyPaySlip(inputMonth, inputYear);
    }

    @Override
    public List<EmployeeSalaryProcess> getEmployeeMonthlyEPFSatement(String inputMonth, String inputYear) {
        return reportRepository.getEmployeeMonthlyEPFSatement(inputMonth,inputYear);
    }

    @Override
    public List<Employee> getAdultEmployeeRegister() {
        return reportRepository.getAdultEmployeeRegister();
    }

    @Override
    public List<EmployeeAttendance> getTodayMarkedEmployeeAbsentReport(Date todayDate, int monthName, EmployeeType employeeType, long departmentId) {
        return reportRepository.getTodayMarkedEmployeeAbsentReport(todayDate, monthName, employeeType, departmentId);
    }

    @Override
    public List<EmployeeAttendance> getEmployeeAbsentWeeklyReport(Date todayDate, Date toDate, EmployeeType employeeType, long departmentId) {
        return reportRepository.getEmployeeAbsentWeeklyReport(todayDate,toDate,employeeType,departmentId);
    }
}
