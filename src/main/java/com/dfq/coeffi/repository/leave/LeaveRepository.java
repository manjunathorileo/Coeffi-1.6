package com.dfq.coeffi.repository.leave;

import java.util.Date;
import java.util.List;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaveRepository extends JpaRepository<Leave, Long>
{
	@Query("select l from Leave l where l.leaveStatus= :leaveStatus and l.refId= :refId and l.refName= :refName and l.academicYear= :academicYear")
	List<Leave> findByRefIdAndRefNameAndAcademicYear(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("refId") Integer refId, @Param("refName") String refName, @Param("academicYear") AcademicYear academicYear);

	List<Leave> findStudentLeaveByRefIdAndRefNameAndAcademicYear(Integer refId, String refName, AcademicYear academicYear);

	@Query("select l from Leave l where l.leaveStatus='CREATED'")
	List<Leave> getLeaves();

	@Query("select count(*) from Leave l where l.leaveStatus= :leaveStatus and l.academicYear= :academicYear")
	List<Leave> getCreatedLeaveCountByAcademicYear(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("academicYear") AcademicYear academicYear);

	@Query("delete from Leave l where l.id= :id and l.leaveStatus= :leaveStatus")
	@Modifying
	public void deleteLeave(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("id") Long id);

	@Query("select l from Leave l where l.leaveStatus= :leaveStatus and l.leaveType= :leaveType and l.refId= :refId and l.refName= :refName and l.academicYear= :academicYear and l.leaveRequestOn between :startDate and :endDate")
	List<Leave> getCountOfApprovedLeaveByRefIdAndDate(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("leaveType") LeaveType leaveType, @Param("refId") Integer refId, @Param("refName") String refName, @Param("academicYear") AcademicYear academicYear, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("select l from Leave l where l.leaveStatus= :leaveStatus and l.academicYear= :academicYear")
	List<Leave> getApprovedOrRejectedLeaveList(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("academicYear") AcademicYear academicYear);

	@Query("select l from Leave l where l.refId= :refId and l.refName= :refName and l.leaveType= :leaveType and l.leaveStatus= :leaveStatus and l.academicYear= :academicYear and l.leaveRequestOn between :startDate and :endDate")
	List<Leave> checkCasulaLeaveContinuation(@Param("refId") Integer refId, @Param("refName") String refName, @Param("leaveType") LeaveType leaveType, @Param("leaveStatus") LeaveStatus leaveStatus, @Param("academicYear") AcademicYear academicYear, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("select l from Leave l where l.refId= :refId and l.refName= :refName and l.leaveStatus= :leaveStatus and l.academicYear= :academicYear")
	List<Leave> getEmployeeApprovedOrRejectedLeaveList(@Param("refId") Integer refId, @Param("refName") String refName, @Param("leaveStatus") LeaveStatus leaveStatus, @Param("academicYear") AcademicYear academicYear);

	@Query("select l from Leave l where l.refId= :refId and l.refName= :refName and l.academicYear= :academicYear")
	List<Leave> getEmployeeAllLeaveList(@Param("refId") Integer refId, @Param("refName") String refName, @Param("academicYear") AcademicYear academicYear);

	@Query("select l from Leave l where l.leaveStatus= :leaveStatus and l.academicYear= :academicYear and l.leaveRequestOn between :startDate and :endDate")
	List<Leave> getApprovedLeaveForAttendance(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("academicYear") AcademicYear academicYear, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

	@Query("from Leave l where l.leaveStatus in ('APPROVED','REJECTED') and l.refId= :refId and l.refName= :refName and l.academicYear= :academicYear")
	List<Leave> findELeaveByRefIdAndRefNameAndAcademicYear(@Param("refId") Integer refId, @Param("refName") String refName, @Param("academicYear") AcademicYear academicYear);

	@Query("select count(*) from Leave l where l.leaveStatus= :leaveStatus and l.academicYear= :academicYear and l.refId= :refId and l.refName= :refName")
	List<Leave> getCreatedLeaveCountByAcademicYearByRefId(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("academicYear") AcademicYear academicYear, @Param("refId") Integer refId, @Param("refName") String refName);

	@Query("select l from Leave l where l.leaveStatus= :leaveStatus and l.refId= :refId and l.refName= :refName and l.academicYear= :academicYear")
	List<Leave> getEmployeeApprovedLeaveList(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("refId") Integer refId, @Param("refName") String refName, @Param("academicYear") AcademicYear academicYear);

	@Query("select l from Leave l where l.leaveStatus= :leaveStatus and l.firstApprovalManager =:employee")
	List<Leave> findByFirstApprovalManagerAndLeaveStatus(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("employee") Employee employee);

	@Query("select l from Leave l where l.leaveStatus= :leaveStatus and l.secondApprovalManager =:employee")
	List<Leave> findBySecondApprovalManagerAndLeaveStatus(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("employee") Employee employee);

	@Query("select l from Leave l where l.firstApprovalManager =:employee")
	List<Leave> getAllLeavesByApprovalManager(@Param("employee") Employee employee);

	@Query("select l from Leave l where l.secondApprovalManager =:employee and l.leaveStatus =:leaveStatus")
	List<Leave> getAllLeavesBySecondApprovalManager(@Param("employee") Employee employee, @Param("leaveStatus") LeaveStatus leaveStatus);

	@Query("select l from Leave l where l.leaveStatus= :leaveStatus and l.refId= :refId and (:todayDate BETWEEN l.leaveStartDate AND l.leaveEndDate)")
	Leave getApprovedLeavesForAttendanceMark(@Param("leaveStatus") LeaveStatus leaveStatus, @Param("refId") Integer refId, @Param("todayDate") Date todayDate);
}
