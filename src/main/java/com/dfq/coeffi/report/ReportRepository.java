package com.dfq.coeffi.report;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReportRepository extends JpaRepository<EmployeeAttendance,Long> {

    @Query("select s from EmployeeAttendance s where " +
            " (date(s.markedOn) in (:todayDate) OR MONTH(s.markedOn) =(:monthName)) and " +
            " (s.employee.department.id in (:departmentId))")
    List<EmployeeAttendance> getTodayMarkedEmployeeAttendanceReport(@Param("todayDate") Date todayDate, @Param("monthName") int monthName, @Param("departmentId") long departmentId);

    @Query("SELECT e FROM Employee e WHERE (date(e.dateOfJoining) in (:todayDate) OR MONTH(e.dateOfJoining) =(:monthName))  and " +
            "(e.department.id in (:departmentId)) and " +
            "(:employeeType is NULL OR e.employeeType in (:employeeType)) AND e.status=true")
    List<Employee> getEmployeesByJoiningMonth(@Param("todayDate") Date todayDate, @Param("monthName") int monthName, @Param("employeeType") EmployeeType employeeType, @Param("departmentId") long departmentId);

    @Query("SELECT e FROM Employee e WHERE ((date(e.dateOfLeaving) in (:todayDate) OR MONTH(e.dateOfLeaving) =(:monthName)) and " +
            " (e.department.id in (:departmentId)) and " +
            " (:employeeType is NULL OR e.employeeType in (:employeeType))) AND e.status=false")
    List<Employee> getEmployeesByLeavingMonth(@Param("todayDate") Date todayDate, @Param("monthName") int monthName, @Param("employeeType") EmployeeType employeeType, @Param("departmentId") long departmentId);

    @Query("select s from EmployeeAttendance s where " +
            " (date(s.markedOn) BETWEEN :todayDate AND :toDate) and " +
            " (s.employee.department.id in (:departmentId)) and " +
            " (:employeeType is NULL OR s.employee.employeeType in (:employeeType))")
    List<EmployeeAttendance> getEmployeeAttendanceWeeklyReport(@Param("todayDate") Date todayDate, @Param("toDate") Date toDate, @Param("employeeType") EmployeeType employeeType, @Param("departmentId") long departmentId);

    @Query("select s from EmployeeAttendance s where " +
            " (date(s.markedOn) in (:todayDate) OR MONTH(s.markedOn) =(:monthName)) and " +
            " (s.employee.department.id in (:departmentId)) and " +
            " (:employeeType is NULL OR s.employee.employeeType in (:employeeType))")
    List<EmployeeAttendance> getEmployeesExtraHours(@Param("todayDate") Date todayDate, @Param("monthName") int monthName, @Param("employeeType") EmployeeType employeeType, @Param("departmentId") long departmentId);

    @Query("SELECT es FROM EmployeeSalaryProcess es WHERE es.salaryMonth =:monthName and es.salaryYear =:year")
    List<EmployeeSalaryProcess> getMonthlyProfessionalTaxReport(@Param("monthName") String monthName, @Param("year") String year);

    @Query("select l from Leave l where l.leaveStatus= :leaveStatus and (MONTH(l.leaveStartDate)=(:monthName) OR MONTH(l.leaveEndDate)=(:monthName) AND (YEAR(l.leaveStartDate)=(:year) OR YEAR(l.leaveEndDate)=(:year)))")
    List<Leave> getMonthlyLeaveReport(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("monthName") int monthName, @Param("year") int year);

    @Query("select s from EmployeeSalaryProcess s where " +
            " s.salaryMonth =:monthName and s.salaryYear =:year")
    List<EmployeeSalaryProcess> getEmployeeSalaryMonthWiseReport(@Param("monthName") String monthName, @Param("year") String year);


    @Query("SELECT es FROM EmployeeSalaryProcess es where" +
            "((es.salaryMonth =:monthName AND es.salaryYear =:inputYear))")
    List<EmployeeSalaryProcess> getEmployeeSalaryProcessMonthlyYearlyESIC(@Param("monthName") String monthName,
                                                                          @Param("inputYear") String inputYear);
    @Query("SELECT es FROM EmployeeSalaryProcess es where" +
            " (:inputMonth is NULL OR  (MONTH(es.salaryProcessingDate) = (:inputMonth))) OR " +
            "(es.salaryYear in (:inputYear))")
    List<EmployeeSalaryProcess> getEmployeeSalaryProcessMonthlyYearlyPaySlip(@Param("inputMonth") String inputMonth,
                                                                             @Param("inputYear") String inputYear);

    @Query("SELECT es FROM EmployeeSalaryProcess es where" +
            "(es.salaryMonth =:inputMonth) AND " +
            "(es.salaryYear in (:inputYear))")
    List<EmployeeSalaryProcess> getEmployeeMonthlyEPFSatement(@Param("inputMonth") String inputMonth, @Param("inputYear") String inputYear);

    @Query("SELECT e FROM Employee e")
    List<Employee> getAdultEmployeeRegister();

    @Query("select s from EmployeeAttendance s where " +
            " (date(s.markedOn) in (:todayDate) OR MONTH(s.markedOn) =(:monthName)) and " +
            " (s.employee.department.id in (:departmentId)) and " +
            " (:employeeType is NULL OR s.employee.employeeType in (:employeeType)) and"+
            "(s.attendanceStatus = 'ABSENT')")
    List<EmployeeAttendance> getTodayMarkedEmployeeAbsentReport(@Param("todayDate") Date todayDate, @Param("monthName") int monthName, @Param("employeeType") EmployeeType employeeType, @Param("departmentId") long departmentId);

    @Query("select s from EmployeeAttendance s where " +
            " (date(s.markedOn) BETWEEN :todayDate AND :toDate) and " +
            " (s.employee.department.id in (:departmentId)) and " +
            " (:employeeType is NULL OR s.employee.employeeType in (:employeeType)) and " +
            "(s.attendanceStatus = 'ABSENT')")
    List<EmployeeAttendance> getEmployeeAbsentWeeklyReport(Date todayDate, Date toDate, EmployeeType employeeType, long departmentId);


}
