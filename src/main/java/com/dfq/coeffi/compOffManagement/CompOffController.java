package com.dfq.coeffi.compOffManagement;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.master.assignShifts.EmployeeShiftAssignmentDto;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.LeaveService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static com.dfq.coeffi.util.DateUtil.getDaysBetweenDates;

@RestController
@Slf4j
public class CompOffController extends BaseController {

    @Autowired
    CompOffRepository compOffRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    LeaveService leaveService;

    @Autowired
    CompOffTrackerRepository compOffTrackerRepository;

    @Autowired
    EmployeeAttendanceService employeeAttendanceService;

    @Autowired
    DatesForCompOffRepo datesForCompOffRepo;


    /**
     * Generate compoff
     *
     * @param compOff
     */
    @PostMapping("compoff")
    public void generateCompOff(@RequestBody CompOff compOff) {

        Optional<Employee> employee = employeeService.getEmployee(compOff.getEmployeeId());

        compOff.setFirstName(employee.get().getFirstName() + employee.get().getLastName());
        compOff.setCompOffStatus(LeaveStatus.CREATED);
        compOff.setEmployeeCode(employee.get().getEmployeeCode());

        if (employee.get().getFirstApprovalManager() != null) {
            compOff.setFirstMgrId(employee.get().getFirstApprovalManager().getId());
        } else {
            throw new EntityNotFoundException("Please assign reporting manager for this employee");
        }

        if (employee.get().getSecondApprovalManager() != null) {
            compOff.setSecondMgrId(employee.get().getSecondApprovalManager().getId());
        }
        compOffRepository.save(compOff);
    }

    /**
     * GetAll
     *
     * @param empId
     * @return
     */
    @GetMapping("comoff-generated/{empId}")
    public ResponseEntity<List<CompOff>> getEmpCompOffs(@PathVariable long empId) {
        List<CompOff> compOff = compOffRepository.findByEmployeeId(empId);
        return new ResponseEntity<>(compOff, HttpStatus.OK);
    }

    /**
     * Delete
     *
     * @param id
     */
    @DeleteMapping("comoff-delete/{id}")
    public void deleteCompOff(@PathVariable long id) {
        compOffRepository.delete(id);
    }

    /**
     * List for manager approval
     *
     * @param mgrId
     * @return
     */
    @GetMapping("compoff-manager-approval/{mgrId}")
    public ResponseEntity<List<CompOff>> getCompOffForApproval(@PathVariable long mgrId) {
        List<CompOff> compOffList = compOffRepository.findByFirstMgrId(mgrId);
        List<CompOff> compOffListCreated = new ArrayList<>();
        for (CompOff compOff : compOffList) {
            if (compOff.getCompOffStatus().equals(LeaveStatus.CREATED)) {
                compOffListCreated.add(compOff);
            }
        }
        Collections.reverse(compOffListCreated);
        return new ResponseEntity<>(compOffListCreated, HttpStatus.OK);
    }

    @GetMapping("compoff-approve/{mgrId}/{id}")
    public void approve(@PathVariable long mgrId, @PathVariable long id) {
        long availed = 0;
        long generated = 0;
        CompOff compOff = compOffRepository.findOne(id);
        compOff.setCompOffStatus(LeaveStatus.APPROVED);
        Optional<Employee> employeef = employeeService.getEmployee(compOff.getFirstMgrId());
        compOff.setCompOffApprovedBy(employeef.get().getFirstName() + employeef.get().getLastName());
        compOffRepository.save(compOff);
        //TODO tracker
        //-------------tracker---------------
        String month = DateUtil.getMonthName(compOff.getCompOffGenDate());
        String year = String.valueOf(DateUtil.getCurrentYear());
        CompOffTracker compOffTracker = compOffTrackerRepository.findByEmployeeIdAndMonthAndYear(compOff.getEmployeeId(), month, year);
        if (compOffTracker != null) {
            availed = compOffTracker.getCompOffAvailedDays();
            generated = compOffTracker.getCompOffGeneratedDays();

            compOffTracker.setCompOffAvailedDays(availed);
            compOffTracker.setCompOffGeneratedDays(generated + 1);

            List<DatesForCompOff> genDates = compOffTracker.getGeneratedDates();

            DatesForCompOff datesForCompOff = new DatesForCompOff();
            datesForCompOff.setGeneratedDates(compOff.getCompOffGenDate());
            datesForCompOffRepo.save(datesForCompOff);

            genDates.add(datesForCompOff);

            compOffTracker.setGeneratedDates(genDates);
            compOffTracker.setBalance(compOffTracker.getCompOffGeneratedDays() - compOffTracker.getCompOffAvailedDays());
            compOffTrackerRepository.save(compOffTracker);
        } else {
            compOffTracker = new CompOffTracker();
            compOffTracker.setCompOffAvailedDays(availed);
            compOffTracker.setCompOffGeneratedDays(generated + 1);

            List<DatesForCompOff> genDates = new ArrayList<>();

            DatesForCompOff datesForCompOff = new DatesForCompOff();
            datesForCompOff.setGeneratedDates(compOff.getCompOffGenDate());
            datesForCompOffRepo.save(datesForCompOff);

            genDates.add(datesForCompOff);

            compOffTracker.setGeneratedDates(genDates);
            compOffTracker.setEmployeeId(compOff.getEmployeeId());
            compOffTracker.setMonth(month);
            compOffTracker.setYear(year);
            compOffTracker.setBalance(compOffTracker.getCompOffGeneratedDays() - compOffTracker.getCompOffAvailedDays());
            compOffTrackerRepository.save(compOffTracker);
        }
        //----------------------------------------
    }

    @GetMapping("compoff-reject/{mgrId}/{id}")
    public void reject(@PathVariable long mgrId, @PathVariable long id) {
        CompOff compOff = compOffRepository.findOne(id);
        compOff.setCompOffStatus(LeaveStatus.REJECTED);
        compOffRepository.save(compOff);
    }


    @GetMapping("compoff-get-approved-rejected/{mgrId}")
    public ResponseEntity<List<CompOff>> getForMgr(@PathVariable long mgrId) {
        List<CompOff> compOff = compOffRepository.findByFirstMgrId(mgrId);
        return new ResponseEntity(compOff, HttpStatus.OK);
    }


    @PostMapping("compoff-generate-hr")
    public void generateCompOff(@RequestBody EmployeeShiftAssignmentDto employeeShiftAssignmentDto) {

        for (long employeeId : employeeShiftAssignmentDto.getEmployeeIds()) {
            long availed = 0;
            long generated = 0;
            CompOff compOff = new CompOff();
            Optional<Employee> employee = employeeService.getEmployee(employeeId);

            compOff.setFirstName(employee.get().getFirstName() + employee.get().getLastName());
            compOff.setCompOffStatus(LeaveStatus.APPROVED);
            compOff.setEmployeeCode(employee.get().getEmployeeCode());
            compOff.setCompOffApprovedBy("ADMIN");
            compOff.setEmployeeId(employeeId);
            if (employee.get().getFirstApprovalManager() != null) {
                compOff.setFirstMgrId(employee.get().getFirstApprovalManager().getId());
            } else {
                throw new EntityNotFoundException("Please assign reporting manager for this employee");
            }

            if (employee.get().getSecondApprovalManager() != null) {
                compOff.setSecondMgrId(employee.get().getSecondApprovalManager().getId());
            }

            compOff.setCompOffGenDate(employeeShiftAssignmentDto.getGeneratedDate());
            compOff.setReason(employeeShiftAssignmentDto.getReason());
            compOffRepository.save(compOff);
            //-------------tracker---------------
            //TODO tracker
            //-------------tracker---------------
            String month = DateUtil.getMonthName(compOff.getCompOffGenDate());
            String year = String.valueOf(DateUtil.getCurrentYear());
            CompOffTracker compOffTracker = compOffTrackerRepository.findByEmployeeIdAndMonthAndYear(compOff.getEmployeeId(), month, year);
            if (compOffTracker != null) {
                availed = compOffTracker.getCompOffAvailedDays();
                generated = compOffTracker.getCompOffGeneratedDays();

                compOffTracker.setCompOffAvailedDays(availed);
                compOffTracker.setCompOffGeneratedDays(generated + 1);

                List<DatesForCompOff> genDates = compOffTracker.getGeneratedDates();

                DatesForCompOff datesForCompOff = new DatesForCompOff();
                datesForCompOff.setGeneratedDates(compOff.getCompOffGenDate());
                datesForCompOffRepo.save(datesForCompOff);

                genDates.add(datesForCompOff);

                compOffTracker.setGeneratedDates(genDates);
                compOffTracker.setBalance(compOffTracker.getCompOffGeneratedDays() - compOffTracker.getCompOffAvailedDays());
                compOffTrackerRepository.save(compOffTracker);
            } else {
                compOffTracker = new CompOffTracker();
                compOffTracker.setCompOffAvailedDays(availed);
                compOffTracker.setCompOffGeneratedDays(generated + 1);

                List<DatesForCompOff> genDates = new ArrayList<>();

                DatesForCompOff datesForCompOff = new DatesForCompOff();
                datesForCompOff.setGeneratedDates(compOff.getCompOffGenDate());
                datesForCompOffRepo.save(datesForCompOff);

                genDates.add(datesForCompOff);

                compOffTracker.setGeneratedDates(genDates);
                compOffTracker.setEmployeeId(compOff.getEmployeeId());
                compOffTracker.setMonth(month);
                compOffTracker.setYear(year);
                compOffTracker.setBalance(compOffTracker.getCompOffGeneratedDays() - compOffTracker.getCompOffAvailedDays());
                compOffTrackerRepository.save(compOffTracker);
            }
            //----------------------------------------
        }
    }


    @PostMapping("compoff-apply-hr")
    public void applyCompOff(@RequestBody EmployeeShiftAssignmentDto employeeShiftAssignmentDto) {

        for (long employeeId : employeeShiftAssignmentDto.getEmployeeIds()) {

            Leave leave = new Leave();
            Optional<Employee> employee = employeeService.getEmployee(employeeId);

            leave.setFirstName(employee.get().getFirstName());
            leave.setLeaveStatus(LeaveStatus.APPROVED);
            leave.setLeaveType(LeaveType.COMP_OFF);
            leave.setRefId((int) employeeId);
            leave.setEmployeeCode(employee.get().getEmployeeCode());

            if (employee.get().getFirstApprovalManager() != null) {
                leave.setFirstApprovalManager(employee.get().getFirstApprovalManager());
            } else {
                throw new EntityNotFoundException("Please assign reporting manager for this employee");
            }

            if (employee.get().getSecondApprovalManager() != null) {
                leave.setSecondApprovalManager(employee.get().getSecondApprovalManager());
            }

            leave.setLeaveStartDate(employeeShiftAssignmentDto.getGeneratedDate());
            leave.setLeaveEndDate(employeeShiftAssignmentDto.getGeneratedDate());
            leave.setReason(employeeShiftAssignmentDto.getReason());
            //TODO check balance of employee and subtract existing balance by 1

            leaveService.applyLeave(leave);
            updateCompOff(leave);
            updateAttendanceData(leave);
        }
    }

    void updateAttendanceData(Leave leave) {
        List<Date> dates = getDaysBetweenDates(leave.getLeaveStartDate(), leave.getLeaveEndDate());
        for (Date date : dates) {
            EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, leave.getRefId());
            if (employeeAttendance != null) {
                employeeAttendance.setMarkedOn(date);
                if (leave.isHalfDay()) {
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
                } else if (leave.getLeaveType() == LeaveType.EARN_LEAVE) {
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.EL);
                } else if (leave.getLeaveType() == LeaveType.MEDICAL_LEAVE) {
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.ML);
                } else if (leave.getLeaveType() == LeaveType.CASUAL_LEAVE) {
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.CL);
                } else if (leave.getLeaveType() == LeaveType.UNPAID_LEAVE) {
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.LEAVE);
                } else if (leave.getLeaveType() == LeaveType.WOP) {
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.LEAVE);
                }else if (leave.getLeaveType()==LeaveType.COMP_OFF) {
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.COMP_OFF);
                }
                employeeAttendance.setRecordedTime(date);
                employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
            } else {
                EmployeeAttendance attendance = new EmployeeAttendance();
                attendance.setMarkedOn(date);
                if (leave.isHalfDay()) {
                    attendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
                } else if (leave.getLeaveType() == LeaveType.EARN_LEAVE) {
                    attendance.setAttendanceStatus(AttendanceStatus.EL);
                } else if (leave.getLeaveType() == LeaveType.MEDICAL_LEAVE) {
                    attendance.setAttendanceStatus(AttendanceStatus.ML);
                } else if (leave.getLeaveType() == LeaveType.CASUAL_LEAVE) {
                    attendance.setAttendanceStatus(AttendanceStatus.CL);
                } else if (leave.getLeaveType() == LeaveType.UNPAID_LEAVE) {
                    attendance.setAttendanceStatus(AttendanceStatus.LEAVE);
                } else if (leave.getLeaveType() == LeaveType.WOP) {
                    attendance.setAttendanceStatus(AttendanceStatus.LEAVE);
                } else if (leave.getLeaveType()==LeaveType.COMP_OFF) {
                    attendance.setAttendanceStatus(AttendanceStatus.COMP_OFF);
                }
                Optional<Employee> employee = employeeService.getEmployee(Long.valueOf(leave.getRefId()));
                if (!employee.isPresent()) {
                    throw new EntityNotFoundException("Employee not found: " + leave.getRefId());
                }
                attendance.setEmployee(employee.get());
                employeeAttendanceService.createEmployeeAttendance(attendance,false);
            }
        }
    }

    public void updateCompOff(Leave leave) {
        String month = DateUtil.getMonthName(leave.getLeaveStartDate());
        String year = String.valueOf(DateUtil.getCurrentYear());
        CompOffTracker compOffTracker = compOffTrackerRepository.findByEmployeeIdAndMonthAndYear(leave.getRefId(), month, year);
//        long days = DateUtil.getDifferenceDays(leave.getLeaveStartDate(), leave.getLeaveEndDate());
        long days = 1;
        if (compOffTracker != null) {
            long availdDays = compOffTracker.getCompOffAvailedDays();
            long genDays = compOffTracker.getCompOffGeneratedDays();
            compOffTracker.setCompOffAvailedDays(availdDays + days);
            compOffTracker.setCompOffGeneratedDays(genDays);
            compOffTracker.setBalance((compOffTracker.getCompOffGeneratedDays()) - (compOffTracker.getCompOffAvailedDays()));
        } else {
            compOffTracker = new CompOffTracker();
            long availdDays = 0;
            long genDays = 0;
            compOffTracker.setCompOffAvailedDays(availdDays + days);
            compOffTracker.setEmployeeId(leave.getRefId());
            compOffTracker.setEmployeeName(leave.getFirstName());
            compOffTracker.setCompOffGeneratedDays(genDays);
            compOffTracker.setMonth(month);
            compOffTracker.setYear(year);
            compOffTracker.setEmployeeCode(leave.getEmployeeCode());
            compOffTracker.setBalance((compOffTracker.getCompOffGeneratedDays()) - (compOffTracker.getCompOffAvailedDays()));

        }
        compOffTrackerRepository.save(compOffTracker);

    }


    @GetMapping("comoff-generated-hr")
    public ResponseEntity<List<CompOff>> getCompOffsForHr() {
        List<CompOff> compOffList = compOffRepository.findAll();
        List<CompOff> compOffList1 = new ArrayList<>();
        for (CompOff compOff : compOffList) {
            if (compOff.getCompOffStatus().equals(LeaveStatus.APPROVED)) {
                compOffList1.add(compOff);
            }
        }
        return new ResponseEntity<>(compOffList1, HttpStatus.OK);
    }

    @GetMapping("comoff-applied-hr")
    public ResponseEntity<List<Leave>> getCompOffsAppliedForHr() {
        List<Leave> compOffList = leaveService.getAllLeaves();
        List<Leave> compOffList1 = new ArrayList<>();
        for (Leave compOff : compOffList) {
            if (compOff.getLeaveType().equals(LeaveType.COMP_OFF)) {
                compOffList1.add(compOff);
            }
        }
        return new ResponseEntity<>(compOffList1, HttpStatus.OK);
    }

    @GetMapping("compoff-tracker-hr")
    public ResponseEntity<List<CompOffTracker>> getAllEmployeesTrack() {
        List<CompOffTracker> compOffTrackers = compOffTrackerRepository.findAll();
        List<CompOffTracker> compOffTrackersList = new ArrayList<>();
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            List<CompOffTracker> compOffTrackersEmployeeWise = compOffTrackerRepository.findByEmployeeId(employee.getId());
            Collections.reverse(compOffTrackersEmployeeWise);
            if (!compOffTrackersEmployeeWise.isEmpty()) {
                compOffTrackersList.add(compOffTrackersEmployeeWise.get(0));
            }
        }
        return new ResponseEntity<>(compOffTrackersList, HttpStatus.OK);
    }


    @GetMapping("compoff-tracker-employee/{empId}")
    public ResponseEntity<CompOffTracker> getCompOffBalanceForEmployee(@PathVariable long empId) {
        List<CompOffTracker> compOffTrackersEmployeeWise = compOffTrackerRepository.findByEmployeeId(empId);
        Collections.reverse(compOffTrackersEmployeeWise);
        CompOffTracker compOffTracker = null;
        if (!compOffTrackersEmployeeWise.isEmpty()) {
            compOffTracker = compOffTrackersEmployeeWise.get(0);
        }
        return new ResponseEntity<>(compOffTracker, HttpStatus.OK);
    }
}
