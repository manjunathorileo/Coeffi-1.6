package com.dfq.coeffi.excel;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.OpeningLeave;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRule;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRuleService;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.employeePerformanceManagement.entity.EmployeePerformanceManagement;
import com.dfq.coeffi.employeePerformanceManagement.entity.GoalStatusEnum;
import com.dfq.coeffi.employeePerformanceManagement.service.EmployeePerformanceManagementService;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.holiday.Holiday;
import com.dfq.coeffi.entity.hr.DepartmentTracker;
import com.dfq.coeffi.entity.hr.DepartmentTrackerRepository;
import com.dfq.coeffi.entity.hr.employee.*;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.repository.leave.AvailLeaveRepo;
import com.dfq.coeffi.repository.leave.ClosingLeaveRepo;
import com.dfq.coeffi.repository.leave.OpeningLeaveRepo;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.holiday.HolidayService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.LeaveService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.vivo.entity.TypeOfVehicle;
import com.dfq.coeffi.vivo.entity.VivoPass;
import com.dfq.coeffi.vivo.repository.TypeOfVehicleRepository;
import com.dfq.coeffi.vivo.repository.VivoInfoRepository;
import com.dfq.coeffi.vivo.repository.VivoPassRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.dfq.coeffi.util.DateUtil.getDaysBetweenDates;

@RestController
@Slf4j
public class ExcelUpload extends BaseController {
    @Autowired
    VivoPassRepo vivoPassRepo;
    @Autowired
    TypeOfVehicleRepository typeOfVehicleRepository;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeePerformanceManagementService employeePerformanceManagementService;
    @Autowired
    ShiftService shiftService;
    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    AcademicYearService academicYearService;
    @Autowired
    LeaveService leaveService;
    @Autowired
    HolidayService holidayService;
    @Autowired
    EmployeeLeaveBalanceService employeeLeaveBalanceService;
    @Autowired
    EarningLeaveRuleService earningLeaveRuleService;
    @Autowired
    AvailLeaveRepo availLeaveRepo;
    @Autowired
    ClosingLeaveRepo closingLeaveRepo;
    @Autowired
    OpeningLeaveRepo openingLeaveRepo;
    @Autowired
    DepartmentTrackerRepository departmentTrackerRepository;


    @PostMapping("/goal-excel-upload/{empid}")
    public ResponseEntity<List<EmployeePerformanceManagement>> importQuestion(@RequestParam("file") MultipartFile file, @PathVariable("empid") long empid) {
        List<EmployeePerformanceManagement> employeePerformanceManagements = goalImport(file, empid);
        return new ResponseEntity<>(employeePerformanceManagements, HttpStatus.OK);
    }

    public List<EmployeePerformanceManagement> goalImport(MultipartFile file, long empid) {
        Date todayDate = new Date();
        List<EmployeePerformanceManagement> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i <= rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    EmployeePerformanceManagement employeePerformanceManagement = new EmployeePerformanceManagement();
                    employeePerformanceManagement.setGoalDiscription(row.getCell(1).getStringCellValue());
                    Optional<Employee> employee = employeeService.getEmployee(empid);
                    Employee employee1 = employee.get();
                    employeePerformanceManagement.setEmployee(employee1);
                    employeePerformanceManagement.setCreatedOn(todayDate);
                    employeePerformanceManagement.setFirstManager(employee1.getFirstApprovalManager());
                    employeePerformanceManagement.setSecondManager(employee1.getSecondApprovalManager());
                    employeePerformanceManagement.setStatus(true);
                    employeePerformanceManagement.setGoalStatus(GoalStatusEnum.YET_TO_SUBMIT_GOAL);

                    EmployeePerformanceManagement employeePerformanceManagement1 = employeePerformanceManagementService.createEmployeePerformanceManagement(employeePerformanceManagement);
                    dto.add(employeePerformanceManagement1);

                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }


    /**
     * Upload Bulk Attendance through excel Sheet
     *
     * @param file
     * @return All employee attendance
     */

    @PostMapping("bulk-attendance-excel-upload")
    public ResponseEntity<List<EmployeeAttendance>> importQuestion(@RequestParam("file") MultipartFile file) {
        List<EmployeeAttendance> employeeAttendances = attendanceImport(file);
        if (employeeAttendances.isEmpty()) {
            throw new EntityNotFoundException("List is empty");
        }
        return new ResponseEntity<>(employeeAttendances, HttpStatus.OK);
    }

    public List<EmployeeAttendance> attendanceImport(MultipartFile file) {
        List<EmployeeAttendance> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    EmployeeAttendance employeeAttendance = new EmployeeAttendance();
//                    employeePerformanceManagement.setId(i);\
                    String att = row.getCell(0).getStringCellValue();
                    if (att.equalsIgnoreCase("P")) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                    } else if (att.equalsIgnoreCase("AB")) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                    } else if (att.equalsIgnoreCase("H")) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.HOLIDAY);
                    } else if (att.equalsIgnoreCase("L")) {
                        employeeAttendance.setAttendanceStatus(AttendanceStatus.LEAVE);
                    } else {
                        throw new EntityNotFoundException("Attendance Status is Not Valid at row :" + rowNumber);
                    }
                    employeeAttendance.setMarkedOn(row.getCell(1).getDateCellValue());
                    employeeAttendance.setInTime(row.getCell(2).getDateCellValue());
                    employeeAttendance.setOutTime(row.getCell(3).getDateCellValue());
//                    if (row.getCell(4) != null) {
//                        employeeAttendance.setWorkedHours(String.valueOf(row.getCell(4).getNumericCellValue()));
//                    }
//                    if (row.getCell(5) != null) {
//                        employeeAttendance.setLateEntry(String.valueOf(row.getCell(5).getNumericCellValue()));
//                    }
//                    if (row.getCell(6) != null) {
//                        employeeAttendance.setOverTime(String.valueOf(row.getCell(6).getNumericCellValue()));
//                    }

                    String empCode = row.getCell(4).getStringCellValue();
                    String employeeCode = String.valueOf(empCode);
                    Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(employeeCode);
                    System.out.println("empcode+" + employeeCode);
                    if (employee.isPresent()) {
                        employeeAttendance.setEmployee(employee.get());
                        if (row.getCell(5) != null) {
                            Shift s = shiftService.getShift((long) row.getCell(5).getNumericCellValue());
                            employeeAttendance.setShift(s);
                            employeeAttendance.setInTime(s.getStartTime());
                            employeeAttendance.setOutTime(s.getEndTime());
                            employeeAttendance.setRecordedTime(new Date());
                        }
                        EmployeeAttendance employeeAttendance1 = checkAttendance(employeeAttendance);
                        if (employeeAttendance1 != null) {
                            System.out.println("Attendance Already Marked On This Date " + employeeAttendance.getMarkedOn() + " for " + employee.get().getEmployeeCode());
                        } else
                            employeeAttendanceService.createEmployeeAttendance(employeeAttendance, false);

                        dto.add(employeeAttendance);
                    }

                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    public EmployeeAttendance checkAttendance(EmployeeAttendance employeeAttendance) {
        EmployeeAttendance employeeAttendanceList = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(employeeAttendance.getMarkedOn(), employeeAttendance.getEmployee().getId());
        if (employeeAttendanceList == null) {
            return null;
        }
        return employeeAttendanceList;
    }


    //---Bulk-leave-upload

    @PostMapping("leave-bulk-upload")
    public List<Leave> leaveImport(@RequestParam("file") MultipartFile file) {
        List<Leave> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    Leave leave = new Leave();
                    Optional<Employee> employee = employeeService.getEmployee((long) row.getCell(0).getNumericCellValue());
                    leave.setLeaveStartDate(row.getCell(1).getDateCellValue());
                    leave.setLeaveEndDate(row.getCell(2).getDateCellValue());
                    LeaveType l = LeaveType.valueOf(row.getCell(3).getStringCellValue());
                    leave.setHalfDay(row.getCell(4).getBooleanCellValue());
                    checkLeaveBalance((long) row.getCell(0).getNumericCellValue(), LeaveType.valueOf(row.getCell(3).getStringCellValue()));

                    try {
                        checkHolidayOrNot(leave);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (leave.isHalfDay()) {
                        if (true) {
                            Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
                            leave.setAcademicYear(academicYear.get());
                            leave.setLeaveEndDate(leave.getLeaveStartDate());
                            leave.setTotalLeavesApplied(0.5);
                            leave.setLeaveStatus(LeaveStatus.CREATED);

                            leave.setLeaveType(l);

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
                                smgrLight.setId(smgr.getId());
                                smgrLight.setFirstName(smgr.getFirstName());
                                smgrLight.setLastName(smgr.getLastName());
                                leave.setSecondApprovalManager(smgr);
                            }
                            //----------------------------------------------------
                            leave.setFirstName(employee.get().getFirstName() + " " + employee.get().getLastName());
                            leave.setRefName("EMPLOYEE");
                            checkDuplicates(leave);
                            leaveService.applyLeave(leave);
                        }
                    } else if (true) {
                        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
                        leave.setAcademicYear(academicYear.get());
                        leave.setLeaveStatus(LeaveStatus.CREATED);
                        leave.setLeaveType(leave.getLeaveType());
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
                            smgrLight.setId(smgr.getId());
                            smgrLight.setFirstName(smgr.getFirstName());
                            smgrLight.setLastName(smgr.getLastName());
                            leave.setSecondApprovalManager(smgr);
                        }
                        //----------------------------------------------------
                        leave.setFirstName(employee.get().getFirstName() + " " + employee.get().getLastName());
                        leave.setRefName("EMPLOYEE");
                        checkDuplicates(leave);
                        leaveService.applyLeave(leave);
                    } else {
                        throw new Exception("Cannot apply leave on sunday");
                    }

                    dto.add(leave);

                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
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

    private void checkLeaveBalance(long employeeId, LeaveType leaveType) {
        Optional<AcademicYear> academicYearObj = academicYearService.getActiveAcademicYear();
        AcademicYear academicYear = academicYearObj.get();
        EmployeeLeaveBalance employeeLeaveBalance = employeeLeaveBalanceService.getEmployeeLeaveBalanceByEmployeeIdByFinancialYearId(employeeId, academicYear.getId());
        if (employeeLeaveBalance == null) {
            throw new NullPointerException("Employee Leave Balance not set for : " + employeeLeaveBalance.getEmployee().getFirstName());
        }
        if (employeeLeaveBalance.getOpeningLeave() != null) {
            if (leaveType.toString().equalsIgnoreCase("CASUAL_LEAVE")) {
                BigDecimal casualLeave = employeeLeaveBalance.getOpeningLeave().getClearanceLeave();
                if (!(casualLeave.doubleValue() > 0)) {
                    throw new NullPointerException("CASUAL_LEAVES are not available");
                }
            } else if (leaveType.toString().equalsIgnoreCase("MEDICAL_LEAVE")) {
                BigDecimal medicalLeave = employeeLeaveBalance.getOpeningLeave().getMedicalLeave();
                if (!(medicalLeave.doubleValue() > 0)) {
                    throw new NullPointerException("MEDICAL_LEAVES are not available");
                }
            }
            if (leaveType.toString().equalsIgnoreCase("EARN_LEAVE")) {
                BigDecimal earnLeave = employeeLeaveBalance.getOpeningLeave().getEarnLeave();
                if (!(earnLeave.doubleValue() > 0)) {
                    throw new NullPointerException("EARN_LEAVES are not available");
                }
            }
        }
    }

    private void checkHolidayOrNot(Leave leave) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        List<Holiday> holidays = holidayService.getHolidayBetweenStartDateAndEndDate(leave.getLeaveStartDate(), leave.getLeaveEndDate());
        for (Holiday holiday : holidays) {
            String leaveStartDate = dateFormat.format(leave.getLeaveStartDate());
            String leaveEndDate = dateFormat.format(leave.getLeaveEndDate());
            String holidayDate = dateFormat.format(holiday.getStartDate());
            Date startDate = dateFormat.parse(leaveStartDate);
            Date endDate = dateFormat.parse(leaveEndDate);
            Date betterHolidayDate = dateFormat.parse(holidayDate);
            if (startDate.equals(betterHolidayDate) || (endDate.equals(betterHolidayDate))) {
                throw new EntityNotFoundException("Holiday on " + holidayDate);
            }
        }
    }


    //-----EMPLOYEE UPLOAD------------
    @PostMapping("bulk-employee-excel-upload")
    public ResponseEntity<List<Employee>> importEmployees(@RequestParam("file") MultipartFile file) {
        List<Employee> employees = employeeImport(file);
        if (employees.isEmpty()) {
            throw new EntityNotFoundException("List is empty");
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    public List<Employee> employeeImport(MultipartFile file) {
        List<Employee> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber >= 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    Employee employee = new Employee();
                    employee.setEmployeeCode(String.valueOf(row.getCell(0).getStringCellValue()));
                    if (employee.getEmployeeCode()==null ||employee.getEmployeeCode().equals("")){
                        return dto;
                    }
                    Optional<Employee> employeeCheck = employeeService.getEmployeeByEmployeeCode(employee.getEmployeeCode());
                    if (!employeeCheck.isPresent()) {
                        employee.setFirstName(row.getCell(1).getStringCellValue());
                        employee.setRfid(row.getCell(2).getStringCellValue());
                        employee.setAdharNumber(String.valueOf((long) row.getCell(3).getNumericCellValue()));
                        employee.setDepartmentName(row.getCell(4).getStringCellValue());
                        employee.setRole(row.getCell(5).getStringCellValue());
                        String employeeType = row.getCell(6).getStringCellValue();
                        EmployeeType type = null;
                        if (employeeType.equalsIgnoreCase("PERMANENT_STAFF")) {
                            type = EmployeeType.PERMANENT;
                        } else if (employeeType.equalsIgnoreCase("PERMANENT_WORKER")) {
                            type = EmployeeType.PERMANENT_WORKER;
                        }
                        employee.setEmployeeType(type);
                        if (row.getCell(7) != null) {
                            employee.setLastName(row.getCell(7).getStringCellValue());
                        }
                        if (row.getCell(8) != null) {
                            employee.setFatherName(row.getCell(8).getStringCellValue());
                        }
                        if (row.getCell(9) != null) {
                            employee.setMotherName(row.getCell(9).getStringCellValue());
                        }
                        if (row.getCell(10) != null) {
                            employee.setGender(row.getCell(10).getStringCellValue());
                        }
                        if (row.getCell(11) != null) {
                            employee.setMaritalStatus(row.getCell(11).getStringCellValue());
                        }
                        if (row.getCell(12) != null) {
                            employee.setDateOfMarriage(row.getCell(12).getDateCellValue());
                        }
                        if (row.getCell(13) != null) {
                            employee.setDateOfBirth(row.getCell(13).getDateCellValue());
                        }
                        if (row.getCell(14) != null) {
                            employee.setAge(row.getCell(14).getStringCellValue());
                        }
                        if (row.getCell(15) != null) {
                            employee.setPhoneNumber(row.getCell(15).getStringCellValue());
                        }
                        if (row.getCell(16) != null) {
                            employee.setEmergencyPhoneNumber(row.getCell(16).getStringCellValue());
                        }
                        if (row.getCell(17) != null) {
                            employee.setBloodGroup(row.getCell(17).getStringCellValue());
                        }
                        if (row.getCell(18) != null) {
                            employee.setReligion(row.getCell(18).getStringCellValue());
                        }
                        if (row.getCell(19) != null) {
                            employee.setCaste(row.getCell(19).getStringCellValue());
                        }
                        if (row.getCell(20) != null) {
                            employee.setFamilyDependents((int) row.getCell(20).getNumericCellValue());
                        }
                        if (row.getCell(21) != null) {
                            employee.setPermanentAddress(row.getCell(21).getStringCellValue());
                        }
                        if (row.getCell(22) != null) {
                            employee.setCurrentAddress(row.getCell(22).getStringCellValue());
                        }
                        if (row.getCell(23) != null) {
                            employee.setLevel(row.getCell(23).getStringCellValue());
                        }
                        if (row.getCell(24) != null) {
                            employee.setDateOfJoining(row.getCell(24).getDateCellValue());
                        }
                        if (row.getCell(25) != null) {
                            employee.setProbationaryPeriod(row.getCell(25).getStringCellValue());
                        }
                        if (row.getCell(26) != null) {
                            employee.setUanNumber(row.getCell(26).getStringCellValue());
                        }
                        if (row.getCell(27) != null) {
                            employee.setPfNumber(row.getCell(27).getStringCellValue());
                        }
                        if (row.getCell(28) != null) {
                            employee.setEsiNumber(row.getCell(28).getStringCellValue());
                        }
                        if (row.getCell(29) != null) {
                            employee.setPanNumber(row.getCell(29).getStringCellValue());
                        }
                        if (row.getCell(30) != null) {
                            FamilyMember familyMember = new FamilyMember();
                            familyMember.setName(row.getCell(30).getStringCellValue());//fam mem name
                            if (row.getCell(31) != null) {
                                familyMember.setDateOfBirth(row.getCell(31).getDateCellValue());//fam mem dateofBirth
                            }
                            if (row.getCell(32) != null) {
                                familyMember.setRelation(row.getCell(32).getStringCellValue());
                            }

                            employee.setFamilyMember(Arrays.asList(familyMember));
                        }

                        if (row.getCell(33) != null) {
                            Qualification qualification = new Qualification();
                            qualification.setCourse(row.getCell(33).getStringCellValue());//Graduation
                            if (row.getCell(34) != null) {
                                qualification.setCourseType(row.getCell(34).getStringCellValue());//Course type
                            }
                            if (row.getCell(35) != null) {
                                qualification.setPlaceOfGraduation(row.getCell(35).getStringCellValue());
                            }
                            if (row.getCell(36) != null) {
                                qualification.setBranch(row.getCell(36).getStringCellValue());
                            }
                            if (row.getCell(37) != null) {
                                if (row.getCell(37).getCellType()!=Cell.CELL_TYPE_BLANK) {
                                    qualification.setAggregate(Double.valueOf(row.getCell(37).getStringCellValue()));
                                }
                            }
                            if (row.getCell(38) != null) {
                                qualification.setYearOfCompletion(row.getCell(38).getDateCellValue());
                            }
                            employee.setQualification(Arrays.asList(qualification));
                        }

                        if (row.getCell(39) != null) {
                            EmployeeCertification employeeCertification = new EmployeeCertification();
                            employeeCertification.setCertificationName(row.getCell(39).getStringCellValue());
                            if (row.getCell(40) != null) {
                                employeeCertification.setInstituteName(row.getCell(40).getStringCellValue());
                            }
                            if (row.getCell(41) != null) {
                                employeeCertification.setStartDate(row.getCell(41).getDateCellValue());
                            }
                            if (row.getCell(42) != null) {
                                employeeCertification.setEndDate(row.getCell(42).getDateCellValue());
                            }
                            employee.setEmployeeCertifications(Arrays.asList(employeeCertification));
                        }

                        if (row.getCell(43) != null) {
                            PreviousEmployement previousEmployement = new PreviousEmployement();
                            previousEmployement.setEmployeeName(row.getCell(43).getStringCellValue());
                            if (row.getCell(44) != null) {
                                previousEmployement.setEmployementType(row.getCell(44).getStringCellValue());
                            }
                            if (row.getCell(45) != null) {
                                previousEmployement.setPosition(row.getCell(45).getStringCellValue());
                            }
                            if (row.getCell(46) != null) {
                                if (row.getCell(46).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setSalary(row.getCell(46).getNumericCellValue());
                                }
                            }
                            if (row.getCell(47) != null) {
                                previousEmployement.setStartDate(row.getCell(47).getDateCellValue());
                            }
                            if (row.getCell(48) != null) {
                                previousEmployement.setEndDate(row.getCell(48).getDateCellValue());
                            }
                            if (row.getCell(49) != null) {
                                if (row.getCell(49).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setExperienceInYear(row.getCell(49).getNumericCellValue());
                                }
                            }
                            if (row.getCell(50) != null) {
                                if (row.getCell(50).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setExperienceInMonth(row.getCell(50).getNumericCellValue());
                                }
                            }
                            employee.setPreviousEmployement(Arrays.asList(previousEmployement));
                        }

                        employee = employeeService.save(employee);
                        //TODO uncomment other imports
                        allocateLeave(employee);
                        if (row.getCell(30) != null) {
                            FamilyMember familyMember = new FamilyMember();
                            familyMember.setName(row.getCell(30).getStringCellValue());//fam mem name
                            if (row.getCell(31) != null) {
                                familyMember.setDateOfBirth(row.getCell(31).getDateCellValue());//fam mem dateofBirth
                            }
                            if (row.getCell(32) != null) {
                                familyMember.setRelation(row.getCell(32).getStringCellValue());
                            }
                            familyMember.setEmployee(employee);
                            employee.setFamilyMember(Arrays.asList(familyMember));
                        }

                        if (row.getCell(33) != null) {
                            Qualification qualification = new Qualification();
                            qualification.setCourse(row.getCell(33).getStringCellValue());//Graduation
                            if (row.getCell(34) != null) {
                                qualification.setCourseType(row.getCell(34).getStringCellValue());//Course type
                            }
                            if (row.getCell(35) != null) {
                                qualification.setPlaceOfGraduation(row.getCell(35).getStringCellValue());
                            }
                            if (row.getCell(36) != null) {
                                qualification.setBranch(row.getCell(36).getStringCellValue());
                            }
                            if (row.getCell(37) != null) {
                                if (row.getCell(37).getCellType()!=Cell.CELL_TYPE_BLANK) {
                                    qualification.setAggregate(Double.valueOf(row.getCell(37).getStringCellValue()));
                                }
                            }
                            if (row.getCell(38) != null) {
                                qualification.setYearOfCompletion(row.getCell(38).getDateCellValue());
                            }
                            qualification.setEmployee(employee);
                            employee.setQualification(Arrays.asList(qualification));
                        }

                        if (row.getCell(39) != null) {
                            EmployeeCertification employeeCertification = new EmployeeCertification();
                            employeeCertification.setCertificationName(row.getCell(39).getStringCellValue());
                            if (row.getCell(40) != null) {
                                employeeCertification.setInstituteName(row.getCell(40).getStringCellValue());
                            }
                            if (row.getCell(41) != null) {
                                employeeCertification.setStartDate(row.getCell(41).getDateCellValue());
                            }
                            if (row.getCell(42) != null) {
                                employeeCertification.setEndDate(row.getCell(42).getDateCellValue());
                            }
                            employeeCertification.setEmployee(employee);
                            employee.setEmployeeCertifications(Arrays.asList(employeeCertification));
                        }

                        if (row.getCell(43) != null) {
                            PreviousEmployement previousEmployement = new PreviousEmployement();
                            previousEmployement.setEmployeeName(row.getCell(43).getStringCellValue());
                            if (row.getCell(44) != null) {
                                previousEmployement.setEmployementType(row.getCell(44).getStringCellValue());
                            }
                            if (row.getCell(45) != null) {
                                previousEmployement.setPosition(row.getCell(45).getStringCellValue());
                            }
                            if (row.getCell(46) != null) {
                                if (row.getCell(46).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setSalary(row.getCell(46).getNumericCellValue());
                                }
                            }
                            if (row.getCell(47) != null) {
                                previousEmployement.setStartDate(row.getCell(47).getDateCellValue());
                            }
                            if (row.getCell(48) != null) {
                                previousEmployement.setEndDate(row.getCell(48).getDateCellValue());
                            }
                            if (row.getCell(49) != null) {
                                if (row.getCell(49).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setExperienceInYear(row.getCell(49).getNumericCellValue());
                                }
                            }
                            if (row.getCell(50) != null) {
                                if (row.getCell(50).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setExperienceInMonth(row.getCell(50).getNumericCellValue());
                                }
                            }
                            previousEmployement.setEmployee(employee);
                            employee.setPreviousEmployement(Arrays.asList(previousEmployement));
                        }

                        dto.add(employee);
                    } else {
                        System.out.println("entry exits");
                        employee = employeeCheck.get();
                        employee.setFirstName(row.getCell(1).getStringCellValue());
                        employee.setRfid(row.getCell(2).getStringCellValue());
                        employee.setAdharNumber(String.valueOf((long) row.getCell(3).getNumericCellValue()));
                        employee.setDepartmentName(row.getCell(4).getStringCellValue());
                        employee.setRole(row.getCell(5).getStringCellValue());
                        String employeeType = row.getCell(6).getStringCellValue();
                        EmployeeType type = null;
                        if (employeeType.equalsIgnoreCase("PERMANENT_STAFF")) {
                            type = EmployeeType.PERMANENT;
                        } else if (employeeType.equalsIgnoreCase("PERMANENT_WORKER")) {
                            type = EmployeeType.PERMANENT_WORKER;
                        }
                        employee.setEmployeeType(type);
                        if (row.getCell(7) != null) {
                            employee.setLastName(row.getCell(7).getStringCellValue());
                        }
                        if (row.getCell(8) != null) {
                            employee.setFatherName(row.getCell(8).getStringCellValue());
                        }
                        if (row.getCell(9) != null) {
                            employee.setMotherName(row.getCell(9).getStringCellValue());
                        }
                        if (row.getCell(10) != null) {
                            employee.setGender(row.getCell(10).getStringCellValue());
                        }
                        if (row.getCell(11) != null) {
                            employee.setMaritalStatus(row.getCell(11).getStringCellValue());
                        }
                        if (row.getCell(12) != null) {
                            employee.setDateOfMarriage(row.getCell(12).getDateCellValue());
                        }
                        if (row.getCell(13) != null) {
                            employee.setDateOfBirth(row.getCell(13).getDateCellValue());
                        }
                        if (row.getCell(14) != null) {
                            employee.setAge(row.getCell(14).getStringCellValue());
                        }
                        if (row.getCell(15) != null) {
                            employee.setPhoneNumber(row.getCell(15).getStringCellValue());
                        }
                        if (row.getCell(16) != null) {
                            employee.setEmergencyPhoneNumber(row.getCell(16).getStringCellValue());
                        }
                        if (row.getCell(17) != null) {
                            employee.setBloodGroup(row.getCell(17).getStringCellValue());
                        }
                        if (row.getCell(18) != null) {
                            employee.setReligion(row.getCell(18).getStringCellValue());
                        }
                        if (row.getCell(19) != null) {
                            employee.setCaste(row.getCell(19).getStringCellValue());
                        }
                        if (row.getCell(20) != null) {
                            employee.setFamilyDependents((int) row.getCell(20).getNumericCellValue());
                        }
                        if (row.getCell(21) != null) {
                            employee.setPermanentAddress(row.getCell(21).getStringCellValue());
                        }
                        if (row.getCell(22) != null) {
                            employee.setCurrentAddress(row.getCell(22).getStringCellValue());
                        }
                        if (row.getCell(23) != null) {
                            employee.setLevel(row.getCell(23).getStringCellValue());
                        }
                        if (row.getCell(24) != null) {
                            employee.setDateOfJoining(row.getCell(24).getDateCellValue());
                        }
                        if (row.getCell(25) != null) {
                            employee.setProbationaryPeriod(row.getCell(25).getStringCellValue());
                        }
                        if (row.getCell(26) != null) {
                            employee.setUanNumber(row.getCell(26).getStringCellValue());
                        }
                        if (row.getCell(27) != null) {
                            employee.setPfNumber(row.getCell(27).getStringCellValue());
                        }
                        if (row.getCell(28) != null) {
                            employee.setEsiNumber(row.getCell(28).getStringCellValue());
                        }
                        if (row.getCell(29) != null) {
                            employee.setPanNumber(row.getCell(29).getStringCellValue());
                        }
                        employee = employeeService.save(employee);
                        if (row.getCell(30) != null) {
                            FamilyMember familyMember = new FamilyMember();
                            familyMember.setName(row.getCell(30).getStringCellValue());//fam mem name
                            if (row.getCell(31) != null) {
                                familyMember.setDateOfBirth(row.getCell(31).getDateCellValue());//fam mem dateofBirth
                            }
                            if (row.getCell(32) != null) {
                                familyMember.setRelation(row.getCell(32).getStringCellValue());
                            }
                            familyMember.setEmployee(employee);
                            employee.setFamilyMember(Arrays.asList(familyMember));
                        }

                        if (row.getCell(33) != null) {
                            Qualification qualification = new Qualification();
                            qualification.setCourse(row.getCell(33).getStringCellValue());//Graduation
                            if (row.getCell(34) != null) {
                                qualification.setCourseType(row.getCell(34).getStringCellValue());//Course type
                            }
                            if (row.getCell(35) != null) {
                                qualification.setPlaceOfGraduation(row.getCell(35).getStringCellValue());
                            }
                            if (row.getCell(36) != null) {
                                qualification.setBranch(row.getCell(36).getStringCellValue());
                            }
                            if (row.getCell(37) != null) {
                                if (row.getCell(37).getCellType()!=Cell.CELL_TYPE_BLANK) {
                                    qualification.setAggregate(Double.valueOf(row.getCell(37).getStringCellValue()));
                                }
                            }
                            if (row.getCell(38) != null) {
                                qualification.setYearOfCompletion(row.getCell(38).getDateCellValue());
                            }
                            qualification.setEmployee(employee);
                            employee.setQualification(Arrays.asList(qualification));
                        }

                        if (row.getCell(39) != null) {
                            EmployeeCertification employeeCertification = new EmployeeCertification();
                            employeeCertification.setCertificationName(row.getCell(39).getStringCellValue());
                            if (row.getCell(40) != null) {
                                employeeCertification.setInstituteName(row.getCell(40).getStringCellValue());
                            }
                            if (row.getCell(41) != null) {
                                employeeCertification.setStartDate(row.getCell(41).getDateCellValue());
                            }
                            if (row.getCell(42) != null) {
                                employeeCertification.setEndDate(row.getCell(42).getDateCellValue());
                            }
                            employeeCertification.setEmployee(employee);
                            employee.setEmployeeCertifications(Arrays.asList(employeeCertification));
                        }

                        if (row.getCell(43) != null) {
                            PreviousEmployement previousEmployement = new PreviousEmployement();
                            previousEmployement.setEmployeeName(row.getCell(43).getStringCellValue());
                            if (row.getCell(44) != null) {
                                previousEmployement.setEmployementType(row.getCell(44).getStringCellValue());
                            }
                            if (row.getCell(45) != null) {
                                previousEmployement.setPosition(row.getCell(45).getStringCellValue());
                            }
                            if (row.getCell(46) != null) {
                                if (row.getCell(46).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setSalary(row.getCell(46).getNumericCellValue());
                                }
                            }
                            if (row.getCell(47) != null) {
                                previousEmployement.setStartDate(row.getCell(47).getDateCellValue());
                            }
                            if (row.getCell(48) != null) {
                                previousEmployement.setEndDate(row.getCell(48).getDateCellValue());
                            }
                            if (row.getCell(49) != null) {
                                if (row.getCell(49).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setExperienceInYear(row.getCell(49).getNumericCellValue());
                                }
                            }
                            if (row.getCell(50) != null) {
                                if (row.getCell(50).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    previousEmployement.setExperienceInMonth(row.getCell(50).getNumericCellValue());
                                }
                            }
                            previousEmployement.setEmployee(employee);
                            employee.setPreviousEmployement(Arrays.asList(previousEmployement));
                        }
                        dto.add(employee);
                    }
                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }


    public void allocateLeave(Employee employee) {
        System.out.println("FRESH YEAR ENTRY");
        long currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        EmployeeLeaveBalance employeeLeaveBalances = new EmployeeLeaveBalance();
        Optional<AcademicYear> academicYearOptional = academicYearService.getActiveAcademicYear();
        employeeLeaveBalances.setEmployee(employee);
        employeeLeaveBalances.setAcademicYear(academicYearOptional.get());
        employeeLeaveBalances.setStatus(true);
        OpeningLeave openingLeave = new OpeningLeave();
        ClosingLeave closingLeave = new ClosingLeave();
        AvailLeave availLeave = new AvailLeave();

        List<EarningLeaveRule> earningLeaveRuleList = earningLeaveRuleService.getAllEarningLeaveRule();
        for (EarningLeaveRule earningLeaveRule : earningLeaveRuleList) {
            if (earningLeaveRule.getLeaveType().equals(LeaveType.EARN_LEAVE)) {
                openingLeave.setEarnLeave(new BigDecimal(10));
                closingLeave.setEarnLeave(new BigDecimal(10));
            } else {
                openingLeave.setEarnLeave(new BigDecimal(10));
                closingLeave.setEarnLeave(new BigDecimal(10));
            }

            if (earningLeaveRule.getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {

                openingLeave.setMedicalLeave(new BigDecimal(10));
                closingLeave.setMedicalLeave(new BigDecimal(10));


            } else {

                openingLeave.setMedicalLeave(new BigDecimal(10));
                closingLeave.setMedicalLeave(new BigDecimal(10));

            }

            openingLeave.setClearanceLeave(new BigDecimal(0));
            closingLeave.setClearanceLeave(new BigDecimal(0));
            availLeave.setEarnLeave(new BigDecimal(0));
            availLeave.setMedicalLeave(new BigDecimal(0));
            availLeave.setClearanceLeave(new BigDecimal(0));
            availLeave.setTotalLeave(new BigDecimal(0));


            openingLeave.setTotalLeave(openingLeave.getEarnLeave().add(openingLeave.getClearanceLeave().add(openingLeave.getMedicalLeave())));
            closingLeave.setTotalLeave(openingLeave.getEarnLeave().add(openingLeave.getClearanceLeave().add(openingLeave.getMedicalLeave())));

            availLeaveRepo.save(availLeave);
            openingLeaveRepo.save(openingLeave);
            closingLeaveRepo.save(closingLeave);
            employeeLeaveBalances.setAvailLeave(availLeave);
            employeeLeaveBalances.setOpeningLeave(openingLeave);
            employeeLeaveBalances.setClosingLeave(closingLeave);
            employeeLeaveBalances.setCurrentMonth(currentMonth);
            employeeLeaveBalanceService.createEmployeeLeaveBalance(employeeLeaveBalances);
        }
    }


    @PostMapping("bulk-contract-excel-upload")
    public ResponseEntity<List<EmpPermanentContract>> importContract(@RequestParam("file") MultipartFile file) {
        List<EmpPermanentContract> employees = contractImport(file);
        if (employees.isEmpty()) {
            throw new EntityNotFoundException("List is empty");
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Autowired
    PermanentContractService permanentContractService;

    public List<EmpPermanentContract> contractImport(MultipartFile file) {
        List<EmpPermanentContract> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber >= 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    EmpPermanentContract employee = new EmpPermanentContract();
                    if (row.getCell(0).getStringCellValue() != "") {
                        employee.setEmployeeCode(String.valueOf(row.getCell(0).getStringCellValue()));
                        EmpPermanentContract employeeCheck = permanentContractService.get(employee.getEmployeeCode());
                        if (employeeCheck == null) {
                            employee.setFirstName(row.getCell(1).getStringCellValue());
                            employee.setLastName(row.getCell(2).getStringCellValue());
                            employee.setAccessId(String.valueOf(row.getCell(3).getStringCellValue()));
                            employee.setCardId(String.valueOf(row.getCell(3).getStringCellValue()));
                            employee.setDepartmentName(row.getCell(4).getStringCellValue());
                            employee.setContractCompany(row.getCell(5).getStringCellValue());
                            String employeeType = row.getCell(6).getStringCellValue();
                            EmployeeType type = null;
                            if (employeeType.equalsIgnoreCase("TEMPORARY_CONTRACT")) {
                                type = EmployeeType.CONTRACT;
                            } else if (employeeType.equalsIgnoreCase("PERMANENT_CONTRACT")) {
                                type = EmployeeType.PERMANENT_CONTRACT;
                            }
                            if (type != null) {
                                employee.setEmployeeType(type);
                            } else {
                                employee.setEmployeeType(EmployeeType.CONTRACT);
                            }
                            if (employee.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                                employee.setEmployeeType(EmployeeType.CONTRACT);
                                employee.setTemporaryContract(false);
                            } else if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                                employee.setEmployeeType(EmployeeType.CONTRACT);
                                employee.setTemporaryContract(true);
                            }
                            if (row.getCell(7) != null) {
                                employee.setPhoneNumber(row.getCell(7).getStringCellValue());
                            }
                            if (row.getCell(8) != null) {
                                employee.setEmail(row.getCell(8).getStringCellValue());
                            }
                            if (row.getCell(9) != null) {
                                employee.setPermanentAddress(row.getCell(9).getStringCellValue());
                            }
//                            employee.setCo(row.getCell(10).getStringCellValue());
                            if (row.getCell(11) != null) {
                                employee.setRole(row.getCell(11).getStringCellValue());
                            }
                            if (row.getCell(12) != null) {
                                employee.setDateOfJoining(row.getCell(12).getDateCellValue());
                            }
                            permanentContractService.save(employee);
                            dto.add(employee);
                        } else {
                            System.out.println("Entry exists");
                            employee = employeeCheck;
                            employee.setFirstName(row.getCell(1).getStringCellValue());
                            employee.setLastName(row.getCell(2).getStringCellValue());
                            employee.setAccessId(String.valueOf(row.getCell(3).getStringCellValue()));
                            employee.setCardId(String.valueOf(row.getCell(3).getStringCellValue()));
                            employee.setDepartmentName(row.getCell(4).getStringCellValue());
                            employee.setContractCompany(row.getCell(5).getStringCellValue());
                            String employeeType = row.getCell(6).getStringCellValue();
                            EmployeeType type = null;
                            if (employeeType.equalsIgnoreCase("TEMPORARY_CONTRACT")) {
                                type = EmployeeType.CONTRACT;
                            } else if (employeeType.equalsIgnoreCase("PERMANENT_CONTRACT")) {
                                type = EmployeeType.PERMANENT_CONTRACT;
                            }
                            if (type != null) {
                                employee.setEmployeeType(type);
                            } else {
                                employee.setEmployeeType(EmployeeType.CONTRACT);
                            }
                            if (employee.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                                employee.setEmployeeType(EmployeeType.CONTRACT);
                                employee.setTemporaryContract(false);
                            } else if (employee.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                                employee.setEmployeeType(EmployeeType.CONTRACT);
                                employee.setTemporaryContract(true);
                            }
                            if (row.getCell(7) != null) {
                                employee.setPhoneNumber(row.getCell(7).getStringCellValue());
                            }
                            if (row.getCell(8) != null) {
                                employee.setEmail(row.getCell(8).getStringCellValue());
                            }
                            if (row.getCell(9) != null) {
                                employee.setPermanentAddress(row.getCell(9).getStringCellValue());
                            }
//                            employee.setCo(row.getCell(10).getStringCellValue());
                            if (row.getCell(11) != null) {
                                employee.setRole(row.getCell(11).getStringCellValue());
                            }
                            if (row.getCell(12) != null) {
                                employee.setDateOfJoining(row.getCell(12).getDateCellValue());
                            }
                            permanentContractService.save(employee);
                            dto.add(employee);
                        }
                    }
                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }


    @PostMapping("vehicles-menzies-upload")
    public ResponseEntity<List<VivoPass>> vehicleBulkUpload(@RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<VivoPass> vivoPassList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            VivoPass vivoPass = new VivoPass();
            XSSFRow row = sheet.getRow(i);
            vivoPass.setVehicleNumber(row.getCell(0).getStringCellValue());
            vivoPass.setDriverDetails(row.getCell(1).getStringCellValue());

            long ph = 0;
            try {
                ph = (long) row.getCell(2).getNumericCellValue();
            } catch (Exception e) {

            }

            vivoPass.setMobileNumber(String.valueOf(ph));
            if (row.getCell(5).getStringCellValue().equalsIgnoreCase("TWO")) {
                Optional<TypeOfVehicle> two = typeOfVehicleRepository.findById(7);
                vivoPass.setTypeOfVehicle(two.get().getTypeOfVehicle());
                vivoPass.setVehicleType(two.get());
            }
            if (row.getCell(5).getStringCellValue().equalsIgnoreCase("FOUR")) {
                Optional<TypeOfVehicle> two = typeOfVehicleRepository.findById(9);
                vivoPass.setTypeOfVehicle(two.get().getTypeOfVehicle());
                vivoPass.setVehicleType(two.get());
            }
            try {
                vivoPass.setDlNumber(row.getCell(7).getStringCellValue());
            } catch (Exception e) {

            }


            vivoPass.setCardId((long) row.getCell(8).getNumericCellValue());
            vivoPass.setCompanyName(row.getCell(13).getStringCellValue());
            vivoPass.setEmpname(row.getCell(1).getStringCellValue());
            vivoPass.setAllowOrDeny(true);
            vivoPass.setEmployeeType("PERMANENT");
//            vivoPass.setEmpId();
            vivoPassRepo.save(vivoPass);
        }
        return new ResponseEntity<>(vivoPassList, HttpStatus.OK);
    }
}
