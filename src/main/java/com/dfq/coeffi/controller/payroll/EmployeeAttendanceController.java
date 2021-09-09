package com.dfq.coeffi.controller.payroll;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.CompanySettings.Service.CompanyNameService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.*;
import com.dfq.coeffi.entity.holiday.Holiday;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.DepartmentTrackerDto;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.leave.Leave;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.master.assignShifts.EmployeeShiftAssignment;
import com.dfq.coeffi.master.assignShifts.EmployeeShiftAssignmentService;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftRepository;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.resource.EmployeeAttendanceResource;
import com.dfq.coeffi.resource.EmployeeAttendanceResourceConverter;
import com.dfq.coeffi.resource.EmployeeAttendanceSheet;
import com.dfq.coeffi.service.holiday.HolidayService;
import com.dfq.coeffi.service.hr.DepartmentService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.LeaveService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import com.dfq.coeffi.util.AttendanceUtil;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.util.GeneratePdfReport;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.format.Colour;
import jxl.write.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.dfq.coeffi.util.DateUtil.getDaysBetweenDates;
import static jxl.format.Alignment.*;


@RestController
public class EmployeeAttendanceController extends BaseController {

    @Autowired
    private EmployeeAttendanceService employeeAttendanceService;
    private EmployeeAttendanceResourceConverter employeeAttendanceResourceConverter;
    private EmployeeService employeeService;
    private DepartmentService departmentService;
    private LeaveService leaveService;
    private EmployeeShiftAssignmentService employeeShiftAssignmentService;
    private ShiftService shiftService;
    private HolidayService holidayService;

    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static double otFactor = 1.0;
    private static long otGraceMins = 30;
    private static boolean shiftValidateRequiredOnWeekOff = false;


    @Autowired
    public EmployeeAttendanceController(EmployeeAttendanceService employeeAttendanceService, EmployeeService employeeService, EmployeeAttendanceResourceConverter employeeAttendanceResourceConverter,
                                        DepartmentService departmentService, LeaveService leaveService,
                                        EmployeeShiftAssignmentService employeeShiftAssignmentService,
                                        ShiftService shiftService,
                                        HolidayService holidayService) {
        this.employeeAttendanceService = employeeAttendanceService;
        this.employeeAttendanceResourceConverter = employeeAttendanceResourceConverter;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.leaveService = leaveService;
        this.employeeShiftAssignmentService = employeeShiftAssignmentService;
        this.shiftService = shiftService;
        this.holidayService = holidayService;
    }

    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    CompanyNameService companyNameService;
    @Autowired
    CompanyConfigureService companyConfigureService;

    /**
     * @return all the Employees Attendance List with details in the database
     */

    @GetMapping("employeeAttendance")
    public ResponseEntity<List<EmployeeAttendance>> getAllEmployeeAttendance() {
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getAllEmployeeAttendance();
        if (CollectionUtils.isEmpty(employeeAttendances)) {
            throw new EntityNotFoundException("employeeAttendances");
        }
        return new ResponseEntity<>(employeeAttendances, HttpStatus.OK);
    }

    @PostMapping("employeeAttendance/employee-attendance-filter")
    public ResponseEntity<List<EmployeeAttendanceSheetDto>> listOfWeekAttendance(@RequestBody DateDto dateDto) {

        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeetWeekAttendance(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()));
        List<EmployeeAttendanceSheetDto> sheetObject = AttendanceUtil.employeeAttendanceWeeklyReport(employeeAttendances);
        if (CollectionUtils.isEmpty(employeeAttendances)) {
            throw new EntityNotFoundException("attendances");
        }
        return new ResponseEntity<>(sheetObject, HttpStatus.OK);
    }

    @PostMapping("employeeAttendance/employee-attendance-filter-by-month")
    public ResponseEntity<List<MonthlyEmployeeAttendanceDto>> filterByMonth(@RequestBody DateDto dateDto) throws Exception {
        List<MonthlyEmployeeAttendanceDto> list = new ArrayList<MonthlyEmployeeAttendanceDto>();
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
//            attendanceCompute(employee.getId(), dateDto.getStartDate(), dateDto.getEndDate());
            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
            for (EmployeeAttendance employeeAttendance : monthlyEmployeeAttendance) {
                MonthlyStatusDto dto = new MonthlyStatusDto();
                dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
                dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
                dto.setMarkedOn(employeeAttendance.getMarkedOn());
                dto.setWorkedHours(employeeAttendance.getWorkedHours());
                dto.setInTime(employeeAttendance.getInTime());
                dto.setOutTime(employeeAttendance.getOutTime());
                dto.setId(employeeAttendance.getId());
                dto.setLateEntry(employeeAttendance.getLateEntry());
                monthlyStatusDtos.add(dto);
            }
            mADto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            mADto.setMonthlyStatus(monthlyStatusDtos);
            mADto.setEmployeeId(employee.getId());
            mADto.setEmployeeCode(employee.getEmployeeCode());
            if (employee.getDepartment() != null) {
                mADto.setDepartmentId(employee.getDepartment().getId());
                mADto.setDepartmentName(employee.getDepartment().getName());
                if (employee.getDesignation() != null) {
                    mADto.setDesignationId(employee.getDesignation().getId());
                    mADto.setDesignationName(employee.getDesignation().getName());
                } else {
                    mADto.setDesignationId(0);
                    mADto.setDesignationName("");
                }
            } else {
                mADto.setDepartmentId(0);
                mADto.setDepartmentName("");
            }

//            updateSunday(dateDto.startDate, dateDto.endDate, employee);
            if (employee.getDateOfJoining() != null) {
                if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
                    list.add(mADto);
                }
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new EntityNotFoundException("EmployeeAttendances");
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * @return today marked attendance
     */
    @GetMapping("employeeAttendance/today-marked-attendance")
    public ResponseEntity<List<EmployeeAttendance>> getTodayMarkedEmployeeAttendance() {
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getTodayMarkedEmployeeAttendance(DateUtil.getTodayDate());
        if (CollectionUtils.isEmpty(employeeAttendances)) {
            throw new EntityNotFoundException("today-marked-attendance");
        }
        return new ResponseEntity<>(employeeAttendances, HttpStatus.OK);
    }

    /**
     * @param eresource : save object to database and return the saved object
     * @return
     */
    @PostMapping("employeeAttendance")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EmployeeAttendance> createEmployeeAttendance(@Valid @RequestBody final EmployeeAttendanceResource eresource) {
        List<EmployeeAttendanceSheet> employeeAttendances = eresource.getEmployeeAttendanceReport();
        for (EmployeeAttendanceSheet attendanceSheet : employeeAttendances) {
            EmployeeAttendance employeeAttendanc = employeeAttendanceResourceConverter.toEntity(eresource, attendanceSheet);
            employeeAttendanceService.createEmployeeAttendance(employeeAttendanc,false);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("employeeAttendance/update-employee-attendance")
    public ResponseEntity<EmployeeAttendance> updateEmployeeAttendance(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) {
        Optional<EmployeeAttendance> employeeAttendanceObj = employeeAttendanceService.getEmployeeAttendance(employeeAttendanceDto.getId());
        if (!employeeAttendanceObj.isPresent()) {
            throw new EntityNotFoundException("EmployeeAttendance");
        }
        EmployeeAttendance employeeAttendance = employeeAttendanceObj.get();
        employeeAttendance.setAttendanceStatus(employeeAttendanceDto.getAttendanceStatus());
        Optional<Employee> employee = employeeService.getEmployee(employeeAttendanceDto.getEmployeeId());
        employeeAttendance.setEmployee(employee.get());

        employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("employee-attendance/monthly-attendance/{employeeId}")
    public ResponseEntity<List<MonthlyStatusDto>> monthlyEmployeeAttendanceById(@RequestBody DateDto dateDto, @PathVariable long employeeId) {
        Optional<Employee> employeeA = employeeService.getEmployee(employeeId);
        Employee employee1 = employeeA.get();
        List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee1.getId());
        List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
        for (EmployeeAttendance employeeAttendance : monthlyEmployeeAttendance) {
            MonthlyStatusDto dto = new MonthlyStatusDto();
            dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
            dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
            dto.setMarkedOn(employeeAttendance.getMarkedOn());
            dto.setEmployeeName(employee1.getFirstName() + " " + employee1.getLastName());
            dto.setInTime(employeeAttendance.getInTime());
            dto.setOutTime(employeeAttendance.getOutTime());
            dto.setWorkedHours(employeeAttendance.getWorkedHours());
            monthlyStatusDtos.add(dto);
        }
        return new ResponseEntity<>(monthlyStatusDtos, HttpStatus.OK);
    }

    @PostMapping("employeeAttendance/today-marked-attendance-by-department-designation")
    public ResponseEntity<List<EmployeeAttendance>> getTodayMarkedEmployeeAttendanceByDeptIdAndDesigId(@RequestBody DateDto dateDto) {
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getTodayMarkedEmployeeAttendanceByDeptIdAndDesgId(DateUtil.convertDateToFormat(dateDto.getStartDate()), dateDto.getDepartmentId(), dateDto.getDesignationId());
        if (CollectionUtils.isEmpty(employeeAttendances)) {
            throw new EntityNotFoundException("today-marked-attendance");
        }
        return new ResponseEntity<>(employeeAttendances, HttpStatus.OK);
    }

    @PostMapping("employeeAttendance/attendance-by-status/{employeeId}")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeAttendanceByEmployeeIdAndStatusAndStartDateAndEnd(@RequestBody DateDto dateDto, @PathVariable long employeeId) {
        Optional<Employee> employeeObj = employeeService.getEmployee(employeeId);
        Employee employee = employeeObj.get();

        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employee.getId(), AttendanceStatus.PRESENT, DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()));
        if (CollectionUtils.isEmpty(employeeAttendances)) {
            throw new EntityNotFoundException("Employee Attendance");
        }
        return new ResponseEntity<>(employeeAttendances, HttpStatus.OK);
    }

    @PostMapping("employeeAttendance/report/employee-attendance-filter-by-month")
    public ResponseEntity<InputStreamResource> filterByMonthReport(@RequestBody DateDto dateDto) throws DocumentException {
        List<MonthlyEmployeeAttendanceDto> list = new ArrayList<MonthlyEmployeeAttendanceDto>();
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
            for (EmployeeAttendance employeeAttendance : monthlyEmployeeAttendance) {
                MonthlyStatusDto dto = new MonthlyStatusDto();
                dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
                dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
                dto.setMarkedOn(employeeAttendance.getMarkedOn());
                dto.setInTime(employeeAttendance.getInTime());
                monthlyStatusDtos.add(dto);
            }
            mADto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            mADto.setMonthlyStatus(monthlyStatusDtos);
            mADto.setEmployeeId(employee.getId());
            if (employee.getDepartment() != null) {
                mADto.setDepartmentId(employee.getDepartment().getId());
                mADto.setDepartmentName(employee.getDepartment().getName());
                if (employee.getDesignation() != null) {
                    mADto.setDesignationId(employee.getDesignation().getId());
                    mADto.setDesignationName(employee.getDesignation().getName());
                } else {
                    mADto.setDesignationId(0);
                    mADto.setDesignationName("");
                }
            } else {
                mADto.setDepartmentId(0);
                mADto.setDepartmentName("");
            }
            if (employee.getDateOfJoining() != null) {
                if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
                    list.add(mADto);
                }
            }
        }
        ByteArrayInputStream bis = GeneratePdfReport.attendanceReportByMonth(list);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=TodayEmployeeAttendanceReport.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }


    //@GetMapping("employee-attendance/update/absent")
    @Scheduled(cron = "0 0 10,17 ? * SUN-SAT")
    @GetMapping("employee-attendance/update/absent")
    public void attendanceAbsentEntryShiftG() throws Exception {
        List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceService.getTodayMarkedEmployeeAttendance(new Date());
        if (employeeAttendanceList == null && employeeAttendanceList.size() == 0) {
            throw new EntityNotFoundException("No attendance captured on : " + DateUtil.getTodayDate());
        } else {
            Shift shiftA = shiftService.getShiftByName("G Shift");
            if (shiftA == null) {
                throw new EntityNotFoundException("G Shift not found");
            }
            List<Employee> employeeList = employeeShiftAssignmentService.getEmployeeListByShiftAndDate(DateUtil.getTodayDate(), shiftA);
            if (!(employeeList != null && employeeList.size() > 0)) {
                throw new EntityNotFoundException("Employees not assigned to SHIFTS");
            }
            for (Employee employee : employeeList) {
                EmployeeAttendance employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employee.getId(), AttendanceStatus.PRESENT, DateUtil.getTodayDate());
                if (employeeAttendances != null) {
                    System.out.println(employeeAttendances.getEmployee().getFirstName() + " is Present");
                } else {
                    EmployeeAttendance markedAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(DateUtil.getTodayDate(), employee.getId());
                    if (markedAttendance == null) {
                        Leave leave = leaveService.getApprovedLeavesForAttendanceMark(LeaveStatus.APPROVED, Math.toIntExact(employee.getId()), DateUtil.getTodayDate());
                        Holiday holiday = holidayService.getHolidayByStartDate(DateUtil.getTodayDate());
                        boolean sunday = isSunday(DateUtil.getTodayDate());
                        if (sunday) {
                            updateEmployeeAbsent(AttendanceStatus.SUNDAY, employee);
                        } else if (leave != null) {
                            updateEmployeeAbsent(AttendanceStatus.LEAVE, employee);
                        } else if (holiday != null) {
                            if (holiday.getHolidayType() == "Paid") {
                                updateEmployeeAbsent(AttendanceStatus.PH, employee);
                            } else if (holiday.getHolidayType() == "Declared") {
                                updateEmployeeAbsent(AttendanceStatus.HOLIDAY, employee);
                            }
                        } else {
                            updateEmployeeAbsent(AttendanceStatus.ABSENT, employee);
                        }
                    } else
                        System.out.println("ABSENT Marked already :" + employee.getId());
                }
            }
        }
    }

    @Scheduled(cron = "0 0 16 ? * SUN-SAT")
    public void attendanceAbsentEntryShiftB() {
        // TODO Auto-generated method stub
    }

    @Scheduled(cron = "0 0 5 ? * SUN-SAT")
    public void attendanceAbsentEntryShiftC() {
        // TODO Auto-generated method stub
    }

    @PostMapping("employee-attendance/update/{id}")
    public ResponseEntity<EmployeeAttendance> updateEmployeeAttendance(@PathVariable long id, @RequestBody EmployeeAttendance employeeAttendance) throws Exception {
        Optional<EmployeeAttendance> employeeAttendancesObj = employeeAttendanceService.getEmployeeAttendance(id);
//        if (employeeAttendancesObj.get().getAttendanceStatus() == AttendanceStatus.HOLIDAY) {
//            throw new EntityNotFoundException("Holiday Cannot Be Modifed");
//        } else if (employeeAttendancesObj.get().getAttendanceStatus() == AttendanceStatus.SUNDAY) {
//            throw new EntityNotFoundException("Sunday Cannot Be Modifed");
//        } else if (employeeAttendancesObj.get().getAttendanceStatus() == AttendanceStatus.PH) {
//            throw new EntityNotFoundException("Paid hoiday Cannot Be Modifed");
//        } else
        if (!employeeAttendancesObj.isPresent()) {
            throw new EntityNotFoundException("EmployeeAttendance not found for id: " + id);
        }
        Date intime = employeeAttendance.getInTime();
        Date outTime = employeeAttendance.getOutTime();

        Date startDate = employeeAttendance.getStartDate();
        Date endDate = employeeAttendance.getEndDate();

        intime.setYear(startDate.getYear());
        intime.setDate(startDate.getDate());
        intime.setMonth(startDate.getMonth());
        employeeAttendance.setInTime(intime);

        outTime.setYear(endDate.getYear());
        outTime.setDate(endDate.getDate());
        outTime.setMonth(endDate.getMonth());
        employeeAttendance.setOutTime(outTime);
        employeeAttendance.setId(id);
        //-------------------------------------
        Employee employee = employeeAttendancesObj.get().getEmployee();
        Employee employee1 = new Employee();
        employee1.setId(employee.getId());
        employee1.setFirstName(employee.getFirstName());
        employee1.setLastName(employee.getLastName());
        employee1.setEmployeeCode(employee.getEmployeeCode());
        employeeAttendance.setEmployee(employee1);
        //--------------------------------------
        employeeAttendance.setRecordedTime(employeeAttendancesObj.get().getRecordedTime());
//        employeeAttendance.setShift(employeeAttendancesObj.get().getShift());
        employeeAttendance.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
        updateWorkedHours(employeeAttendance);
        employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
        return new ResponseEntity<>(employeeAttendance, HttpStatus.OK);
    }

    @GetMapping("employee-attendance/dashboard/present-absent")
    public ResponseEntity<EmployeeAttendanceDto> getPresentAndAbsent() {
        EmployeeAttendanceDto dto = new EmployeeAttendanceDto();

        List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceService.getTodayMarkedEmployeeAttendance(DateUtil.getTodayDate());
        if (employeeAttendanceList == null) {
            throw new EntityNotFoundException("EmployeeAttendance not found");
        }
        System.out.println(employeeAttendanceList);
        long permanentPresent = 0;
        long permanentAbsent = 0;
        long contractPresent = 0;
        long contractAbsent = 0;
        for (EmployeeAttendance attendance : employeeAttendanceList) {
            if (attendance.getEmployee().getEmployeeType().toString().equalsIgnoreCase("PERMANENT")) {
                if (attendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                    permanentPresent = permanentPresent + 1;
                } else
                    permanentAbsent = permanentAbsent + 1;
            } else if (attendance.getEmployee().getEmployeeType().toString().equalsIgnoreCase("PERMANENT_WORKER")) {
                if (attendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                    contractPresent = contractPresent + 1;
                } else
                    contractAbsent = contractAbsent + 1;
            }
        }
        dto.setPermanentPresent(permanentPresent);
        dto.setPermanentAbsent(permanentAbsent);
        dto.setContractPresent(contractPresent);
        dto.setContractAbsent(contractAbsent);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("employee-attendance/dashboard/present-absent-contract")
    public ResponseEntity<EmployeeAttendanceDto> getPresentAndAbsentContract() {
        EmployeeAttendanceDto dto = new EmployeeAttendanceDto();

        List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceService.getTodayMarkedEmployeeAttendance(DateUtil.getTodayDate());
        if (employeeAttendanceList == null) {
            throw new EntityNotFoundException("EmployeeAttendance not found");
        }
        System.out.println(employeeAttendanceList);
        long permanentPresent = 0;
        long permanentAbsent = 0;
        long contractPresent = 0;
        long contractAbsent = 0;
        for (EmployeeAttendance attendance : employeeAttendanceList) {
            if (attendance.getEmployee().getEmployeeType().toString().equalsIgnoreCase("CONTRACT")) {
                if (attendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                    permanentPresent = permanentPresent + 1;
                } else
                    permanentAbsent = permanentAbsent + 1;
            } else if (attendance.getEmployee().getEmployeeType().toString().equalsIgnoreCase("PERMANENT_CONTRACT")) {
                if (attendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                    contractPresent = contractPresent + 1;
                } else
                    contractAbsent = contractAbsent + 1;
            }
        }
        dto.setPermanentPresent(permanentPresent);
        dto.setPermanentAbsent(permanentAbsent);
        dto.setContractPresent(contractPresent);
        dto.setContractAbsent(contractAbsent);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("employee-attendance/dashboard/employee-by-department")
    public ResponseEntity<List<DepartmentDashboard>> getEmployeeDepartmentwise() {
        List<DepartmentDashboard> departmentList = new ArrayList<>();
        List<Department> departments = departmentService.findAll();
        for (Department department : departments) {
            List<Employee> employeeAttendanceList = employeeService.getEmployeesByDepartment(department.getId());
            DepartmentDashboard departmentDashboard = new DepartmentDashboard();
            departmentDashboard.setDeprtmentName(department.getName());
            departmentDashboard.setNoOfEmployee(employeeAttendanceList.size());
            departmentDashboard.setDepartmentId(department.getId());
            departmentList.add(departmentDashboard);
        }
        System.out.println(departmentList);
        return new ResponseEntity(departmentList, HttpStatus.OK);
    }

    @GetMapping("employee-attendance/dashboard/present-absent-department/{departmentId}")
    public ResponseEntity<DepartmentDashboard> getPresentAndAbsentDepartmentwise(@PathVariable long departmentId) {
        DepartmentDashboard dto = new DepartmentDashboard();

        List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceService.getEmployeeAttendanceByDepartment(DateUtil.getTodayDate(), departmentId);
        if (employeeAttendanceList == null) {
            throw new EntityNotFoundException("EmployeeAttendance not found");
        }
        long permanentPresent = 0;
        long permanentAbsent = 0;
        long contractPresent = 0;
        long contractAbsent = 0;
        for (EmployeeAttendance attendance : employeeAttendanceList) {
            if (attendance.getEmployee().getEmployeeType().toString().equalsIgnoreCase("PERMANENT")) {
                if (attendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                    permanentPresent = permanentPresent + 1;
                } else
                    permanentAbsent = permanentAbsent + 1;
            } else {
                if (attendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                    contractPresent = contractPresent + 1;
                } else
                    contractAbsent = contractAbsent + 1;
            }
        }
        dto.setPermanentPresent(permanentPresent);
        dto.setPermanentAbsent(permanentAbsent);
        dto.setContractPresent(contractPresent);
        dto.setContractAbsent(contractAbsent);
        dto.setDepartmentId(departmentId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("employee-attendance/dashboard/present-absent-contract/department/{departmentId}")
    public ResponseEntity<DepartmentDashboard> getPresentAndAbsentDepartmentwiseContract(@PathVariable long departmentId) {
        DepartmentDashboard dto = new DepartmentDashboard();

        List<EmployeeAttendance> employeeAttendanceList = employeeAttendanceService.getEmployeeAttendanceByDepartment(DateUtil.getTodayDate(), departmentId);
        if (employeeAttendanceList == null) {
            throw new EntityNotFoundException("EmployeeAttendance not found");
        }
        long permanentPresent = 0;
        long permanentAbsent = 0;
        long contractPresent = 0;
        long contractAbsent = 0;
        for (EmployeeAttendance attendance : employeeAttendanceList) {
            if (attendance.getEmployee().getEmployeeType().toString().equalsIgnoreCase("CONTRACT")) {
                if (attendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                    permanentPresent = permanentPresent + 1;
                } else
                    permanentAbsent = permanentAbsent + 1;
            } else if (attendance.getEmployee().getEmployeeType().toString().equalsIgnoreCase("PERMANENT_CONTRACT")) {
                if (attendance.getAttendanceStatus().toString().equalsIgnoreCase("PRESENT")) {
                    contractPresent = contractPresent + 1;
                } else
                    contractAbsent = contractAbsent + 1;
            }
        }
        dto.setPermanentPresent(permanentPresent);
        dto.setPermanentAbsent(permanentAbsent);
        dto.setContractPresent(contractPresent);
        dto.setContractAbsent(contractAbsent);
        dto.setDepartmentId(departmentId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    //    public String convert(long miliSeconds) {
//        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
//        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
//        //int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
//        return String.format("%02d:%02d", hrs, min/*:%2d, sec*/);
//    }
    public String convert(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        //int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        if (hrs == 0 && min < 10) {
            hrs = 0;
            min = 0;
        }
        return String.format("%02d:%02d", hrs, min/*:%2d, sec*/);
    }

    public String convertOt(String d) {
        String hrs = String.valueOf((long) (Double.parseDouble(d)));
        String min;
        String numberD = String.valueOf(d);
        numberD = numberD.substring(numberD.indexOf(".")).substring(1);
//        System.out.println("numberD " + numberD + " and d" + d);
        if (numberD.length() == 1) {
            numberD = numberD + "0";
        }
        double mind = Double.parseDouble(numberD);
        mind = mind / (1.67);
        long minl = (long) mind;
        min = String.valueOf(minl);
        if (min.length() == 1) {
            min = "0" + min;
        }
        String ot = hrs + "." + min;
        return ot;
    }

    @PostMapping("employee-attendance/import-excel")
    public ResponseEntity importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        Workbook workbook = uploadExcelFile(file);
        return new ResponseEntity(HttpStatus.OK);
    }

    private Workbook uploadExcelFile(MultipartFile multipartFile) throws Exception {
        Workbook workbook = new HSSFWorkbook(multipartFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();
        System.out.println("\n\nIterating over Rows and Columns using for-each loop\n");
        for (Row row : sheet) {
            EmployeeAttendance attendance = new EmployeeAttendance();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                String cellValue = dataFormatter.formatCellValue(row.getCell(i));
                attendance.setMarkedOn(row.getCell(1).getDateCellValue());
                attendance.setInTime(row.getCell(3).getDateCellValue());
                attendance.setOutTime(row.getCell(4).getDateCellValue());
                Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(row.getCell(5).getStringCellValue());
                attendance.setEmployee(employee.get());
                Shift shift = shiftService.getShift((long) row.getCell(6).getNumericCellValue());
                attendance.setShift(shift);
                if (row.getCell(2).getStringCellValue().equalsIgnoreCase("PRESENT")) {
                    attendance = updateWorkedHours(attendance);
                } else if (row.getCell(2).getStringCellValue().equalsIgnoreCase("ABSENT")) {
                    attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                } else if (row.getCell(2).getStringCellValue().equalsIgnoreCase("LEAVE")) {
                    attendance.setAttendanceStatus(AttendanceStatus.LEAVE);
                } else if (row.getCell(2).getStringCellValue().equalsIgnoreCase("HOLIDAY")) {
                    attendance.setAttendanceStatus(AttendanceStatus.HOLIDAY);
                }
            }
            employeeAttendanceService.createEmployeeAttendance(attendance,false);
        }
        workbook.close();
        return workbook;
    }

//    private EmployeeAttendance updateWorkedHours(EmployeeAttendance attendance, Shift shift) throws ParseException {
////        attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
//        DateFormat timeFormat = new SimpleDateFormat("HH.mm");
//        String inTime = timeFormat.format(attendance.getInTime());
//        String outTime = timeFormat.format(attendance.getOutTime());
//        long attendanceInTime = timeFormat.parse(inTime).getTime();
//        long attendanceOutTime = timeFormat.parse(outTime).getTime();
//        //Time attTime = new Time(attendanceTime);
//        String shiftStartTime = timeFormat.format(shift.getStartTime());
//        long shiftStart = timeFormat.parse(shiftStartTime).getTime();
//        double workedMinutes = 0;
//        long workedMillis;
//        if (attendanceInTime > shiftStart + shift.getGraceInTime()) {
//            long lateEntry = attendanceInTime - shift.getStartTime().getTime();
//            String lateEntryTime = convert(lateEntry);
//            attendance.setLateEntry(lateEntryTime);
//            workedMillis = attendanceOutTime - attendanceInTime;
//        } else {
//            attendance.setLateEntry("00:00");
//            workedMillis = attendanceOutTime - shiftStart;
//        }
//        workedMinutes = TimeUnit.MILLISECONDS.toMinutes(workedMillis);
//        double workedHrs = workedMinutes / 60;
//        attendance.setWorkedHours(String.format("%.2f", workedHrs));
//        if (workedMinutes > 660) {
//            double extraHour = workedMinutes - 660;
//            double overTimeHrs = (extraHour + 150) / 60;
//            attendance.setOverTime(String.format("%.2f", overTimeHrs));
////            double effectiveExtraHour = overTimeHrs * 0.7;
//            double effectiveExtraHour = overTimeHrs * 1.0;
//            attendance.setEffectiveOverTime(String.format("%.2f", effectiveExtraHour));
//
//        } else {
//            attendance.setOverTime(String.valueOf(0.00));
//            attendance.setEffectiveOverTime(String.valueOf(0.00));
//        }
//        return attendance;
//    }

    @PostMapping("employee-attendance/monthly-attendance")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeMonthlyAttendanceReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {

//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
        if(dateDto.getReportType().equalsIgnoreCase("monthly")){
            dateDto.setStartDate(DateUtil.startDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
            dateDto.setEndDate(DateUtil.endDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Attendance_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    @PostMapping("employee-attendance/monthly-attendance/late-entry")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeMonthlyAttendanceLateEntryReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(dateDto.getReportType().equalsIgnoreCase("monthly")){
            dateDto.setStartDate(DateUtil.startDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
            dateDto.setEndDate(DateUtil.endDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Monthly_Late_Entry_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceLateEntry(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    @PostMapping("employee-attendance/monthly-attendance/over-time")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeMonthlyAttendanceOverTimeReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(dateDto.getReportType().equalsIgnoreCase("monthly")){
            dateDto.setStartDate(DateUtil.startDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
            dateDto.setEndDate(DateUtil.endDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Monthly_over_Time_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceOverTime(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    @PostMapping("employee-attendance/monthly-attendance/early-time")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeMonthlyAttendanceEarlyTimeReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(dateDto.getReportType().equalsIgnoreCase("monthly")){
            dateDto.setStartDate(DateUtil.startDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
            dateDto.setEndDate(DateUtil.endDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Monthly_over_Time_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEarlyOutTime(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }

    @PostMapping("employee-attendance/view-monthly-attendance")
    public ResponseEntity<List<EmployeeAttendance>> viewEmployeeMonthlyAttendance(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(dateDto.getReportType().equalsIgnoreCase("monthly")){
            dateDto.setStartDate(DateUtil.startDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
            dateDto.setEndDate(DateUtil.endDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        List<MonthlyEmployeeAttendanceDto> mADto = new ArrayList<>();
        for (MonthlyEmployeeAttendanceDto monthlyEmployeeAttendanceDto : monthlyEmployeeAttendanceDtos) {
            Optional<Employee> employee = employeeService.getEmployee(monthlyEmployeeAttendanceDto.getEmployeeId());
            mADto.add(monthlyEmployeeAttendanceDto);
//            if (employee.get().getDateOfJoining().before(dateDto.startDate) || (employee.get().getDateOfJoining().after(dateDto.startDate) && employee.get().getDateOfJoining().before(dateDto.endDate))) {
//                mADto.add(monthlyEmployeeAttendanceDto);
//            }
        }
        if (monthlyEmployeeAttendanceDtos.isEmpty()) {
            throw new EntityNotFoundException("EmployeeAttendance viewEmployeeMonthlyAttendance not found");
        }

        return new ResponseEntity(mADto, HttpStatus.OK);
    }

    @PostMapping("report/employee-attendance/early-checkout")
    public ResponseEntity<List<EmployeeAttendance>> viewEmployeeEarlyCheckOut(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        if(dateDto.getReportType().equalsIgnoreCase("monthly")){
            dateDto.setStartDate(DateUtil.startDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
            dateDto.setEndDate(DateUtil.endDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendanceEarlyCheckOut(dateDto);
        List<MonthlyEmployeeAttendanceDto> mADto = new ArrayList<>();

        if (monthlyEmployeeAttendanceDtos.isEmpty()) {
            throw new EntityNotFoundException("EmployeeAttendance viewEmployeeMonthlyAttendance not found");
        }

        return new ResponseEntity(mADto, HttpStatus.OK);
    }


    @PostMapping("employee-attendance/monthly-attendance/extra-hrs")
    public ResponseEntity<List<EmployeeAttendance>> getEmployeeMonthlyExtraHrs(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(dateDto.getReportType().equalsIgnoreCase("monthly")){
            dateDto.setStartDate(DateUtil.startDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
            dateDto.setEndDate(DateUtil.endDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Monthly_Late_Entry_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceLateEntry(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }


    private WritableWorkbook attendanceEntry(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Monthly-Attendance-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 8);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 8);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 8);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);

        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(Colour.GRAY_25);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        cf.setBoldStyle(WritableFont.NO_BOLD);
        WritableCellFormat c = new WritableCellFormat(cf);
        c.setAlignment(CENTRE);
        c.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
        cellFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
        cellFormatRight.setAlignment(RIGHT);
        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
        cellFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
        cellFormatLeft.setAlignment(LEFT);
        WritableFont cellFontSimpleRight = new WritableFont(WritableFont.TIMES);
        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleRight);
        cellFormatSimpleRight.setAlignment(RIGHT);

        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.mergeCells(0, 0, 33, 0);
        s.mergeCells(0, 1, 33, 1);
        s.mergeCells(2, 18, 5, 18);
        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName() + " PVT LTD", headerFormat);
        s.addCell(lable);
        SimpleDateFormat dff = new SimpleDateFormat("dd-MMM-yyyy");
        String fd = dff.format(fromDate);
        String td = dff.format(toDate);
        Label lableSlip = new Label(0, 1, "Monthly attendance report - From:" + fd + "   To:" + td, headerFormat);
        s.addCell(lableSlip);

        int j = 5;
        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
        for (int i = 0; i < dates.size(); i++) {
            s.mergeCells(j, 2, j + 1, 2);
            DateFormat formatter = new SimpleDateFormat("dd");
            s.addCell(new Label(j, 2, "" + formatter.format(dates.get(i)), cellFormatDate));
            s.addCell(new Label(j, 3, "In-Time", cellFormat));
            s.addCell(new Label(j + 1, 3, "Out-Time", cellFormat));
            s.addCell(new Label(j + 2, 3, "WH", cellFormat));
            s.addCell(new Label(j + 3, 3, "OT", cellFormat));
            j = j + 4;
        }
        s.addCell(new Label(j + 1, 3, "PRESENT", cellFormat));
        s.addCell(new Label(j + 2, 3, "HOLIDAYS", cellFormat));
        s.addCell(new Label(j + 3, 3, "WO", cellFormat));
        s.addCell(new Label(j + 4, 3, "LEAVES", cellFormat));

        s.addCell(new Label(j + 5, 3, "ABSENT", cellFormat));
        s.addCell(new Label(j + 6, 3, "COMP_OFF", cellFormat));
        s.addCell(new Label(j + 7, 3, "TOTAL_HOURS", cellFormat));
        s.addCell(new Label(j + 8, 3, "TOTAL_OT_HOURS", cellFormat));
        s.addCell(new Label(j + 9, 3, "LATE_ENTRY", cellFormat));
        s.addCell(new Label(j + 10, 3, "EARLY_OUT", cellFormat));

        int rowNum = 4;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cLeft));
            s.addCell(new Label(1, 3, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 3, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartmentName(), cLeft));
            s.addCell(new Label(4, 3, "Designation Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDesignationName(), cLeft));
            int colNum = 5;
            double leaves;
            double present;
            long sundays;
            long holidays;
            long absents;
            long compoffs;
            double totalWorkedHours;
            double totalOtHours;
            double lateIn;
            double earlyOut;
            String inTime = null;
            String outTime = null;
            String workedHours = null;
            String overTime = null;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
                System.out.println("employeeAttendanceDto.getMonthlyStatus().size(): " + employeeAttendanceDto.getMonthlyStatus().size());
                if (/*employeeAttendanceDto.getMonthlyStatus() != null &&*/ employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays <= employeeAttendanceDto.getMonthlyStatus().size()) {
                    DateFormat timeFormat = new SimpleDateFormat("HH.mm");
                    try {
//                        employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null
                        if (employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null && employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {

                            inTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getInTime());
                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                inTime = "A";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                                inTime = "H";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
                                inTime = "PH";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                                inTime = "WO";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
                                inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                inTime = "HALF-DAY";

                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
                                inTime = "CO";
                                workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
                                overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();
                            }
//                            inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                        if (/*employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null &&*/ employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null) {
                                outTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime());
                            }

                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                outTime = "A";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                                outTime = "H";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
                                outTime = "PH";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                                outTime = "WO";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
                                outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                outTime = "HALF-DAY";

                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
                                outTime = "CO";

                            }
//                            outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Index out of bound");
                    }

                    if (workedHours != null) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        workedHours = df.format(Double.valueOf(workedHours));
                        workedHours = String.valueOf(Double.valueOf(workedHours));
                    }
                    s.addCell(new Label(colNum, rowNum, "" + inTime, c));
                    s.addCell(new Label(colNum + 1, rowNum, "" + outTime, c));
                    s.addCell(new Label(colNum + 2, rowNum, "" + workedHours, c));
                    s.addCell(new Label(colNum + 3, rowNum, "" + overTime, c));
                    colNum = colNum + 4;
                } else {
                    s.addCell(new Label(colNum, rowNum, "" + "-", c));
                    s.addCell(new Label(colNum + 1, rowNum, "" + "-", c));
                    s.addCell(new Label(colNum + 2, rowNum, "" + "-", c));
                    s.addCell(new Label(colNum + 3, rowNum, "" + "-", c));
                    colNum = colNum + 4;
                }

            }

            for (int i = noOfDays + 1; i <= noOfDays + 8; i++) {
                if (employeeAttendanceDto.getMonthlyStatus() != null && employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays < employeeAttendanceDto.getMonthlyStatus().size()) {
                    present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfPresentDays();
//                  present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHalfDays();
                    sundays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfSudays();
                    holidays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHolidays();
                    leaves = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfLeaves();

                    absents = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfAbsent();
                    compoffs = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfCompOffs();
                    totalWorkedHours = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalHours();
                    totalOtHours = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalOtHours();

                    lateIn = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalLateEntry();
                    earlyOut = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalEarlyCheckOut();
                    s.addCell(new Label(j + 1, rowNum, "" + present, c));
                    s.addCell(new Label(j + 2, rowNum, "" + holidays, c));
                    s.addCell(new Label(j + 3, rowNum, "" + sundays, c));
                    s.addCell(new Label(j + 4, rowNum, "" + leaves, c));

                    s.addCell(new Label(j + 5, rowNum, "" + absents, c));
                    s.addCell(new Label(j + 6, rowNum, "" + compoffs, c));
                    s.addCell(new Label(j + 7, rowNum, "" + totalWorkedHours, c));
                    s.addCell(new Label(j + 8, rowNum, "" + totalOtHours, c));

                    s.addCell(new Label(j + 9, rowNum, "" + lateIn, c));
                    s.addCell(new Label(j + 10, rowNum, "" + earlyOut, c));
                } else {
                    s.addCell(new Label(j + 1, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 2, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 3, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 4, rowNum, "" + "-", c));

                    s.addCell(new Label(j + 5, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 6, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 7, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 8, rowNum, "" + "-", c));

                    s.addCell(new Label(j + 9, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 10, rowNum, "" + "-", c));
                }
            }
            rowNum = rowNum + 1;


        }
        return workbook;
    }

    private WritableWorkbook attendanceLateEntry(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException, ParseException {
        WritableSheet s = workbook.createSheet("Monthly-Late-Entry-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 8);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 8);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 8);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);

        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(Colour.GRAY_25);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        cf.setBoldStyle(WritableFont.NO_BOLD);
        WritableCellFormat c = new WritableCellFormat(cf);
        c.setAlignment(CENTRE);
        c.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
        cellFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
        cellFormatRight.setAlignment(RIGHT);
        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
        cellFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
        cellFormatLeft.setAlignment(LEFT);
        WritableFont cellFontSimpleRight = new WritableFont(WritableFont.TIMES);
        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleRight);
        cellFormatSimpleRight.setAlignment(RIGHT);
        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.mergeCells(0, 0, 33, 0);
        s.mergeCells(0, 1, 33, 1);
        s.mergeCells(2, 18, 5, 18);
        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName() + " PVT LTD", headerFormat);
        s.addCell(lable);
        SimpleDateFormat dff = new SimpleDateFormat("dd-MMM-yyyy");
        String fd = dff.format(fromDate);
        String td = dff.format(toDate);
        Label lableSlip = new Label(0, 1, "Monthly late entry report - From:" + fd + "   To:" + td, headerFormat);
        s.addCell(lableSlip);

        int month = DateUtil.getMonthNumber(fromDate);
        YearMonth yearMonth = YearMonth.of(fromDate.getYear(), month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(fromDate.getYear(), month - 1, 1);
        int numOfDaysInMonth = yearMonth.lengthOfMonth();

        int j = 5;
        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);

        for (int i = 0; i < dates.size(); i++) {
            // s.mergeCells(j, 1, j, 1);
            DateFormat formatter = new SimpleDateFormat("dd");
            s.addCell(new Label(j, 2, "" + formatter.format(dates.get(i)), cellFormatDate));
            s.addCell(new Label(j, 3, "In-Time", cellFormat));
            j = j + 1;
        }

        int rowNum = 4;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cLeft));
            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 3, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartmentName(), cLeft));
            s.addCell(new Label(4, 3, "Designation Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDesignationName(), cLeft));
            int colNum = 5;
            String lateEntry = null;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
                if (employeeAttendanceDto.getMonthlyStatus().size() > 0 && employeeAttendanceDto.getMonthlyStatus() != null) {
                    DateFormat timeFormat = new SimpleDateFormat("HH.mm");
                    if (employeeAttendanceDto.getMonthlyStatus().get(m).getLateEntry() != null) {
                        lateEntry = employeeAttendanceDto.getMonthlyStatus().get(m).getLateEntry();
                        if (lateEntry.equalsIgnoreCase("00:00") || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().toString().equalsIgnoreCase("ABSENT")) {
                            lateEntry = "--";
                        } else {
                            lateEntry = employeeAttendanceDto.getMonthlyStatus().get(m).getLateEntry();
                        }
                    }
                    s.addCell(new Label(colNum, rowNum, "" + lateEntry, c));
                    colNum = colNum + 1;
                }

            }
            rowNum = rowNum + 1;
        }

        return workbook;
    }

    public static boolean isSunday(Date givenDate) throws Exception {
        Date date1 = givenDate;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        boolean sunday = false;
        if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            sunday = true;
        }
        return sunday;
    }

    public static boolean isWeekOff(Date givenDate, long wo1, long wo2) throws Exception {
        Date date1 = givenDate;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        boolean off = false;
        System.out.println("hlo2" + givenDate + "wo1-" + wo1 + "c1.get(Calendar.DAY_OF_WEEK)-" + c1.get(Calendar.DAY_OF_WEEK));
        if (c1.get(Calendar.DAY_OF_WEEK) == (wo1 + 1) || c1.get(Calendar.DAY_OF_WEEK) == (wo2 + 1)) {
            off = true;
        }
        return off;
    }

    public boolean isAbsentForWo(Date givenDate, long employeeId) {
        boolean status = false;
        Date yesterdayDate = DateUtil.yesterday(givenDate);
        Date tomorrow = DateUtil.tomorrow(givenDate);
        EmployeeAttendance employeeAttendanceYest = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(yesterdayDate, employeeId);
        EmployeeAttendance employeeAttendanceTomor = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(tomorrow, employeeId);

        if (employeeAttendanceTomor == null) {
            employeeAttendanceTomor = new EmployeeAttendance();
            employeeAttendanceTomor.setAttendanceStatus(AttendanceStatus.ABSENT);
            Optional<Employee> employee = employeeService.getEmployee(employeeId);
            employeeAttendanceTomor.setEmployee(employee.get());
            employeeAttendanceTomor.setMarkedOn(tomorrow);
            employeeAttendanceTomor.setRecordedTime(tomorrow);
            employeeAttendanceTomor = employeeAttendanceService.createEmployeeAttendance(employeeAttendanceTomor,true);
        }
        if (employeeAttendanceYest == null) {
            employeeAttendanceYest = new EmployeeAttendance();
            employeeAttendanceYest.setAttendanceStatus(AttendanceStatus.ABSENT);
            Optional<Employee> employee = employeeService.getEmployee(employeeId);
            employeeAttendanceYest.setEmployee(employee.get());
            employeeAttendanceYest.setMarkedOn(yesterdayDate);
            employeeAttendanceYest.setRecordedTime(yesterdayDate);
            employeeAttendanceYest = employeeAttendanceService.createEmployeeAttendance(employeeAttendanceYest,true);
        }

        if (employeeAttendanceYest.getAttendanceStatus().equals(AttendanceStatus.ABSENT) && employeeAttendanceTomor.getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
            status = true;
        } else {
            status = false;
        }
        return status;
    }

    private WritableWorkbook attendanceOverTime(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Monthly-Attendance-OverTime", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 8);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 8);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 8);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);

        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(Colour.GRAY_25);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        cf.setBoldStyle(WritableFont.NO_BOLD);
        WritableCellFormat c = new WritableCellFormat(cf);
        c.setAlignment(CENTRE);
        c.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
        cellFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
        cellFormatRight.setAlignment(RIGHT);
        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
        cellFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
        cellFormatLeft.setAlignment(LEFT);
        WritableFont cellFontSimpleRight = new WritableFont(WritableFont.TIMES);
        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleRight);
        cellFormatSimpleRight.setAlignment(RIGHT);
        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.mergeCells(0, 0, 33, 0);
        s.mergeCells(0, 1, 33, 1);
        s.mergeCells(2, 18, 5, 18);
        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName() + " PVT LTD", headerFormat);
        s.addCell(lable);
        SimpleDateFormat dff = new SimpleDateFormat("dd-MMM-yyyy");
        String fd = dff.format(fromDate);
        String td = dff.format(toDate);
        Label lableSlip = new Label(0, 1, "Monthly over time report - From:" + fd + "   To:" + td, headerFormat);
        s.addCell(lableSlip);

        int j = 5;
        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
        for (int i = 0; i < dates.size(); i++) {
//            s.mergeCells(j, 2, j + 1, 2);
            DateFormat formatter = new SimpleDateFormat("dd");
            s.addCell(new Label(j, 2, "" + formatter.format(dates.get(i)), cellFormatDate));
            s.addCell(new Label(j, 3, "Over-Time", cellFormat));
            j = j + 1;
        }


        int rowNum = 4;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cLeft));
            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 3, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartmentName(), cLeft));
            s.addCell(new Label(4, 3, "Designation Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDesignationName(), cLeft));
            int colNum = 5;
            String inTime = null;
            String outTime = null;
            String overTime = null;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
                if (employeeAttendanceDto.getMonthlyStatus() != null && employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays <= employeeAttendanceDto.getMonthlyStatus().size()) {
                    DateFormat timeFormat = new SimpleDateFormat("HH.mm");
                    System.out.println("m: " + m + " in time: " + employeeAttendanceDto.getMonthlyStatus().get(m).getMarkedOn());
                    if (employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null) {
                        inTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getInTime());
                        if (inTime.equalsIgnoreCase("00.00")) {
                            inTime = "A";
                        }
                    } else {
                        inTime = "A";
                    }
                    if (employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null) {
                        outTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime());
                        if (outTime.equalsIgnoreCase("00.00")) {
                            outTime = "A";
                        }
                    } else {
                        outTime = "A";
                    }
                    if (employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs() != null) {
                        overTime = (employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs());
                    } else {
                        overTime = "0.0";
                    }

                    s.addCell(new Label(colNum, rowNum, "" + overTime, c));
                    colNum = colNum + 1;
                }
            }
            rowNum = rowNum + 1;
        }
        return workbook;
    }

    private WritableWorkbook attendanceEarlyOutTime(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Monthly-Early-Out", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 8);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 8);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 8);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);

        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(Colour.GRAY_25);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        cf.setBoldStyle(WritableFont.NO_BOLD);
        WritableCellFormat c = new WritableCellFormat(cf);
        c.setAlignment(CENTRE);
        c.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
        cellFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
        cellFormatRight.setAlignment(RIGHT);
        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
        cellFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
        cellFormatLeft.setAlignment(LEFT);
        WritableFont cellFontSimpleRight = new WritableFont(WritableFont.TIMES);
        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleRight);
        cellFormatSimpleRight.setAlignment(RIGHT);
        s.setColumnView(0, 5);
        s.setColumnView(1, 20);
        s.setColumnView(2, 15);
        s.setColumnView(3, 10);
        s.setColumnView(4, 10);
        s.setColumnView(5, 10);
        s.mergeCells(0, 0, 33, 0);
        s.mergeCells(0, 1, 33, 1);
        s.mergeCells(2, 18, 5, 18);
        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName() + " PVT LTD", headerFormat);
        s.addCell(lable);
        SimpleDateFormat dff = new SimpleDateFormat("dd-MMM-yyyy");
        String fd = dff.format(fromDate);
        String td = dff.format(toDate);
        Label lableSlip = new Label(0, 1, "Monthly early out report - From:" + fd + "   To:" + td, headerFormat);
        s.addCell(lableSlip);

        int j = 5;
        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
        for (int i = 0; i < dates.size(); i++) {
//            s.mergeCells(j, 2, j + 1, 2);
            DateFormat formatter = new SimpleDateFormat("dd");
            s.addCell(new Label(j, 2, "" + formatter.format(dates.get(i)), cellFormatDate));
            s.addCell(new Label(j, 3, "Early-out-Time", cellFormat));
            j = j + 1;
        }


        int rowNum = 4;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cLeft));
            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 3, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getDepartmentName(), cLeft));
            s.addCell(new Label(4, 3, "Designation Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getDesignationName(), cLeft));
            int colNum = 5;
            String inTime = null;
            String outTime = null;
            String overTime = null;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
                if (employeeAttendanceDto.getMonthlyStatus() != null && employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays <= employeeAttendanceDto.getMonthlyStatus().size()) {
                    DateFormat timeFormat = new SimpleDateFormat("HH.mm");
                    System.out.println("m: " + m + " in time: " + employeeAttendanceDto.getMonthlyStatus().get(m).getMarkedOn());
                    if (employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null) {
                        inTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getInTime());
                        if (inTime.equalsIgnoreCase("00.00")) {
                            inTime = "A";
                        }
                    } else {
                        inTime = "A";
                    }
                    if (employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null) {
                        outTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime());
                        if (outTime.equalsIgnoreCase("00.00")) {
                            outTime = "A";
                        }
                    } else {
                        outTime = "A";
                    }
                    if (employeeAttendanceDto.getMonthlyStatus().get(m).getEarlyOut() != null) {
                        overTime = (employeeAttendanceDto.getMonthlyStatus().get(m).getEarlyOut());
                    } else {
                        overTime = "0.0";
                    }


                    s.addCell(new Label(colNum, rowNum, "" + overTime, c));
                    colNum = colNum + 1;
                }
            }
            rowNum = rowNum + 1;
        }
        return workbook;
    }

    private void updateEmployeeAbsent(AttendanceStatus attendanceStatus, Employee employee) {
        EmployeeAttendance absentEmployee = new EmployeeAttendance();
        EmployeeShiftAssignment employeeShiftAssignment = employeeShiftAssignmentService.getCurrentShiftByEmployeeId(employee.getId(), DateUtil.getTodayDate());
        Shift shift;
        if (employeeShiftAssignment == null) {
//            throw new EntityNotFoundException("Shift Not Assigned to Employee Id :" + employee.getId());
            shift = shiftService.getCurrentShiftWithGrace();
        } else {
            shift = employeeShiftAssignment.getShift();
        }
        absentEmployee.setAttendanceStatus(attendanceStatus);
        if (shift != null) {
            absentEmployee.setShift(shift);
        } else {
            absentEmployee.setShift(null);
        }
        Date zeroTime = new Date();
        zeroTime.setHours(0);
        zeroTime.setMinutes(0);
        zeroTime.setSeconds(0);
        absentEmployee.setInTime(zeroTime);
        absentEmployee.setOutTime(zeroTime);
        absentEmployee.setEffectiveOverTime("0");
        absentEmployee.setOverTime("0");
        absentEmployee.setWorkedHours("0");
        absentEmployee.setRecordedTime(new Date());
        absentEmployee.setLateEntry("00.00");
        absentEmployee.setEmployee(employee);
        absentEmployee.setMarkedOn(DateUtil.getTodayDate());
        employeeAttendanceService.createEmployeeAttendance(absentEmployee,false);
    }

//    private List<MonthlyEmployeeAttendanceDto> viewMonthlyEmployeeAttendance(DateDto dateDto) throws ParseException {
//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
//        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = new ArrayList<MonthlyEmployeeAttendanceDto>();
//        List<Employee> employees = employeeService.findAll();
//        for (Employee employee : employees) {
//            // TODO
//            attendanceCompute(employee.getId(), dateDto.startDate, dateDto.getEndDate());
////          updateAttendanceLeaveWise(employee.getId(),dateDto.startDate,dateDto.getEndDate());
//            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
//            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId
//                    (DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
//            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
//            List<EmployeeAttendance> presentSize = new ArrayList<>();
//            List<EmployeeAttendance> holidaySize = new ArrayList<>();
//            List<EmployeeAttendance> sundaySize = new ArrayList<>();
//            List<EmployeeAttendance> halfDaySize = new ArrayList<>();
//            List<EmployeeAttendance> leaveSize = new ArrayList<>();
//            for (EmployeeAttendance employeeAttendance : monthlyEmployeeAttendance) {
//                MonthlyStatusDto dto = new MonthlyStatusDto();
//                dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
//                dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
//                dto.setMarkedOn(employeeAttendance.getMarkedOn());
//                dto.setInTime(employeeAttendance.getInTime());
//                dto.setOutTime(employeeAttendance.getOutTime());
//                dto.setLateEntry(employeeAttendance.getLateEntry());
//                dto.setWorkedHours(employeeAttendance.getWorkedHours());
//                dto.setExtraHrs(employeeAttendance.getOverTime());
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.PRESENT) {
//                    presentSize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.SUNDAY) {
//                    sundaySize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.HOLIDAY) {
//                    holidaySize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.LEAVE || employeeAttendance.getAttendanceStatus() == AttendanceStatus.EL || employeeAttendance.getAttendanceStatus() == AttendanceStatus.CL || employeeAttendance.getAttendanceStatus() == AttendanceStatus.ML) {
//                    leaveSize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.PH) {
//                    holidaySize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.HALF_DAY) {
//                    halfDaySize.add(employeeAttendance);
//                }
//                double totalPresent = presentSize.size() + (halfDaySize.size() * 0.5);
//                dto.setNoOfPresentDays(totalPresent);
//                dto.setNoOfSudays(sundaySize.size());
//                dto.setNoOfHolidays(holidaySize.size());
//                dto.setNoOfLeaves(leaveSize.size() + (halfDaySize.size() * 0.5));
//                monthlyStatusDtos.add(dto);
//            }
//            mADto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
//            mADto.setEmployeeCode(employee.getEmployeeCode());
//            mADto.setMonthlyStatus(monthlyStatusDtos);
//            mADto.setEmployeeId(employee.getId());
//            if (employee.getDepartment() != null) {
//                mADto.setDepartmentId(employee.getDepartment().getId());
//                mADto.setDepartmentName(employee.getDepartment().getName());
//                mADto.setDesignationId(employee.getDesignation().getId());
//                mADto.setDesignationName(employee.getDesignation().getName());
//            }
//            if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
//                monthlyEmployeeAttendanceDtos.add(mADto);
//            }
//        }
//        return monthlyEmployeeAttendanceDtos;
//    }


    //TODO uncomment imp
//    private List<MonthlyEmployeeAttendanceDto> viewMonthlyEmployeeAttendance(DateDto dateDto) throws ParseException {
//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
//        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.fromStartDate, dateDto.endDate);
//
//        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = new ArrayList<MonthlyEmployeeAttendanceDto>();
//        List<Employee> employees = employeeService.findAll();
//        for (Employee employee : employees) {
//            // TODO
//            attendanceCompute(employee.getId(), dateDto.startDate, dateDto.getEndDate());
////          updateAttendanceLeaveWise(employee.getId(),dateDto.startDate,dateDto.getEndDate());
//            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
//            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
//            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
//            List<EmployeeAttendance> presentSize = new ArrayList<>();
//            List<EmployeeAttendance> holidaySize = new ArrayList<>();
//            List<EmployeeAttendance> sundaySize = new ArrayList<>();
//            List<EmployeeAttendance> halfDaySize = new ArrayList<>();
//            List<EmployeeAttendance> leaveSize = new ArrayList<>();
//            for (EmployeeAttendance employeeAttendance : monthlyEmployeeAttendance) {
//                MonthlyStatusDto dto = new MonthlyStatusDto();
//                dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
//                dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
//                dto.setMarkedOn(employeeAttendance.getMarkedOn());
//                dto.setInTime(employeeAttendance.getInTime());
//                dto.setOutTime(employeeAttendance.getOutTime());
//                dto.setLateEntry(employeeAttendance.getLateEntry());
//                dto.setWorkedHours(employeeAttendance.getWorkedHours());
//                dto.setExtraHrs(employeeAttendance.getOverTime());
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.PRESENT) {
//                    presentSize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.SUNDAY && employeeAttendance.getAttendanceStatus() == AttendanceStatus.COMP_OFF) {
//                    sundaySize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.HOLIDAY) {
//                    holidaySize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.LEAVE || employeeAttendance.getAttendanceStatus() == AttendanceStatus.EL || employeeAttendance.getAttendanceStatus() == AttendanceStatus.CL || employeeAttendance.getAttendanceStatus() == AttendanceStatus.ML) {
//                    leaveSize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.PH) {
//                    holidaySize.add(employeeAttendance);
//                }
//                if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.HALF_DAY) {
//                    halfDaySize.add(employeeAttendance);
//                }
//                double totalPresent = presentSize.size() + (halfDaySize.size() * 0.5);
//                dto.setNoOfPresentDays(totalPresent);
//                dto.setNoOfSudays(sundaySize.size());
//                dto.setNoOfHolidays(holidaySize.size());
//                dto.setNoOfLeaves(leaveSize.size() + (halfDaySize.size() * 0.5));
//                monthlyStatusDtos.add(dto);
//            }
//            mADto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
//            mADto.setEmployeeCode(employee.getEmployeeCode());
//            mADto.setMonthlyStatus(monthlyStatusDtos);
//            mADto.setEmployeeId(employee.getId());
////            if (employee.getDepartment().getId() == null) {
////                throw new EntityNotFoundException("Department not assigned to " + employee.getFirstName());
////            }
//            if (employee.getDepartment() != null) {
//                mADto.setDepartmentId(employee.getDepartment().getId());
//                mADto.setDepartmentName(employee.getDepartment().getName());
//                mADto.setDesignationId(employee.getDesignation().getId());
//                mADto.setDesignationName(employee.getDesignation().getName());
//            }
//            if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
//                monthlyEmployeeAttendanceDtos.add(mADto);
//            }
//        }
//        return monthlyEmployeeAttendanceDtos;
//    }

    private static DecimalFormat df = new DecimalFormat("0.00");

    public List<MonthlyEmployeeAttendanceDto> viewMonthlyEmployeeAttendance(DateDto dateDto) throws Exception {
//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);

        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = new ArrayList<MonthlyEmployeeAttendanceDto>();
        EmployeeType type = null;
        if (dateDto.getEmployeeType().equalsIgnoreCase(EmployeeType.PERMANENT.name())) {
            type = EmployeeType.PERMANENT;
        } else if (dateDto.getEmployeeType().equalsIgnoreCase(EmployeeType.CONTRACT.name())) {
            type = EmployeeType.CONTRACT;
        } else if (dateDto.getEmployeeType().equalsIgnoreCase(EmployeeType.PERMANENT_CONTRACT.name())) {
            type = EmployeeType.PERMANENT_CONTRACT;
        } else if (dateDto.getEmployeeType().equalsIgnoreCase(EmployeeType.PERMANENT_WORKER.name())) {
            type = EmployeeType.PERMANENT_WORKER;
        } else if (dateDto.getEmployeeType().equalsIgnoreCase(EmployeeType.PERMANENT_ALL.name())) {
            type = EmployeeType.PERMANENT_ALL;
        } else if (dateDto.getEmployeeType().equalsIgnoreCase(EmployeeType.CONTRACT_ALL.name())) {
            type = EmployeeType.CONTRACT_ALL;
        }
        List<Employee> employees = employeeService.getEmployeeByType(type, true);
        if (type.equals(EmployeeType.PERMANENT_ALL)) {
            List<Employee> employeesPs = employeeService.getEmployeeByType(EmployeeType.PERMANENT, true);
            List<Employee> employeesPw = employeeService.getEmployeeByType(EmployeeType.PERMANENT_WORKER, true);
            employees.addAll(employeesPs);
            employees.addAll(employeesPw);
        }
        if (type.equals(EmployeeType.CONTRACT_ALL)) {
            List<Employee> employeesPs = employeeService.getEmployeeByType(EmployeeType.CONTRACT, true);
            List<Employee> employeesPw = employeeService.getEmployeeByType(EmployeeType.PERMANENT_CONTRACT, true);
            employees.addAll(employeesPs);
            employees.addAll(employeesPw);
        }
        if (employees.isEmpty()) {
            throw new EntityNotFoundException("No report for selected employee type");
        }
        for (Employee employee : employees) {
            // TODO
            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
            List<EmployeeAttendance> presentSize = new ArrayList<>();
            List<EmployeeAttendance> holidaySize = new ArrayList<>();
            List<EmployeeAttendance> sundaySize = new ArrayList<>();
            List<EmployeeAttendance> halfDaySize = new ArrayList<>();
            List<EmployeeAttendance> leaveSize = new ArrayList<>();
            List<EmployeeAttendance> compOffs = new ArrayList<>();
            List<EmployeeAttendance> absents = new ArrayList<>();
            double totalWorkedHrsOfEmployee = 0;
            double totalOTHrsOfEmployee = 0;
            double lateEntryCount = 0;
            double totalearlyOutHrs = 0;
            double exactWorkedHrs = 0;
            for (Date date : dates) {
                System.out.println("date " + date + " empId " + employee.getId());
                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, employee.getId());
                MonthlyStatusDto dto = new MonthlyStatusDto();
                if (employeeAttendance != null) {
                    if (employeeAttendance.getLeaveHalfType() != null) {
                        dto.setLeaveHalfType(employeeAttendance.getLeaveHalfType());
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                        if (!employeeAttendance.isDataProcessed()) {
                            updateWorkedHours(employeeAttendance);
                        }
                        presentSize.add(employeeAttendance);
                    }

                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.LEAVE) ||
                            employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.EL) ||
                            employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.CL) ||
                            employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.ML)) {
                        leaveSize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PH)) {
                        holidaySize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                        employeeAttendance.setWorkedHours("4");
                        employeeAttendanceService.createEmployeeAttendance(employeeAttendance,true);
                        halfDaySize.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
                        compOffs.add(employeeAttendance);
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                        if (isSunday(date)) {
                            employeeAttendance.setAttendanceStatus(AttendanceStatus.SUNDAY);
                            employeeAttendanceService.createEmployeeAttendance(employeeAttendance,true);
//                            sundaySize.add(employeeAttendance);
                        } else {
                            absents.add(employeeAttendance);
                        }
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.SUNDAY) ||
                            employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.WO)) {
                        if (isAbsentForWo(employeeAttendance.getMarkedOn(), employeeAttendance.getEmployee().getId())) {
                            employeeAttendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                            employeeAttendance = employeeAttendanceService.createEmployeeAttendance(employeeAttendance,true);
                            absents.add(employeeAttendance);
                        } else {
                            sundaySize.add(employeeAttendance);
                        }
                    }
                    if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                        if (isAbsentForWo(employeeAttendance.getMarkedOn(), employeeAttendance.getEmployee().getId())) {
                            employeeAttendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                            employeeAttendance = employeeAttendanceService.createEmployeeAttendance(employeeAttendance,true);
                            absents.add(employeeAttendance);
                        } else {
                            holidaySize.add(employeeAttendance);
                        }
                    }
                    double totalPresent = presentSize.size() + (halfDaySize.size() * 0.5);
                    dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
                    dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
                    dto.setMarkedOn(employeeAttendance.getMarkedOn());
                    dto.setInTime(employeeAttendance.getInTime());
                    dto.setOutTime(employeeAttendance.getOutTime());
                    dto.setLateEntry(employeeAttendance.getLateEntry());
                    dto.setWorkedHours(employeeAttendance.getWorkedHours());
                    dto.setWorkedHours(employeeAttendance.getWhF());
                    if (employee.isOtRequired()) {
                        dto.setExtraHrs(employeeAttendance.getOtF());
                    } else {
                        dto.setExtraHrs("0");
                    }
                    dto.setNoOfPresentDays(totalPresent);
                    dto.setNoOfSudays(sundaySize.size());
                    dto.setNoOfHolidays(holidaySize.size());
                    dto.setNoOfLeaves(leaveSize.size() + (halfDaySize.size() * 0.5));
                    dto.setNoOfHalfDays(halfDaySize.size());
                    dto.setNoOfCompOffs(compOffs.size());
                    dto.setLateEntry(employeeAttendance.getLateEntry());
                    dto.setNoOfAbsent(absents.size());
                    dto.setEarlyOut(employeeAttendance.getEarlyOut());
                    if (employeeAttendance.getWorkedHours() != null) {
                        totalWorkedHrsOfEmployee = totalWorkedHrsOfEmployee + Double.valueOf(employeeAttendance.getWorkedHours());
                        totalWorkedHrsOfEmployee = Double.parseDouble(df.format(totalWorkedHrsOfEmployee));
                    }
                    if (employeeAttendance.getEffectiveOverTime() != null) {
                        totalOTHrsOfEmployee = totalOTHrsOfEmployee + Double.valueOf(employeeAttendance.getEffectiveOverTime());
                    }
                    if (employeeAttendance.getLateEntryNotFormated() != null) {
                        lateEntryCount = lateEntryCount + Double.valueOf(employeeAttendance.getLateEntryNotFormated());
                        lateEntryCount = Double.parseDouble(df.format(lateEntryCount));
                    }

                    if (employeeAttendance.getEarlyOutNotFormated() != null) {
                        totalearlyOutHrs = totalearlyOutHrs + Double.valueOf(employeeAttendance.getEarlyOutNotFormated());
                        totalearlyOutHrs = Double.parseDouble(df.format(totalearlyOutHrs));
                    }
                    exactWorkedHrs = totalWorkedHrsOfEmployee - (totalearlyOutHrs + lateEntryCount);

                    String convertedTotalWorkedHrs = convertOt(String.valueOf(totalWorkedHrsOfEmployee));
                    String convertedLateEntryHrs = convertOt(String.valueOf(lateEntryCount));
                    String convertedEarlyOutHrs = convertOt(String.valueOf(totalearlyOutHrs));
                    String convertedExactTotalHrs = convertOt(String.valueOf(exactWorkedHrs));

                    dto.setTotalHours(Double.parseDouble(convertedTotalWorkedHrs));
                    dto.setTotalExactWorkedHrs(Double.parseDouble(convertedExactTotalHrs));
                    totalOTHrsOfEmployee = Double.valueOf(String.format("%.2f", totalOTHrsOfEmployee));
                    if (employee.isOtRequired()) {
                        String convertedTotalOverTimeHrs = convertOt(String.valueOf(totalOTHrsOfEmployee));
                        dto.setTotalOtHours(Double.parseDouble(convertedTotalOverTimeHrs));
                    } else {
                        dto.setTotalOtHours(0);
                    }
                    dto.setTotalLateEntry(Double.parseDouble(convertedLateEntryHrs));
                    dto.setTotalEarlyCheckOut(Double.parseDouble(convertedEarlyOutHrs));
                    dto.setWeekOffPresent(employeeAttendance.isWeekOffPresent());
                } else {
                    if (employee.getFirstWeekOff() == 0 && employee.getSecondWeekOff() == 0) {
                        if (isSunday(date)) {
                            EmployeeAttendance employeeAttendance1 = new EmployeeAttendance();
                            employeeAttendance1.setAttendanceStatus(AttendanceStatus.SUNDAY);
                            employeeAttendance1.setEmployee(employee);
                            employeeAttendance1.setMarkedOn(date);
                            employeeAttendance1.setRecordedTime(date);
                            if (isAbsentForWo(employeeAttendance1.getMarkedOn(), employeeAttendance1.getEmployee().getId())) {
                                employeeAttendance1.setAttendanceStatus(AttendanceStatus.ABSENT);
                                dto.setAttendanceStatus(AttendanceStatus.ABSENT);
                                dto.setDay(DateUtil.getDay(date));
                                dto.setMarkedOn(date);
                                dto.setInTime(null);
                                dto.setOutTime(null);
                                dto.setLateEntry("");
                                dto.setWorkedHours("0");
                                dto.setExtraHrs("0");
                                dto.setEarlyOut("");
                                absents.add(employeeAttendance);
                                employeeAttendanceService.createEmployeeAttendance(employeeAttendance1,true);
                            } else {
                                employeeAttendanceService.createEmployeeAttendance(employeeAttendance1,true);
                                sundaySize.add(employeeAttendance1);
                                dto.setAttendanceStatus(AttendanceStatus.SUNDAY);
                                dto.setDay(DateUtil.getDay(date));
                                dto.setMarkedOn(date);
                                dto.setInTime(null);
                                dto.setOutTime(null);
                                dto.setLateEntry("");
                                dto.setWorkedHours("0");
                                dto.setExtraHrs("0");
                                dto.setEarlyOut("");
                            }
                        } else {
                            dto.setAttendanceStatus(AttendanceStatus.ABSENT);
                            dto.setDay(DateUtil.getDay(date));
                            dto.setMarkedOn(date);
                            absents.add(employeeAttendance);
                            dto.setNoOfAbsent(absents.size());
                            dto.setInTime(null);
                            dto.setOutTime(null);
                            dto.setLateEntry("");
                            dto.setWorkedHours("0");
                            dto.setExtraHrs("0");
                            dto.setEarlyOut("");
                        }
                    } else if (isWeekOff(date, employee.getFirstWeekOff(), employee.getSecondWeekOff())) {
                        EmployeeAttendance employeeAttendance1 = new EmployeeAttendance();
                        employeeAttendance1.setAttendanceStatus(AttendanceStatus.WO);
                        employeeAttendance1.setMarkedOn(date);
                        employeeAttendance1.setEmployee(employee);
                        if (isAbsentForWo(employeeAttendance1.getMarkedOn(), employeeAttendance1.getEmployee().getId())) {
                            employeeAttendance1.setAttendanceStatus(AttendanceStatus.ABSENT);
                            dto.setAttendanceStatus(AttendanceStatus.ABSENT);
                            dto.setDay(DateUtil.getDay(date));
                            dto.setMarkedOn(date);
                            dto.setInTime(null);
                            dto.setOutTime(null);
                            dto.setLateEntry("");
                            dto.setWorkedHours("0");
                            dto.setExtraHrs("0");
                            dto.setEarlyOut("");
                            absents.add(employeeAttendance);
                            employeeAttendanceService.createEmployeeAttendance(employeeAttendance1,true);
                        } else {
                            employeeAttendanceService.createEmployeeAttendance(employeeAttendance1,true);
                            sundaySize.add(employeeAttendance1);
                            dto.setAttendanceStatus(AttendanceStatus.SUNDAY);
                            dto.setDay(DateUtil.getDay(date));
                            dto.setMarkedOn(date);
                            dto.setInTime(null);
                            dto.setOutTime(null);
                            dto.setLateEntry("");
                            dto.setWorkedHours("0");
                            dto.setExtraHrs("0");
                            dto.setEarlyOut("");
                        }
                    } else {
                        dto.setAttendanceStatus(AttendanceStatus.ABSENT);
                        dto.setDay(DateUtil.getDay(date));
                        dto.setMarkedOn(date);
                        absents.add(employeeAttendance);
                        dto.setNoOfAbsent(absents.size());
                        dto.setInTime(null);
                        dto.setOutTime(null);
                        dto.setLateEntry("");
                        dto.setWorkedHours("0");
                        dto.setExtraHrs("0");
                        dto.setEarlyOut("");
                    }

                }
                dto.setNoOfSudays(sundaySize.size());
                monthlyStatusDtos.add(dto);
            }

            mADto.setEmployeeName(employee.getFirstName() /*+ " " + employee.getLastName()*/);
            mADto.setEmployeeCode(employee.getEmployeeCode());
            mADto.setMonthlyStatus(monthlyStatusDtos);
            mADto.setEmployeeId(employee.getId());

            mADto.setEmployeeType(String.valueOf(employee.getEmployeeType()));
            if (employee.getDepartment() != null) {
                mADto.setDepartmentId(employee.getDepartment().getId());
                mADto.setDepartmentName(employee.getDepartment().getName());
                if (employee.getDesignation() != null) {
                    mADto.setDesignationId(employee.getDesignation().getId());
                    mADto.setDesignationName(employee.getDesignation().getName());
                } else {
                    mADto.setDesignationId(0);
                    mADto.setDesignationName("");
                }
            } else {
                mADto.setDepartmentId(0);
                mADto.setDepartmentName("");
            }

            if (employee.getDateOfJoining() != null) {
                if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
                    monthlyEmployeeAttendanceDtos.add(mADto);
                }
            } else {
                monthlyEmployeeAttendanceDtos.add(mADto);
            }
        }
        return monthlyEmployeeAttendanceDtos;
    }

    public List<MonthlyEmployeeAttendanceDto> viewMonthlyEmployeeAttendanceEarlyCheckOut(DateDto dateDto) throws ParseException {
//        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
//            throw new EntityNotFoundException("Selected Date of Month Should be same");
//        }
        List<Date> dates = DateUtil.getDaysBetweenDates(dateDto.startDate, dateDto.endDate);

        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = new ArrayList<MonthlyEmployeeAttendanceDto>();
        EmployeeType type = null;
        if (dateDto.getEmployeeType().equalsIgnoreCase("PERMANENT")) {
            type = EmployeeType.PERMANENT;
        } else if (dateDto.getEmployeeType().equalsIgnoreCase("CONTRACT")) {
            type = EmployeeType.CONTRACT;
        }
        List<Employee> employees = employeeService.getEmployeeByType(type, true);
        for (Employee employee : employees) {
            // TODO
//          updateAttendanceLeaveWise(employee.getId(),dateDto.startDate,dateDto.getEndDate());
            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
            List<EmployeeAttendance> monthlyEmployeeAttendance = employeeAttendanceService.getEmployeeMontlyAttendanceByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
            List<EmployeeAttendance> presentSize = new ArrayList<>();
            List<EmployeeAttendance> holidaySize = new ArrayList<>();
            List<EmployeeAttendance> sundaySize = new ArrayList<>();
            List<EmployeeAttendance> halfDaySize = new ArrayList<>();
            List<EmployeeAttendance> leaveSize = new ArrayList<>();
            List<EmployeeAttendance> compOffs = new ArrayList<>();
            List<EmployeeAttendance> absents = new ArrayList<>();
            double totalWorkedHrsOfEmployee = 0;
            double totalOTHrsOfEmployee = 0;
            long lateEntryCount = 0;
            for (Date date : dates) {
                System.out.println("date " + date + " empId " + employee.getId());
                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, employee.getId());
                MonthlyStatusDto dto = new MonthlyStatusDto();
                if (employeeAttendance != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date date1 = sdf.parse("17:00");
                    Date date2 = employeeAttendance.getOutTime();
                    if (date2.getTime() < date1.getTime()) {
                        dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
                        dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
                        dto.setMarkedOn(employeeAttendance.getMarkedOn());
                        dto.setInTime(employeeAttendance.getInTime());
                        dto.setOutTime(employeeAttendance.getOutTime());
                        dto.setLateEntry(employeeAttendance.getLateEntry());
                        dto.setWorkedHours(employeeAttendance.getWorkedHours());
                        dto.setExtraHrs(employeeAttendance.getOverTime());
                        if (employeeAttendance.getLeaveHalfType() != null) {
                            dto.setLeaveHalfType(employeeAttendance.getLeaveHalfType());
                        }
                        if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                            presentSize.add(employeeAttendance);
                        }

                        dto.setTotalHours(totalWorkedHrsOfEmployee);
                        dto.setTotalOtHours(totalOTHrsOfEmployee);
                        dto.setTotalLateEntry(lateEntryCount);
                    } else {
                        dto.setAttendanceStatus(AttendanceStatus.ABSENT);
                        dto.setDay(DateUtil.getDay(date));
                        dto.setMarkedOn(date);
                        absents.add(employeeAttendance);
                        dto.setNoOfAbsent(absents.size());
                        dto.setInTime(null);
                        dto.setOutTime(null);
                        dto.setLateEntry("null");
                        dto.setWorkedHours("0");
                        dto.setExtraHrs("0");
                    }
                } else {
                    dto.setAttendanceStatus(AttendanceStatus.ABSENT);
                    dto.setDay(DateUtil.getDay(date));
                    dto.setMarkedOn(date);
                    absents.add(employeeAttendance);
                    dto.setNoOfAbsent(absents.size());
                    dto.setInTime(null);
                    dto.setOutTime(null);
                    dto.setLateEntry("null");
                    dto.setWorkedHours("0");
                    dto.setExtraHrs("0");
                }

                monthlyStatusDtos.add(dto);

            }
            mADto.setEmployeeName(employee.getFirstName() /*+ " " + employee.getLastName()*/);
            mADto.setEmployeeCode(employee.getEmployeeCode());
            mADto.setMonthlyStatus(monthlyStatusDtos);
            mADto.setEmployeeId(employee.getId());
            mADto.setEmployeeType(String.valueOf(employee.getEmployeeType()));
            if (employee.getDepartment() != null) {
                mADto.setDepartmentId(employee.getDepartment().getId());
                mADto.setDepartmentName(employee.getDepartment().getName());
                if (employee.getDesignation() != null) {
                    mADto.setDesignationId(employee.getDesignation().getId());
                    mADto.setDesignationName(employee.getDesignation().getName());
                } else {
                    mADto.setDesignationId(0);
                    mADto.setDesignationName("");
                }
            } else {
                mADto.setDepartmentId(0);
                mADto.setDepartmentName("");
            }

            if (employee.getDateOfJoining() != null) {
                if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
                    monthlyEmployeeAttendanceDtos.add(mADto);
                }
            } else {
                monthlyEmployeeAttendanceDtos.add(mADto);
            }
        }
        return monthlyEmployeeAttendanceDtos;
    }

    @PostMapping("employee-attendance/create-new")
    public ResponseEntity<EmployeeAttendance> createNewEmployeeAttendance(@RequestBody EmployeeAttendance employeeAttendance) throws Exception {
        if (employeeAttendance == null) {
            throw new EntityNotFoundException("EmployeeAttendance not found ");
        }
        Optional<Employee> employee = employeeService.getEmployee(employeeAttendance.getEmployee().getId());
//        Shift shift = shiftService.getShift(1);
        EmployeeShiftAssignment employeeShiftAssignment = employeeShiftAssignmentService.getCurrentShiftByEmployeeId(employee.get().getId(), employeeAttendance.getMarkedOn());
        Shift shift;
        if (employeeShiftAssignment == null) {
//            throw new EntityNotFoundException("Shift Not Assigned to Employee Id :" + employee.getId());
            shift = shiftService.getCurrentShiftWithGrace();
        } else {
            shift = employeeShiftAssignment.getShift();
        }
        Date intime = employeeAttendance.getInTime();
        Date outTime = employeeAttendance.getOutTime();

        Date startDate = employeeAttendance.getStartDate();
        Date endDate = employeeAttendance.getEndDate();

        intime.setYear(startDate.getYear());
        intime.setDate(startDate.getDate());
        intime.setMonth(startDate.getMonth());
        employeeAttendance.setInTime(intime);

        outTime.setYear(endDate.getYear());
        outTime.setDate(endDate.getDate());
        outTime.setMonth(endDate.getMonth());
        employeeAttendance.setOutTime(outTime);

//      employeeShiftAssignmentService.getCurrentShiftByEmployeeId(employeeAttendance.getShift().getId(),employeeAttendance.getMarkedOn());
        updateWorkedHours(employeeAttendance);
        employeeAttendance.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
        //-------------------------------------
        Employee employee1 = new Employee();
        employee1.setId(employee.get().getId());
        employee1.setFirstName(employee.get().getFirstName());
        employee1.setLastName(employee.get().getLastName());
        employee1.setEmployeeCode(employee.get().getEmployeeCode());
        employeeAttendance.setEmployee(employee1);
        //--------------------------------------
        if (shift != null) {
            employeeAttendance.setShift(shift);
        } else {
            employeeAttendance.setShift(null);
        }
        employeeAttendance.setRecordedTime(new Date());
        employeeAttendance = employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
        return new ResponseEntity<>(employeeAttendance, HttpStatus.OK);
    }


    // TODO uncomment important later
//    public void attendanceCompute(long empId, Date startDate, Date endDate) throws ParseException {
//        /*startDate= DateUtil.getYesterdayDate();
//        endDate=DateUtil.getYesterdayDate();*/
//        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(empId, AttendanceStatus.PRESENT, startDate, endDate);
//        ArrayList allPresent = new ArrayList();
//        List halfDayList = new ArrayList();
//        long count = 0;
//        for (EmployeeAttendance attendance : employeeAttendances) {
//            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
//            String shiftStartTime = timeFormat.format(attendance.getShift().getStartTime());
//            long shiftStart = timeFormat.parse(shiftStartTime).getTime();
//            String shiftEndTime = timeFormat.format(attendance.getShift().getEndTime());
//            long shiftEnd = timeFormat.parse(shiftEndTime).getTime();
//            if (attendance.getInTime() == null) {
//                throw new NullPointerException("No InTime for " + attendance.getEmployee().getFirstName() + " On " + attendance.getMarkedOn());
//            }
//            String inTime = timeFormat.format(attendance.getInTime());
//            if (attendance.getOutTime() == null) {
//                attendance.setOutTime(attendance.getShift().getEndTime());
//                employeeAttendanceService.createEmployeeAttendance(attendance);
////                throw new NullPointerException("No Out-Time for " + attendance.getEmployee().getFirstName() + " On " + attendance.getMarkedOn());
//            }
//            String outTime = timeFormat.format(attendance.getOutTime());
//            long attendanceInTime = timeFormat.parse(inTime).getTime();
//            long attendanceOutTime = timeFormat.parse(outTime).getTime();
//            //Time attTime = new Time(attendanceTime);
//            if (attendanceInTime < shiftStart && attendanceOutTime > shiftEnd) {
//                System.out.println("********pakka present********" + attendance.getEmployee().getFirstName() + "date" + attendance.getMarkedOn());
//                allPresent.add(attendance);
//            } else if (attendanceInTime > shiftStart) {
//                System.out.println("After Shift In time: " + attendance.getInTime() + "******Name*****: " + attendance.getEmployee().getFirstName());
//                if (attendanceInTime > (shiftStart + 600000)) {
//                    count++;
//                }
//                System.out.println("Count: " + count);
//                long lateEntryTwoHours = shiftStart + 7200000;
//                long earlyExitTwoHours = shiftEnd - 7200000;
//                if (count > 2) {
//                    if (attendanceInTime > lateEntryTwoHours || attendanceOutTime < earlyExitTwoHours) {
//                        System.out.println("***Half-Day late entry" + attendance.getEmployee().getFirstName() + "date" + attendance.getMarkedOn());
//                        attendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
//                        EmployeeAttendance employeeAttendance = employeeAttendanceService.createEmployeeAttendance(attendance);
//                        halfDayList.add(employeeAttendance);
//                    }
//                } else if (attendanceOutTime < (shiftEnd - 7200000)) {
//                    System.out.println("*************************Late Entry but HalfDay******************");
//                    attendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
//                    EmployeeAttendance employeeAttendance = employeeAttendanceService.createEmployeeAttendance(attendance);
//                    halfDayList.add(employeeAttendance);
//                }
//            } else if (attendanceOutTime < shiftEnd) {
//                System.out.println("Before Shift Out time: " + attendance.getOutTime());
//                if (attendanceInTime > (shiftStart + 7200000) || attendanceOutTime < (shiftEnd - 7200000)) {
////                    System.out.println("***Half-Day early exit" + attendance.getEmployee().getFirstName() + "date" + attendance.getMarkedOn());
//                    /*attendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
//                    EmployeeAttendance employeeAttendance=employeeAttendanceService.createEmployeeAttendance(attendance);
//                    halfDayList.add(employeeAttendance);*/
//                } else {
//                    System.out.println("*************Early exit but present*********");
//                    allPresent.add(attendance);
//                }
//            }
//        }
//    }


    //TODO comment if not worked
    public void attendanceCompute(long empId, Date startDate, Date endDate) throws ParseException {
        /*startDate= DateUtil.getYesterdayDate();
        endDate=DateUtil.getYesterdayDate();*/
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(empId, AttendanceStatus.PRESENT, startDate, endDate);
        ArrayList allPresent = new ArrayList();
        List halfDayList = new ArrayList();
        long count = 0;
        for (EmployeeAttendance attendance : employeeAttendances) {
            DecimalFormat df = new DecimalFormat("0.00");
            System.out.println("at:" + attendance.getWorkedHours());
            if (attendance.getWorkedHours() == null) {
                attendance.setWorkedHours("0");
            }
            String wh = df.format(Double.valueOf(attendance.getWorkedHours()));
            attendance.setWorkedHours(wh);
            if (attendance.getShift() != null) {
                DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String shiftStartTime = timeFormat.format(attendance.getShift().getStartTime());
                long shiftStart = timeFormat.parse(shiftStartTime).getTime();
                String shiftEndTime = timeFormat.format(attendance.getShift().getEndTime());
                long shiftEnd = timeFormat.parse(shiftEndTime).getTime();
//                if (attendance.getInTime() == null) {
//                    throw new NullPointerException("No InTime for " + attendance.getEmployee().getFirstName() + " On " + attendance.getMarkedOn());
//                }
                if (attendance.getInTime() != null) {
                    String inTime = timeFormat.format(attendance.getInTime());
                    if (attendance.getOutTime() == null) {
                        attendance.setOutTime(attendance.getShift().getEndTime());
                        employeeAttendanceService.createEmployeeAttendance(attendance,false);
//                throw new NullPointerException("No Out-Time for " + attendance.getEmployee().getFirstName() + " On " + attendance.getMarkedOn());
                    }
                    String outTime = timeFormat.format(attendance.getOutTime());
                    long attendanceInTime = timeFormat.parse(inTime).getTime();
                    long attendanceOutTime = timeFormat.parse(outTime).getTime();
                    //Time attTime = new Time(attendanceTime);
                    long graceIn = attendance.getShift().getGraceInTime();
                    long graceOut = attendance.getShift().getGraceOutTme();
                    if (attendanceInTime < shiftStart && attendanceOutTime > shiftEnd) {
                        System.out.println("********pakka present********" + attendance.getEmployee().getFirstName() + "date" + attendance.getMarkedOn());
                        allPresent.add(attendance);
                    } else if (attendanceInTime > shiftStart) {
                        System.out.println("After Shift In time: " + attendance.getInTime() + "******Name*****: " + attendance.getEmployee().getFirstName());
                        if (attendanceInTime > (shiftStart + graceIn)) {
                            count++;
                        }
                        System.out.println("Count: " + count);
                        long lateEntryTwoHours = shiftStart + graceIn;
                        long earlyExitTwoHours = shiftEnd - graceOut;
                        if (count > 2) {
                            if (attendanceInTime > lateEntryTwoHours || attendanceOutTime < earlyExitTwoHours) {
                                System.out.println("***Half-Day late entry" + attendance.getEmployee().getFirstName() + "date" + attendance.getMarkedOn());
                                attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                                EmployeeAttendance employeeAttendance = employeeAttendanceService.createEmployeeAttendance(attendance,false);
                                halfDayList.add(employeeAttendance);
                            }
                        } else if (attendanceOutTime < (shiftEnd - graceOut)) {
                            System.out.println("*************************Late Entry but HalfDay******************");
                            attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
                            EmployeeAttendance employeeAttendance = employeeAttendanceService.createEmployeeAttendance(attendance,false);
                            halfDayList.add(employeeAttendance);
                        }
                    } else if (attendanceOutTime < shiftEnd) {
                        System.out.println("Before Shift Out time: " + attendance.getOutTime());
                        if (attendanceInTime > (shiftStart + graceIn) || attendanceOutTime < (shiftEnd - graceOut)) {
//                    System.out.println("***Half-Day early exit" + attendance.getEmployee().getFirstName() + "date" + attendance.getMarkedOn());
                    /*attendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
                    EmployeeAttendance employeeAttendance=employeeAttendanceService.createEmployeeAttendance(attendance);
                    halfDayList.add(employeeAttendance);*/
                        } else {
                            System.out.println("*************Early exit but present*********");
                            allPresent.add(attendance);
                        }
                    }

                }
            }
        }
    }

    public void updateAttendanceLeaveWise(long empid, Date startDate, Date endDate) {
        List<Date> dates = getDaysBetweenDates(startDate, endDate);
        List<Employee> employeeList = employeeService.getEmployeeLightWeight();
        for (Date date : dates) {
            EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, empid);
            Leave leave = leaveService.getApprovedLeavesForAttendanceMark(LeaveStatus.APPROVED, Math.toIntExact(empid), date);
            if (leave == null) {
                System.out.println("********** no leave");
            } else if (employeeAttendance.getAttendanceStatus() == AttendanceStatus.LEAVE) {
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

    @GetMapping("employee-attendance/years")
    public ResponseEntity<List<Long>> last10Years() {
        long year = Calendar.getInstance().get(Calendar.YEAR) + 1;
        List<Long> years = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            long y = year--;
            years.add(year);
        }
        Collections.reverse(years);
        return new ResponseEntity<>(years, HttpStatus.OK);
    }

    @PostMapping("employee-attendance/punch-updation")
    void updateAttendanceData(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) {
        Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(employeeAttendanceDto.getEmpCode());
        if (!employee.isPresent()) {
            throw new EntityNotFoundException("Employee not found Enter Valid Employee Code:" + employeeAttendanceDto.getEmpCode());
        }
        EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(employeeAttendanceDto.getMarkedOn(), employee.get().getId());
        if (employeeAttendance != null) {
            employeeAttendance.setMarkedOn(employeeAttendanceDto.getMarkedOn());
            employeeAttendance.setAttendanceStatus(employeeAttendanceDto.getAttendanceStatus());
            employeeAttendance.setInTime(employeeAttendanceDto.getIn());
            employeeAttendance.setOutTime(employeeAttendanceDto.getOut());
            Shift shift = shiftService.getShift(employeeAttendanceDto.getShiftId());
            employeeAttendance.setShift(shift);
            Date d = new Date();
            //-----------------------------------------------
            Employee employee1 = new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            employeeAttendance.setEmployee(employee1);
            //-----------------------------------------------
            employeeAttendance.setRecordedTime(d);
            employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
        } else {
            EmployeeAttendance attendance = new EmployeeAttendance();
            attendance.setMarkedOn(employeeAttendanceDto.getMarkedOn());
            attendance.setInTime(employeeAttendanceDto.getIn());
            attendance.setOutTime(employeeAttendanceDto.getOut());
            attendance.setAttendanceStatus(employeeAttendanceDto.getAttendanceStatus());
            Shift shift = shiftService.getShift(employeeAttendanceDto.getShiftId());
            attendance.setShift(shift);
            attendance.setRecordedTime(new Date());
            //-----------------------------------------------
            Employee employee1 = new Employee();
            employee1.setId(employee.get().getId());
            employee1.setFirstName(employee.get().getFirstName());
            employee1.setLastName(employee.get().getLastName());
            employee1.setEmployeeCode(employee.get().getEmployeeCode());
            attendance.setEmployee(employee1);
            //-----------------------------------------------
            employeeAttendanceService.createEmployeeAttendance(attendance,false);
        }
    }


//    @Scheduled(initialDelay = 1000, fixedRate = 360000)
    public void updateSundayAndAbsentDaily() throws Exception {
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            updateSunday(DateUtil.getDayBeforeYesterdayDate(), DateUtil.getYesterdayDate(), employee);
        }

    }

    public void updateSunday(Date startDate, Date endDate, Employee employee) throws Exception {

        if (endDate.after(new Date())) {
            endDate = DateUtil.getYesterdayDate();
        }
//        System.out.println("Start " + startDate + " end " + endDate);
        List<Date> dates = DateUtil.getDaysBetweenDates(startDate, endDate);

        List<Date> dateList = new ArrayList<>();
        for (Date date : dates) {
            Date date1 = date;
            Calendar c1 = Calendar.getInstance();
            c1.setTime(date1);
            boolean sunday = false;
            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                sunday = true;
                dateList.add(date);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String d = df.format(date);
                Date markedOn = df.parse(d);
                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(markedOn, employee.getId());
                if (employeeAttendance == null && employee.getDateOfJoining().before(markedOn)) {
                    EmployeeAttendance attendance = new EmployeeAttendance();
                    attendance.setMarkedOn(markedOn);
                    attendance.setAttendanceStatus(AttendanceStatus.SUNDAY);
                    Shift shift = shiftService.getShift(1);
                    attendance.setShift(shift);
                    attendance.setRecordedTime(new Date());
                    //-----------------------------------------------
                    Employee employee1 = new Employee();
                    employee1.setId(employee.getId());
                    employee1.setFirstName(employee.getFirstName());
                    employee1.setLastName(employee.getLastName());
                    employee1.setEmployeeCode(employee.getEmployeeCode());
                    attendance.setEmployee(employee1);
                    employeeAttendanceService.createEmployeeAttendance(attendance,false);
                    //-----------------------------------------------
                } else {
//                    System.out.println("sunday marked already");

                    if (employeeAttendance != null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                        employeeAttendance.setAttendanceStatus(AttendanceStatus.COMP_OFF);
                        updateWorkedHours(employeeAttendance);
                        employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
                    }

                }
//                System.out.println("Sunday" + d + " " + employee.getFirstName());
            } else {
                Date doj = employee.getDateOfJoining();
                SimpleDateFormat dfj = new SimpleDateFormat("yyyy-MM-dd");
                String dojs = dfj.format(doj);
                Date dateOfJoining = dfj.parse(dojs);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String d = df.format(date);
                Date markedOn = df.parse(d);
                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(markedOn, employee.getId());
                if (employeeAttendance == null && dateOfJoining.before(markedOn)) {
                    System.out.println("Date of joining " + dateOfJoining + "  Date " + markedOn);
                    EmployeeAttendance attendance = new EmployeeAttendance();
                    attendance.setMarkedOn(markedOn);
                    attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
                    Shift shift = shiftService.getShift(1);
                    attendance.setShift(shift);
                    attendance.setRecordedTime(new Date());
                    //-----------------------------------------------
                    Employee employee1 = new Employee();
                    employee1.setId(employee.getId());
                    employee1.setFirstName(employee.getFirstName());
                    employee1.setLastName(employee.getLastName());
                    employee1.setEmployeeCode(employee.getEmployeeCode());
                    attendance.setEmployee(employee1);
                    employeeAttendanceService.createEmployeeAttendance(attendance,false);
                    //-----------------------------------------------
                } else {
//                    System.out.println("Entry marked already");
                }
            }
        }
    }


//    @GetMapping("employee-attendance/update-sunday")
//    public void updateSunday(Date startDate, Date endDate, Employee employee) throws Exception {
//
//        if (endDate.after(new Date())) {
//            endDate = DateUtil.getYesterdayDate();
//        }
////        System.out.println("Start " + startDate + " end " + endDate);
//        List<Date> dates = DateUtil.getDaysBetweenDates(startDate, endDate);
//
//        List<Date> dateList = new ArrayList<>();
//        for (Date date : dates) {
//            Date date1 = date;
//            Calendar c1 = Calendar.getInstance();
//            c1.setTime(date1);
//            boolean sunday = false;
//            if (c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//                sunday = true;
//                dateList.add(date);
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                String d = df.format(date);
//                Date markedOn = df.parse(d);
//                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(markedOn, employee.getId());
//                if (employeeAttendance == null && employee.getDateOfJoining().before(markedOn)) {
//                    EmployeeAttendance attendance = new EmployeeAttendance();
//                    attendance.setMarkedOn(markedOn);
//                    attendance.setEmployee(employee);
//                    attendance.setAttendanceStatus(AttendanceStatus.SUNDAY);
//                    Shift shift = shiftService.getShift(1);
//                    attendance.setShift(shift);
//                    attendance.setRecordedTime(new Date());
//                    //-----------------------------------------------
//                    Employee employee1 = new Employee();
//                    employee1.setId(employee.getId());
//                    employee1.setFirstName(employee.getFirstName());
//                    employee1.setLastName(employee.getLastName());
//                    employee1.setEmployeeCode(employee.getEmployeeCode());
//                    attendance.setEmployee(employee1);
//                    employeeAttendanceService.createEmployeeAttendance(attendance);
//                    //-----------------------------------------------
//                } else {
////                    System.out.println("sunday marked already");
//                }
////                System.out.println("Sunday" + d + " " + employee.getFirstName());
//            } else {
//                Date doj = employee.getDateOfJoining();
//                SimpleDateFormat dfj = new SimpleDateFormat("yyyy-MM-dd");
//                String dojs = dfj.format(doj);
//                Date dateOfJoining = dfj.parse(dojs);
//
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                String d = df.format(date);
//                Date markedOn = df.parse(d);
//                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(markedOn, employee.getId());
//                if (employeeAttendance == null && dateOfJoining.before(markedOn)) {
//                    System.out.println("Date of joining " + dateOfJoining + "  Date " + markedOn);
//                    EmployeeAttendance attendance = new EmployeeAttendance();
//                    attendance.setMarkedOn(markedOn);
//                    attendance.setEmployee(employee);
//                    attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
//                    Shift shift = shiftService.getShift(1);
//                    attendance.setShift(shift);
//                    attendance.setRecordedTime(new Date());
//                    //-----------------------------------------------
//                    Employee employee1 = new Employee();
//                    employee1.setId(employee.getId());
//                    employee1.setFirstName(employee.getFirstName());
//                    employee1.setLastName(employee.getLastName());
//                    employee1.setEmployeeCode(employee.getEmployeeCode());
//                    attendance.setEmployee(employee1);
//                    employeeAttendanceService.createEmployeeAttendance(attendance);
//                    //-----------------------------------------------
//                } else {
////                    System.out.println("Entry marked already");
//                }
//            }
//        }
//    }


    @GetMapping("employee-attendance/update-sunday/{month}")
    public void updateSun(@PathVariable int month) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(df.format(cal.getTime()));
        for (int i = 1; i < maxDay; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i + 1);
            String ds = df.format(cal.getTime());
            Date markedOn = df.parse(ds);
            System.out.println(df.format(cal.getTime()));
        }
    }


    //ARUN FORM-22
    @PostMapping("form-22")
    public ResponseEntity<List<EmployeeAttendance>> form22(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(dateDto.getReportType().equalsIgnoreCase("monthly")){
            dateDto.setStartDate(DateUtil.startDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
            dateDto.setEndDate(DateUtil.endDateOfMonthAndYear(dateDto.getYear(),dateDto.getMonth()));
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);

        OutputStream out = null;
        String fileName = "Monthly_Late_Entry_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                form22(workbook, monthlyEmployeeAttendanceDtos, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("entries");
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity(monthlyEmployeeAttendanceDtos, HttpStatus.OK);
    }


    private WritableWorkbook form22(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("FORM_22", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 8);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 14);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 14);
        headerFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatRight = new WritableCellFormat(headerFontRight);
        headerFormatRight.setAlignment(RIGHT);

        WritableFont cellFont = new WritableFont(WritableFont.TIMES, 7);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(Colour.GRAY_25);

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        cf.setBoldStyle(WritableFont.NO_BOLD);
        WritableCellFormat c = new WritableCellFormat(cf);
        c.setAlignment(CENTRE);
        c.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(Colour.ICE_BLUE);

        WritableCellFormat cellFormatDate = new WritableCellFormat(cellFont);
        cellFormatDate.setAlignment(CENTRE);
        cellFormatDate.setBackground(Colour.ICE_BLUE);
        WritableFont cellFontRight = new WritableFont(WritableFont.TIMES);
        cellFontRight.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatRight = new WritableCellFormat(cellFontRight);
        cellFormatRight.setAlignment(RIGHT);
        WritableFont cellFontLeft = new WritableFont(WritableFont.TIMES);
        cellFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormatLeft = new WritableCellFormat(cellFontLeft);
        cellFormatLeft.setAlignment(LEFT);
        WritableFont cellFontSimpleRight = new WritableFont(WritableFont.TIMES);
        WritableCellFormat cellFormatSimpleRight = new WritableCellFormat(cellFontSimpleRight);
        cellFormatSimpleRight.setAlignment(RIGHT);
        s.setColumnView(0, 5);
        s.setColumnView(1, 6);
        s.setColumnView(2, 15);
        s.mergeCells(0, 0, 33, 0);
        s.mergeCells(0, 1, 33, 1);
        s.mergeCells(0, 2, 33, 2);
//        s.mergeCells(2, 18, 5, 18);
        List<CompanyName> companyName = companyNameService.getCompany();
        Collections.reverse(companyName);
        Label lable = new Label(0, 0, companyName.get(0).getCompanyName() + " PVT LTD", headerFormat);
        s.addCell(lable);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String fd = df.format(fromDate);
        String td = df.format(toDate);
        Label lableSlip = new Label(0, 1, "FORM-22 - From:" + fd + "   To:" + td, headerFormat);
        Label lableSlip1 = new Label(0, 2, "Prescribed under rule 137 of the Karnataka Factories Rules, 1969 under Karnakata M.W. Rules & P.W. Rules 1963", headerFormat);
        s.addCell(lableSlip);
        s.addCell(lableSlip1);

        int j = 3;
        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
        for (int i = 0; i < dates.size(); i++) {
            s.mergeCells(j, 2, j + 1, 2);
            DateFormat formatter = new SimpleDateFormat("dd");
//            s.addCell(new Label(j, 2, "" + formatter.format(dates.get(i)), cellFormatDate));
            s.addCell(new Label(j, 3, formatter.format(dates.get(i)), cellFormat));
//            s.addCell(new Label(j + 1, 3, "Out-Time", cellFormat));
//            s.addCell(new Label(j + 2, 3, "WH", cellFormat));
//            s.addCell(new Label(j + 3, 3, "OT", cellFormat));
            j = j + 1;
        }
        s.addCell(new Label(j + 1, 3, "PRESENT", cellFormat));
        s.addCell(new Label(j + 2, 3, "HOLIDAYS", cellFormat));
        s.addCell(new Label(j + 3, 3, "WO", cellFormat));
        s.addCell(new Label(j + 4, 3, "LEAVES", cellFormat));

        s.addCell(new Label(j + 5, 3, "ABSENT", cellFormat));
        s.addCell(new Label(j + 6, 3, "COMP_OFF", cellFormat));
        s.addCell(new Label(j + 7, 3, "TOTAL_HOURS", cellFormat));
        s.addCell(new Label(j + 8, 3, "TOTAL_OT_HOURS", cellFormat));
        s.addCell(new Label(j + 9, 3, "LATE_ENTRY", cellFormat));
        s.addCell(new Label(j + 10, 3, "EARLY_OUT", cellFormat));
        s.addCell(new Label(j + 11, 3, "CALENDAR_DAYS", cellFormat));

        int rowNum = 4;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3), cLeft));
            s.addCell(new Label(1, 3, "Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 3, "Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            int colNum = 3;
            double leaves;
            double present;
            long sundays;
            long holidays;
            long absents;
            long compoffs;
            double totalWorkedHours;
            double totalOtHours;
            double lateIn;
            double earlyOut;
            String inTime = null;
//            String outTime = null;
//            String workedHours = null;
//            String overTime = null;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
                s.setColumnView(colNum, 4);
                System.out.println("employeeAttendanceDto.getMonthlyStatus().size(): " + employeeAttendanceDto.getMonthlyStatus().size());
                if (/*employeeAttendanceDto.getMonthlyStatus() != null &&*/ employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays <= employeeAttendanceDto.getMonthlyStatus().size()) {
                    DateFormat timeFormat = new SimpleDateFormat("HH.mm");
                    try {
//                        employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null
                        if (employeeAttendanceDto.getMonthlyStatus().get(m).getInTime() != null && employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
//                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();
                            inTime = "P";
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).isWeekOffPresent()) {
                                inTime = "WW";
                            }

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                inTime = "LOP";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                                inTime = "H";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
                                inTime = "PH";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                                inTime = "WO";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) ||
                                    employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) ||
                                    employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) ||
                                    employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL) ||
                                    employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ESIC) ||
                                    employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LOP) ||
                                    employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.MATERNITY) ||
                                    employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.OOD)) {
                                inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                if (employeeAttendanceDto.getMonthlyStatus().get(m).getLeaveHalfType() == null) {
                                    inTime = "HALF-DAY";
                                }
                                if (employeeAttendanceDto.getMonthlyStatus().get(m).getLeaveHalfType().equalsIgnoreCase("FH")) {
                                    inTime = "1/2L+1/2P";
                                }
                                if (employeeAttendanceDto.getMonthlyStatus().get(m).getLeaveHalfType().equalsIgnoreCase("SH")) {
                                    inTime = "1/2P+1/2L";
                                }

                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
                                inTime = "CO";
                            }
//                            inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                        if (/*employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null &&*/ employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
//                            outTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime());
//                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
//                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
//                                outTime = "A";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
//                                outTime = "H";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
//                                outTime = "PH";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
//                                outTime = "WO";
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
//                                outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
//                                workedHours = "0";
//                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
//                                outTime = "HALF-DAY";

                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.COMP_OFF)) {
//                                outTime = "COMP-OFF";

                            }
//                            outTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Index out of bound");
                    }

//                    if (workedHours != null) {
//                        DecimalFormat df = new DecimalFormat("0.00");
//                        workedHours = df.format(Double.valueOf(workedHours));
//                        workedHours = String.valueOf(Double.valueOf(workedHours));
//                    }
                    s.addCell(new Label(colNum, rowNum, "" + inTime, c));
//                    s.addCell(new Label(colNum + 1, rowNum, "" + outTime));
//                    s.addCell(new Label(colNum + 2, rowNum, "" + workedHours));
//                    s.addCell(new Label(colNum + 3, rowNum, "" + overTime));
                    colNum = colNum + 1;
                } else {
                    s.addCell(new Label(colNum, rowNum, "" + "-", c));
//                    s.addCell(new Label(colNum + 1, rowNum, "" + "-"));
//                    s.addCell(new Label(colNum + 2, rowNum, "" + "-"));
//                    s.addCell(new Label(colNum + 3, rowNum, "" + "-"));
                    colNum = colNum + 1;
                }

            }

            for (int i = noOfDays + 1; i <= noOfDays + 8; i++) {
                if (employeeAttendanceDto.getMonthlyStatus() != null && employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays < employeeAttendanceDto.getMonthlyStatus().size()) {
                    present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfPresentDays();
//                  present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHalfDays();
                    sundays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfSudays();
                    holidays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHolidays();
                    leaves = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfLeaves();

                    absents = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfAbsent();
                    compoffs = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfCompOffs();
                    totalWorkedHours = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalHours();
                    totalOtHours = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalOtHours();

                    lateIn = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalLateEntry();
                    earlyOut = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getTotalEarlyCheckOut();

                    s.addCell(new Label(j + 1, rowNum, "" + present, c));
                    s.addCell(new Label(j + 2, rowNum, "" + holidays, c));
                    s.addCell(new Label(j + 3, rowNum, "" + sundays, c));
                    s.addCell(new Label(j + 4, rowNum, "" + leaves, c));

                    s.addCell(new Label(j + 5, rowNum, "" + absents, c));
                    s.addCell(new Label(j + 6, rowNum, "" + compoffs, c));
                    s.addCell(new Label(j + 7, rowNum, "" + totalWorkedHours, c));
                    s.addCell(new Label(j + 8, rowNum, "" + totalOtHours, c));
                    s.addCell(new Label(j + 9, rowNum, "" + lateIn, c));
                    s.addCell(new Label(j + 10, rowNum, "" + earlyOut, c));
                    s.addCell(new Label(j + 11, rowNum, "" + dates.size(), c));
                } else {
                    s.addCell(new Label(j + 1, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 2, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 3, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 4, rowNum, "" + "-", c));

                    s.addCell(new Label(j + 5, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 6, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 7, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 8, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 9, rowNum, "" + "-", c));
                    s.addCell(new Label(j + 10, rowNum, "" + "-", c));
                }
            }
            rowNum = rowNum + 1;


        }
        return workbook;
    }

    public long getDaysWorked(long empId) {
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getAllEmployeeAttendance();
        int count = 0;
        for (EmployeeAttendance e : employeeAttendances) {
            if (e.getEmployee().getId() == empId) {
                count = count + 1;
            }
        }
        return count;
    }

    @GetMapping("employee-attendance/eva-count")
    public ResponseEntity<List<EmployeeAttendance>> get() {
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getTodayMarkedEmployeeAttendance(new Date());
        List<EmployeeAttendance> employeeAttendanceList = new ArrayList<>();
        for (EmployeeAttendance e : employeeAttendances) {
            if (e.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                employeeAttendanceList.add(e);
            }
        }
        return new ResponseEntity<>(employeeAttendanceList, HttpStatus.OK);
    }

    @PostMapping("employee-attendance/reassignment")
    public void reassignment(@RequestBody EmployeeAttendanceDto employeeAttendanceDto) {
        Optional<EmployeeAttendance> employeeAttendance = employeeAttendanceService.getEmployeeAttendance(employeeAttendanceDto.getId());
        EmployeeAttendance employeeAttendance1 = employeeAttendance.get();
        Shift shift = shiftService.getShift(employeeAttendanceDto.getShiftId());
        employeeAttendance1.setShift(shift);
        employeeAttendanceService.createEmployeeAttendance(employeeAttendance1,false);
    }

    @PostMapping("employee-attendance/for-reassignment")
    public ResponseEntity<List<EmployeeAttendance>> getNotAssignedEmployees(@RequestBody DateDto dateDto) {
        List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getTodayMarkedEmployeeAttendance(dateDto.startDate);
        List<EmployeeAttendance> employeeAttendanceList = new ArrayList<>();
        for (EmployeeAttendance employeeAttendance : employeeAttendances) {
            if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                if (employeeAttendance.getShift() == null) {
                    employeeAttendanceList.add(employeeAttendance);
                }
            }
        }
        return new ResponseEntity<>(employeeAttendanceList, HttpStatus.OK);
    }


    //----UPDATE WHILE REPORT-----------
    private EmployeeAttendance updateWorkedHours(EmployeeAttendance attendance) throws Exception {
        CompanyConfigure companyConfigure = companyConfigureService.getCompanyById(1);
        if (companyConfigure != null) {
            if (companyConfigure.getOtFactor() > 0) {
                otFactor = companyConfigure.getOtFactor();
            }
            if (companyConfigure.getOtGraceTime() > 0) {
                otGraceMins = companyConfigure.getOtGraceTime();
            }
        }

        if (attendance.getInTime() != null) {
            System.out.println("Hours: " + attendance.getInTime().getHours());
            Shift shift = getCurrentShiftWithGrace((int) attendance.getInTime().getHours() + 1);
            if (shift != null) {
                if (shift.getId() == 5) {
                    Date tomorrow = DateUtil.tomorrow(attendance.getMarkedOn());
                    System.out.println("tmrw date " + tomorrow);
                    EmployeeAttendance employeeAttendanceTomorrow = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(DateUtil.convertDateToFormat(tomorrow), attendance.getEmployee().getId());
                    if (employeeAttendanceTomorrow != null && employeeAttendanceTomorrow.getInTime() != null) {
                        if (employeeAttendanceTomorrow.getInTime().after(attendance.getInTime())) {
                            if (attendance.getOutTime() == null) {
                                if (employeeAttendanceTomorrow.getInTime().getHours() <= 7) {
                                    attendance.setOutTime(employeeAttendanceTomorrow.getInTime());
                                }

                            }
                            employeeAttendanceTomorrow.setAttendanceStatus(AttendanceStatus.PRESENT);
                            System.out.println("outset");
                            if (employeeAttendanceTomorrow.getOutTime() != null) {
                                if (employeeAttendanceTomorrow.getInTime().equals(employeeAttendanceTomorrow.getOutTime())) {
                                    attendance.setOutTime(employeeAttendanceTomorrow.getInTime());
                                    if (isSunday(employeeAttendanceTomorrow.getMarkedOn())) {
                                        employeeAttendanceTomorrow.setAttendanceStatus(AttendanceStatus.SUNDAY);
                                    } else {
                                        employeeAttendanceTomorrow.setAttendanceStatus(AttendanceStatus.ABSENT);
                                    }
                                }
                            }
                            employeeAttendanceService.createEmployeeAttendance(attendance,true);

                            employeeAttendanceService.createEmployeeAttendance(employeeAttendanceTomorrow,true);
                        }
                    }
                }
            }

            if(shift == null && shiftValidateRequiredOnWeekOff == false){
                setWeekOffTotalHrs(attendance);
            }
            if (shift == null || attendance.getOutTime() == null) {
                return attendance;
            }
            attendance.setShift(shift);
            DateFormat timeFormat = new SimpleDateFormat("HH.mm");
            String inTime = timeFormat.format(attendance.getInTime());
            String outTime = timeFormat.format(attendance.getOutTime());
            long attendanceInTime = timeFormat.parse(inTime).getTime();
            long attendanceOutTime = timeFormat.parse(outTime).getTime();
            //Time attTime = new Time(attendanceTime);
            String shiftStartTime = timeFormat.format(shift.getStartTime());
            long shiftStart = timeFormat.parse(shiftStartTime).getTime();

            String shiftEndTime = timeFormat.format(shift.getEndTime());
            long shiftEnd = timeFormat.parse(shiftEndTime).getTime();

            attendance.setWorkedHours("0");
            attendance.setEarlyOut("0");
            attendance.setOverTime("0");
            attendance.setEffectiveOverTime("0");
            attendance.setLateEntry("0");
            attendance.setLateEntryNotFormated(String.valueOf(0));
            attendance.setEarlyOutNotFormated(String.valueOf(0));

            double workedMinutes = 0;
            long workedMillis = 0;
            double lateEntryMins = 0;
            double earlyOutMins = 0;
            workedMillis = attendanceOutTime - shiftStart;

            //------LATE ENTRY-------------
            if (attendanceInTime > (shiftStart + shift.getGraceInTime())) {
                long lateEntry = attendanceInTime - shift.getStartTime().getTime();
//                if (shift.getId()==5){
//                    lateEntry =   shift.getStartTime().getTime()-attendanceInTime;
//                }
                String lateEntryTime = convert(lateEntry);
                attendance.setLateEntry(lateEntryTime);

                lateEntryMins = TimeUnit.MILLISECONDS.toMinutes(lateEntry);
                double lateHrsSimple = lateEntryMins / 60;
                attendance.setLateEntryNotFormated(String.format("%.2f", lateHrsSimple));
                if (attendance.getLateEntry().equalsIgnoreCase("00:00")) {
                    attendance.setLateEntryNotFormated(String.valueOf(0));
                }
//                workedMillis = attendanceOutTime - attendanceInTime;
                workedMillis = attendanceOutTime - shiftStart;
            } else {
                attendance.setLateEntry("00.00");
                attendance.setLateEntryNotFormated(String.valueOf(0));
//                workedMillis = attendanceOutTime - shiftStart;
            }
            //------LATE ENTRY-------------

            //---------EarlyOut------------
            if (attendanceOutTime < (shiftEnd + shift.getGraceOutTme())) {
                long earlyOut = shift.getEndTime().getTime() - attendanceOutTime;
                String earlyOutTime = convert(earlyOut);
                attendance.setEarlyOut(earlyOutTime);
                earlyOutMins = TimeUnit.MILLISECONDS.toMinutes(earlyOut);
                double ealyOutSimple = earlyOutMins / 60;
                attendance.setEarlyOutNotFormated(String.format("%.2f", ealyOutSimple));
                if (attendance.getEarlyOut().equalsIgnoreCase("00:00")) {
                    attendance.setEarlyOutNotFormated(String.valueOf(0));
                }
//                workedMillis = attendanceOutTime - attendanceInTime;
            } else {
                attendance.setEarlyOut("00:00");
                attendance.setEarlyOutNotFormated("0");
//                workedMillis = attendanceOutTime - shiftStart;
            }
            //---------EarlyOut------------

            if (attendanceInTime > attendanceOutTime && shift.getId() == 5) {
                Shift s = shift;
                Date sn = s.getStartTime();
                Date sndate = new Date();

                sndate.setDate(attendance.getInTime().getDate());
                sndate.setYear(attendance.getInTime().getYear());
                sndate.setMonth(attendance.getInTime().getMonth());

                sndate.setHours(sn.getHours());
                sndate.setMinutes(sn.getMinutes());
                sndate.setSeconds(sn.getSeconds());
//                workedMillis = attendance.getOutTime().getTime() - attendance.getInTime().getTime();
                workedMillis = attendance.getOutTime().getTime() - sndate.getTime();
            }

            if (workedMillis < 0) {
                attendance.setOutTime(null);
                workedMillis = 0;
            }

            workedMinutes = TimeUnit.MILLISECONDS.toMinutes(workedMillis);
            double workedHrs = workedMinutes / 60;
//            String workedHrs = convert(workedMillis);

            attendance.setWorkedHours(String.format("%.2f", workedHrs));
            attendance.setWhF(convertOt((attendance.getWorkedHours())));
            if (attendance.getEmployee().isOtRequired()) {
                if (isWeekOff(attendance.getMarkedOn(), attendance.getEmployee().getFirstWeekOff(), attendance.getEmployee().getSecondWeekOff()) || isSunday(attendance.getMarkedOn())) {
                    double extraHour = workedMinutes - 0;
                    double overTimeHrs = (extraHour / 60);
                    attendance.setOverTime(String.format("%.2f", overTimeHrs));
                    double effectiveExtraHour = overTimeHrs * otFactor;
                    attendance.setEffectiveOverTime(String.format("%.2f", effectiveExtraHour));
                    attendance.setOtF(convertOt((attendance.getOverTime())));
                    attendance.setEOtF(convertOt((attendance.getEffectiveOverTime())));
                    attendance.setWeekOffPresent(true);
                    attendance.setLateEntryNotFormated(String.valueOf(0));
                    attendance.setEarlyOutNotFormated(String.valueOf(0));
                    attendance.setLateEntry("00.00");
                    attendance.setEarlyOut("00.00");
                } else if (workedMinutes > (510 + otGraceMins)) {
                    double extraHour = workedMinutes - 510;
                    double overTimeHrs = (extraHour / 60); /*+ 150) / 60;*/
                    attendance.setOverTime(String.format("%.2f", overTimeHrs));
                    double effectiveExtraHour = overTimeHrs * otFactor;
                    attendance.setEffectiveOverTime(String.format("%.2f", effectiveExtraHour));
                    attendance.setOtF(convertOt((attendance.getOverTime())));
                    attendance.setEOtF(convertOt((attendance.getEffectiveOverTime())));
                } else {
                    attendance.setOverTime(String.valueOf(0.00));
                    attendance.setEffectiveOverTime(String.valueOf(0.00));
                    attendance.setOtF(String.valueOf(0.00));
                    attendance.setEOtF(String.valueOf(0.00));
                }
            } else {
                attendance.setOverTime(String.valueOf(0.00));
                attendance.setEffectiveOverTime(String.valueOf(0.00));
                attendance.setOtF(String.valueOf(0.00));
                attendance.setEOtF(String.valueOf(0.00));
            }

        }
        employeeAttendanceService.createEmployeeAttendance(attendance,true);
        return attendance;
    }

    public void setWeekOffTotalHrs(EmployeeAttendance attendance) throws Exception {
        if (attendance.getOutTime() != null && attendance.getInTime() != null) {
            long workedMillis = attendance.getOutTime().getTime() - attendance.getInTime().getTime();
            double workedMinutes = TimeUnit.MILLISECONDS.toMinutes(workedMillis);
            double workedHrs = workedMinutes / 60;
            attendance.setWorkedHours(String.format("%.2f", workedHrs));
            attendance.setWhF(convertOt((attendance.getWorkedHours())));
            if (attendance.getEmployee().isOtRequired()) {
                if (isWeekOff(attendance.getMarkedOn(), attendance.getEmployee().getFirstWeekOff(), attendance.getEmployee().getSecondWeekOff()) || isSunday(attendance.getMarkedOn())) {
                    double extraHour = workedMinutes - 0;
                    double overTimeHrs = (extraHour / 60);
                    attendance.setOverTime(String.format("%.2f", overTimeHrs));
                    double effectiveExtraHour = overTimeHrs * otFactor;
                    attendance.setEffectiveOverTime(String.format("%.2f", effectiveExtraHour));
                    attendance.setOtF(convertOt((attendance.getOverTime())));
                    attendance.setEOtF(convertOt((attendance.getEffectiveOverTime())));
                    attendance.setWeekOffPresent(true);
                    attendance.setLateEntryNotFormated(String.valueOf(0));
                    attendance.setEarlyOutNotFormated(String.valueOf(0));
                    attendance.setLateEntry("00.00");
                    attendance.setEarlyOut("00.00");
                }
            } else {
                attendance.setOverTime(String.valueOf(0.00));
                attendance.setEffectiveOverTime(String.valueOf(0.00));
                attendance.setOtF(String.valueOf(0.00));
                attendance.setEOtF(String.valueOf(0.00));
            }
            employeeAttendanceService.createEmployeeAttendance(attendance,true);
        }
    }

//    private EmployeeAttendance updateWorkedHours(EmployeeAttendance attendance) throws ParseException {
//        if (attendance.getInTime() != null) {
//            System.out.println("Hours: " + attendance.getInTime().getHours());
//            Shift shift = getCurrentShiftWithGrace((int) attendance.getInTime().getHours() + 1);
//            if (shift == null || attendance.getOutTime() == null) {
//                return attendance;
//            }
//            attendance.setShift(shift);
//            DateFormat timeFormat = new SimpleDateFormat("HH.mm");
//           long shiftStart = timeFormat.parse(timeFormat.format(shift.getStartTime())).getTime();
//           long attendanceInTime = timeFormat.parse(timeFormat.format(attendance.getInTime())).getTime();
//           long shiftEnd = timeFormat.parse(timeFormat.format(shift.getEndTime())).getTime();
//           long attendanceOutTime = timeFormat.parse(timeFormat.format(attendance.getOutTime())).getTime();
//
//            double workedMinutes = 0;
//            long workedMillis =0;
//            System.out.println("shiftStart------------------------------------------------------- "+(shiftStart));
//            if (/*attendanceInTime > shiftStart*/ true  /*shift.getGraceInTime()*/ /*600000*/) {
//                long lateEntry = attendanceInTime - shift.getStartTime().getTime();
//                if (lateEntry>0) {
//                    String lateEntryTime = convert(lateEntry);
//                    attendance.setLateEntry(lateEntryTime);
//                }
//
//            } else {
//                attendance.setLateEntry("00:00");
//            }
//
//            if (attendanceOutTime < shiftEnd /*shift.getGraceOutTme()*/) {
//                long lateEntry = shift.getEndTime().getTime() - attendanceOutTime;
//                String lateEntryTime = convert(lateEntry);
//                attendance.setEarlyOut(lateEntryTime);
//                workedMillis = attendanceOutTime - attendanceInTime;
//            } else {
//                attendance.setLateEntry("00:00");
//                workedMillis = attendanceOutTime - shiftStart;
//            }
//
//            workedMinutes = TimeUnit.MILLISECONDS.toMinutes(workedMillis);
//            double workedHrs = workedMinutes / 60;
////            String workedHrs = convert(workedMillis);
//
//            attendance.setWorkedHours(String.format("%.2f",workedHrs));
//            if (workedMinutes > 510) {
//                double extraHour = workedMinutes - 510;
//                double overTimeHrs = (extraHour / 60); /*+ 150) / 60;*/
//                attendance.setOverTime(String.format("%.2f", overTimeHrs));
//                double effectiveExtraHour = overTimeHrs * 1.0;
//                attendance.setEffectiveOverTime(String.format("%.2f", effectiveExtraHour));
//            } else {
//                attendance.setOverTime(String.valueOf(0.00));
//                attendance.setEffectiveOverTime(String.valueOf(0.00));
//            }
//
//        }
//        employeeAttendanceService.createEmployeeAttendance(attendance);
//        return attendance;
//    }

    public Shift getCurrentShiftWithGrace(int inTime) {

        int currentTime = inTime;
        System.out.println("CurrentTime " + currentTime);
        List<Shift> shiftList = new ArrayList<>();
        List<Shift> shifts = null;
        if (shifts == null) {
            shifts = shiftRepository.findAll();
        }
        for (Shift runningShift : shifts) {
            if (currentTime >= DateUtil.getRunningHour(runningShift.getStartTime()) && currentTime <= DateUtil.getRunningHour(runningShift.getEndTime())) {
                shiftList.add(runningShift);
            }
        }
//        if (shiftList.isEmpty()) {
//            for (Shift runningShift : shifts) {
//                if ((currentTime + runningShift.getShiftGraceBefore()) >= DateUtil.getRunningHour(runningShift.getStartTime()) && (currentTime + runningShift.getShiftGraceAfter()) <= DateUtil.getRunningHour(runningShift.getEndTime())) {
//                    shiftList.add(runningShift);
//                }
//            }
//
//        }
        System.out.println("SHIFTSIZE1 " + shiftList.size());
        if (currentTime >= 18) {
            System.out.println("SHIFT 5");
            Shift shift = shiftService.getShift(5);
            if (shift != null) {
                shiftList.add(shift);
            }
        }

        System.out.println("SHIFTSIZE2 " + shiftList.size());
        if (shiftList.isEmpty()) {
            return null;
        } else {
            Collections.reverse(shiftList);
            System.out.println("shiftId " + shiftList.get(0).getId());
            Shift shift = shiftList.get(0);
            return shift;
        }

    }


    //--------ARUN WEEKOFF------
    @PostMapping("employee/weekoff/{empId}")
    public ResponseEntity<Employee> saveWeekOff(@RequestBody DepartmentTrackerDto departmentTrackerDto, @PathVariable("empId") long empId) throws Exception {
        String weekDay1, weekDay2 = null;
        Optional<Employee> employee1 = employeeService.getEmployee(empId);
        Employee employee = employee1.get();

        employee.setFirstWeekOff(departmentTrackerDto.getFirstWeekOff());
        employee.setSecondWeekOff(departmentTrackerDto.getSecondWeekOff());
        weekDay1 = getDayName(departmentTrackerDto.getFirstWeekOff());
        weekDay2 = getDayName(departmentTrackerDto.getSecondWeekOff());
        employee.setFirstWeekOffName(weekDay1);
        employee.setSecondWeekOffName(weekDay2);
        employeeService.save(employee);
        setWeekOff(empId, departmentTrackerDto.getFirstWeekOff(), departmentTrackerDto.getSecondWeekOff());
        return new ResponseEntity<>(employee, HttpStatus.OK);

    }

    public void setWeekOff(long employeeId, int firstWeekOff, int secondWeekOff) throws Exception {
        Optional<Employee> employee1 = employeeService.getEmployee(employeeId);
        Employee employee = employee1.get();
        Date startDate = getFirstDay(new Date());
        Date endDate = getLastDay(new Date());

        List<Date> monthDates = DateUtil.getDaysBetweenDates(startDate, endDate);
        for (Date d : monthDates) {
            if (d.getDay() == firstWeekOff) {
                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(d, employeeId);
                if (employeeAttendance == null) {
                    EmployeeAttendance weekOffAttendance = new EmployeeAttendance();
                    Date zeroTime = new Date();
                    zeroTime.setHours(0);
                    zeroTime.setMinutes(0);
                    zeroTime.setSeconds(0);
                    weekOffAttendance.setInTime(zeroTime);
                    weekOffAttendance.setOutTime(zeroTime);
                    weekOffAttendance.setEffectiveOverTime("0");
                    weekOffAttendance.setOverTime("0");
                    weekOffAttendance.setWorkedHours("0");
                    weekOffAttendance.setRecordedTime(d);
                    weekOffAttendance.setLateEntry("00.00");
                    weekOffAttendance.setEmployee(employee);
                    weekOffAttendance.setMarkedOn(d);
                    weekOffAttendance.setAttendanceStatus(AttendanceStatus.WO);
                    employeeAttendanceService.createEmployeeAttendance(weekOffAttendance,false);
                } else if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                    Date zeroTime = new Date();
                    zeroTime.setHours(0);
                    zeroTime.setMinutes(0);
                    zeroTime.setSeconds(0);
                    employeeAttendance.setInTime(zeroTime);
                    employeeAttendance.setOutTime(zeroTime);
                    employeeAttendance.setEffectiveOverTime("0");
                    employeeAttendance.setOverTime("0");
                    employeeAttendance.setWorkedHours("0");
                    employeeAttendance.setRecordedTime(d);
                    employeeAttendance.setLateEntry("00.00");
                    employeeAttendance.setEmployee(employee);
                    employeeAttendance.setMarkedOn(d);
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.WO);
                    employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
                }

            }
            if (d.getDay() == secondWeekOff) {
                EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(d, employeeId);
                if (employeeAttendance == null) {
                    EmployeeAttendance weekOffAttendance = new EmployeeAttendance();
                    Date zeroTime = new Date();
                    zeroTime.setHours(0);
                    zeroTime.setMinutes(0);
                    zeroTime.setSeconds(0);
                    weekOffAttendance.setInTime(zeroTime);
                    weekOffAttendance.setOutTime(zeroTime);
                    weekOffAttendance.setEffectiveOverTime("0");
                    weekOffAttendance.setOverTime("0");
                    weekOffAttendance.setWorkedHours("0");
                    weekOffAttendance.setRecordedTime(new Date());
                    weekOffAttendance.setLateEntry("00.00");
                    weekOffAttendance.setEmployee(employee);
                    weekOffAttendance.setMarkedOn(d);
                    weekOffAttendance.setAttendanceStatus(AttendanceStatus.WO);
                    employeeAttendanceService.createEmployeeAttendance(weekOffAttendance,false);
                } else if (employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                    Date zeroTime = new Date();
                    zeroTime.setHours(0);
                    zeroTime.setMinutes(0);
                    zeroTime.setSeconds(0);
                    employeeAttendance.setInTime(zeroTime);
                    employeeAttendance.setOutTime(zeroTime);
                    employeeAttendance.setEffectiveOverTime("0");
                    employeeAttendance.setOverTime("0");
                    employeeAttendance.setWorkedHours("0");
                    employeeAttendance.setRecordedTime(new Date());
                    employeeAttendance.setLateEntry("00.00");
                    employeeAttendance.setEmployee(employee);
                    employeeAttendance.setMarkedOn(d);
                    employeeAttendance.setAttendanceStatus(AttendanceStatus.WO);
                    employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
                }
            }

        }

    }

    public String getDayName(int dayNumber) {
        String dayName = null;
        if (dayNumber == 0) {
            dayName = "SUNDAY";
        } else if (dayNumber == 1) {
            dayName = "MONDAY";
        } else if (dayNumber == 2) {
            dayName = "TUESDAY";
        } else if (dayNumber == 3) {
            dayName = "WEDNESDAY";
        } else if (dayNumber == 4) {
            dayName = "THURSDAY";
        } else if (dayNumber == 5) {
            dayName = "FRIDAY";
        } else if (dayNumber == 6) {
            dayName = "SATURDAY";
        }
        return dayName;
    }

    public Date getFirstDay(Date d) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dddd = calendar.getTime();
        return dddd;
    }

    public Date getLastDay(Date d) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date dddd = calendar.getTime();
        return dddd;
    }

//    public long findTotalDistance(int size,int[] array){
//        long sum = 0;
//        for (int i = 0; i < (size-1); i++) {
//            sum  = sum + array[i]-array[i+1];
//        }
//        return sum;
//    }
//    public void ch() {
//        System.out.println(findTotalDistance(5, new int[]{10,11,7,12,4}));
//    }



}
