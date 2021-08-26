package com.dfq.coeffi.employeePermanentContract.repositories;

import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@EnableJpaRepositories
@Transactional
public interface PermanentContractAttendanceRepo extends JpaRepository<PermanentContractAttendance, Long> {

    @Query("SELECT p FROM PermanentContractAttendance p where p.empId = :employeeId and p.markedOn =:startDate")
    PermanentContractAttendance getEmployeeAttendanceByEmployeeId(@Param("startDate") Date startDate, @Param("employeeId") long employeeId);

    @Query("SELECT e FROM PermanentContractAttendance e where e.markedOn between :startDate and :endDate ORDER BY e.inTime ASC")
    List<PermanentContractAttendance> getEmployeeAttendanceBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT e FROM PermanentContractAttendance e where e.empId = :employeeId and e.markedOn between :startDate and :endDate ORDER BY e.markedOn ASC ")
    List<PermanentContractAttendance> getEmployeeAttendanceBetweenDateByEmployeeId(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("employeeId") Long employeeId);

    List<PermanentContractAttendance> findByMarkedOn(Date markedOn);

    @Query(" SELECT e FROM PermanentContractAttendance e where e.markedOn = :todayDate ORDER BY e.markedOn ASC")
    List<PermanentContractAttendance> findByMarkedOnAsc(@Param("todayDate") Date todayDate);

    @Query("SELECT e FROM PermanentContractAttendance e ORDER BY e.recordedTime ASC ")
    List<PermanentContractAttendance> findByAsc();

    @Query("SELECT p FROM PermanentContractAttendance p where p.empId = :employeeId and p.markedOn =:startDate")
    List<PermanentContractAttendance> getEmployeeAttendanceByEmployeeIdGEBE(@Param("startDate") Date startDate, @Param("employeeId") long employeeId);

    PermanentContractAttendance findByEmployeeCodeAndInTime(String employeeCode, Date entryDate);

    List<PermanentContractAttendance> findByEmployeeCodeAndOutTime(String employeeCode, Date entryDate);

    @Query("SELECT e FROM PermanentContractAttendance e where e.markedOn=:todayDate AND e.empPermanentContract.department.id =:departmentId")
    List<PermanentContractAttendance> getEmployeeAttendanceByDepartment(@Param("todayDate") Date todayDate, @Param("departmentId") long departmentId);


}
