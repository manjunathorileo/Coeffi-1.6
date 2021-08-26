package com.dfq.coeffi.service.leave;
/**
 * @Auther H Kapil Kumar on 21/3/18.
 * @Company Orileo Technologies
 */

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;

public interface LeaveService {
    Leave applyLeave(Leave leave);

    List<Leave> getLeaves();

    List<Leave> getLeaveByRefNameAndRefId();

    List<Leave> getLeaveByYear(long year);

    Optional<Leave> getLeave(long id);

    boolean isLeaveExists(long id);

    void deactivateLeave(long id);

    List<Leave> getAllLeaves();

    List<Leave> getCreatedEmployeeLeaveByRefNameRefNumber(LeaveStatus leaveStatus, Integer refId, String refName, AcademicYear academicYear);

    List<Leave> getStudentAllLeaveByRefNameRefNumber(Integer refId, String refName, AcademicYear academicYear);

    List<Leave> getCreatedLeaveCountByAcademicYear(LeaveStatus leaveStatus, AcademicYear academicYear);

    void deleteLeave(LeaveStatus leaveStatus, long id);

    List<Leave> getEmployeeLeaveDetails(LeaveStatus leaveStatus, LeaveType leaveType, Integer refId, String refName, AcademicYear academicYear, Date startDate, Date endDate);

    List<Leave> getApprovedOrRejectedLeaveList(LeaveStatus leaveStatus, AcademicYear academicYear);

    List<Leave> checkCasulaLeaveContinuation(Integer refId, String refName, LeaveType leaveType, LeaveStatus leaveStatus, AcademicYear academicYear, Date startDate, Date endDate);

    List<Leave> getEmployeeApprovedOrRejectedLeaveList(Integer refId, String refName, LeaveStatus leaveStatus, AcademicYear academicYear);

    List<Leave> getApprovedLeavesForAttendance(LeaveStatus leaveStatus, AcademicYear academicYear, Date startDate, Date endDate);

    List<Leave> getApprovedAndRejectedEmployeeleaveByRefNameRefNumber(Integer refId, String refName, AcademicYear academicYear);

    List<Leave> getCreatedLeaveCountByAcademicYearByRefIdAndRefName(LeaveStatus leaveStatus, AcademicYear academicYear, Integer refId, String refName);

    List<Leave> getEmployeeApprovedLeaveByRefNameRefNumber(LeaveStatus leaveStatus, Integer refId, String refName, AcademicYear academicYear);

    List<Leave> getByFirstApprovalManagerId(Employee employee);

    List<Leave> getBySecondApprovalManagerId(LeaveStatus leaveStatus, Employee employee);

    List<Leave> getAllLeavesByApprovalManager(Employee employee);

    List<Leave> getAllLeavesBySecondApprovalManager(Employee employee);

    Leave getApprovedLeavesForAttendanceMark(LeaveStatus leaveStatus, Integer refId, Date todayDate);

}