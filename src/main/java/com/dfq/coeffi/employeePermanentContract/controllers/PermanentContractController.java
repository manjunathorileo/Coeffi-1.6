package com.dfq.coeffi.employeePermanentContract.controllers;

import com.dfq.coeffi.GeoLocation.GeoLocationDto;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.hr.EmployeeController;
import com.dfq.coeffi.dto.*;
import com.dfq.coeffi.employeePermanentContract.entities.ContractCompany;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.entities.EmployeePass;
import com.dfq.coeffi.employeePermanentContract.entities.PermanentContractAttendance;
import com.dfq.coeffi.employeePermanentContract.repositories.EmployeePassRepo;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractAttendanceRepo;
import com.dfq.coeffi.employeePermanentContract.repositories.PermanentContractRepo;
import com.dfq.coeffi.employeePermanentContract.services.ContractCompanyService;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.Department;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.service.hr.DepartmentService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import jxl.format.Colour;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static jxl.format.Alignment.*;

@RestController
@Slf4j
public class PermanentContractController extends BaseController {
    @Autowired
    PermanentContractService permanentContractService;
    @Autowired
    PermanentContractAttendanceRepo permanentContractAttendanceRepo;
    @Autowired
    EmployeePassRepo employeePassRepo;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    PermanentContractRepo permanentContractRepo;
    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    ContractCompanyService contractCompanyService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("permanentContract")
    public ResponseEntity<EmpPermanentContract> save(@RequestBody EmpPermanentContract empPermanentContract) {
        empPermanentContract.setStatus(true);
        empPermanentContract.setBelongsTo(true);
        if(empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)){
            empPermanentContract.setEmployeeType(EmployeeType.CONTRACT);
            empPermanentContract.setTemporaryContract(false);
        }
        if(empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)){
            empPermanentContract.setEmployeeType(EmployeeType.CONTRACT);
            empPermanentContract.setTemporaryContract(true);
        }
        EmpPermanentContract obj = permanentContractService.save(empPermanentContract);
        return new ResponseEntity<>(obj, HttpStatus.CREATED);
    }

    @PostMapping("permanentContract/{empId}")
    public ResponseEntity<EmpPermanentContract> getEmployeeById(@PathVariable long empId) {
        EmpPermanentContract obj = permanentContractService.get(empId);
        PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeId(new Date(), empId);
        if (permanentContractAttendance != null) {
            if (permanentContractAttendance.getInTime() != null) {
                obj.setInTime(permanentContractAttendance.getInTime().getTime());
            }
            if (permanentContractAttendance.getOutTime() != null) {
                obj.setOutTime(permanentContractAttendance.getOutTime().getTime());
            }
            obj.setPermanentContractAttendance(permanentContractAttendance);
        }
        return new ResponseEntity<>(obj, HttpStatus.CREATED);
    }

    @PostMapping("permanentContract-employee-code/{employeeCode}")
    public ResponseEntity<EmpPermanentContract> getEmployeeById(@PathVariable String employeeCode) {
        EmpPermanentContract obj = permanentContractService.get(employeeCode);
        PermanentContractAttendance permanentContractAttendance = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeId(new Date(), obj.getId());
        if (permanentContractAttendance != null) {
            if (permanentContractAttendance.getInTime() != null) {
                obj.setInTime(permanentContractAttendance.getInTime().getTime());
            }
            if (permanentContractAttendance.getOutTime() != null) {
                obj.setOutTime(permanentContractAttendance.getOutTime().getTime());
            }
            obj.setPermanentContractAttendance(permanentContractAttendance);
        }
        return new ResponseEntity<>(obj, HttpStatus.CREATED);
    }


    @GetMapping("permanentContracts")
    public ResponseEntity<List<EmpPermanentContract>> getAllPermanentContract() throws Exception {
//        List<EmpPermanentContract> obj = permanentContractService.getAll(true);
        List<EmpPermanentContract> obj = permanentContractService.getAll(false);
        if (obj.isEmpty()) {
            throw new Exception("No registered Contract employees");
        }
        List<EmpPermanentContract> empPermanentContractList = new ArrayList<>();
        for (EmpPermanentContract permanentContract : obj) {
            if (true) {
                EmployeePass employeePass = employeePassRepo.findByEmpId(permanentContract.getId());
                if (employeePass != null) {
                    Date date = new Date();
                    if (employeePass.getEndDate().before(date)) {
                        permanentContract.setValid(false);
                        permanentContract.setStartDate(employeePass.getStartDate());
                        permanentContract.setEndDate(employeePass.getEndDate());
                        permanentContractService.save(permanentContract);
                        if (permanentContract.getEmployeeType().equals(EmployeeType.CONTRACT) && permanentContract.isTemporaryContract() == false ) {
                            empPermanentContractList.add(permanentContract);
                        }
//                    throw new Exception("Pass expired");
                    } else {
                        permanentContract.setStartDate(employeePass.getStartDate());
                        permanentContract.setEndDate(employeePass.getEndDate());
                        if (permanentContract.getEmployeeType().equals(EmployeeType.CONTRACT) && permanentContract.isTemporaryContract() == false) {
                            permanentContract.setEmployeeType(EmployeeType.PERMANENT_CONTRACT);
                            empPermanentContractList.add(permanentContract);
                            permanentContract.setEmployeeType(EmployeeType.CONTRACT);
                        }
                    }
                } else {
                    if (permanentContract.getEmployeeType().equals(EmployeeType.CONTRACT) && permanentContract.isTemporaryContract() == false) {
                        permanentContract.setEmployeeType(EmployeeType.PERMANENT_CONTRACT);
                        empPermanentContractList.add(permanentContract);
                        permanentContract.setEmployeeType(EmployeeType.CONTRACT);
                    }
                }
            }
        }
        return new ResponseEntity<>(empPermanentContractList, HttpStatus.OK);
    }

    @GetMapping("contracts")
    public ResponseEntity<List<EmpPermanentContract>> getAllContract() throws Exception {
        List<EmpPermanentContract> obj = permanentContractService.getAll(true);
        if (obj.isEmpty()) {
            throw new Exception("No registered Contract employees");
        }
        List<EmpPermanentContract> empPermanentContractList = new ArrayList<>();
        for (EmpPermanentContract permanentContract : obj) {
            EmployeePass employeePass = employeePassRepo.findByEmpId(permanentContract.getId());
            if (employeePass != null) {
                Date date = new Date();
                if (employeePass.getEndDate().before(date)) {
                    permanentContract.setValid(false);
                    permanentContract.setStartDate(employeePass.getStartDate());
                    permanentContract.setEndDate(employeePass.getEndDate());
                    permanentContractService.save(permanentContract);
                    if (permanentContract.getEmployeeType().equals(EmployeeType.CONTRACT) && permanentContract.isTemporaryContract()) {
                        empPermanentContractList.add(permanentContract);
                    }
//                    throw new Exception("Pass expired");
                } else {
                    permanentContract.setStartDate(employeePass.getStartDate());
                    permanentContract.setEndDate(employeePass.getEndDate());
                    if (permanentContract.getEmployeeType().equals(EmployeeType.CONTRACT) && permanentContract.isTemporaryContract()) {
                        empPermanentContractList.add(permanentContract);
                    }
                }
            } else {
                if (permanentContract.getEmployeeType().equals(EmployeeType.CONTRACT) && permanentContract.isTemporaryContract()) {
                    empPermanentContractList.add(permanentContract);
                }
            }
        }
        return new ResponseEntity<>(empPermanentContractList, HttpStatus.CREATED);
    }


    @PostMapping("generate-pass")
    public ResponseEntity<EmployeePass> generatePass(@RequestBody EmployeePass employeePass) throws Exception {
        EmpPermanentContract empPermanentContract = permanentContractService.get(employeePass.getEmpId());
        ContractCompany contractCompany = contractCompanyService.getByName(empPermanentContract.getContractCompany());
        EmployeePass employeePassExistedObj = null;
        if (contractCompany != null && contractCompany.getDateOfTermination().before(new Date())) {
            throw new Exception("Contract terminated");
        }
        if (empPermanentContract == null) {
            throw new EntityNotFoundException("No Such Contract_Employee");
        } else {
            EmployeePass employeePassExisted = employeePassRepo.findByEmpId(employeePass.getEmpId());
            if (employeePassExisted == null) {
                empPermanentContract.setValid(true);
                permanentContractService.save(empPermanentContract);
                employeePass.setValid(true);
                employeePass.setEmpPermanentContract(empPermanentContract);
                employeePassExistedObj = employeePassRepo.save(employeePass);
            } else {
                employeePassExisted.setStartDate(employeePass.getStartDate());
                employeePassExisted.setEndDate(employeePass.getEndDate());
                employeePassExisted.setValid(true);
                employeePassExisted.setEmpPermanentContract(empPermanentContract);
                employeePassExistedObj = employeePassRepo.save(employeePassExisted);
                empPermanentContract.setValid(true);
                permanentContractService.save(empPermanentContract);
            }
        }
        return new ResponseEntity<>(employeePassExistedObj, HttpStatus.OK);
    }

    @PostMapping("contract/mark-out-attendance")
    public PermanentContractAttendance markAttendanceThroughGeoLocation(@RequestBody GeoLocationDto dto) throws Exception {
        Date today = new Date();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
        PermanentContractAttendance employeeAttendance = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeId(dformat.parse(dformat.format(today)), dto.getEmpId());
        if (employeeAttendance == null) {
            throw new Exception("Mark In First");
        }
        if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
            employeeAttendance.setOutTime(today);
            employeeAttendance.setExitBodyTemperature(dto.getExitBodyTemperature());
            employeeAttendance.setExitGateNumber(dto.getExitGateNumber());
            employeeAttendance.setMaskWearing(dto.isMaskWearing());
            employeeAttendance.setRecordedTime(today);
            //TODO for dubai
//            calculateTotalStayTime(employeeAttendance);
            permanentContractAttendanceRepo.save(employeeAttendance);
        } else {
            throw new Exception("Out Time Already Marked");
        }
        return employeeAttendance;
    }


    @PostMapping("contract/mark-in-attendance")
    public ResponseEntity<PermanentContractAttendance> markInAttendace(@RequestBody GeoLocationDto dto) throws Exception {
        Date today = new Date();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
        Date t = dformat.parse(dformat.format(today));
        EmployeePass employeePass = null;
        employeePass = employeePassRepo.findByEmpId(dto.getEmpId());
        if (employeePass == null) {
            throw new Exception("Generate Pass first");
        }
        if (!employeePass.isValid()) {
            throw new Exception("Pass Expired");
        }
        System.out.println("EMPCODE+ " + employeePass.getEmpPermanentContract().getEmployeeCode());
        PermanentContractAttendance employeeAttendance = permanentContractAttendanceRepo.getEmployeeAttendanceByEmployeeId(t, dto.getEmpId());
        if (employeeAttendance == null) {
            PermanentContractAttendance newEmployeeAttendance = new PermanentContractAttendance();
            newEmployeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
            newEmployeeAttendance.setEmpId(dto.getEmpId());
            newEmployeeAttendance.setEmployeeCode(employeePass.getEmpPermanentContract().getEmployeeCode());
            newEmployeeAttendance.setInTime(today);
            newEmployeeAttendance.setMaskWearing(dto.isMaskWearing());
            newEmployeeAttendance.setEntryBodyTemperature(dto.getEntryBodyTemperature());
            newEmployeeAttendance.setEntryGateNumber(dto.getEntryGateNumber());
            newEmployeeAttendance.setMarkedOn(t);
            newEmployeeAttendance.setRecordedTime(today);
            permanentContractAttendanceRepo.save(newEmployeeAttendance);
        } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
            throw new Exception("In Time Already Marked");
        } else {
            throw new Exception("In Time Already Marked For Today");
        }
        return new ResponseEntity<>(employeeAttendance, HttpStatus.OK);
    }


    @GetMapping("contract/logged-in")
    public ResponseEntity<Long> insideCount(/*@RequestBody DateDto date*/) {
        List<PermanentContractAttendance> attendances = permanentContractAttendanceRepo.getEmployeeAttendanceBetweenDate(DateUtil.getTodayDate(), DateUtil.getTodayDate());
        long count = 0;
        for (PermanentContractAttendance e : attendances) {
            if (e.getInTime() != null && e.getOutTime() == null) {
                count++;
            }
        }
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("contract/logged-out")
    public ResponseEntity<Long> loggedOut() {
        List<PermanentContractAttendance> attendances = permanentContractAttendanceRepo.getEmployeeAttendanceBetweenDate(DateUtil.getTodayDate(), DateUtil.getTodayDate());
        long count = 0;
        for (PermanentContractAttendance e : attendances) {
            if (e.getInTime() != null && e.getOutTime() != null) {
                count++;
            }
        }
        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    @GetMapping("contract/total-today")
    public ResponseEntity<Long> totalCount() {
        List<PermanentContractAttendance> attendances = permanentContractAttendanceRepo.getEmployeeAttendanceBetweenDate(DateUtil.getTodayDate(), DateUtil.getTodayDate());
        long count = attendances.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    @PostMapping("contract/company-wise-report")
    public ResponseEntity<List<EmployeeAttendance>> viewEmployeeMonthlyAttendance(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        List<MonthlyEmployeeAttendanceDto> mADto = new ArrayList<>();
        for (MonthlyEmployeeAttendanceDto monthlyEmployeeAttendanceDto : monthlyEmployeeAttendanceDtos) {
            EmpPermanentContract employee = permanentContractService.get(monthlyEmployeeAttendanceDto.getEmployeeId());
            if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate)
                    && employee.getDateOfJoining().before(dateDto.endDate))) {
                if (dateDto.getCompanyName().equalsIgnoreCase(employee.getContractCompany())) {
                    mADto.add(monthlyEmployeeAttendanceDto);
                }
            }
        }
        if (monthlyEmployeeAttendanceDtos.isEmpty()) {
            throw new EntityNotFoundException("EmployeeAttendance viewEmployeeMonthlyAttendance not found");
        }
        return new ResponseEntity(mADto, HttpStatus.OK);
    }

    @PostMapping("contract/payment-wise-report")
    public ResponseEntity<List<EmployeeAttendance>> viewEmployeeMonthlyAttendancePayment(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        List<MonthlyEmployeeAttendanceDto> mADto = new ArrayList<>();
        for (MonthlyEmployeeAttendanceDto monthlyEmployeeAttendanceDto : monthlyEmployeeAttendanceDtos) {
            EmpPermanentContract employee = permanentContractService.get(monthlyEmployeeAttendanceDto.getEmployeeId());
            if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate)
                    && employee.getDateOfJoining().before(dateDto.endDate))) {
                mADto.add(monthlyEmployeeAttendanceDto);
            }
        }
        if (monthlyEmployeeAttendanceDtos.isEmpty()) {
            throw new EntityNotFoundException("EmployeeAttendance viewEmployeeMonthlyAttendance not found");
        }

        return new ResponseEntity(mADto, HttpStatus.OK);
    }

    @PostMapping("contract/department-wise-report")
    public ResponseEntity<List<EmployeeAttendance>> viewEmployeeMonthlyAttendanceDep(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        List<MonthlyEmployeeAttendanceDto> mADto = new ArrayList<>();
        for (MonthlyEmployeeAttendanceDto monthlyEmployeeAttendanceDto : monthlyEmployeeAttendanceDtos) {
            EmpPermanentContract employee = permanentContractService.get(monthlyEmployeeAttendanceDto.getEmployeeId());
            if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate)
                    && employee.getDateOfJoining().before(dateDto.endDate))) {
                if (dateDto.getDepartmentName().equalsIgnoreCase(employee.getDepartmentName())) {
                    mADto.add(monthlyEmployeeAttendanceDto);
                }
            }
        }
        if (monthlyEmployeeAttendanceDtos.isEmpty()) {
            throw new EntityNotFoundException("EmployeeAttendance viewEmployeeMonthlyAttendance not found");
        }
        return new ResponseEntity(mADto, HttpStatus.OK);
    }


    @PostMapping("contract/view-contract-report")
    public ResponseEntity<List<EmployeeAttendance>> companyWise(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParseException {
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        List<MonthlyEmployeeAttendanceDto> mADto = new ArrayList<>();
        for (MonthlyEmployeeAttendanceDto monthlyEmployeeAttendanceDto : monthlyEmployeeAttendanceDtos) {
            EmpPermanentContract employee = permanentContractService.get(monthlyEmployeeAttendanceDto.getEmployeeId());
//            if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) &&
//                    employee.getDateOfJoining().before(dateDto.endDate))) {
            mADto.add(monthlyEmployeeAttendanceDto);
//            }
        }
        if (monthlyEmployeeAttendanceDtos.isEmpty()) {
            throw new EntityNotFoundException("EmployeeAttendance viewEmployeeMonthlyAttendance not found");
        }
        return new ResponseEntity(mADto, HttpStatus.OK);
    }


    private List<MonthlyEmployeeAttendanceDto> viewMonthlyEmployeeAttendance(DateDto dateDto) throws ParseException {
        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
            throw new EntityNotFoundException("Selected Date of Month Should be same");
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = new ArrayList<MonthlyEmployeeAttendanceDto>();
        List<EmpPermanentContract> employees = permanentContractService.getAll(true);
        for (EmpPermanentContract employee : employees) {
            // TODO
            MonthlyEmployeeAttendanceDto mADto = new MonthlyEmployeeAttendanceDto();
            List<PermanentContractAttendance> monthlyEmployeeAttendance = permanentContractAttendanceRepo.getEmployeeAttendanceBetweenDateByEmployeeId(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()), employee.getId());
            List<MonthlyStatusDto> monthlyStatusDtos = new ArrayList<MonthlyStatusDto>();
            for (PermanentContractAttendance employeeAttendance : monthlyEmployeeAttendance) {
                MonthlyStatusDto dto = new MonthlyStatusDto();
                dto.setAttendanceStatus(employeeAttendance.getAttendanceStatus());
                dto.setDay(DateUtil.getDay(employeeAttendance.getMarkedOn()));
                dto.setMarkedOn(employeeAttendance.getMarkedOn());
                dto.setInTime(employeeAttendance.getInTime());
                dto.setOutTime(employeeAttendance.getOutTime());
                dto.setWorkedHours(employeeAttendance.getWorkedHours());
                dto.setExtraHrs(employeeAttendance.getOverTime());
                monthlyStatusDtos.add(dto);
            }
            mADto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            mADto.setEmployeeCode(employee.getEmployeeCode());
            mADto.setMonthlyStatus(monthlyStatusDtos);
            mADto.setEmployeeId(employee.getId());
            mADto.setDepartmentName(employee.getDepartmentName());
            mADto.setCompanyName(employee.getContractCompany());

//            if (employee.getDateOfJoining().before(dateDto.startDate) || (employee.getDateOfJoining().after(dateDto.startDate) && employee.getDateOfJoining().before(dateDto.endDate))) {
            monthlyEmployeeAttendanceDtos.add(mADto);
//        }

        }
        return monthlyEmployeeAttendanceDtos;
    }


    private PermanentContractAttendance calculateTotalStayTime(PermanentContractAttendance permanentContractAttendance) throws ParseException {
        DateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String inTime = timeFormat.format(permanentContractAttendance.getInTime());
        String outTime = timeFormat.format(permanentContractAttendance.getOutTime());
        long employeeInTime = timeFormat.parse(inTime).getTime();
        long employeeOutTime = timeFormat.parse(outTime).getTime();
        double workedMinutes = 0;
        double workedMillis;
        workedMillis = employeeInTime - employeeOutTime;
        workedMillis = Math.abs(workedMillis);
        workedMinutes = TimeUnit.MILLISECONDS.toMinutes((long) workedMillis);
        double workedHrs = workedMinutes / 60;
        permanentContractAttendance.setWorkedHours(String.valueOf(workedHrs));
        EmpPermanentContract empPermanentContract = permanentContractService.get(permanentContractAttendance.getEmpId());
        ContractCompany contractCompany = contractCompanyService.getByName(empPermanentContract.getContractCompany());
        if (workedHrs > contractCompany.getStayTime()) {
            double extraHour = workedHrs - contractCompany.getStayTime();
            double overTimeHrs = (extraHour + 150) / 60;
            System.out.println("ExtraTime: " + extraHour);
            permanentContractAttendance.setOverTime(String.valueOf(extraHour));
            permanentContractAttendance.setExtraTime((extraHour));
            double payableAmount = 0;
            double rate;
            rate = contractCompany.getRatePerHour();
            payableAmount = extraHour * rate;
            if (contractCompany.isPaymentApplicable()) {
                permanentContractAttendance.setPayableAmount(payableAmount);
            }
        } else {
            permanentContractAttendance.setExtraTime(0);
            permanentContractAttendance.setPayableAmount(0);
        }
        return permanentContractAttendance;
    }

    @PostMapping("contract/add-payment")
    public ResponseEntity<ContractCompany> setPaymentRule(@RequestBody ContractCompany c) {
        ContractCompany contractCompany = contractCompanyService.getContractCompanyById(c.getId());
        contractCompany.setRatePerHour(c.getRatePerHour());
        contractCompany.setStayTime(c.getStayTime());
        contractCompanyService.saveContractCompany(contractCompany);
        return new ResponseEntity<>(contractCompany, HttpStatus.OK);
    }

    @GetMapping("contract/security-screen")
    public ResponseEntity<PermanentContractAttendance> securityScreenView() {
        PermanentContractAttendance permanentContractAttendance = null;
        List<PermanentContractAttendance> permanentContractAttendances = permanentContractAttendanceRepo.findByAsc();
        if (!permanentContractAttendances.isEmpty()) {
            Collections.reverse(permanentContractAttendances);
            permanentContractAttendance = permanentContractAttendances.get(0);
            EmpPermanentContract empPermanentContract = permanentContractService.get(permanentContractAttendance.getEmpId());
            permanentContractAttendance.setCompanyName(empPermanentContract.getContractCompany());
            permanentContractAttendance.setEmployeeName(empPermanentContract.getFirstName());
            if (empPermanentContract.getProfilePicDocument() != null) {
                permanentContractAttendance.setImgId(empPermanentContract.getProfilePicDocument().getId());
            }
            permanentContractAttendance.setValidation("Valid");
        }
        return new ResponseEntity<>(permanentContractAttendance, HttpStatus.OK);
    }

    @PostMapping("contract/all-permanent-contract-report")
    public ResponseEntity<List<PermanentContractAttendance>> getContractEmployeeAttendanceReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
            throw new EntityNotFoundException("Selected Date of Month Should be same");
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        OutputStream out = null;
        String fileName = "Contract-Attendance_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
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

    private WritableWorkbook attendanceEntry(WritableWorkbook workbook, List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDto, HttpServletResponse response, int index, Date fromDate, Date toDate) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Monthly-Attendance-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
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
        WritableFont cellFont = new WritableFont(WritableFont.TIMES);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(jxl.format.Colour.GRAY_25);
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
        s.mergeCells(0, 0, 64, 0);
        s.mergeCells(0, 1, 64, 1);
        s.mergeCells(2, 18, 5, 18);
        Label lable = new Label(0, 0, "***** PVT LTD", headerFormat);
        s.addCell(lable);
        String monthName = DateUtil.getMonthName(fromDate);
        Label lableSlip = new Label(0, 1, "Monthly Attendance Report:  " + monthName, headerFormat);
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
        s.addCell(new Label(j + 1, 3, "Present", cellFormat));
        s.addCell(new Label(j + 2, 3, "Holidays", cellFormat));
        s.addCell(new Label(j + 3, 3, "Sundays", cellFormat));
        s.addCell(new Label(j + 4, 3, "Leaves", cellFormat));

        int rowNum = 4;
        for (MonthlyEmployeeAttendanceDto employeeAttendanceDto : monthlyEmployeeAttendanceDto) {
            s.addCell(new Label(0, 3, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 3)));
            s.addCell(new Label(1, 3, "Employee Id", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode()));
            s.addCell(new Label(2, 3, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName()));
            s.addCell(new Label(3, 3, "Department Name", cellFormat));
            s.addCell(new Label(3, rowNum, "" + null));
            s.addCell(new Label(4, 3, "Designation Name", cellFormat));
            s.addCell(new Label(4, rowNum, "" + null));
            int colNum = 5;
            double leaves;
            double present;
            long sundays;
            long holidays;
            String inTime = null;
            String outTime = null;
            String workedHours = null;
            String overTime = null;
            int noOfDays = DateUtil.calculateDaysBetweenDate(fromDate, toDate);
            for (int m = 0; m <= noOfDays; m++) {
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
                                inTime = "AB";
                                workedHours = "0";
                                overTime = "0";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HOLIDAY)) {
                                inTime = "H";
                                workedHours = "0";
                                overTime = "0";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PH)) {
                                inTime = "PH";
                                workedHours = "0";
                                overTime = "0";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.SUNDAY)) {
                                inTime = "S";
                                workedHours = "0";
                                overTime = "0";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
                                inTime = "L";
                            }
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                inTime = "HALF-DAY";
                            }
//                            inTime = String.valueOf(employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus());
                        }
                        if (/*employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime() != null &&*/ employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
                            outTime = timeFormat.format(employeeAttendanceDto.getMonthlyStatus().get(m).getOutTime());
                            workedHours = employeeAttendanceDto.getMonthlyStatus().get(m).getWorkedHours();
                            overTime = employeeAttendanceDto.getMonthlyStatus().get(m).getExtraHrs();

                        } else {
                            if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                                outTime = "AB";
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
                                outTime = "S";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.LEAVE) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.CL) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.ML) || employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.EL)) {
                                outTime = "L";
                                workedHours = "0";
                                overTime = "0";
                            } else if (employeeAttendanceDto.getMonthlyStatus().get(m).getAttendanceStatus().equals(AttendanceStatus.HALF_DAY)) {
                                outTime = "HALF-DAY";

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
                    s.addCell(new Label(colNum, rowNum, "" + inTime));
                    s.addCell(new Label(colNum + 1, rowNum, "" + outTime));
                    s.addCell(new Label(colNum + 2, rowNum, "" + workedHours));
                    s.addCell(new Label(colNum + 3, rowNum, "" + overTime));
                    colNum = colNum + 4;
                } else {
                    s.addCell(new Label(colNum, rowNum, "" + "-"));
                    s.addCell(new Label(colNum + 1, rowNum, "" + "-"));
                    s.addCell(new Label(colNum + 2, rowNum, "" + "-"));
                    s.addCell(new Label(colNum + 3, rowNum, "" + "-"));
                    colNum = colNum + 4;
                }

            }

            for (int i = noOfDays + 1; i <= noOfDays + 4; i++) {
                if (employeeAttendanceDto.getMonthlyStatus() != null && employeeAttendanceDto.getMonthlyStatus().size() > 0 && noOfDays < employeeAttendanceDto.getMonthlyStatus().size()) {
                    present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfPresentDays();
//                  present = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHalfDays();
                    sundays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfSudays();
                    holidays = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfHolidays();
                    leaves = employeeAttendanceDto.getMonthlyStatus().get(noOfDays).getNoOfLeaves();
                    s.addCell(new Label(j + 1, rowNum, "" + present));
                    s.addCell(new Label(j + 2, rowNum, "" + holidays));
                    s.addCell(new Label(j + 3, rowNum, "" + sundays));
                    s.addCell(new Label(j + 4, rowNum, "" + leaves));
                } else {
                    s.addCell(new Label(j + 1, rowNum, "" + "-"));
                    s.addCell(new Label(j + 2, rowNum, "" + "-"));
                    s.addCell(new Label(j + 3, rowNum, "" + "-"));
                }
            }
            rowNum = rowNum + 1;


        }
        return workbook;
    }


    @PostMapping("contract/department-permanent-contract-report")
    public ResponseEntity<List<PermanentContractAttendance>> getDepartmentEmployeeAttendanceReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
            throw new EntityNotFoundException("Selected Date of Month Should be same");
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtoslist = new ArrayList<>();

        for (MonthlyEmployeeAttendanceDto m : monthlyEmployeeAttendanceDtos) {
            if (m.getDepartmentName().equals(dateDto.getDepartmentName())) {
                monthlyEmployeeAttendanceDtoslist.add(m);
            }
        }
        OutputStream out = null;
        String fileName = "Contract-Attendance_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null && monthlyEmployeeAttendanceDtos.get(0).getDepartmentName().equals(dateDto.getDepartmentName())) {
                attendanceEntry(workbook, monthlyEmployeeAttendanceDtoslist, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
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


    @PostMapping("contract/company-wise-permanent-contract-report")
    public ResponseEntity<List<PermanentContractAttendance>> getCompanyWiseEmployeeAttendanceReport(@RequestBody DateDto dateDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {
        if (!DateUtil.getMonthName(dateDto.getStartDate()).equalsIgnoreCase(DateUtil.getMonthName(dateDto.getEndDate()))) {
            throw new EntityNotFoundException("Selected Date of Month Should be same");
        }
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtos = viewMonthlyEmployeeAttendance(dateDto);
        List<MonthlyEmployeeAttendanceDto> monthlyEmployeeAttendanceDtoslist = new ArrayList<>();
        for (MonthlyEmployeeAttendanceDto m : monthlyEmployeeAttendanceDtos) {
            if (m.getCompanyName().equals(dateDto.getCompanyName())) {
                monthlyEmployeeAttendanceDtoslist.add(m);
            }
        }
        OutputStream out = null;
        String fileName = "Contract-Attendance_Report_" + DateUtil.getMonthName(dateDto.getStartDate());
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry(workbook, monthlyEmployeeAttendanceDtoslist, response, 0, dateDto.getStartDate(), dateDto.getEndDate());
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


    //    -----Arun--------
    @GetMapping("get-checkin-employees-list")
    public ResponseEntity<List<PermanentContractAttendance>> getCheckInEmployees() {
        Date date = new Date();
        List<PermanentContractAttendance> permanentContractAttendanceList = permanentContractAttendanceRepo.findByMarkedOn(date);
        List<PermanentContractAttendance> permanentContractAttendanceList1 = new ArrayList<>();
        for (PermanentContractAttendance permanentContractAttendance : permanentContractAttendanceList) {
            EmpPermanentContract empPermanentContract = permanentContractService.get(permanentContractAttendance.getEmployeeCode());
            permanentContractAttendance.setEmployeeName(empPermanentContract.getFirstName());
            permanentContractAttendance.setCompanyName(empPermanentContract.getContractCompany());
            permanentContractAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
            permanentContractAttendanceList1.add(permanentContractAttendance);
        }
        return new ResponseEntity<>(permanentContractAttendanceList1, HttpStatus.OK);

    }

    @GetMapping("evacuation-pc-count")
    public ResponseEntity<List<PermanentContractAttendance>> getCoCount() {
        Date date = new Date();
        List<PermanentContractAttendance> permanentContractAttendanceList = permanentContractAttendanceRepo.findByMarkedOn(date);
        List<PermanentContractAttendance> permanentContractAttendanceList1 = new ArrayList<>();
        for (PermanentContractAttendance permanentContractAttendance : permanentContractAttendanceList) {
            EmpPermanentContract empPermanentContract = permanentContractService.get(permanentContractAttendance.getEmployeeCode());
            if (empPermanentContract != null) {
                permanentContractAttendance.setEmployeeName(empPermanentContract.getFirstName());
                permanentContractAttendance.setCompanyName(empPermanentContract.getContractCompany());
                permanentContractAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                    permanentContractAttendanceList1.add(permanentContractAttendance);
                }
            }
        }
        return new ResponseEntity<>(permanentContractAttendanceList1, HttpStatus.OK);

    }

    @GetMapping("evacuation-con-count")
    public ResponseEntity<List<PermanentContractAttendance>> getPcCount() {
        Date date = new Date();
        List<PermanentContractAttendance> permanentContractAttendanceList = permanentContractAttendanceRepo.findByMarkedOn(date);
        List<PermanentContractAttendance> permanentContractAttendanceList1 = new ArrayList<>();
        for (PermanentContractAttendance permanentContractAttendance : permanentContractAttendanceList) {
            EmpPermanentContract empPermanentContract = permanentContractService.get(permanentContractAttendance.getEmployeeCode());
            if (empPermanentContract != null) {
                permanentContractAttendance.setEmployeeName(empPermanentContract.getFirstName());
                permanentContractAttendance.setCompanyName(empPermanentContract.getContractCompany());
                permanentContractAttendance.setEmployeeCode(empPermanentContract.getEmployeeCode());
                if (empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                    permanentContractAttendanceList1.add(permanentContractAttendance);
                }
            }
        }
        return new ResponseEntity<>(permanentContractAttendanceList1, HttpStatus.OK);

    }

    @GetMapping("department-permanent-employee-count")
    public ResponseEntity<List<ContractDepartmentDto>> getPermanent() {
        List<Employee> employeeList = employeeService.getEmployeeByType(EmployeeType.PERMANENT, true);
        List<Department> departmentList = departmentService.findAll();
        List<ContractDepartmentDto> contractDepartmentDtoList = new ArrayList<>();
        Date date = new Date();
        for (Department d : departmentList) {
            long inCount = 0;
            long outCount = 0;
            for (Employee e : employeeList) {
                if (e.getDepartment() != null) {
                    if (e.getDepartment().getName().equals(d.getName())) {
                        EmployeeAttendance attendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, e.getId());
                        if (attendance != null && attendance.getInTime() != null && attendance.getOutTime() == null) {
                            inCount = inCount + 1;
                        } else if (attendance != null && attendance.getOutTime() != null) {
                            outCount = outCount + 1;
                        }
                    }
                }


            }
            ContractDepartmentDto contractDepartmentDto = new ContractDepartmentDto();
            contractDepartmentDto.setDepartment(d.getName());
            contractDepartmentDto.setCheckInCount(inCount);
            contractDepartmentDto.setCheckOutCount(outCount);
            contractDepartmentDtoList.add(contractDepartmentDto);
        }
        return new ResponseEntity<>(contractDepartmentDtoList, HttpStatus.OK);
    }

    @GetMapping("department-contract-employee-count")
    public ResponseEntity<List<ContractDepartmentDto>> getContract() {
        List<EmpPermanentContract> employeeList = permanentContractRepo.findByEmployeeType(EmployeeType.CONTRACT);
        List<Department> departmentList = departmentService.findAll();
        List<ContractDepartmentDto> contractDepartmentDtoList = new ArrayList<>();
        Date date = new Date();
        for (Department d : departmentList) {
            long inCount = 0;
            long outCount = 0;
//            for (EmpPermanentContract e : employeeList) {
//                if (e.getDepartmentName() != null) {
//                    if (e.getDepartmentName().equalsIgnoreCase(d.getName())) {
//                        PermanentContractAttendance attendance = permanentContractAttendanceRepo.;
//                        if (attendance != null && attendance.getInTime() != null && attendance.getOutTime() == null) {
//                            inCount = inCount + 1;
//                        } else if (attendance != null && attendance.getOutTime() != null) {
//                            outCount = outCount + 1;
//                        }
//                    }
//                }
//            }

            List<PermanentContractAttendance> attendanceList = permanentContractAttendanceRepo.findByMarkedOn(DateUtil.getTodayDate());
            for (PermanentContractAttendance attendance : attendanceList) {
                EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(attendance.getEmployeeCode());
                if (empPermanentContract != null) {
                    if (empPermanentContract.getDepartmentName() != null) {
                        if (empPermanentContract.getDepartmentName().equalsIgnoreCase(d.getName())) {
                            if (empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                                if (attendance != null && attendance.getInTime() != null && attendance.getOutTime() == null) {
                                    inCount = inCount + 1;
                                } else if (attendance != null && attendance.getOutTime() != null) {
                                    outCount = outCount + 1;
                                }
                            }
                        }
                    }
                }
            }
            ContractDepartmentDto contractDepartmentDto = new ContractDepartmentDto();
            contractDepartmentDto.setDepartment(d.getName());
            contractDepartmentDto.setCheckInCount(inCount);
            contractDepartmentDto.setCheckOutCount(outCount);
            contractDepartmentDtoList.add(contractDepartmentDto);
        }
        return new ResponseEntity<>(contractDepartmentDtoList, HttpStatus.OK);
    }


    @GetMapping("department-permanent-contract-employee-count")
    public ResponseEntity<List<ContractDepartmentDto>> getPermanentContract() {
        List<EmpPermanentContract> employeeList = permanentContractRepo.findAll();
        List<Department> departmentList = departmentService.findAll();
        List<ContractDepartmentDto> contractDepartmentDtoList = new ArrayList<>();
        Date date = new Date();
        for (Department d : departmentList) {
            long inCount = 0;
            long outCount = 0;
            List<PermanentContractAttendance> attendanceList = permanentContractAttendanceRepo.findByMarkedOn(DateUtil.getTodayDate());
            for (PermanentContractAttendance attendance : attendanceList) {
                EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(attendance.getEmployeeCode());
                if (empPermanentContract != null) {
                    if (empPermanentContract.getDepartmentName() != null) {
                        if (empPermanentContract.getDepartmentName().equalsIgnoreCase(d.getName())) {
                            if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                                if (attendance != null && attendance.getInTime() != null && attendance.getOutTime() == null) {
                                    inCount = inCount + 1;
                                } else if (attendance != null && attendance.getOutTime() != null) {
                                    outCount = outCount + 1;
                                }
                            }
                        }
                    }
                }
            }
            ContractDepartmentDto contractDepartmentDto = new ContractDepartmentDto();
            contractDepartmentDto.setDepartment(d.getName());
            contractDepartmentDto.setCheckInCount(inCount);
            contractDepartmentDto.setCheckOutCount(outCount);
            contractDepartmentDtoList.add(contractDepartmentDto);
        }
        return new ResponseEntity<>(contractDepartmentDtoList, HttpStatus.OK);
    }


    @GetMapping("get-permanent-total-count")
    public ResponseEntity<List<ContractDepartmentDto>> getTotalPermanent() {
        List<Employee> employeeList = employeeService.getEmployeeByType(EmployeeType.PERMANENT, true);
        List<ContractDepartmentDto> contractDepartmentDtoList = new ArrayList<>();
        Date date = new Date();
        List<EmployeeAttendance> totalEmployeeList = employeeAttendanceService.getTodayMarkedEmployeeAttendance(date);
        long inCount = 0;
        long outCount = 0;
        long totalCount = 0;
        for (Employee e : employeeList) {
            EmployeeAttendance attendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(date, e.getId());
            if (attendance != null && attendance.getInTime() != null && attendance.getOutTime() == null) {
                inCount = inCount + 1;
            } else if (attendance != null && attendance.getOutTime() != null) {
                outCount = outCount + 1;
            }
        }
        totalCount = totalEmployeeList.size();
        ContractDepartmentDto contractDepartmentDto = new ContractDepartmentDto();
        contractDepartmentDto.setEmployeeCount(totalCount);
        contractDepartmentDto.setTotalInCount(inCount);
        contractDepartmentDto.setTotalOutCount(outCount);
        contractDepartmentDtoList.add(contractDepartmentDto);

        return new ResponseEntity<>(contractDepartmentDtoList, HttpStatus.OK);
    }

    @GetMapping("get-contract-total-count")
    public ResponseEntity<List<ContractDepartmentDto>> getTotalContract() {
        List<EmpPermanentContract> employeeList = permanentContractRepo.findByEmployeeType(EmployeeType.CONTRACT);
        List<PermanentContractAttendance> attendanceList = permanentContractAttendanceRepo.findByMarkedOn(DateUtil.getTodayDate());
        List<ContractDepartmentDto> contractDepartmentDtoList = new ArrayList<>();
        Date date = new Date();
        List<PermanentContractAttendance> totalEmployeeList = permanentContractAttendanceRepo.findByMarkedOn(date);
        long inCount = 0;
        long outCount = 0;
        long totalCount = 0;

        for (PermanentContractAttendance attendance : attendanceList) {
            EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(attendance.getEmployeeCode());
            if (empPermanentContract != null) {
                if (empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)) {
                    if (attendance != null && attendance.getInTime() != null && attendance.getOutTime() == null) {
                        inCount = inCount + 1;
                    } else if (attendance != null && attendance.getOutTime() != null) {
                        outCount = outCount + 1;
                    }
                }
            }
        }

        totalCount = totalEmployeeList.size();
        ContractDepartmentDto contractDepartmentDto = new ContractDepartmentDto();
        contractDepartmentDto.setEmployeeCount(totalCount);
        contractDepartmentDto.setTotalInCount(inCount);
        contractDepartmentDto.setTotalOutCount(outCount);
        contractDepartmentDtoList.add(contractDepartmentDto);

        return new ResponseEntity<>(contractDepartmentDtoList, HttpStatus.OK);
    }

    @GetMapping("get-permanent-contract-total-count")
    public ResponseEntity<List<ContractDepartmentDto>> getTotalPermanentContract() {
        List<EmpPermanentContract> employeeList = permanentContractRepo.findByEmployeeType(EmployeeType.PERMANENT_CONTRACT);
        List<ContractDepartmentDto> contractDepartmentDtoList = new ArrayList<>();
        Date date = new Date();
        List<PermanentContractAttendance> totalEmployeeList = permanentContractAttendanceRepo.findByMarkedOn(date);
        long inCount = 0;
        long outCount = 0;
        long totalCount = 0;
        List<PermanentContractAttendance> attendanceList = permanentContractAttendanceRepo.findByMarkedOn(DateUtil.getTodayDate());
        for (PermanentContractAttendance attendance : attendanceList) {
            EmpPermanentContract empPermanentContract = permanentContractRepo.findByEmployeeCode(attendance.getEmployeeCode());
            if (empPermanentContract != null) {
                if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
                    if (attendance != null && attendance.getInTime() != null && attendance.getOutTime() == null) {
                        inCount = inCount + 1;
                    } else if (attendance != null && attendance.getOutTime() != null) {
                        outCount = outCount + 1;
                    }
                }
            }
        }
        totalCount = totalEmployeeList.size();
        ContractDepartmentDto contractDepartmentDto = new ContractDepartmentDto();
        contractDepartmentDto.setEmployeeCount(totalCount);
        contractDepartmentDto.setTotalInCount(inCount);
        contractDepartmentDto.setTotalOutCount(outCount);
        contractDepartmentDtoList.add(contractDepartmentDto);

        return new ResponseEntity<>(contractDepartmentDtoList, HttpStatus.OK);
    }


    //02-06-2020(Arun)

    @PostMapping("get-report-by-contractor")
    public ResponseEntity<List<ContractorDto>> getContractor(@RequestBody ContractorDto contractor, HttpServletRequest request, HttpServletResponse response) {
        List<ContractCompany> contractCompanyList = contractCompanyService.getAllContractCompany();
        List<Department> departmentList = departmentService.findAll();
        List<ContractorDto> contractorDtoList = new ArrayList<>();
        for (ContractCompany c : contractCompanyList) {
            ContractorDto contractorDto = new ContractorDto();
            List<DepartmanentWiseDto> departmanentWiseDtos = new ArrayList<>();
            List<Date> dates = getDatesBetween(contractor.getStartDate(), contractor.getEndDate());
            for (Date dobj : dates) {
                if (dobj.after(c.getDateOfOnBoarding()) && dobj.before(c.getDateOfTermination())) {
                    contractorDto.setContractorName(c.getCompanyName());
                    contractorDto.setStartDate(c.getDateOfOnBoarding());
                    contractorDto.setEndDate(c.getDateOfTermination());
                    contractorDto.setDueDate(c.getDateOfTermination());
                    List<EmpPermanentContract> empPermanentContractList = permanentContractRepo.findByContractCompany(c.getCompanyName());
                    long size = empPermanentContractList.size();
                    contractorDto.setTotalEmployees(size);
                    departmanentWiseDtos.clear();
                    for (Department d : departmentList) {
                        long count = 0;
                        List<Department> departments = new ArrayList<>();
                        for (EmpPermanentContract e : empPermanentContractList) {
                            if (d.getName().equals(e.getDepartmentName())) {
                                count = count + 1;
                                departments.add(d);
                            }
                        }
                        DepartmanentWiseDto departmanentWiseDto = new DepartmanentWiseDto();
                        departmanentWiseDto.setDepartmentName(d.getName());
                        long deptSize = departments.size();
                        departmanentWiseDto.setEmployeeCount(deptSize);
                        departmanentWiseDtos.add(departmanentWiseDto);
                    }

                }

            }
            contractorDto.setDepartmanentWiseDtoList(departmanentWiseDtos);
            contractorDtoList.add(contractorDto);
        }
        return new ResponseEntity<>(contractorDtoList, HttpStatus.OK);

    }

    @PostMapping("get-excel-download-by-contractor")
    //@RequestMapping(value = "reports/download/filter", method = RequestMethod.POST, headers = {"content-type=multipart/mixed","content-type=multipart/form-data"})
    private ResponseEntity<List<ContractorDto>> createExcel(@RequestBody ContractorDto contractorDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<ContractorDto> contractorDtoList = new ArrayList<>();
        contractorDtoList = (List<ContractorDto>) getContractors(contractorDto);

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= ContractReport.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            contractorEntry(workbook, contractorDtoList, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(contractorDtoList, HttpStatus.OK);
    }


    public List<ContractorDto> getContractors(ContractorDto contractor) {
        List<ContractCompany> contractCompanyList = contractCompanyService.getAllContractCompany();
        List<Department> departmentList = departmentService.findAll();
        List<ContractorDto> contractorDtoList = new ArrayList<>();
        for (ContractCompany c : contractCompanyList) {
            ContractorDto contractorDto = new ContractorDto();
            List<DepartmanentWiseDto> departmanentWiseDtos = new ArrayList<>();
            List<Date> dates = getDatesBetween(contractor.getStartDate(), contractor.getEndDate());
            for (Date dobj : dates) {
                if (dobj.after(c.getDateOfOnBoarding()) && dobj.before(c.getDateOfTermination())) {
                    contractorDto.setContractorName(c.getCompanyName());
                    contractorDto.setStartDate(c.getDateOfOnBoarding());
                    contractorDto.setEndDate(c.getDateOfTermination());
                    contractorDto.setDueDate(c.getDateOfTermination());
                    List<EmpPermanentContract> empPermanentContractList = permanentContractRepo.findByContractCompany(c.getCompanyName());
                    long size = empPermanentContractList.size();
                    contractorDto.setTotalEmployees(size);
                    departmanentWiseDtos.clear();
                    for (Department d : departmentList) {
                        long count = 0;
                        List<Department> departments = new ArrayList<>();
                        for (EmpPermanentContract e : empPermanentContractList) {
                            if (d.getName().equals(e.getDepartmentName())) {
                                count = count + 1;
                                departments.add(d);
                            }
                        }
                        DepartmanentWiseDto departmanentWiseDto = new DepartmanentWiseDto();
                        departmanentWiseDto.setDepartmentName(d.getName());
                        long deptSize = departments.size();
                        departmanentWiseDto.setEmployeeCount(deptSize);
                        departmanentWiseDtos.add(departmanentWiseDto);
                    }

                }

            }
            contractorDto.setDepartmanentWiseDtoList(departmanentWiseDtos);
            contractorDtoList.add(contractorDto);
        }
        return contractorDtoList;

    }


    private WritableWorkbook contractorEntry(WritableWorkbook workbook, List<ContractorDto> contractorDtos, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Monthly-Attendance-Report", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
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
        WritableFont cellFont = new WritableFont(WritableFont.TIMES);
        cellFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setAlignment(CENTRE);
        cellFormat.setBackground(jxl.format.Colour.GRAY_25);
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
        int rowNum = 1;
        for (ContractorDto contractorDto : contractorDtos) {
            s.addCell(new Label(0, 0, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1)));
            s.addCell(new Label(1, 0, "Contractor Name", cellFormat));
            s.addCell(new Label(1, rowNum, "" + contractorDto.getContractorName()));
            s.addCell(new Label(2, 0, "Contract Duration From", cellFormat));
            s.addCell(new Label(2, rowNum, "" + contractorDto.getStartDate()));
            s.addCell(new Label(3, 0, "Contract Duration To", cellFormat));
            s.addCell(new Label(3, rowNum, "" + contractorDto.getEndDate()));
            s.addCell(new Label(4, 0, "Contract DueDate", cellFormat));
            s.addCell(new Label(4, rowNum, "" + contractorDto.getDueDate()));
            s.addCell(new Label(4, 0, "Total Employees", cellFormat));
            s.addCell(new Label(4, rowNum, "" + contractorDto.getTotalEmployees()));
            List<DepartmanentWiseDto> departmanentWiseDtoList = contractorDto.getDepartmanentWiseDtoList();
            int colNum = 5;
            for (DepartmanentWiseDto dobj : departmanentWiseDtoList) {
                s.addCell(new Label(colNum, 0, "Department", cellFormat));
                s.addCell(new Label(colNum, rowNum, "" + dobj.getDepartmentName()));
                s.addCell(new Label(colNum + 1, 0, "Employees", cellFormat));
                s.addCell(new Label(colNum + 1, rowNum, "" + dobj.getEmployeeCount()));
                colNum = colNum + 2;
            }
            rowNum = rowNum + 1;


        }
        return workbook;
    }

    public static List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }


    @GetMapping("get-contract-employee-notifications")
    public ResponseEntity<List<String>> getContractNotificatios() throws Exception {
        List<PermanentContractAttendance> permanentContractAttendance = permanentContractAttendanceRepo.findAll();
        List<String> strings = new ArrayList<>();
        Date date = new Date();
        for (PermanentContractAttendance p : permanentContractAttendance) {
            if (p.getMarkedOn().before(date)) {
                if (p.getAttendanceStatus().equals(AttendanceStatus.ABSENT)) {
                    Optional<Employee> employee = employeeService.getEmployeeByLogin(p.getEmpId());
                    String s = employee.get().getFirstName() + " Absent";
                    strings.add(s);
                }
            }

        }
        return new ResponseEntity<>(strings, HttpStatus.OK);
    }

    @PostMapping("permanent-contract-bulk-upload")
    public ResponseEntity<List<EmpPermanentContract>> permanentContractBulkUpload(@RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<EmpPermanentContract> employeeList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            EmpPermanentContract employee = new EmpPermanentContract();
            XSSFRow row = sheet.getRow(i);
            employee.setFirstName(row.getCell(1).getStringCellValue());
            employee.setLastName(row.getCell(2).getStringCellValue());
            employee.setMiddleName(row.getCell(3).getStringCellValue());
            employee.setDateOfBirth(row.getCell(4).getDateCellValue());
            employee.setGender(row.getCell(5).getStringCellValue());
            employee.setPhoneNumber(row.getCell(6).getStringCellValue());
            employee.setEmergencyPhoneNumber(row.getCell(7).getStringCellValue());
            employee.setPermanentAddress(row.getCell(8).getStringCellValue());
            employee.setCurrentAddress(row.getCell(9).getStringCellValue());
            employee.setFatherName(row.getCell(10).getStringCellValue());
            employee.setMotherName(row.getCell(11).getStringCellValue());
            employee.setAdharNumber(row.getCell(12).getStringCellValue());
            employee.setJobTitle(row.getCell(13).getStringCellValue());
            employee.setBloodGroup(row.getCell(14).getStringCellValue());
            employee.setCaste(row.getCell(15).getStringCellValue());
            employee.setMaritalStatus(row.getCell(16).getStringCellValue());
            employee.setEmployeeType(EmployeeType.PERMANENT_CONTRACT);
            employee.setEmployeeType(EmployeeType.CONTRACT);
            employee.setTemporaryContract(false);
            permanentContractService.save(employee);
            employeeList.add(employee);

        }
        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    @PostMapping("contract-bulk-upload")
    public ResponseEntity<List<EmpPermanentContract>> contractBulkUpload(@RequestParam("file") MultipartFile file) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = wb.getSheetAt(0);
        List<EmpPermanentContract> employeeList = new ArrayList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            EmpPermanentContract employee = new EmpPermanentContract();
            XSSFRow row = sheet.getRow(i);
            employee.setFirstName(row.getCell(1).getStringCellValue());
            employee.setLastName(row.getCell(2).getStringCellValue());
            employee.setMiddleName(row.getCell(3).getStringCellValue());
            employee.setDateOfBirth(row.getCell(4).getDateCellValue());
            employee.setGender(row.getCell(5).getStringCellValue());
            employee.setPhoneNumber(row.getCell(6).getStringCellValue());
            employee.setEmergencyPhoneNumber(row.getCell(7).getStringCellValue());
            employee.setPermanentAddress(row.getCell(8).getStringCellValue());
            employee.setCurrentAddress(row.getCell(9).getStringCellValue());
            employee.setFatherName(row.getCell(10).getStringCellValue());
            employee.setMotherName(row.getCell(11).getStringCellValue());
            employee.setAdharNumber(row.getCell(12).getStringCellValue());
            employee.setJobTitle(row.getCell(13).getStringCellValue());
            employee.setBloodGroup(row.getCell(14).getStringCellValue());
            employee.setCaste(row.getCell(15).getStringCellValue());
            employee.setMaritalStatus(row.getCell(16).getStringCellValue());
            employee.setEmployeeType(EmployeeType.CONTRACT);
            employee.setTemporaryContract(true);
            permanentContractService.save(employee);
            employeeList.add(employee);

        }
        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    /**
     * Check duplicate
     *
     * @param employeeCode
     * @param id
     * @return
     */
    @GetMapping("contract-company/check-employee-code-exists/{employeeCode}/{id}")
    public ResponseEntity<EmpPermanentContract> rfidExists(@PathVariable String employeeCode, @PathVariable long id) {
        EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
        if (empPermanentContract != null) {
            if (empPermanentContract.getId() == id) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
                throw new EntityNotFoundException("Enter another workOrderNumber");
            }
        } else {
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @DeleteMapping("permanent-contract/{id}")
    public void deleteContractEmployee(@PathVariable long id) {
        permanentContractRepo.delete(id);
    }


    /**
     * Template
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping("permanent-contract/template-download")
    private void createPermanentContractDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= permanent_contract.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * Template
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping("contract/template-download")
    private void createContractDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        OutputStream out = null;
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename= contract.xlsx");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            writeToSheet(workbook, response, 0);
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
    }


    private WritableWorkbook writeToSheet(WritableWorkbook workbook, HttpServletResponse response, int index) throws WriteException {
        WritableSheet s = workbook.createSheet("Data Input", index);
        s.getSettings().setPrintGridLines(false);

        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 14);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        headerFormat.setBackground(Colour.GRAY_25);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);
        s.setColumnView(9, 15);
        s.setColumnView(10, 15);
        s.setColumnView(11, 15);
        s.setColumnView(12, 15);
        s.setColumnView(13, 15);
        s.setColumnView(14, 15);
        s.setColumnView(15, 15);
        s.setColumnView(16, 15);
        s.addCell(new Label(0, 0, "#", headerFormat));
        s.addCell(new Label(1, 0, "First Name", headerFormat));
        s.addCell(new Label(2, 0, "Last Name", headerFormat));
        s.addCell(new Label(3, 0, "Middle Name", headerFormat));
        s.addCell(new Label(4, 0, "Date Of Birth", headerFormat));
        s.addCell(new Label(5, 0, "Gender", headerFormat));
        s.addCell(new Label(6, 0, "Phone Number", headerFormat));
        s.addCell(new Label(7, 0, "Emergency Phone Number", headerFormat));
        s.addCell(new Label(8, 0, "Permanent Address", headerFormat));
        s.addCell(new Label(9, 0, "Current Address", headerFormat));
        s.addCell(new Label(10, 0, "Father Name", headerFormat));
        s.addCell(new Label(11, 0, "Mother Name", headerFormat));
        s.addCell(new Label(12, 0, "Adhar Number", headerFormat));
        s.addCell(new Label(13, 0, "Job Title", headerFormat));
        s.addCell(new Label(14, 0, "Blood Group", headerFormat));
        s.addCell(new Label(15, 0, "Caste", headerFormat));
        s.addCell(new Label(16, 0, "Martial Status", headerFormat));
        return workbook;
    }


    @Autowired
    EmployeeController employeeController;

    @PostMapping("contract/add/employee-contract/{imgId}")
    public void saveEmployeeContractor(@RequestBody EmpPermanentContract empPermanentContract, @PathVariable long imgId) {
        Document document = fileStorageService.getDocument(imgId);
        if (document != null) {
            empPermanentContract.setProfilePicDocument(document);
        }
        empPermanentContract.setStatus(true);
        if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)) {
            empPermanentContract.setBelongsTo(true);
        }

        if(empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT)){
            empPermanentContract.setEmployeeType(EmployeeType.CONTRACT);
            empPermanentContract.setTemporaryContract(false);
        }else if(empPermanentContract.getEmployeeType().equals(EmployeeType.CONTRACT)){
            empPermanentContract.setEmployeeType(EmployeeType.CONTRACT);
            empPermanentContract.setTemporaryContract(true);
        }


        permanentContractService.save(empPermanentContract);
//        employeeController.syncContractEmployees();
    }


    /**
     * Gat attendance date wise
     *
     * @param dateDto
     * @return
     */
    @PostMapping("get-checkin-employees-list")
    public ResponseEntity<List<PermanentContractAttendance>> getCheckInEmployee(@RequestBody DateDto dateDto) {
        Date date = new Date();
        List<PermanentContractAttendance> permanentContractAttendanceList = permanentContractAttendanceRepo.findByMarkedOn(dateDto.startDate);
        List<PermanentContractAttendance> permanentContractAttendanceList1 = new ArrayList<>();
        for (PermanentContractAttendance permanentContractAttendance : permanentContractAttendanceList) {
            EmpPermanentContract empPermanentContract = permanentContractService.get(permanentContractAttendance.getEmployeeCode());
            if (empPermanentContract != null) {
                permanentContractAttendance.setEmployeeName(empPermanentContract.getFirstName());
                permanentContractAttendance.setCompanyName(empPermanentContract.getContractCompany());
                permanentContractAttendance.setEmployeeCode(permanentContractAttendance.getEmployeeCode());
            }
            if (empPermanentContract == null) {
                EmpPermanentContract empPermanentContract1 = permanentContractService.get(permanentContractAttendance.getEmpId());
                if (empPermanentContract1 != null) {
                    permanentContractAttendance.setEmployeeName(empPermanentContract1.getFirstName());
                    permanentContractAttendance.setCompanyName(empPermanentContract1.getContractCompany());
                    permanentContractAttendance.setEmployeeCode(permanentContractAttendance.getEmployeeCode());
                }
            }
            permanentContractAttendanceList1.add(permanentContractAttendance);
        }
        return new ResponseEntity<>(permanentContractAttendanceList1, HttpStatus.OK);

    }

    @PostMapping("contract/mark-out-attendance/{id}")
    public PermanentContractAttendance markAttendanceThroughGeoLocation(@RequestBody long id) throws Exception {
        Date today = new Date();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
        PermanentContractAttendance employeeAttendance = permanentContractAttendanceRepo.findOne(id);
        if (employeeAttendance == null) {
            throw new Exception("Mark In First");
        }
        if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
            employeeAttendance.setOutTime(today);
            employeeAttendance.setRecordedTime(today);
            //TODO for dubai
//            calculateTotalStayTime(employeeAttendance);
            permanentContractAttendanceRepo.save(employeeAttendance);
        } else {
            throw new Exception("Out Time Already Marked");
        }
        return employeeAttendance;
    }


    @GetMapping("employee-contract-attendance/dashboard/present-absent")
    public ResponseEntity<EmployeeAttendanceDto> getContractPresentAndAbsent() {
        EmployeeAttendanceDto dto = new EmployeeAttendanceDto();

        List<PermanentContractAttendance> employeeAttendanceList = permanentContractService.getTodayMarkedEmployeeAttendance(DateUtil.getTodayDate());
        if (employeeAttendanceList == null) {
            throw new EntityNotFoundException("EmployeeAttendance not found");
        }
        System.out.println(employeeAttendanceList);
        long permanentPresent = 0;
        long permanentAbsent = 0;
        long contractPresent = 0;
        long contractAbsent = 0;
        for (PermanentContractAttendance attendance : employeeAttendanceList) {
            if (attendance.getEmpPermanentContract().getEmployeeType().toString().equalsIgnoreCase("PERMANENT")) {
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
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("employee-contract-attendance/dashboard/employee-by-department")
    public ResponseEntity<List<DepartmentDashboard>> getEmployeeContractDepartmentwise() {
        List<DepartmentDashboard> departmentList = new ArrayList<>();
        List<Department> departments = departmentService.findAll();
        for (Department department : departments) {
            List<EmpPermanentContract> employeeAttendanceList = permanentContractService.getEmployeesByDepartment(department.getId());
            DepartmentDashboard departmentDashboard = new DepartmentDashboard();
            departmentDashboard.setDeprtmentName(department.getName());
            departmentDashboard.setNoOfEmployee(employeeAttendanceList.size());
            departmentDashboard.setDepartmentId(department.getId());
            departmentList.add(departmentDashboard);
        }
        System.out.println(departmentList);
        return new ResponseEntity(departmentList, HttpStatus.OK);
    }

    @GetMapping("employee-contract-attendance/dashboard/present-absent-department/{departmentId}")
    public ResponseEntity<DepartmentDashboard> getContractPresentAndAbsentDepartmentwise(@PathVariable long departmentId) {
        DepartmentDashboard dto = new DepartmentDashboard();

        List<PermanentContractAttendance> employeeAttendanceList = permanentContractService.getEmployeeAttendanceByDepartment(DateUtil.getTodayDate(), departmentId);
        if (employeeAttendanceList == null) {
            throw new EntityNotFoundException("EmployeeAttendance not found");
        }
        long permanentPresent = 0;
        long permanentAbsent = 0;
        long contractPresent = 0;
        long contractAbsent = 0;
        for (PermanentContractAttendance attendance : employeeAttendanceList) {
            if (attendance.getEmpPermanentContract().getEmployeeType().toString().equalsIgnoreCase("PERMANENT")) {
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

    @GetMapping("contract/expired-employees")
    public ResponseEntity<List<EmpPermanentContract>> getExpiredList(){
        List<EmpPermanentContract> empPermanentContracts = permanentContractService.getAll(true);
        List<EmpPermanentContract> empPermanentContractList = new ArrayList<>();
        for (EmpPermanentContract empPermanentContract : empPermanentContracts){
            EmployeePass employeePass = employeePassRepo.findByEmpId(empPermanentContract.getId());
            if (employeePass == null) {
                empPermanentContract.setExpiryStatus("Pass not generated");
                empPermanentContractList.add(empPermanentContract);
            } else if (employeePass.getEndDate().after(new Date())) {
                empPermanentContract.setExpiryStatus("Pass expired");
                empPermanentContractList.add(empPermanentContract);
            }else {
                long dates = DateUtil.getDifferenceDays(new Date(),employeePass.getEndDate());
                if(dates<30){
                    empPermanentContract.setExpiryStatus("Pass expiring soon..");
                    empPermanentContractList.add(empPermanentContract);
                }
            }

        }
        return new ResponseEntity<>(empPermanentContractList,HttpStatus.OK);
    }


}

