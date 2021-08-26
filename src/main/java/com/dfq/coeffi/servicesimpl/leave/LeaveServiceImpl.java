package com.dfq.coeffi.servicesimpl.leave;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.repository.leave.LeaveRepository;
import com.dfq.coeffi.service.leave.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService,Serializable

{
    private static final long serialVersionUID = 2819836741374331289L;
    @Autowired
    private LeaveRepository leaveRepository;

    @Override
    public Leave applyLeave(Leave leave) {
        leave.setLeaveRequestOn(new Date());
        return leaveRepository.save(leave);
    }

    @Override
    public List<Leave> getLeaves() {
        return leaveRepository.getLeaves();
    }

    @Override
    public List<Leave> getLeaveByRefNameAndRefId() {
        return null;
    }

    @Override
    public List<Leave> getLeaveByYear(long year) {
        return null;
    }

    @Override
    public Optional<Leave> getLeave(long id) {
        return Optional.ofNullable(leaveRepository.findOne(id));
    }

    @Override
    public boolean isLeaveExists(long id) {
        return leaveRepository.exists(id);
    }

    @Override
    public void deactivateLeave(long id) {

    }

    @Override
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    @Override
    public List<Leave> getCreatedEmployeeLeaveByRefNameRefNumber(LeaveStatus leaveStatus, Integer refId, String refName, AcademicYear academicYear) {
        return leaveRepository.findByRefIdAndRefNameAndAcademicYear(leaveStatus,refId,refName,academicYear);
    }

    @Override
    public List<Leave> getStudentAllLeaveByRefNameRefNumber(Integer refId, String refName, AcademicYear academicYear) {
        return leaveRepository.findStudentLeaveByRefIdAndRefNameAndAcademicYear(refId,refName,academicYear);
    }

    @Override
    public List<Leave> getCreatedLeaveCountByAcademicYear(LeaveStatus leaveStatus,AcademicYear academicYear) {
        return leaveRepository.getCreatedLeaveCountByAcademicYear(leaveStatus,academicYear);
    }

    @Override
    public void deleteLeave(LeaveStatus leaveStatus, long id) {
        leaveRepository.deleteLeave(leaveStatus,id);
    }

    @Override
    public List<Leave> getEmployeeLeaveDetails(LeaveStatus leaveStatus, LeaveType leaveType, Integer refId, String refName, AcademicYear academicYear, Date startDate, Date endDate) {
        return leaveRepository.getCountOfApprovedLeaveByRefIdAndDate(leaveStatus,leaveType,refId,refName,academicYear,startDate,endDate);
    }

    @Override
    public List<Leave> getApprovedOrRejectedLeaveList(LeaveStatus leaveStatus, AcademicYear academicYear) {
        return leaveRepository.getApprovedOrRejectedLeaveList(leaveStatus, academicYear);
    }

    @Override
    public List<Leave> checkCasulaLeaveContinuation(Integer refId, String refName, LeaveType leaveType, LeaveStatus leaveStatus, AcademicYear academicYear, Date startDate, Date endDate) {
        return leaveRepository.checkCasulaLeaveContinuation(refId,refName,leaveType,leaveStatus,academicYear,startDate,endDate);
    }

    @Override
    public List<Leave> getEmployeeApprovedOrRejectedLeaveList(Integer refId, String refName, LeaveStatus leaveStatus, AcademicYear academicYear) {
        return leaveRepository.getEmployeeApprovedOrRejectedLeaveList(refId,refName,leaveStatus,academicYear);
    }

    @Override
    public List<Leave> getApprovedLeavesForAttendance(LeaveStatus leaveStatus, AcademicYear academicYear, Date startDate, Date endDate) {
        return leaveRepository.getApprovedLeaveForAttendance(leaveStatus,academicYear,startDate,endDate);
    }

    @Override
    public List<Leave> getApprovedAndRejectedEmployeeleaveByRefNameRefNumber(Integer refId, String refName, AcademicYear academicYear) {
        return leaveRepository.findELeaveByRefIdAndRefNameAndAcademicYear(refId,refName,academicYear);
    }

    @Override
    public List<Leave> getCreatedLeaveCountByAcademicYearByRefIdAndRefName(LeaveStatus leaveStatus, AcademicYear academicYear, Integer refId, String refName) {
        return leaveRepository.getCreatedLeaveCountByAcademicYearByRefId(leaveStatus,academicYear,refId,refName);
    }

    @Override
    public List<Leave> getEmployeeApprovedLeaveByRefNameRefNumber(LeaveStatus leaveStatus, Integer refId, String refName, AcademicYear academicYear) {
        return leaveRepository.getEmployeeApprovedLeaveList(leaveStatus,refId,refName,academicYear);
    }

    @Override
    public List<Leave> getByFirstApprovalManagerId(Employee employee) {
        return leaveRepository.findByFirstApprovalManagerAndLeaveStatus(LeaveStatus.CREATED,employee);
    }

    @Override
    public List<Leave> getBySecondApprovalManagerId(LeaveStatus leaveStatus, Employee employee) {
        return leaveRepository.findBySecondApprovalManagerAndLeaveStatus(LeaveStatus.FORWARD,employee);
    }

    @Override
    public List<Leave> getAllLeavesByApprovalManager(Employee employee) {
        return leaveRepository.getAllLeavesByApprovalManager(employee);
    }

    @Override
    public List<Leave> getAllLeavesBySecondApprovalManager(Employee employee) {
        return leaveRepository.getAllLeavesBySecondApprovalManager(employee,LeaveStatus.APPROVED);
    }

    @Override
    public Leave getApprovedLeavesForAttendanceMark(LeaveStatus leaveStatus,Integer refId,Date todayDate) {
        return leaveRepository.getApprovedLeavesForAttendanceMark(leaveStatus,refId,todayDate);
    }
}