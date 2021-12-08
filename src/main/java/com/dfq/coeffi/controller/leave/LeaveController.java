package com.dfq.coeffi.controller.leave;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.dfq.coeffi.auditlog.log.ApplicationLog;
import com.dfq.coeffi.auditlog.log.ApplicationLogService;
import com.dfq.coeffi.compOffManagement.CompOffTracker;
import com.dfq.coeffi.compOffManagement.CompOffTrackerRepository;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.OpeningLeave;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.EmployeeLeaveBalanceDto;
import com.dfq.coeffi.dto.timesheetDto.LeaveDto;
import com.dfq.coeffi.entity.holiday.Holiday;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.resource.LeaveApprove;
import com.dfq.coeffi.resource.LeaveResource;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.holiday.HolidayService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.LeaveBucketService;
import com.dfq.coeffi.service.leave.LeaveService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import static com.dfq.coeffi.util.DateUtil.getDaysBetweenDates;

/**
 * @Auther H Kapil Kumar on 21/3/18.
 * @Company Orileo Technologies
 */

@RestController
@Slf4j
public class LeaveController extends BaseController {

    private final LeaveService leaveService;
    private final AcademicYearService academicYearService;
    private final EmployeeService employeeService;
    private final LeaveBucketService leaveBucketService;
    private final EmployeeLeaveBalanceService employeeLeaveBalanceService;
    private final HolidayService holidayService;
    private final EmployeeAttendanceService employeeAttendanceService;

    @Autowired
    CompOffTrackerRepository compOffTrackerRepository;
    @Autowired
    ApplicationLogService applicationLogService;

    @Autowired
    public LeaveController(LeaveService leaveService, AcademicYearService academicYearService,
                           EmployeeService employeeService, LeaveBucketService leaveBucketService,
                           EmployeeLeaveBalanceService employeeLeaveBalanceService,
                           HolidayService holidayService, EmployeeAttendanceService employeeAttendanceService) {
        this.leaveService = leaveService;
        this.employeeService = employeeService;
        this.leaveBucketService = leaveBucketService;
        this.academicYearService = academicYearService;
        this.employeeLeaveBalanceService = employeeLeaveBalanceService;
        this.holidayService = holidayService;
        this.employeeAttendanceService = employeeAttendanceService;
    }

    /**
     * @return all the leaves available in the database
     */
    @GetMapping("leave")
    public ResponseEntity<List<Leave>> getEmployeeAndStudentCreatedLeaves() {
        List<Leave> leaves = leaveService.getLeaves();
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping(value = "leave/get-all-leaves")
    public ResponseEntity<List<Leave>> getAllLeaves() {
        List<Leave> leaves = leaveService.getAllLeaves();
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("No Applied Leaves");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    /**
     * @param year
     * @return list of available leaves of the particular year
     */
    @GetMapping("leave/{year}")
    public ResponseEntity<List<Leave>> getLeaveByYear(@PathVariable int year) {
        List<Leave> leaves = leaveService.getLeaveByYear(year);
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("No Leaves For Year");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    /**
     * @param leave : save object to database and return the saved object
     * @return
     */
    @PostMapping("leave")
    public ResponseEntity<Leave> applyLeave(@Valid @RequestBody Leave leave) throws Exception {
        checkLeaveBalance(leave.getRefId(), leave.getLeaveType());
        checkOffBalance(leave);
        try {
            checkHolidayOrNot(leave);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Leave persistedLeave = null;
        if (leave.isHalfDay()) {
            if (true) {
                Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
                leave.setAcademicYear(academicYear.get());
                leave.setLeaveEndDate(leave.getLeaveStartDate());
                leave.setTotalLeavesApplied(0.5);
                leave.setLeaveStatus(LeaveStatus.CREATED);
                leave.setLeaveType(leave.getLeaveType());
                Optional<Employee> employee = employeeService.getEmployee(Long.valueOf(leave.getRefId()));
                //----------------------------------------------------
                Employee fmgr = null;
                Employee smgr = null;
                if (employee.get().getFirstApprovalManager() != null) {
                    fmgr = employee.get().getFirstApprovalManager();
                }
                if (employee.get().getSecondApprovalManager() != null) {
                    smgr = employee.get().getSecondApprovalManager();
                }
                Employee fmgrLight = new Employee();
                if (fmgr != null) {
                    fmgrLight.setId(fmgr.getId());
                    fmgrLight.setFirstName(fmgr.getFirstName());
                    fmgrLight.setLastName(fmgr.getLastName());
                    leave.setFirstApprovalManager(fmgr);
                }

                if (smgr != null) {
                    Employee smgrLight = new Employee();
                    smgrLight.setId(fmgr.getId());
                    smgrLight.setFirstName(fmgr.getFirstName());
                    smgrLight.setLastName(fmgr.getLastName());
                    leave.setSecondApprovalManager(smgr);
                }

                //----------------------------------------------------
                leave.setFirstName(employee.get().getFirstName() + " " + employee.get().getLastName());
                leave.setEmployeeCode(employee.get().getEmployeeCode());
                leave.setRefName("EMPLOYEE");
                checkDuplicates(leave);
                //-----------log-----------------------
                applicationLogService.recordApplicationLog("Admin", "" + leave.getLeaveStatus(), "Post", leave.getRefId());
                //-----------log-----------------------
                persistedLeave = leaveService.applyLeave(leave);
            }
        } else if (true) {
            Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
            leave.setAcademicYear(academicYear.get());
            leave.setLeaveStatus(LeaveStatus.CREATED);
            leave.setLeaveType(leave.getLeaveType());
            Optional<Employee> employee = employeeService.getEmployee(Long.valueOf(leave.getRefId()));
            //----------------------------------------------------
            Employee fmgr = null;
            Employee smgr = null;
            if (employee.get().getFirstApprovalManager() != null) {
                fmgr = employee.get().getFirstApprovalManager();
            }
            if (employee.get().getSecondApprovalManager() != null) {
                smgr = employee.get().getSecondApprovalManager();
            }
            Employee fmgrLight = new Employee();
            if (fmgr != null) {
                fmgrLight.setId(fmgr.getId());
                fmgrLight.setFirstName(fmgr.getFirstName());
                fmgrLight.setLastName(fmgr.getLastName());
                leave.setFirstApprovalManager(fmgr);
            }

            if (smgr != null) {
                Employee smgrLight = new Employee();
                smgrLight.setId(fmgr.getId());
                smgrLight.setFirstName(fmgr.getFirstName());
                smgrLight.setLastName(fmgr.getLastName());
                leave.setSecondApprovalManager(smgr);
            }


            //----------------------------------------------------
            leave.setFirstName(employee.get().getFirstName() + " " + employee.get().getLastName());
            leave.setEmployeeCode(employee.get().getEmployeeCode());
            leave.setRefName("EMPLOYEE");
            checkDuplicates(leave);
            //-----------log-----------------------
            applicationLogService.recordApplicationLog("Admin", "" + leave.getLeaveStatus(), "Post", leave.getRefId());
            //-----------log-----------------------
            persistedLeave = leaveService.applyLeave(leave);
        } else {
            throw new Exception("Cannot apply leave on sunday");
        }
        return new ResponseEntity<>(persistedLeave, HttpStatus.CREATED);
    }


    @PostMapping(value = "leave/reject-leave")
    public ResponseEntity<Leave> rejectedLeave(@RequestBody LeaveResource leaveResource) {
        Leave leave = null;
        Optional<Leave> leaveObj = leaveService.getLeave(leaveResource.getLeaveId());
        if (leaveObj.isPresent()) {
            leave = leaveObj.get();
            leave.setRejectedRemark(leaveResource.getMessage());
            leave.setLeaveStatus(LeaveStatus.REJECTED);
            //-----------log-----------------------
            applicationLogService.recordApplicationLog("Admin", "" + leave.getLeaveStatus(), "Post", leave.getRefId());
            //-----------log-----------------------
            leaveService.applyLeave(leave);
        } else {
            throw new EntityNotFoundException();
        }
        return new ResponseEntity<>(leave, HttpStatus.OK);
    }

    @PostMapping(value = "leave/approve-leave")
    public ResponseEntity<Leave> approveLeave(@RequestBody LeaveApprove leaveApprove, Principal user) {
        Leave leave = null;
        Optional<Leave> leaveObj = leaveService.getLeave(leaveApprove.getLeaveId());
        if (leaveObj.isPresent()) {
            leave = leaveObj.get();
            leave.setLeaveStatus(LeaveStatus.APPROVED);
            Principal loggedUser = user;
            leave.setLeaveApprovedBy(loggedUser.getName());
            updateEmployeeAvailLeaveBalance(leave.getId());
            //-----------log-----------------------
            applicationLogService.recordApplicationLog("Admin", "" + leave.getLeaveStatus(), "Post", leave.getRefId());
            //-----------log-----------------------
            Leave approvedLeave = leaveService.applyLeave(leave);
            if (approvedLeave != null) {
                updateAttendanceData(approvedLeave);
            }
            if (leave.getLeaveType().equals(LeaveType.COMP_OFF)) {
                updateCompOff(leave);
            }
        } else {
            throw new EntityNotFoundException();
        }
        return new ResponseEntity<>(leave, HttpStatus.OK);
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
                }else {
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                }
                if (leave.getLeaveHalfType()!=null) {
                    employeeAttendance.setLeaveHalfType(leave.getLeaveHalfType());
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
                }else {
                    attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                }
                Optional<Employee> employee = employeeService.getEmployee(Long.valueOf(leave.getRefId()));
                if (!employee.isPresent()) {
                    throw new EntityNotFoundException("Employee not found: " + leave.getRefId());
                }
                if (leave.getLeaveHalfType()!=null) {
                    attendance.setLeaveHalfType(leave.getLeaveHalfType());
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
        long days = DateUtil.getDifferenceDays(leave.getLeaveStartDate(), leave.getLeaveEndDate());
        if (days==0){
            days = 1;
        }
        if (compOffTracker != null) {
            long availdDays = compOffTracker.getCompOffAvailedDays();
            long genDays = compOffTracker.getCompOffGeneratedDays();
            compOffTracker.setEmployeeId(leave.getRefId());
            compOffTracker.setEmployeeName(leave.getFirstName());
            compOffTracker.setCompOffAvailedDays(availdDays + days);
            compOffTracker.setCompOffGeneratedDays(genDays);
            compOffTracker.setBalance((compOffTracker.getCompOffGeneratedDays()) - (compOffTracker.getCompOffAvailedDays()));
        }
        compOffTrackerRepository.save(compOffTracker);

    }

    @GetMapping("leave/update-attendance")
    public void leaveWiseAttendance() {
        Optional<AcademicYear> acad = academicYearService.getActiveAcademicYear();
        List<Leave> leaves = leaveService.getApprovedOrRejectedLeaveList(LeaveStatus.APPROVED, acad.get());
        for (Leave leave : leaves) {
            System.out.println("******** Date: " + leave.getLeaveStartDate() + " endDate: " + leave.getLeaveEndDate());
            updateAttendanceData(leave);
        }
    }

    /**
     * @param id
     * @return return single leave object by passing id
     */
    @GetMapping("leave/{id}")
    private ResponseEntity<Leave> getLeave(@PathVariable long id) {
        Optional<Leave> leave = leaveService.getLeave(id);
        if (!leave.isPresent()) {
            throw new EntityNotFoundException(Leave.class.getSimpleName());
        }
        return new ResponseEntity<>(leave.get(), HttpStatus.OK);
    }

    /**
     * @param id
     * @param leave
     * @return to update the expense object
     */
    @PutMapping("leave/{id}")
    public ResponseEntity<Leave> updateLeave(@PathVariable long id, @Valid @RequestBody Leave leave) {
        Optional<Leave> persistedLeave = leaveService.getLeave(id);
        if (!persistedLeave.isPresent()) {
            log.warn("Leave with ID {} not found", id);
            throw new EntityNotFoundException(Leave.class.getSimpleName());
        }
        leave.setId(id);
        //-----------log-----------------------
        applicationLogService.recordApplicationLog("Admin", "" + leave.getLeaveStatus(), "Update", leave.getRefId());
        //-----------log-----------------------
        leaveService.applyLeave(leave);
        return new ResponseEntity<>(leave, HttpStatus.OK);
    }

    @GetMapping("leave/employee/created/{refId}/{refName}")
    public ResponseEntity<List<Leave>> getEmployeeCreatedLeaveByRefNameAndRefId(@PathVariable Integer refId, @PathVariable String refName) {
        Optional<AcademicYear> acad = academicYearService.getActiveAcademicYear();
        List<Leave> leaves = leaveService.getCreatedEmployeeLeaveByRefNameRefNumber(LeaveStatus.CREATED, refId, refName, acad.get());
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("No Created Leaves");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping("leave/employee/approved-rejected/{refId}/{refName}")
    public ResponseEntity<List<Leave>> getEmployeeApprovedAndRejectedLeaveByRefNameAndRefId(@PathVariable Integer refId, @PathVariable String refName) {
        Optional<AcademicYear> acad = academicYearService.getActiveAcademicYear();
        List<Leave> leaves = leaveService.getApprovedAndRejectedEmployeeleaveByRefNameRefNumber(refId, refName, acad.get());
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("No Approved Leave List");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping("leave/leave-dashboard")
    public ResponseEntity<Map> getCreatedLeaveCountByAcademicYearId() {
        Optional<AcademicYear> acad = academicYearService.getActiveAcademicYear();
        List<Leave> createdLeaves = leaveService.getCreatedLeaveCountByAcademicYear(LeaveStatus.CREATED, acad.get());
        List<Leave> approvedLeaves = leaveService.getCreatedLeaveCountByAcademicYear(LeaveStatus.APPROVED, acad.get());
        List<Leave> rejectedLeaves = leaveService.getCreatedLeaveCountByAcademicYear(LeaveStatus.REJECTED, acad.get());
        if (CollectionUtils.isEmpty(createdLeaves)) {
            throw new EntityNotFoundException("No Created Leave List");
        }
        Map<String, List<Leave>> map = new HashMap<String, List<Leave>>();
        map.put("Created_Leaves", createdLeaves);
        map.put("Approved_Leaves", approvedLeaves);
        map.put("Rejected_Leaves", rejectedLeaves);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("leave/{id}")
    public ResponseEntity<Leave> deleteLeave(@PathVariable long id) {
        if (!leaveService.isLeaveExists(id)) {
            log.warn("Unable to deactivate leave with ID : {} not found", id);
            throw new EntityNotFoundException(Leave.class.getSimpleName());
        }
        leaveService.deleteLeave(LeaveStatus.CREATED, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("leave/employee-leave/{refId}/{refName}")
    public ResponseEntity<List<Leave>> getEmployeeLeaveDetails(@RequestBody DateDto dateDto, @PathVariable Integer refId, @PathVariable String refName) {
        Optional<AcademicYear> academicYearObj = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear = academicYearObj.get();
        List<Leave> leaves = leaveService.getEmployeeLeaveDetails(LeaveStatus.APPROVED, LeaveType.UNPAID_LEAVE, refId, refName, academicYear, DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()));
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("Leaves");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping(value = "leave/approved-leave-list")
    private ResponseEntity<List<Leave>> getApprovedLeaveList() {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
        List<Leave> leaves = leaveService.getApprovedOrRejectedLeaveList(LeaveStatus.APPROVED, academicYear.get());
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            if (leave.getLeaveStartDate().getMonth() == new Date().getMonth()) {
                lightLeaves.add(leave);
            }
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("No Approved Leaves");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping(value = "leave/rejected-leave-list")
    private ResponseEntity<List<Leave>> getRejectedLeaveList() {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
        List<Leave> leaves = leaveService.getApprovedOrRejectedLeaveList(LeaveStatus.REJECTED, academicYear.get());
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            if (leave.getLeaveStartDate().getMonth() == new Date().getMonth()) {
                lightLeaves.add(leave);
            }
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("No rejected Leave List");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @PostMapping("leave/employee-casual-leave/{refId}/{refName}")
    public ResponseEntity<List<Leave>> checkCasulaLeaveContinuation(@RequestBody DateDto dateDto, @PathVariable Integer refId, @PathVariable String refName) {
        Date date = new Date();
        Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date DayBeforeYesterDay = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000 - 24 * 60 * 60 * 1000);

        Optional<AcademicYear> academicYearObj = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear = academicYearObj.get();
        List<Leave> leave = leaveService.checkCasulaLeaveContinuation(refId, refName, LeaveType.CASUAL_LEAVE, LeaveStatus.APPROVED, academicYear, DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()));

        return new ResponseEntity<>(leave, HttpStatus.OK);
    }

    @GetMapping(value = "leave/employee-approved-leave-list/{refId}/{refName}")
    private ResponseEntity<List<Leave>> getEmployeeApprovedLeaveList(@PathVariable Integer refId, @PathVariable String refName) {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();

        List<Leave> leaves = leaveService.getEmployeeApprovedOrRejectedLeaveList(refId, refName, LeaveStatus.APPROVED, academicYear.get());
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("Leaves");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping(value = "leave/employee-rejected-leave-list/{refId}/{refName}")
    private ResponseEntity<List<Leave>> getEmployeeRejectedLeaveList(@PathVariable Integer refId, @PathVariable String refName) {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();

        List<Leave> leaves = leaveService.getEmployeeApprovedOrRejectedLeaveList(refId, refName, LeaveStatus.REJECTED, academicYear.get());
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("No rejected Leaves");
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @PostMapping("leave/approved-leave-attendance")
    public ResponseEntity<List<Leave>> getApprovedLeavesForAttendance(@RequestBody DateDto dateDto) {
        Optional<AcademicYear> academicYearObj = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear = academicYearObj.get();
        List<Leave> leaves = leaveService.getApprovedLeavesForAttendance(LeaveStatus.APPROVED, academicYear, DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()));
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("Leaves");
        }
        return new ResponseEntity<>(leaves, HttpStatus.OK);
    }

    @GetMapping("leave/employee-leave-dashboard/{refId}/{refName}")
    public ResponseEntity<Map> getCreatedLeaveCountByAcademicYearIdByRefIdAndRefname(@PathVariable Integer refId, @PathVariable String refName) {
        Optional<AcademicYear> acad = academicYearService.getActiveAcademicYear();
        List<Leave> createdLeaves = leaveService.getCreatedLeaveCountByAcademicYearByRefIdAndRefName(LeaveStatus.CREATED, acad.get(), refId, refName);
        List<Leave> approvedLeaves = leaveService.getCreatedLeaveCountByAcademicYearByRefIdAndRefName(LeaveStatus.APPROVED, acad.get(), refId, refName);
        List<Leave> rejectedLeaves = leaveService.getCreatedLeaveCountByAcademicYearByRefIdAndRefName(LeaveStatus.REJECTED, acad.get(), refId, refName);
        if (CollectionUtils.isEmpty(createdLeaves)) {
            throw new EntityNotFoundException("createdLeaves");
        }
        Map<String, List<Leave>> map = new HashMap<String, List<Leave>>();
        map.put("Created_Leaves", createdLeaves);
        map.put("Approved_Leaves", approvedLeaves);
        map.put("Rejected_Leaves", rejectedLeaves);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("leave/approved-leave-list/{refId}/{refName}")
    public ResponseEntity<List<Leave>> getEmployeeApprovedLeaveListByRefNameAndRefId(@PathVariable Integer refId, @PathVariable String refName) {
        Optional<AcademicYear> acad = academicYearService.getActiveAcademicYear();
        List<Leave> leaves = leaveService.getEmployeeApprovedLeaveByRefNameRefNumber(LeaveStatus.APPROVED, refId, refName, acad.get());
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("No Approved Leaves");
        }
        return new ResponseEntity<>(leaves, HttpStatus.OK);
    }

    @GetMapping("leave/created-approved-leave-list")
    public ResponseEntity<List<Leave>> getApprovedAndRejectedLeaveList() {
        Optional<AcademicYear> acad = academicYearService.getActiveAcademicYear();
        List<Leave> approvedLeaves = leaveService.getApprovedOrRejectedLeaveList(LeaveStatus.APPROVED, acad.get());
        List<Leave> createdLeaves = leaveService.getApprovedOrRejectedLeaveList(LeaveStatus.CREATED, acad.get());
        approvedLeaves.addAll(createdLeaves);
        if (CollectionUtils.isEmpty(approvedLeaves)) {
            throw new EntityNotFoundException("No Requested Leaves,");
        }
        return new ResponseEntity<>(approvedLeaves, HttpStatus.OK);
    }


    @GetMapping("leave/by-first-approval-id/{id}")
    public ResponseEntity<List<Leave>> getEmployeeAndStudentCreatedLeaves(@PathVariable long id, Principal user) {
        Optional<Employee> employeeObj = employeeService.getEmployee(id);
        Principal loggedUser = user;
        Employee employee = employeeObj.get();
        List<Leave> leaves = leaveService.getByFirstApprovalManagerId(employee);
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
//        if (CollectionUtils.isEmpty(leaves)) {
//            throw new EntityNotFoundException("Leaves are not associated with this manager/employee id: " + employee.getId());
//        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping("leave/by-admin-approval-id/{id}")
    public ResponseEntity<List<Leave>> getEmployeeCreatedLeaves(@PathVariable long id, Principal user) {
        Optional<Employee> employeeObj = employeeService.getEmployee(id);
        Principal loggedUser = user;
        Employee employee = employeeObj.get();
        List<Leave> leaves = leaveService.getLeaves();
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
//        if (CollectionUtils.isEmpty(leaves)) {
//            throw new EntityNotFoundException("Leaves are not associated with this manager/employee id: " + employee.getId());
//        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @PostMapping(value = "leave/forward-leave")
    public ResponseEntity<Leave> forwardLeave(@RequestBody LeaveApprove leaveApprove) {

        Leave leave = null;
        Optional<Leave> leaveObj = leaveService.getLeave(leaveApprove.getLeaveId());
        if (leaveObj.isPresent()) {
            leave = leaveObj.get();
            leave.setLeaveStatus(LeaveStatus.FORWARD);
            //-----------log-----------------------
            applicationLogService.recordApplicationLog("Admin", "" + leave.getLeaveStatus(), "Post", leave.getRefId());
            //-----------log-----------------------
            leaveService.applyLeave(leave);
        } else {
            throw new EntityNotFoundException();
        }
        return new ResponseEntity<>(leave, HttpStatus.OK);
    }

    @GetMapping("leave/by-second-approval-id/{id}")
    public ResponseEntity<List<Leave>> getEmployeeCreatedLeaves(@PathVariable long id) {
        Optional<Employee> employeeObj = employeeService.getEmployee(id);
        Employee employee = employeeObj.get();
        List<Leave> leaves = leaveService.getBySecondApprovalManagerId(LeaveStatus.FORWARD, employee);
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("Leaves are not associated with this manager/employee id: " + employee.getId());
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping("leave/all-leaves-by-approval-id/{id}")
    public ResponseEntity<List<Leave>> getAllLeaves(@PathVariable long id) {
        Optional<Employee> employeeObj = employeeService.getEmployee(id);
        Employee employee = employeeObj.get();
        List<Leave> leaves = leaveService.getAllLeavesByApprovalManager(employee);
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : leaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(leaves)) {
            throw new EntityNotFoundException("Leaves are not associated with this manager/employee id: " + employee.getId());
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    @GetMapping("leave/all-leaves-by-second-approval-id/{id}")
    public ResponseEntity<List<Leave>> getSecondApproval(@PathVariable long id) {
        Optional<Employee> employeeObj = employeeService.getEmployee(id);
        Employee employee = employeeObj.get();
        List<Leave> allApprovedLeaves = leaveService.getAllLeavesBySecondApprovalManager(employee);
        List<Leave> lightLeaves = new ArrayList<>();
        //------------------------------------------
        for (Leave leave : allApprovedLeaves) {
            Employee e = new Employee();
            Employee ee = new Employee();
            if (leave.getFirstApprovalManager() != null) {
                Optional<Employee> fmgremployee = employeeService.getEmployee(leave.getFirstApprovalManager().getId());
                e.setId(fmgremployee.get().getId());
                e.setFirstName(fmgremployee.get().getFirstName());
                e.setLastName(fmgremployee.get().getLastName());
                e.setEmployeeCode(fmgremployee.get().getEmployeeCode());
                leave.setFirstApprovalManager(e);

            }
            if (leave.getSecondApprovalManager() != null) {
                Optional<Employee> smgremployee = employeeService.getEmployee(leave.getSecondApprovalManager().getId());
                ee.setId(smgremployee.get().getId());
                ee.setFirstName(smgremployee.get().getFirstName());
                ee.setLastName(smgremployee.get().getLastName());
                ee.setEmployeeCode(smgremployee.get().getEmployeeCode());
                leave.setSecondApprovalManager(ee);
            }
            lightLeaves.add(leave);
        }
        //--------------------------------------------
        if (CollectionUtils.isEmpty(allApprovedLeaves)) {
            throw new EntityNotFoundException("Leaves are not associated with this manager/employee id: " + employee.getId());
        }
        return new ResponseEntity<>(lightLeaves, HttpStatus.OK);
    }

    private ResponseEntity<EmployeeLeaveBalance> updateEmployeeAvailLeaveBalance(@PathVariable long id) {
        Optional<AcademicYear> currentYear = academicYearService.getActiveAcademicYear();
        Optional<Leave> leaveList = leaveService.getLeave(id);
        Leave leaveObjt = leaveList.get();
        EmployeeLeaveBalance currentYearEmployeeLeaveBalanceByEmployeeId = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(leaveObjt.getRefId(), currentYear.get().getId());
        AvailLeave availLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave();
        ClosingLeave closingLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getClosingLeave();
        EmployeeLeaveBalance employeeLeaveBalance = new EmployeeLeaveBalance();
        BigDecimal earnLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getEarnLeave();
        BigDecimal clearanceLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getClearanceLeave();
        BigDecimal medicalLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getMedicalLeave();
        if (leaveObjt.getLeaveStatus().equals(LeaveStatus.APPROVED)) {
            if (leaveObjt.getLeaveType().equals(LeaveType.EARN_LEAVE)) {
                earnLeave = earnLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setEarnLeave(earnLeave);
                closingLeave.setEarnLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getEarnLeave().subtract(earnLeave));
            } else if (leaveObjt.getLeaveType().equals(LeaveType.CASUAL_LEAVE)) {
                clearanceLeave = clearanceLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setClearanceLeave(clearanceLeave);
                closingLeave.setClearanceLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getClearanceLeave().subtract(clearanceLeave));
            } else if (leaveObjt.getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {
                medicalLeave = medicalLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setMedicalLeave(medicalLeave);
                closingLeave.setMedicalLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getMedicalLeave().subtract(medicalLeave));
            }
            availLeave.setTotalLeave(earnLeave.add(clearanceLeave).add(medicalLeave));
            closingLeave.setTotalLeave(closingLeave.getEarnLeave().add(closingLeave.getClearanceLeave().add(closingLeave.getMedicalLeave())));
            currentYearEmployeeLeaveBalanceByEmployeeId.setAvailLeave(availLeave);
            currentYearEmployeeLeaveBalanceByEmployeeId.setClosingLeave(closingLeave);
            employeeLeaveBalance = employeeLeaveBalanceService.createEmployeeLeaveBalance(currentYearEmployeeLeaveBalanceByEmployeeId);
        }
        return new ResponseEntity(employeeLeaveBalance, HttpStatus.OK);
    }

    @PostMapping("leave/update-approved-leave/{id}")
    public ResponseEntity<Leave> updateApprovedLeave(@PathVariable long id, @Valid @RequestBody Leave leave) {
        EmployeeLeaveBalance employeeLeaveBalance = updateEmployeeLeaveBalance(id);

        if (employeeLeaveBalance != null) {
            Optional<Leave> persistedLeave = leaveService.getLeave(id);
            if (!persistedLeave.isPresent()) {
                log.warn("Leave with ID {} not found", id);
                throw new EntityNotFoundException(Leave.class.getSimpleName());
            }
            leave.setId(id);
            Optional<Employee> employee = employeeService.getEmployee(Long.valueOf(leave.getRefId()));
            leave.setFirstApprovalManager(employee.get().getFirstApprovalManager());
            leave.setSecondApprovalManager(employee.get().getSecondApprovalManager());
            leave.setFirstName(employee.get().getFirstName() + " " + employee.get().getLastName());
            Leave updateLeave = leaveService.applyLeave(leave);
            updateEmployeeAvailLeave(updateLeave.getId());
        }
        return new ResponseEntity<>(leave, HttpStatus.OK);
    }

    private EmployeeLeaveBalance updateEmployeeLeaveBalance(long leaveId) {
        Optional<Leave> getLeave = leaveService.getLeave(leaveId);
        Leave leave = getLeave.get();
        Optional<EmployeeLeaveBalance> getEmployeeLeaveBalance = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeId(leave.getRefId());
        EmployeeLeaveBalance employeeLeaveBalance = getEmployeeLeaveBalance.get();
        if (leave.getLeaveType() == LeaveType.EARN_LEAVE) {
            employeeLeaveBalance.getAvailLeave().setEarnLeave(employeeLeaveBalance.getAvailLeave().getEarnLeave().subtract(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getAvailLeave().setTotalLeave(employeeLeaveBalance.getAvailLeave().getTotalLeave().subtract(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getClosingLeave().setEarnLeave(employeeLeaveBalance.getClosingLeave().getEarnLeave().add(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getClosingLeave().setTotalLeave(employeeLeaveBalance.getClosingLeave().getTotalLeave().add(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
        }
        if (leave.getLeaveType() == LeaveType.CASUAL_LEAVE) {
            employeeLeaveBalance.getAvailLeave().setClearanceLeave(employeeLeaveBalance.getAvailLeave().getClearanceLeave().subtract(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getAvailLeave().setTotalLeave(employeeLeaveBalance.getAvailLeave().getTotalLeave().subtract(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getClosingLeave().setClearanceLeave(employeeLeaveBalance.getClosingLeave().getClearanceLeave().add(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getClosingLeave().setTotalLeave(employeeLeaveBalance.getClosingLeave().getTotalLeave().add(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
        }
        if (leave.getLeaveType() == LeaveType.MEDICAL_LEAVE) {
            employeeLeaveBalance.getAvailLeave().setMedicalLeave(employeeLeaveBalance.getAvailLeave().getMedicalLeave().subtract(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getAvailLeave().setTotalLeave(employeeLeaveBalance.getAvailLeave().getTotalLeave().subtract(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getClosingLeave().setMedicalLeave(employeeLeaveBalance.getClosingLeave().getMedicalLeave().add(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
            employeeLeaveBalance.getClosingLeave().setTotalLeave(employeeLeaveBalance.getClosingLeave().getTotalLeave().add(BigDecimal.valueOf(leave.getTotalLeavesApplied())));
        }
        EmployeeLeaveBalance updateEmployeeLeaveBalance = employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalance);
        return updateEmployeeLeaveBalance;
    }

    private EmployeeLeaveBalance updateEmployeeAvailLeave(long leaveId) {
        Optional<AcademicYear> currentYear = academicYearService.getActiveAcademicYear();
        Optional<Leave> leaveList = leaveService.getLeave(leaveId);
        Leave leaveObjt = leaveList.get();
        EmployeeLeaveBalance currentYearEmployeeLeaveBalanceByEmployeeId = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(leaveObjt.getRefId(), currentYear.get().getId());
        AvailLeave availLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave();
        ClosingLeave closingLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getClosingLeave();
        EmployeeLeaveBalance employeeLeaveBalance = new EmployeeLeaveBalance();
        BigDecimal earnLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getEarnLeave();
        BigDecimal clearanceLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getClearanceLeave();
        BigDecimal medicalLeave = currentYearEmployeeLeaveBalanceByEmployeeId.getAvailLeave().getMedicalLeave();
        if (leaveObjt.getLeaveStatus().equals(LeaveStatus.APPROVED)) {
            if (leaveObjt.getLeaveType().equals(LeaveType.EARN_LEAVE)) {
                earnLeave = earnLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setEarnLeave(earnLeave);
                closingLeave.setEarnLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getEarnLeave().subtract(earnLeave));
            } else if (leaveObjt.getLeaveType().equals(LeaveType.CASUAL_LEAVE)) {
                clearanceLeave = clearanceLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setClearanceLeave(clearanceLeave);
                closingLeave.setClearanceLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getClearanceLeave().subtract(clearanceLeave));
            } else if (leaveObjt.getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {
                medicalLeave = medicalLeave.add(BigDecimal.valueOf(leaveObjt.getTotalLeavesApplied()));
                availLeave.setMedicalLeave(medicalLeave);
                closingLeave.setMedicalLeave(currentYearEmployeeLeaveBalanceByEmployeeId.getOpeningLeave().getMedicalLeave().subtract(medicalLeave));
            }
            availLeave.setTotalLeave(earnLeave.add(clearanceLeave).add(medicalLeave));
            closingLeave.setTotalLeave(closingLeave.getEarnLeave().add(closingLeave.getClearanceLeave().add(closingLeave.getMedicalLeave())));
            currentYearEmployeeLeaveBalanceByEmployeeId.setAvailLeave(availLeave);
            currentYearEmployeeLeaveBalanceByEmployeeId.setClosingLeave(closingLeave);
            employeeLeaveBalance = employeeLeaveBalanceService.createEmployeeLeaveBalance(currentYearEmployeeLeaveBalanceByEmployeeId);
        }
        return employeeLeaveBalance;
    }

    public void checkOffBalance(Leave leave) {
        if (leave.getLeaveType().toString().equalsIgnoreCase("COMP_OFF")) {
            String month = DateUtil.getMonthName(leave.getLeaveStartDate());
            String year = String.valueOf(DateUtil.getCurrentYear());
            long days = DateUtil.getDifferenceDays(leave.getLeaveStartDate(), leave.getLeaveEndDate());
            CompOffTracker compOffTracker = compOffTrackerRepository.findByEmployeeIdAndMonthAndYear(leave.getRefId(), month, year);
            if (compOffTracker != null) {
                if (compOffTracker.getBalance() == 0 || compOffTracker.getBalance() < days) {
                    throw new EntityNotFoundException("no compoff balance");
                }
            } else {
                throw new EntityNotFoundException("No balance");
            }
        }
    }

    private void checkLeaveBalance(long employeeId, LeaveType leaveType) {
        Optional<AcademicYear> academicYearObj = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear = academicYearObj.get();
        EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employeeId, academicYear.getId());
        if (employeeLeaveBalance == null) {
            throw new EntityNotFoundException("Employee Leave Balance not set for : " + employeeLeaveBalance.getEmployee().getFirstName());
        }
        if (employeeLeaveBalance.getOpeningLeave() != null) {
            if (leaveType.toString().equalsIgnoreCase("CASUAL_LEAVE")) {
                BigDecimal casualLeave = employeeLeaveBalance.getOpeningLeave().getClearanceLeave();
                if (!(casualLeave.doubleValue() > 0)) {
                    throw new EntityNotFoundException("CASUAL_LEAVES are not available");
                }
            } else if (leaveType.toString().equalsIgnoreCase("MEDICAL_LEAVE")) {
                BigDecimal medicalLeave = employeeLeaveBalance.getOpeningLeave().getMedicalLeave();
                if (!(medicalLeave.doubleValue() > 0)) {
                    throw new EntityNotFoundException("MEDICAL_LEAVES are not available");
                }
            }
            if (leaveType.toString().equalsIgnoreCase("EARN_LEAVE")) {
                BigDecimal earnLeave = employeeLeaveBalance.getOpeningLeave().getEarnLeave();
                if (!(earnLeave.doubleValue() > 0)) {
                    throw new EntityNotFoundException("EARN_LEAVES are not available");
                }
            }

        }
    }

    private void checkHolidayOrNot(Leave leave) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Optional<AcademicYear> academicYearObj = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear = academicYearObj.get();
        List<Holiday> holidays = holidayService.getHolidayBetweenStartDateAndEndDate(leave.getLeaveStartDate(), leave.getLeaveEndDate());
        System.out.println("Holidays");
        System.out.println(holidays.size());
        for (Holiday holiday : holidays) {
            String leaveStartDate = dateFormat.format(leave.getLeaveStartDate());
            String leaveEndDate = dateFormat.format(leave.getLeaveEndDate());
            String holidayDate = dateFormat.format(holiday.getStartDate());
            Date startDate = dateFormat.parse(leaveStartDate);
            Date endDate = dateFormat.parse(leaveEndDate);
            Date betterHolidayDate = dateFormat.parse(holidayDate);
            System.out.println("*************** Checking Date object ***************");
            System.out.println("Leave Start Date: " + startDate);
            System.out.println("Leave End Date: " + endDate);
            System.out.println("Holiday Date: " + betterHolidayDate);
            if (startDate.equals(betterHolidayDate) || (endDate.equals(betterHolidayDate))) {
//                throw new EntityNotFoundException("Holiday on " + holidayDate);
            }
        }
    }

    public static long getNumberofSundays(Leave leave) throws Exception {
        Date date1 = leave.getLeaveStartDate();
        Date date2 = leave.getLeaveEndDate();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        long sundays = 0;
        while (!c1.after(c2)) {
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                sundays++;
            }
            c1.add(Calendar.DATE, 1);
        }
        return sundays;
    }


    public void updateAttendanceAllByLeave(Date startDate, Date endDate) {
        List<Date> dates = getDaysBetweenDates(startDate, endDate);
        List<Employee> employeeList = employeeService.getEmployeeLightWeight();
        for (Employee employee : employeeList) {
            for (Date date : dates) {
                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, employee.getId());
                Leave leave = leaveService.getApprovedLeavesForAttendanceMark(LeaveStatus.APPROVED, Math.toIntExact(employee.getId()), date);
                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.LEAVE) {
                    if (leave.getLeaveType() == LeaveType.EARN_LEAVE) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.EL);
                    } else if (leave.getLeaveType() == LeaveType.MEDICAL_LEAVE) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.ML);
                    } else if (leave.getLeaveType() == LeaveType.CASUAL_LEAVE) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.CL);
                    } else if (leave.getLeaveType() == LeaveType.UNPAID_LEAVE) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.LEAVE);
                    } else if (leave.getLeaveType() == LeaveType.WOP) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.LEAVE);
                    }
                    EmployeeAttendance attendance = employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
                    System.out.println("After save status: " + attendance.getAttendanceStatus());
                }
            }
        }
    }

    public void checkDuplicates(Leave leave) {
        List<Date> applieddates = getDaysBetweenDates(leave.getLeaveStartDate(), leave.getLeaveEndDate());
        for (Date appliedDate : applieddates) {
            List<Leave> leaves = leaveService.getAllLeaves();
            for (Leave leave1 : leaves) {
                if (leave1.getRefId() == leave.getRefId()) {
                    if (appliedDate.equals(leave1.getLeaveStartDate()) || appliedDate.equals(leave1.getLeaveEndDate())) {
                        throw new EntityNotFoundException("Leave already exists on" + appliedDate);
                    } else if (appliedDate.after(leave1.getLeaveStartDate()) && appliedDate.before(leave1.getLeaveEndDate())) {
                        throw new EntityNotFoundException("Leave already exists on " + appliedDate);
                    }
                }
            }
        }
    }


    @PostMapping("leave/delete-approved")
    public Leave deleteApprovedLeave(@RequestBody LeaveDto leaveDto) {
        Optional<Leave> leave = leaveService.getLeave(leaveDto.getId());
        if (leaveDto.getReason().equalsIgnoreCase("COMPOFF")) {
            EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(leave.get().getRefId(), 1);
            ClosingLeave closingLeave = employeeLeaveBalance.getClosingLeave();
            OpeningLeave openingLeave = employeeLeaveBalance.getOpeningLeave();
            AvailLeave availLeave = employeeLeaveBalance.getAvailLeave();
            if (leave.get().getLeaveType().equals(LeaveType.CASUAL_LEAVE)) {
                double appliedLeaves = leave.get().getTotalLeavesApplied();
                BigDecimal openingLeaveClearanceLeave = openingLeave.getClearanceLeave();
                BigDecimal availLeaveClearanceLeave = availLeave.getClearanceLeave();
                BigDecimal closingLeaveClearanceLeave = closingLeave.getClearanceLeave();

                openingLeaveClearanceLeave = openingLeaveClearanceLeave.add(BigDecimal.valueOf(appliedLeaves));
                availLeaveClearanceLeave = availLeaveClearanceLeave.subtract(BigDecimal.valueOf(appliedLeaves));
                closingLeaveClearanceLeave = openingLeaveClearanceLeave.subtract(availLeaveClearanceLeave);

                employeeLeaveBalance.getOpeningLeave().setClearanceLeave(openingLeaveClearanceLeave);
                employeeLeaveBalance.getAvailLeave().setClearanceLeave(availLeaveClearanceLeave);
                employeeLeaveBalance.getClosingLeave().setClearanceLeave(closingLeaveClearanceLeave);

                employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalance);
            } else if (leave.get().getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {
                double appliedLeaves = leave.get().getTotalLeavesApplied();
                BigDecimal openingLeaveMedicalLeave = openingLeave.getMedicalLeave();
                BigDecimal availLeaveMedicalLeave = availLeave.getMedicalLeave();
                BigDecimal closingLeaveMedicalLeave;

                openingLeaveMedicalLeave = openingLeaveMedicalLeave.add(BigDecimal.valueOf(appliedLeaves));
                availLeaveMedicalLeave = availLeaveMedicalLeave.subtract(BigDecimal.valueOf(appliedLeaves));
                closingLeaveMedicalLeave = openingLeaveMedicalLeave.subtract(availLeaveMedicalLeave);

                employeeLeaveBalance.getOpeningLeave().setMedicalLeave(openingLeaveMedicalLeave);
                employeeLeaveBalance.getAvailLeave().setMedicalLeave(availLeaveMedicalLeave);
                employeeLeaveBalance.getClosingLeave().setMedicalLeave(closingLeaveMedicalLeave);

                employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalance);
            } else if (leave.get().getLeaveType().equals(LeaveType.EARN_LEAVE)) {
                double appliedLeaves = leave.get().getTotalLeavesApplied();
                BigDecimal openingLeaveEarnLeave = openingLeave.getEarnLeave();
                BigDecimal availLeaveEarnLeave = availLeave.getEarnLeave();
                BigDecimal closingLeaveEarnLeave;

                openingLeaveEarnLeave = openingLeaveEarnLeave.add(BigDecimal.valueOf(appliedLeaves));
                availLeaveEarnLeave = availLeaveEarnLeave.subtract(BigDecimal.valueOf(appliedLeaves));
                closingLeaveEarnLeave = openingLeaveEarnLeave.subtract(availLeaveEarnLeave);

                employeeLeaveBalance.getOpeningLeave().setEarnLeave(openingLeaveEarnLeave);
                employeeLeaveBalance.getAvailLeave().setEarnLeave(availLeaveEarnLeave);
                employeeLeaveBalance.getClosingLeave().setEarnLeave(closingLeaveEarnLeave);

                employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalance);
            }
        }
        leaveService.deleteLeave(leave.get().getLeaveStatus(), leave.get().getId());
        return null;

    }


    @PostMapping("leave/edit-leave-balance")
    public void editLeaveBalance(@RequestBody EmployeeLeaveBalanceDto employeeLeaveBalanceDto) {
        EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employeeLeaveBalanceDto.getEmployeeId(), 1);
        ClosingLeave closingLeave = employeeLeaveBalance.getClosingLeave();
        OpeningLeave openingLeave = employeeLeaveBalance.getOpeningLeave();
        AvailLeave availLeave = employeeLeaveBalance.getAvailLeave();

        openingLeave.setEarnLeave(employeeLeaveBalanceDto.getOpeningEarnLeave());
        openingLeave.setClearanceLeave(employeeLeaveBalanceDto.getOpeningCasualLeave());
        openingLeave.setMedicalLeave(employeeLeaveBalanceDto.getOpeningMedicalLeave());

        availLeave.setEarnLeave(BigDecimal.valueOf(0));
        availLeave.setClearanceLeave(BigDecimal.valueOf(0));
        availLeave.setMedicalLeave(BigDecimal.valueOf(0));

        closingLeave.setEarnLeave(employeeLeaveBalanceDto.getOpeningEarnLeave());
        closingLeave.setClearanceLeave(employeeLeaveBalanceDto.getOpeningCasualLeave());
        closingLeave.setMedicalLeave(employeeLeaveBalanceDto.getOpeningMedicalLeave());

        employeeLeaveBalance.setOpeningLeave(openingLeave);
        employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalance);
    }


}