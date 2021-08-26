package com.dfq.coeffi.repository.payroll;

import javax.transaction.Transactional;

import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance,Long> {


    List<EmployeeAttendance> findByMarkedOn(Date todayDate);

    @Query("SELECT e FROM EmployeeAttendance e where e.markedOn between :startDate and :endDate")
    List<EmployeeAttendance> getEmployeeAttendanceBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT e FROM EmployeeAttendance e where e.employee.id = :employeeId and e.markedOn between :startDate and :endDate ORDER BY e.markedOn ASC ")
    List<EmployeeAttendance> getEmployeeAttendanceBetweenDateByEmployeeId(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("employeeId") Long employeeId);

    @Query("select s from EmployeeAttendance s where " +
            " (s.markedOn in (:todayDate)) and " +
            " (s.employee.department.id in (:departmentId)) and " +
            " (s.employee.designation.id in (:designationId))")
    List<EmployeeAttendance> findAttendanceByMarkedOnAndDepartmentIdAndDesignationId(@Param("todayDate") Date todayDate, @Param("departmentId") Long departmentId, @Param("designationId") Long designationId);

    @Query("SELECT e FROM EmployeeAttendance e where e.employee.id = :employeeId and e.attendanceStatus= :attendanceStatus and e.markedOn between :startDate and :endDate ")
    List<EmployeeAttendance> getEmployeeAttendance(@Param("employeeId") Long employeeId, @Param("attendanceStatus") AttendanceStatus attendanceStatus, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("select s from EmployeeAttendance s where " +
            " (date(s.markedOn) in (:todayDate) OR MONTH(s.markedOn) =(:monthName)) and " +
            " (s.employee.department.id in (:departmentId)) and " +
            " (:employeeType is NULL OR s.employee.employeeType in (:employeeType))")
    List<EmployeeAttendance> getTodayMarkedEmployeeAttendanceReport(@Param("todayDate") Date todayDate, @Param("monthName") int monthName, @Param("employeeType") EmployeeType employeeType, @Param("departmentId") long departmentId);

    @Query("SELECT e FROM EmployeeAttendance e where e.employee.id = :employeeId and e.attendanceStatus= :attendanceStatus and e.markedOn = :startDate ")
    EmployeeAttendance getEmployeeAttendanceByEmployeeIdAndStatus(@Param("employeeId") Long employeeId, @Param("attendanceStatus") AttendanceStatus attendanceStatus, @Param("startDate") Date todayDate);

    @Query("SELECT e FROM EmployeeAttendance e where e.employee.id = :employeeId and e.markedOn =:startDate")
    List<EmployeeAttendance> getEmployeeAttendanceByEmployeeId(@Param("startDate") Date startDate, @Param("employeeId") Long employeeId);

    @Query("SELECT e FROM EmployeeAttendance e where e.markedOn=:todayDate AND e.employee.department.id =:departmentId")
    List<EmployeeAttendance> getEmployeeAttendanceByDepartment(@Param("todayDate") Date todayDate, @Param("departmentId") long departmentId);
}
