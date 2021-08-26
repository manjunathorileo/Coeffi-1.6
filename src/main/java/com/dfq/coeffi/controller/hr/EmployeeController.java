package com.dfq.coeffi.controller.hr;

import com.dfq.coeffi.Gate.Entity.EmployeeGateAssignment;
import com.dfq.coeffi.Gate.Entity.Gate;
import com.dfq.coeffi.Gate.Service.EmployeeGateAssignmentService;
import com.dfq.coeffi.Gate.Service.GateService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.LeaveSchedular;
import com.dfq.coeffi.controller.leave.leavebalance.OpeningLeave;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRule;
import com.dfq.coeffi.controller.leave.leavebalance.earningleaverule.EarningLeaveRuleService;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalance;
import com.dfq.coeffi.controller.leave.leavebalance.employeeleavebalance.EmployeeLeaveBalanceService;
import com.dfq.coeffi.dto.*;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.*;
import com.dfq.coeffi.entity.hr.employee.*;
import com.dfq.coeffi.entity.leave.LeaveType;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.EmployeeCTCData;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import com.dfq.coeffi.entity.user.Role;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.policy.document.Document;
import com.dfq.coeffi.policy.document.FileStorageService;
import com.dfq.coeffi.repository.leave.AvailLeaveRepo;
import com.dfq.coeffi.repository.leave.ClosingLeaveRepo;
import com.dfq.coeffi.repository.leave.OpeningLeaveRepo;
import com.dfq.coeffi.repository.payroll.EmployeeCtcDataRepo;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.hr.DepartmentService;
import com.dfq.coeffi.service.hr.DesignationService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.hr.QualificationService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryService;
import com.dfq.coeffi.service.payroll.PayHeadContractService;
import com.dfq.coeffi.service.payroll.PayHeadService;
import com.dfq.coeffi.superadmin.Entity.CompanyConfigure;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import com.dfq.coeffi.visitor.Services.VisitorPassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * @author Ashvini B
 */

@RestController
@Slf4j
public class EmployeeController extends BaseController {
    @Autowired
    AvailLeaveRepo availLeaveRepo;
    @Autowired
    ClosingLeaveRepo closingLeaveRepo;
    @Autowired
    OpeningLeaveRepo openingLeaveRepo;
    @Autowired
    PermanentContractService permanentContractService;
    @Autowired
    CompanyConfigureService companyConfigureService;
    @Autowired
    GateService gateService;
    @Autowired
    EmployeeGateAssignmentService employeeGateAssignmentService;
    @Autowired
    VisitorPassService visitorPassService;

    private final EmployeeService employeeService;
    private final UserService userService;
    private final EmployeeSalaryService employeeSalaryService;
    private final DepartmentService departmentService;
    private final DesignationService designationService;
    private final PayHeadService payHeadService;
    private final PayHeadContractService payHeadNonTeachingService;
    private final FileStorageService fileStorageService;
    private final LeaveSchedular leaveSchedular;
    private final QualificationService qualificationService;
    private final EarningLeaveRuleService earningLeaveRuleService;
    private final EmployeeLeaveBalanceService employeeLeaveBalanceService;
    private final AcademicYearService academicYearService;

    @Autowired
    EmployeeCtcDataRepo employeeCtcDataRepo;
    @Autowired
    DepartmentTrackerRepository departmentTrackerRepository;


    public EmployeeController(EmployeeService employeeService, UserService userService,
                              EmployeeSalaryService employeeSalaryService,
                              DepartmentService departmentService, DesignationService designationService,
                              PayHeadService payHeadService, PayHeadContractService payHeadNonTeachingService,
                              FileStorageService fileStorageService,
                              LeaveSchedular leaveSchedular,
                              QualificationService qualificationService, EarningLeaveRuleService earningLeaveRuleService, EmployeeLeaveBalanceService employeeLeaveBalanceService, AcademicYearService academicYearService) {

        this.employeeService = employeeService;
        this.userService = userService;
        this.employeeSalaryService = employeeSalaryService;
        this.departmentService = departmentService;
        this.designationService = designationService;
        this.payHeadService = payHeadService;
        this.payHeadNonTeachingService = payHeadNonTeachingService;
        this.fileStorageService = fileStorageService;
        this.leaveSchedular = leaveSchedular;
        this.qualificationService = qualificationService;
        this.earningLeaveRuleService = earningLeaveRuleService;
        this.employeeLeaveBalanceService = employeeLeaveBalanceService;
        this.academicYearService = academicYearService;
    }

    /* POST /employee : Create a new employee */
    @PostMapping("employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        List<Qualification> qualification = employee.getQualification();
        List<PreviousEmployement> previousEmployement = employee.getPreviousEmployement();
        List<EmployeeAddress> employeeAddress = employee.getEmployeeAddress();
        List<FamilyMember> employeeFamilyMember = employee.getFamilyMember();
        List<EmployeeCertification> employeeCertifications = employee.getEmployeeCertifications();

        EmpPermanentContract empPermanentContractObj = null;
        if (qualification != null && qualification.size() > 0) {
            for (int i = 0; i < qualification.size(); i++) {
                qualification.get(i).setEmployee(employee);
            }
        }
        if (previousEmployement != null && previousEmployement.size() > 0) {
            for (int i = 0; i < previousEmployement.size(); i++) {
                previousEmployement.get(i).setEmployee(employee);
            }
        }
        if (employeeAddress != null && employeeAddress.size() > 0) {
            for (int i = 0; i < employeeAddress.size(); i++) {
                employeeAddress.get(i).setEmployee(employee);
            }
        }
        if (employeeFamilyMember != null && employeeFamilyMember.size() > 0) {
            for (int i = 0; i < employeeFamilyMember.size(); i++) {
                employeeFamilyMember.get(i).setEmployee(employee);
            }
        }

        if (employeeCertifications != null && employeeCertifications.size() > 0) {
            for (int i = 0; i < employeeCertifications.size(); i++) {
                employeeCertifications.get(i).setEmployee(employee);
            }
        }

        employeeService.isAadhaarExists(employee.getAdharNumber());
        employee.setQualification(qualification);
        employee.setPreviousEmployement(previousEmployement);
        employee.setEmployeeAddress(employeeAddress);
        employee.setFamilyMember(employeeFamilyMember);
        employee.setReferenceId(employee.getEmployeeCode());
        employee.setEpf(true);
        employee.setRfid(employee.getRfid());
        CompanyConfigure companyConfigure = companyConfigureService.getCompanyById(1);
        EmployeeType empAdminType = null;
        if (companyConfigure.isContract()) {
            empAdminType = EmployeeType.CONTRACT;
        } else if (companyConfigure.isPermanentContract()) {
            empAdminType = EmployeeType.PERMANENT_CONTRACT;
        }
        if (empAdminType == null) {
            throw new EntityNotFoundException("Super-Admin configuration pending");
        }
        System.out.println("EMPTYPE: " + empAdminType + ":" + employee.getEmployeeType());
        if (!employee.getEmployeeType().equals(empAdminType)) {
            employee.setUploadFor(1);
            employeeService.save(employee);
            if (employee != null) {
                allocateLeave(employee);

//                if (companyConfigure.getGateAssignEmployeeType() != null && companyConfigure.getInGateIds() != null && companyConfigure.getOutGateIds() != null) {
//                    assigndefaultGatesForNewEmployees(employee, companyConfigure.getInGateIds(), companyConfigure.getOutGateIds());
//                }
            }
        }
        if (employee.getEmployeeType().equals(empAdminType)) {
            EmpPermanentContract employee1 = new EmpPermanentContract();
            employee1.setEmployeeCode(employee.getEmployeeCode());
            employee1.setFirstName(employee.getFirstName());
            employee1.setMiddleName(employee.getMiddleName());
            employee1.setLastName(employee.getLastName());
            employee1.setEmployeeType(employee.getEmployeeType());
            employee1.setDateOfJoining(employee.getDateOfJoining());
            employee1.setDateOfLeaving(employee.getDateOfLeaving());
            employee1.setJobTitle(employee.getJobTitle());
            employee1.setDateOfBirth(employee.getDateOfBirth());
            employee1.setAge(employee.getAge());
            employee1.setBloodGroup(employee.getBloodGroup());
            employee1.setAdharNumber(employee.getAdharNumber());
            employee1.setPhoneNumber(employee.getPhoneNumber());
            employee1.setEmergencyPhoneNumber(employee.getEmergencyPhoneNumber());
            employee1.setGender(employee.getGender());
            employee1.setReligion(employee.getReligion());
            employee1.setCaste(employee.getCaste());
            employee1.setSubCaste(employee.getSubCaste());
            employee1.setMaritalStatus(employee.getMaritalStatus());
            employee1.setFatherName(employee.getFatherName());
            employee1.setMotherName(employee.getMotherName());
            employee1.setWifeName(employee.getWifeName());
            employee1.setPanNumber(employee.getPanNumber());
            employee1.setPermanentAddress(employee.getPermanentAddress());
            employee1.setCurrentAddress(employee.getCurrentAddress());
            employee1.setProfilePicDocument(employee.getProfilePicDocument());
            employee1.setFamilyPicDocument(employee.getFamilyPicDocument());
            employee1.setSafetyVestColour(employee.getSafetyVestColour());
            employee1.setContractCompany(employee.getContractCompany());
            employee1.setIdentificationMarks(employee.getIdentificationMarks());
            employee1.setRole(employee.getRole());
            employee1.setVehicleDetails(employee.getVehicleDetails());
            employee1.setDepartmentName(employee.getDepartmentName());
            employee1.setReportingManager(employee.getReportingManager());
            employee1.setSiteId(employee.getSiteId());
            employee1.setAccessId(employee.getRfid());

            Document document = fileStorageService.getDocument(employee.getProfilePicId());
            Document famdocument = fileStorageService.getDocument(employee.getFamilyPicId());
            if (document != null) {
                employee1.setProfilePicDocument(document);
            }
            if (famdocument != null) {
                employee1.setProfilePicDocument(famdocument);
            }

            if (companyConfigure.isSyncWithAllModules()) {
                employee.setUploadFor(2);
                employee = employeeService.save(employee);
                permanentContractService.save(employee1);
            } else {
                employee.setUploadFor(3);
                empPermanentContractObj = permanentContractService.save(employee1);
            }
        }
        if (empPermanentContractObj != null) {
            employee.setId(empPermanentContractObj.getId());
        }

        return new ResponseEntity(employee, HttpStatus.OK);
    }


    public ResponseEntity<EmployeeGateAssignment> assigndefaultGatesForNewEmployees(Employee employee, List<Long> inGateIds, List<Long> outGateIds) {
        EmployeeGateAssignment employeeGateAssignment = new EmployeeGateAssignment();
        List<Gate> inGates = new ArrayList<>();
        String ingatesRef = "";
        String outgatesRef = "";
        for (long gateId : inGateIds) {
            Gate gateIn = gateService.getGateById(gateId);
            inGates.add(gateIn);
            ingatesRef = ingatesRef + "," + gateIn.getGateNumber();
        }

        List<Gate> outGates = new ArrayList<>();
        for (long gateId : outGateIds) {
            Gate gateOut = gateService.getGateById(gateId);
            outGates.add(gateOut);
            outgatesRef = outgatesRef + "," + gateOut.getGateNumber();
        }
        employeeGateAssignment.setEmployee(employee);
        employeeGateAssignment.setInGates(inGates);
        employeeGateAssignment.setOutGates(outGates);
        employeeGateAssignment.setInGateNumbersList(ingatesRef);
        employeeGateAssignment.setOutGateNumbersList(outgatesRef);
        employeeGateAssignmentService.saveEmployeeGate(employeeGateAssignment);
        return new ResponseEntity<>(employeeGateAssignment, HttpStatus.OK);
    }

    @PostMapping("employee/{id}")
    public ResponseEntity<Employee> update(@PathVariable long id, @Valid @RequestBody Employee employee) {

        Optional<Employee> persistedEmployee = employeeService.getEmployee(id);
        if (!persistedEmployee.isPresent()) {
            throw new EntityNotFoundException(Employee.class.getSimpleName());
        }

        List<Qualification> qualification = employee.getQualification();
        List<Qualification> output = new ArrayList<>();
        for (int i = 0; i < qualification.size(); i++) {
            System.out.println(qualification.get(i).getPlaceOfGraduation());
            qualification.get(i).setEmployee(employee);
            output.add(qualification.get(i));
        }
        List<PreviousEmployement> previousEmployement = employee.getPreviousEmployement();
        List<PreviousEmployement> previousEmployementoutput = new ArrayList<>();
        for (int i = 0; i < previousEmployement.size(); i++) {
            previousEmployement.get(i).setEmployee(employee);
            previousEmployementoutput.add(previousEmployement.get(i));
        }
        List<EmployeeAddress> employeeAddress = employee.getEmployeeAddress();
//        List<EmployeeAddress> employeeAddressoutput = new ArrayList<>();
//        for (int i=0; i<employeeAddress.size(); i++) {
//            employeeAddress.get(i).setEmployee(employee);
//            employeeAddressoutput.add(employeeAddress.get(i));
//        }
        List<FamilyMember> employeeFamilyMember = employee.getFamilyMember();
        List<FamilyMember> employeeFamilyMemberoutput = new ArrayList<>();
        for (int i = 0; i < employeeFamilyMember.size(); i++) {
            employeeFamilyMember.get(i).setEmployee(employee);
            employeeFamilyMemberoutput.add(employeeFamilyMember.get(i));
        }
        List<EmployeeCertification> employeeCertifications = employee.getEmployeeCertifications();
        List<EmployeeCertification> employeeCertificationsoutput = new ArrayList<>();
        for (int i = 0; i < employeeCertifications.size(); i++) {
            employeeCertifications.get(i).setEmployee(employee);
            employeeCertificationsoutput.add(employeeCertifications.get(i));
        }

        /* Login,Manager,Department,Designation,Bank,CTC,KYC Object attachements */
        Employee updatedEmployee = persistedEmployee.get();
        employee.setEmployeeLogin(updatedEmployee.getEmployeeLogin());
        employee.setFirstApprovalManager(updatedEmployee.getFirstApprovalManager());
        employee.setSecondApprovalManager(updatedEmployee.getSecondApprovalManager());
        employee.setDepartment(updatedEmployee.getDepartment());
        employee.setDesignation(updatedEmployee.getDesignation());
        employee.setEmployeeBank(updatedEmployee.getEmployeeBank());
        employee.setEmployeeCTCData(updatedEmployee.getEmployeeCTCData());
        employee.setDocuments(updatedEmployee.getDocuments());

        if (output != null && output.size() > 0) {
            for (int i = 0; i < output.size(); i++) {
                System.out.println("*************** Print data ********************");
                output.get(i).setEmployee(employee);
            }
        }
        if (previousEmployementoutput != null && previousEmployementoutput.size() > 0) {
            for (int i = 0; i < previousEmployementoutput.size(); i++) {
                previousEmployementoutput.get(i).setEmployee(employee);
            }
        }
        if (employeeAddress != null && employeeAddress.size() > 0) {
            for (int i = 0; i < employeeAddress.size(); i++) {
                employeeAddress.get(i).setEmployee(employee);
            }
        }
        if (employeeFamilyMemberoutput != null && employeeFamilyMemberoutput.size() > 0) {
            for (int i = 0; i < employeeFamilyMemberoutput.size(); i++) {
                employeeFamilyMemberoutput.get(i).setEmployee(employee);
            }
        }

        if (employeeCertificationsoutput != null && employeeCertificationsoutput.size() > 0) {
            for (int i = 0; i < employeeCertificationsoutput.size(); i++) {
                employeeCertificationsoutput.get(i).setEmployee(employee);
            }
        }
        employeeService.isAadhaarExists(employee.getAdharNumber());
//        employee.setQualification(qualification);
//        employee.setPreviousEmployement(previousEmployement);
//        employee.setEmployeeAddress(employeeAddress);
//        employee.setFamilyMember(employeeFamilyMember);
        employee.setId(id);
        employee.setReferenceId(employee.getEmployeeCode());
       /* if (employee != null) {
            leaveSchedular.checkForLeaveRules();
        }*/
        employee.setUploadFor(1);
        employeeService.save(employee);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    /* GET /employee : Getting a List of employee */
    @GetMapping("employee")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employeeData = employeeService.findAll();
        List<Employee> employeeList = new ArrayList<>();
        for (Employee e : employeeData) {
            Employee emp = new Employee();
            emp.setId(e.getId());
            emp.setFirstName(e.getFirstName());
            emp.setLastName(e.getLastName());
            emp.setEmployeeCode(e.getEmployeeCode());
            emp.setEmployeeAddress(e.getEmployeeAddress());
            emp.setFatherName(e.getFatherName());
            emp.setMotherName(e.getMotherName());
            emp.setGender(e.getGender());
            emp.setMaritalStatus(e.getMaritalStatus());
            emp.setDateOfMarriage(e.getDateOfMarriage());
            emp.setDateOfBirth(e.getDateOfBirth());
            emp.setAge(e.getAge());
            emp.setPhoneNumber(e.getPhoneNumber());
            emp.setEmergencyPhoneNumber(e.getEmergencyPhoneNumber());
            emp.setBloodGroup(e.getBloodGroup());
            emp.setReligion(e.getReligion());
            emp.setCaste(e.getCaste());
            emp.setAdharNumber(e.getAdharNumber());
            emp.setFamilyDependents(e.getFamilyDependents());
            emp.setCurrentAddress(e.getCurrentAddress());
            emp.setPermanentAddress(e.getPermanentAddress());
            emp.setDateOfJoining(e.getDateOfJoining());
            emp.setProbationaryPeriod(e.getProbationaryPeriod());
            emp.setPfNumber(e.getPfNumber());
            emp.setUanNumber(e.getUanNumber());
            emp.setPanNumber(e.getPanNumber());
            emp.setEsiNumber(e.getEsiNumber());
            emp.setLevel(e.getLevel());
            emp.setWifeName(e.getWifeName());
            emp.setOfferedSalary(e.getOfferedSalary());
            emp.setFamilyMember(e.getFamilyMember());
            emp.setQualification(e.getQualification());
            emp.setPreviousEmployement(e.getPreviousEmployement());
            emp.setEmployeeCertifications(e.getEmployeeCertifications());
            emp.setEmployeeType(e.getEmployeeType());
            emp.setDepartment(e.getDepartment());
            emp.setDesignation(e.getDesignation());
            emp.setDepartmentTrackerList(e.getDepartmentTrackerList());
            emp.setOtRequired(e.isOtRequired());
            emp.setDesignationStartDate(e.getDesignationStartDate());
            emp.setDesignationEndDate(e.getDesignationEndDate());
            emp.setFirstWeekOff(e.getFirstWeekOff());
            emp.setSecondWeekOff(e.getSecondWeekOff());
            emp.setFirstWeekOffName(e.getFirstWeekOffName());
            emp.setSecondWeekOffName(e.getSecondWeekOffName());
            emp.setRfid(e.getRfid());
            emp.setReleaved(e.isReleaved());
            if (e.getDepartmentName() != null) {
                Department department = new Department();
                department.setName(e.getDepartmentName());
                emp.setDepartment(department);
            }
            employeeList.add(emp);
        }
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("employeeData");
        }
        return new ResponseEntity<List<Employee>>(employeeList, HttpStatus.OK);
    }

    /* GET /employee : Getting a List of employee // Light weight api*/
    @GetMapping("employee/load-employees")
    public ResponseEntity<List<Employee>> loadEmployeeLightWeight() {
        List<Employee> employeeData = employeeService.getEmployeeLightWeight();
        if (CollectionUtils.isEmpty(employeeData)) {
//            throw new EntityNotFoundException("employeeData");
        }
        return new ResponseEntity<List<Employee>>(employeeData, HttpStatus.OK);
    }

    @GetMapping("employee/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        if (!employee.isPresent()) {
            throw new EntityNotFoundException(Employee.class.getName());
        }
        return new ResponseEntity<Employee>(employee.get(), HttpStatus.OK);
    }

    @GetMapping("employee/bylogin/{id}")
    public ResponseEntity<Employee> getEmployeeByLoginId(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeByLogin(id);
        Employee employee1 = new Employee();
        employee1.setId(employee.get().getId());
        employee1.setFirstName(employee.get().getFirstName());
        employee1.setLastName(employee.get().getLastName());
        if (!employee.isPresent()) {
            throw new EntityNotFoundException(Employee.class.getName());
        }
        return new ResponseEntity<Employee>(employee.get(), HttpStatus.OK);
    }

    @DeleteMapping("employee/{id}")
    public ResponseEntity<Employee> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployee(id);


        Optional<EmployeeSalary> employeeSalary = employeeSalaryService.getEmployeeSalaryByEmployeeId(id);
        if (employeeSalary.isPresent()) {
            employeeSalary.get().setApprove(false);
        }

        if (!employee.isPresent()) {
            throw new EntityNotFoundException(Employee.class.getName());
        }
        if (id > 0) {
            employeeService.delete(id);
            employee.get().setDateOfLeaving(DateUtil.getTodayDate());
            employeeService.save(employee.get());
            log.info("Employee deactivated");
            User employeeLogin = employee.get().getEmployeeLogin();
            if (employeeLogin != null) {
                employeeLogin.setActive(false);
                userService.saveUser(employeeLogin);
                log.info("Employee login access deactivated");
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    /* GET /employee : Getting the employee details through refName and refNumber */
    @GetMapping("employee/{refName}/{refNumber}")
    public ResponseEntity<List<Employee>> getEmployeeByRefNameRefNumber(@RequestParam("refName") String refName,
                                                                        @RequestParam("refNumber") Integer refNumber) {
        List<Employee> result = employeeService.getEmployeeByRefNameRefNumber(refName, refNumber);
        return ResponseEntity.ok().body(result);
    }

    /* POST /employee/qualification : Create qualification through employee id */
    @PostMapping("employee/qualification/{id}")
    public ResponseEntity<Employee> createQualification(@RequestBody Qualification qualification, @PathVariable Long id,
                                                        UriComponentsBuilder ucBuilder) throws URISyntaxException {
        Employee employee = employeeService.findOne(id);
        qualification.setEmployee(employee);
        employee.getQualification().add(qualification);

        Employee persisted = employeeService.save(employee);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/employee/qualification/{id}").buildAndExpand(1).toUri());

        return new ResponseEntity<>(persisted, headers, HttpStatus.OK);
    }

    /*
     * DELETE /employee/qualification : Delete the qualification through employee id
     */
    @DeleteMapping("employee/qualification/{id}/{qId}")
    public void deleteQualification(@PathVariable Long id, @PathVariable Long qId) {
        Employee employee = employeeService.findOne(id);

        List<Qualification> qualifications = employee.getQualification();
        if (qualifications != null) {
            for (int i = 0; i <= qualifications.size(); i++) {

                if (qualifications.get(i).getId() == qId) {
                    qualifications.remove(i);
                }
            }
            employee.setQualification(qualifications);
            employeeService.save(employee);
        }
    }

    /*
     * POST /employee/previousEmployement : Create PreviousEmployement through
     * employee id
     */
    @PostMapping("employee/previousEmployement/{id}")
    public ResponseEntity<Employee> createPreviousEmployement(@RequestBody PreviousEmployement previousEmployement,
                                                              @PathVariable Long id, UriComponentsBuilder ucBuilder) throws URISyntaxException {
        Employee employee = employeeService.findOne(id);

        previousEmployement.setEmployee(employee);
        employee.getPreviousEmployement().add(previousEmployement);
        Employee persisted = employeeService.save(employee);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/employee/previousEmployement/{id}").buildAndExpand(1).toUri());

        return new ResponseEntity<>(persisted, headers, HttpStatus.OK);
    }

    /*
     * DELETE /employee/previousEmployement : Delete the previousEmployement through
     * employee id
     */
    @DeleteMapping("employee/previousEmployement/{id}/{pId}")
    public void deletePreviousEmployement(@PathVariable Long id, @PathVariable Long pId) {
        Employee employee = employeeService.findOne(id);

        List<PreviousEmployement> previousEmployements = employee.getPreviousEmployement();
        if (previousEmployements != null) {
            for (int i = 0; i <= previousEmployements.size(); i++) {

                if (previousEmployements.get(i).getId() == pId) {
                    previousEmployements.remove(i);
                }
            }
            employee.setPreviousEmployement(previousEmployements);
            employeeService.save(employee);
        }
    }

    /*
     * POST /employee/employeeAddress : Create EmployeeAddress through employee id
     */
    @PostMapping("employee/employeeAddress/{id}")
    public ResponseEntity<Employee> createEmployeeAddress(@RequestBody EmployeeAddress employeeAddress,
                                                          @PathVariable Long id, UriComponentsBuilder ucBuilder) throws URISyntaxException {
        Employee employee = employeeService.findOne(id);
        employeeAddress.setEmployee(employee);
        employee.getEmployeeAddress().add(employeeAddress);
        Employee persisted = employeeService.save(employee);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/employee/previousEmployement/{id}").buildAndExpand(1).toUri());

        return new ResponseEntity<>(persisted, headers, HttpStatus.OK);
    }

    /*
     * DELETE /employee/employeeAddress : Delete the EmployeeAddress through
     * employee id
     */
    @DeleteMapping("employee/employeeAddress/{id}/{eId}")
    public void deleteEmployeeAddress(@PathVariable Long id, @PathVariable Long eId) {
        Employee employee = employeeService.findOne(id);
        List<EmployeeAddress> employeeAddress = employee.getEmployeeAddress();
        if (employeeAddress != null) {
            for (int i = 0; i <= employeeAddress.size(); i++) {

                if (employeeAddress.get(i).getId() == eId) {
                    employeeAddress.remove(i);
                }
            }
            employee.setEmployeeAddress(employeeAddress);
            employeeService.save(employee);
        }
    }

    //private static String UPLOAD_FOLDER = "/home/kapil/kd/project/EDairy2.0/edairy2-ui/src/assets/img/";
    //private static String UPLOAD_FOLDER = "/home/orileo/Desktop/Orileo/build/ ";

    @PostMapping(value = "employee/upload-profile/{id}/{uploadId}")
    public ResponseEntity<Employee> uploadProfilePicture(@PathVariable("id") String id, @PathVariable long uploadId, @RequestParam("file") MultipartFile file) throws IOException {
        Employee persistedEmployee = null;
        if (file.isEmpty()) {
            throw new FileNotFoundException();
        } else {
            if (uploadId == 1) {
                Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(id);
                Document document = fileStorageService.storeFile(file);
                employee.get().setProfilePicDocument(document);
                persistedEmployee = employeeService.save(employee.get());
            } else if (uploadId == 3) {
                EmpPermanentContract empPermanentContract = permanentContractService.get(id);
                Document document = fileStorageService.storeFile(file);
                empPermanentContract.setProfilePicDocument(document);
                permanentContractService.save(empPermanentContract);
            } else if (uploadId == 2) {
                Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(id);
                Document document = fileStorageService.storeFile(file);
                employee.get().setProfilePicDocument(document);
                persistedEmployee = employeeService.save(employee.get());
                EmpPermanentContract empPermanentContract = permanentContractService.get(id);
//                Document document1 = fileStorageService.storeFile(file);
                empPermanentContract.setProfilePicDocument(document);
                permanentContractService.save(empPermanentContract);
            }
        }
        return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
    }

    /* POST /employee : Set login details for employee login*/
    @PostMapping("employee/create-login")
    public ResponseEntity<Employee> createLogin(@Valid @RequestBody EmployeeLoginDto dto) {

        Employee persistedEmployee = null;
        if (dto != null) {
            Optional<Employee> employeeObj = employeeService.getEmployee(dto.getEmpId());
            if (!employeeObj.isPresent()) {
                throw new EntityNotFoundException("empolyee" + dto.getEmpId());
            } else {
                Employee employee = employeeObj.get();
                User user = userService.getUser(dto.getUserId());
                if (user != null) {
                    user.setActive(true);
                    user.setEmail(dto.getEmail());
                    user.setPassword(dto.getPassword());
                    user.setFirstName(employee.getFirstName());
                    user.setLicenceStartDate(DateUtil.getTodayDate());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar c = Calendar.getInstance();
                    c.setTime(DateUtil.getTodayDate()); // Now use today date.
                    c.add(Calendar.YEAR, 1); // Adding 1 year
                    String output = sdf.format(c.getTime());
                    Date inputDate = DateUtil.convertToDate(output);
                    user.setLicenceEndDate(inputDate);
                    ArrayList<Role> roles = new ArrayList<>();
                    roles.add(userService.getRole(dto.getRoleId()));
                    user.setRoles(roles);
                    userService.saveUser(user);
                    employee.setEmployeeLogin(user);
                    persistedEmployee = employeeService.merge(employee);
                } else {
                    User newuser = new User();
                    newuser.setActive(true);
                    newuser.setEmail(dto.getEmail());
                    newuser.setPassword(dto.getPassword());
                    newuser.setFirstName(employee.getFirstName());
                    newuser.setLicenceStartDate(DateUtil.getTodayDate());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar c = Calendar.getInstance();
                    c.setTime(DateUtil.getTodayDate()); // Now use today date.
                    c.add(Calendar.YEAR, 1); // Adding 1 year
                    String output = sdf.format(c.getTime());
                    Date inputDate = DateUtil.convertToDate(output);
                    newuser.setLicenceEndDate(inputDate);
                    newuser.setRoles(Arrays.asList(userService.getRole(dto.getRoleId())));
                    userService.isUserExists(newuser.getEmail());
                    userService.saveUser(newuser);
                    employee.setEmployeeLogin(newuser);
                    persistedEmployee = employeeService.merge(employee);
                }

            }

            return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }


    @PostMapping("employee/create-login-admins")
    public ResponseEntity<User> createLoginForAdmins(@Valid @RequestBody EmployeeLoginDto dto) {

        if (dto != null) {
            User user = userService.getUser(dto.getUserId());
            if (user != null) {
                user.setActive(true);
                user.setEmail(dto.getEmail());
                user.setPassword(dto.getPassword());
                user.setFirstName(dto.getFirstName());
                user.setLicenceStartDate(DateUtil.getTodayDate());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(DateUtil.getTodayDate()); // Now use today date.
                c.add(Calendar.YEAR, 1); // Adding 1 year
                String output = sdf.format(c.getTime());
                Date inputDate = DateUtil.convertToDate(output);
                user.setLicenceEndDate(inputDate);
                ArrayList<Role> roles = new ArrayList<>();
                roles.add(userService.getRole(dto.getRoleId()));
                user.setRoles(roles);
                userService.saveUser(user);
            } else {
                User newuser = new User();
                newuser.setActive(true);
                newuser.setEmail(dto.getEmail());
                newuser.setPassword(dto.getPassword());
                newuser.setFirstName(dto.getFirstName());
                newuser.setLicenceStartDate(DateUtil.getTodayDate());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(DateUtil.getTodayDate()); // Now use today date.
                c.add(Calendar.YEAR, 1); // Adding 1 year
                String output = sdf.format(c.getTime());
                Date inputDate = DateUtil.convertToDate(output);
                newuser.setLicenceEndDate(inputDate);
                newuser.setRoles(Arrays.asList(userService.getRole(dto.getRoleId())));
                userService.isUserExists(newuser.getEmail());
                userService.saveUser(newuser);
            }
            return new ResponseEntity(user, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("employee/employee-bank/{id}")
    public ResponseEntity<Employee> getEmployeeBankDetail(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeBankDetails(id);
        if (!employee.isPresent()) {
            throw new EntityNotFoundException(Employee.class.getName());
        }
        return new ResponseEntity<Employee>(employee.get(), HttpStatus.OK);
    }

    /* POST /employee : Set Employee Bank details*/
    @PostMapping("employee/create-bank")
    public ResponseEntity<Employee> updateEmployeeBank(@Valid @RequestBody EmployeeBankDto dto) {

        Employee persistedEmployee = null;
        if (dto != null) {
            Optional<Employee> employeeObj = employeeService.getEmployee(dto.getEmployeeId());
            if (!employeeObj.isPresent()) {
                throw new EntityNotFoundException("empolyee" + dto.getEmployeeId());
            } else {
                Employee employee = employeeObj.get();
                EmployeeBank employeeBank = new EmployeeBank();
                employeeBank.setBankName(dto.getBankName());
                employeeBank.setBranch(dto.getBranch());
                employeeBank.setAccountNumber(dto.getAccountNumber());
                employeeBank.setAccountName(dto.getAccountName());
                employeeBank.setAccountType(dto.getAccountType());
                employeeBank.setIfscCode(dto.getIfscCode());

                employee.setEmployeeBank(employeeBank);
                persistedEmployee = employeeService.save(employee);
            }
            return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

//    @GetMapping(value = "employee/assign-department-designation/{employeeId}/{departmentId}/{designationId}")
//    public ResponseEntity<Employee> assignDepartmentAndDesignation(@PathVariable("employeeId") Long employeeId, @PathVariable("departmentId") Long departmentId, @PathVariable("designationId") Long designationId) {
//
//        Employee employeeData = null;
//
//        Optional<Department> department = departmentService.getDepartment(departmentId);
//        Optional<Designation> designation = designationService.getDesignation(designationId);
//
//        Optional<Employee> persistedEmployee = employeeService.getEmployee(employeeId);
//        Employee employee = persistedEmployee.get();
//        employee.setDepartment(department.get());
//        employee.setDesignation(designation.get());
//        employee.setDepartmentName(department.get().getName());
//
//        employeeData = employeeService.save(employee);
//
//        return new ResponseEntity<>(employeeData, HttpStatus.OK);
//    }

    //--------ARUN-----------
    @PostMapping(value = "employee/assign-department-designation/{employeeId}/{departmentId}/{designationId}")
    public ResponseEntity<Employee> assignDepartmentAndDesignation(@RequestBody DepartmentTrackerDto departmentTrackerDto, @PathVariable("employeeId") Long employeeId, @PathVariable("departmentId") Long departmentId, @PathVariable("designationId") Long designationId) {

        Employee employeeData = null;

        Optional<Department> department = departmentService.getDepartment(departmentId);
        Optional<Designation> designation = designationService.getDesignation(designationId);

        Optional<Employee> persistedEmployee = employeeService.getEmployee(employeeId);
        Employee employee = persistedEmployee.get();
        employee.setDepartment(department.get());
        employee.setDesignation(designation.get());

        DepartmentTracker departmentTracker = new DepartmentTracker();
        departmentTracker.setDepartmentName(department.get().getName());
        departmentTracker.setDesignation(designation.get().getName());
        if (departmentTrackerDto.getDesignationStartDate() != null) {
            departmentTracker.setStartDate(departmentTrackerDto.getDesignationStartDate());
        }
        if (departmentTrackerDto.getDesignationEndDate() != null) {
            departmentTracker.setEndDate(departmentTrackerDto.getDesignationEndDate());
        }
        departmentTracker.setCreatedOn(new Date());
        departmentTrackerRepository.save(departmentTracker);
        List<DepartmentTracker> oldDepartmentTrackerList = employee.getDepartmentTrackerList();
        oldDepartmentTrackerList.add(departmentTracker);
        employee.setDepartmentTrackerList(oldDepartmentTrackerList);
        employeeData = employeeService.save(employee);

        return new ResponseEntity<>(employeeData, HttpStatus.OK);
    }

    @GetMapping("employee/selected-details")
    public ResponseEntity<List<Employee>> getEmployeeDetails() {
        List<Employee> employeeData = employeeService.getEmployeeSelectedDetails();
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("employeeData");
        }
        return new ResponseEntity<List<Employee>>(employeeData, HttpStatus.OK);
    }

    /**
     * API To check aadhaar number is exist or not with Database
     *
     * @param employeeAadhaarDto
     * @return 200 if exist or 404 if doesnt exists
     */

    @PostMapping("employee/aadhaar-exists")
    public ResponseEntity<Employee> aadhaarExist(@RequestBody EmployeeAadhaarDto employeeAadhaarDto) {
        Optional<Employee> employee = employeeService.isAadhaarExists(employeeAadhaarDto.aadhaarNumber);
        if (employee.isPresent()) {
            return new ResponseEntity(HttpStatus.OK);
        } else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("employee/employeecode-exists")
    public ResponseEntity<Employee> employeeCodeExists(@RequestBody EmployeeAadhaarDto employeeAadhaarDto) {
        Optional<Employee> employee = employeeService.checkEmployeeCode(employeeAadhaarDto.employeeCode);
        if (employee.isPresent()) {
            return new ResponseEntity(HttpStatus.OK);
        } else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("employee/choose-epf/{epf}/{empid}")
    public ResponseEntity<Employee> chooseEpfByEmployee(@PathVariable("epf") boolean epf, @PathVariable("empid") long empid) {
        Optional<Employee> employee = employeeService.getEmployee(empid);
        Employee employee1 = employee.get();
        employee1.setEpf(epf);
        Employee e = employeeService.save(employee1);
        return new ResponseEntity<>(e, HttpStatus.OK);
    }

    @PostMapping("employee/create-salary")
    public ResponseEntity<Employee> createEmployeeSalary(@Valid @RequestBody EmployeeCTCDataDto ctcDataDto) {
        Employee persistedEmployee = null;
        if (ctcDataDto != null) {
            Optional<Employee> employeeObj = employeeService.getEmployee(ctcDataDto.getEmployeeId());
            if (!employeeObj.isPresent()) {
                throw new EntityNotFoundException("empolyee id : " + ctcDataDto.getEmployeeId());
            } else {
                Employee employee = employeeObj.get();
                EmployeeCTCData employeeCTCData = new EmployeeCTCData();
                employeeCTCData.setStatus(true);
                employeeCTCData.setBasicSalary(ctcDataDto.getBasicSalary());
                employeeCTCData.setVariableDearnessAllowance(ctcDataDto.getVariableDearnessAllowance());
                employeeCTCData.setConveyanceAllowance(ctcDataDto.getConveyanceAllowance());
                employeeCTCData.setHouseRentAllowance(ctcDataDto.getHouseRentAllowance());
                employeeCTCData.setEducationalAllowance(ctcDataDto.getEducationalAllowance());
                employeeCTCData.setMealsAllowance(ctcDataDto.getMealsAllowance());
                employeeCTCData.setWashingAllowance(ctcDataDto.getWashingAllowance());
                employeeCTCData.setOtherAllowance(ctcDataDto.getOtherAllowance());
                employeeCTCData.setMiscellaneousAllowance(ctcDataDto.getMiscellaneousAllowance());
                employeeCTCData.setMobileAllowance(ctcDataDto.getMobileAllowance());
                employeeCTCData.setEmployeeEsicContribution(ctcDataDto.getEmployeeEsicContribution());
                employeeCTCData.setRla(ctcDataDto.getRla());
                employeeCTCData.setTpt(ctcDataDto.getTpt());
                employeeCTCData.setUniformAllowance(ctcDataDto.getUniformAllowance());
                employeeCTCData.setShoeAllowance(ctcDataDto.getShoeAllowance());
                employeeCTCData.setBonus(ctcDataDto.getBonus());
                employeeCTCData.setEmpId(employee.getId());
                employeeCTCData.setAffectFrom(ctcDataDto.getAffectedFrom());
                employeeCTCData.setEpfContribution(ctcDataDto.getEpfContribution());
//                if (employee.isEpf()) {
//                    employeeCTCData.setEpfContribution(ctcDataDto.getEpfContribution());
//                } else {
//                    employeeCTCData.setEpfContribution(BigDecimal.ZERO);
//                }
                employeeCTCData.setGratuity(ctcDataDto.getGratuity());
                employeeCTCData.setMedicalPolicy(ctcDataDto.getMedicalPolicy());
                employeeCTCData.setMedicalReimbursement(ctcDataDto.getMedicalReimbursement());
                employeeCTCData.setLeaveTravelAllowance(ctcDataDto.getLeaveTravelAllowance());
                employeeCTCData.setRoyalty(ctcDataDto.getRoyalty());
                employeeCTCData.setMonthlyCtc(ctcDataDto.getMonthlyCtc());
                employeeCTCData.setYearlyCtc(ctcDataDto.getYearlyCtc());
                employee.setEmployeeCTCData(employeeCTCData);
                persistedEmployee = employeeService.save(employee);
            }
            return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @GetMapping("employee/employee-salary")
    public ResponseEntity<Employee> getEmployeeSalaryDetail(@RequestBody EmployeeCTCDataDto ctcDataDto) {
        Optional<Employee> employee = employeeService.getEmployeeSalaryDetails(ctcDataDto.getEmployeeCTCId());
        if (!employee.isPresent()) {
            throw new EntityNotFoundException("Employee Salary Details are not found for the employee id : " + ctcDataDto.getEmployeeCTCId());
        }
        return new ResponseEntity<Employee>(employee.get(), HttpStatus.OK);
    }

    @PostMapping("employee/update-salary/{id}")
    public ResponseEntity<Employee> updateEmployeeSalary(@PathVariable Long id, @Valid @RequestBody EmployeeCTCDataDto ctcDataDto, EmployeeCTCData employeeCTCData) {
        Employee persistedEmployee = null;
        if (ctcDataDto != null) {
            Optional<Employee> employeeObj = employeeService.getEmployeeSalaryDetails(id);
            if (!employeeObj.isPresent()) {
                throw new EntityNotFoundException("empolyee id : " + ctcDataDto.getEmployeeId());
            } else {
                Employee employee = employeeObj.get();

                employee.setEmployeeCTCData(employeeCTCData);
                persistedEmployee = employeeService.save(employee);
            }
            return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @PostMapping(value = "employee/upload-document/{id}")
    public ResponseEntity<Document> uploadEmployeeDocument(@PathVariable("id") long id, @RequestParam("file") MultipartFile file) throws IOException {
        Document document;
        if (file.isEmpty()) {
            throw new FileNotFoundException();
        } else {
            Employee employee = employeeService.findOne(id);
            String prefix = "kyc-document";
            document = fileStorageService.storeEmployeeKycDocument(file, employee, prefix);
        }
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @PostMapping(value = "employee/upload-all-document-employee/{id}")
    public ResponseEntity<Employee> uploadAllKycDocument(@PathVariable("id") long id, @RequestBody EmployeeLoginDto employeeLoginDto) throws IOException {
        Employee employee = employeeService.findOne(id);
        List<Document> documents = new ArrayList<>();
        if (employee != null) {
            for (long documentId : employeeLoginDto.getDocumentIds()) {
                Document document = fileStorageService.getDocument(documentId);
                if (document != null) {
                    documents.add(document);
                }
            }
            employee.setDocuments(documents);
        }
        employeeService.save(employee);
        return new ResponseEntity(documents, HttpStatus.OK);
    }

    @GetMapping(value = "employee/assign-approval-manager/{employeeId}/{firstApproval}/{secondApproval}")
    public ResponseEntity<Employee> assignApprovalManagers(@PathVariable("employeeId") Long employeeId, @PathVariable("firstApproval") Long firstApproval, @PathVariable("secondApproval") Long secondApproval) {

        Employee employeeData = null;
        Optional<Employee> employeeFirstApproval = employeeService.getEmployee(firstApproval);
        Optional<Employee> employeeSecondApproval = employeeService.getEmployee(secondApproval);
        Optional<Employee> persistedEmployee = employeeService.getEmployee(employeeId);
        Employee employee = persistedEmployee.get();
        if (employeeFirstApproval.isPresent()) {
            employee.setFirstApprovalManager(employeeFirstApproval.get());
        } else employee.setFirstApprovalManager(null);

        if (employeeSecondApproval.isPresent()) {
            employee.setSecondApprovalManager(employeeSecondApproval.get());
        } else employee.setSecondApprovalManager(null);


        employeeData = employeeService.save(employee);
        return new ResponseEntity<>(employeeData, HttpStatus.OK);
    }

    @PostMapping(value = "employee/upload-family-picture/{id}/{uploadId}")
    public ResponseEntity<Employee> uploadFamilyPicture(@PathVariable("id") String id, @PathVariable long uploadId, @RequestParam("file") MultipartFile file) throws IOException {
        Employee persistedEmployee = null;
        if (file.isEmpty()) {
            throw new FileNotFoundException();

        } else {
            if (uploadId == 1) {
                Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(id);
                Document document = fileStorageService.storeFile(file);
                employee.get().setFamilyPicDocument(document);
                persistedEmployee = employeeService.save(employee.get());
            } else if (uploadId == 3) {
                EmpPermanentContract empPermanentContract = permanentContractService.get(id);
                Document document = fileStorageService.storeFile(file);
                empPermanentContract.setFamilyPicDocument(document);
                permanentContractService.save(empPermanentContract);
            } else if (uploadId == 2) {
                Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(id);
                Document document = fileStorageService.storeFile(file);
                employee.get().setFamilyPicDocument(document);
                persistedEmployee = employeeService.save(employee.get());

                EmpPermanentContract empPermanentContract = permanentContractService.get(id);
//                Document document1 = fileStorageService.storeFile(file);
                empPermanentContract.setFamilyPicDocument(document);
                permanentContractService.save(empPermanentContract);
            }
        }
        return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
    }

    @PostMapping("employee/calculate-ctc")
    public ResponseEntity<EmployeeCTCCalculation> calculateEmployeeSalary(@Valid @RequestBody EmployeeCTCDataDto ctcDataDto) {
        EmployeeCTCCalculation employeeCTCCalculation = calculateSalary(ctcDataDto);
        if (!(calculateSalary(ctcDataDto) != null)) {
            throw new EntityNotFoundException("SALARY Variable null");
        }
        return new ResponseEntity<>(employeeCTCCalculation, HttpStatus.OK);
    }

    private EmployeeCTCCalculation calculateSalary(EmployeeCTCDataDto ctcDataDto) {
        EmployeeCTCCalculation employeeCTCCalculation = new EmployeeCTCCalculation();
        if (ctcDataDto != null) {
            BigDecimal totalEarningsPerMonth;
            totalEarningsPerMonth = ctcDataDto.getBasicSalary();
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getVariableDearnessAllowance());
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getConveyanceAllowance());
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getHouseRentAllowance());
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getEducationalAllowance());
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getWashingAllowance());
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getOtherAllowance());
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMiscellaneousAllowance());
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMobileAllowance());
            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMealsAllowance());
            Double gross = totalEarningsPerMonth.doubleValue();

            //TODO BOTH
//            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getEpfContribution());
//            totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getGratuity());


            BigDecimal bonus = BigDecimal.ZERO;
            //TODO BONUS
//            bonus = ctcDataDto.getBasicSalary().multiply(BigDecimal.valueOf(0.1));
            BigDecimal grossSalary = BigDecimal.valueOf(gross.doubleValue());
            BigDecimal employeeEsicContribution = BigDecimal.ZERO;
            if (gross <= 21000) {
//                employeeEsicContribution = grossSalary.multiply(BigDecimal.valueOf(0.0325));
                employeeEsicContribution = grossSalary.multiply(BigDecimal.valueOf(0.0075));
                employeeEsicContribution = employeeEsicContribution.setScale(2, BigDecimal.ROUND_UP);
            } else {
                employeeEsicContribution = BigDecimal.ZERO;
            }
//            totalEarningsPerMonth = totalEarningsPerMonth.add(employeeEsicContribution);
            BigDecimal totalEarningsPerYear;
            totalEarningsPerYear = totalEarningsPerMonth.multiply(new BigDecimal(12));
            totalEarningsPerYear = totalEarningsPerYear.add(ctcDataDto.getTpt());
            totalEarningsPerYear = totalEarningsPerYear.add(ctcDataDto.getUniformAllowance());
            totalEarningsPerYear = totalEarningsPerYear.add(ctcDataDto.getShoeAllowance());
            totalEarningsPerYear = totalEarningsPerYear.add(ctcDataDto.getMedicalReimbursement());
            totalEarningsPerYear = totalEarningsPerYear.add(ctcDataDto.getMedicalPolicy());
            totalEarningsPerYear = totalEarningsPerYear.add(ctcDataDto.getLeaveTravelAllowance());
            totalEarningsPerYear = totalEarningsPerYear.add(ctcDataDto.getRoyalty());
            totalEarningsPerYear = totalEarningsPerYear.add(ctcDataDto.getRla());
            totalEarningsPerYear = totalEarningsPerYear.add(bonus.multiply(BigDecimal.valueOf(12)));
            employeeCTCCalculation.setTotalEarningsPerMonth(totalEarningsPerMonth);
            employeeCTCCalculation.setTotalEarningsPerYear(totalEarningsPerYear);
            employeeCTCCalculation.setEmployeeEsicContribution(employeeEsicContribution);
            employeeCTCCalculation.setBonus(bonus.multiply(BigDecimal.valueOf(12)));
        }
        return employeeCTCCalculation;
    }

    @PostMapping("ctc-pt")
    public long getPT(@RequestBody EmployeeCTCDataDto ctcDataDto) {
        BigDecimal totalEarningsPerMonth;
        totalEarningsPerMonth = ctcDataDto.getBasicSalary();
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getVariableDearnessAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getConveyanceAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getHouseRentAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getEducationalAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getWashingAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getOtherAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMiscellaneousAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMobileAllowance());
        totalEarningsPerMonth = totalEarningsPerMonth.add(ctcDataDto.getMealsAllowance());
        Double gross = totalEarningsPerMonth.doubleValue();
        long pt = 0;
        BigDecimal bonus = BigDecimal.ZERO;
        BigDecimal grossSalary = BigDecimal.valueOf(gross.doubleValue());
        BigDecimal grossLim = BigDecimal.valueOf(15000);
        if (gross.doubleValue() >= 15000) {
            pt = 200;
        } else {
            pt = 0;
        }
        return pt;
    }

    @PostMapping(value = "employee/upload-document/form/{id}")
    public ResponseEntity<Employee> uploadFormEmployeeDocument(@PathVariable("id") long id, @RequestParam("file") MultipartFile file) throws IOException {
        Employee persistedEmployee = null;
        if (file.isEmpty()) {
            throw new FileNotFoundException();

        } else {
            Employee employee = employeeService.findOne(id);
            String prefix = "form-16";
            Document document = fileStorageService.storeEmployeeKycDocument(file, employee, prefix);
            employee.setForm16Document(document);
            persistedEmployee = employeeService.save(employee);
        }
        return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
    }

    @GetMapping(value = "employee/probationary")
    private ResponseEntity<Employee> getEmployeesJustCompletedProbationaryPeriod() {
        List<Employee> employees = employeeService.getAllEmployeeUnderProbationaryPeriod();
        List<Employee> employeesCompletedProbationaryToday = new ArrayList<>();
        for (Employee employeeObj : employees) {
            Date todaysDate = DateUtil.getTodayDate();
            int ProbationaryDay = Integer.parseInt(employeeObj.getDateOfJoining().getDay() + employeeObj.getProbationaryPeriod());
            int currentMonth = DateUtil.getMonthNumber(todaysDate);
            int currentYear = DateUtil.getCurrentYear();
            String ProbationaryDate = ProbationaryDay + "/" + currentMonth + "/" + currentYear;
            if (todaysDate.getTime() == DateUtil.convertToDate(ProbationaryDate).getTime()) {
                employeesCompletedProbationaryToday.add(employeeObj);
                employeeObj.setProbationaryPeriodStatus(ProbationaryPeriodStatus.PROBATIONARY_COMPLETED);
                Employee employee = employeeService.save(employeeObj);
            }

        }
        return new ResponseEntity(employeesCompletedProbationaryToday, HttpStatus.OK);
    }

    @GetMapping(value = "employee/manager-hierarchy/{employeeId}")
    private ResponseEntity<Employee> getEmployeesManager(@PathVariable long employeeId) {
        List<Employee> employeeList = employeeService.findAll();
        List<Employee> allEmployeeListForFirstLevel = new ArrayList<>();
        List<Employee> allEmployeeListForSecondLevel = new ArrayList<>();
        List<Employee> allEmployeeListForThirdLevel = new ArrayList<>();
        List<Employee> allEmployeeListForFourthLevel = new ArrayList<>();
        List<Employee> mangerHierarchyList = new ArrayList<>();
        List<Employee> firstLevelFirstMangerEmployeeList = employeeService.getEmployeesByFirstApprovalManager(employeeId);
        List<Employee> firstLevelSecondManagerEmployeeList = employeeService.getEmployeesBySecondApprovalManager(employeeId);
        for (Employee firstLevelFirstMangerEmployeeObj : firstLevelFirstMangerEmployeeList) {
            allEmployeeListForFirstLevel.add(firstLevelFirstMangerEmployeeObj);
        }
        for (Employee firstLevelSecondManagerEmployeeObj : firstLevelSecondManagerEmployeeList) {
            allEmployeeListForFirstLevel.add(firstLevelSecondManagerEmployeeObj);
        }
        LinkedHashSet<Employee> firstLevelhashSet = new LinkedHashSet<>(allEmployeeListForFirstLevel);
        ArrayList<Employee> firstLevelEmployeeArrayList = new ArrayList<>(firstLevelhashSet);
        for (Employee firstLevelEmployeeObj : firstLevelEmployeeArrayList) {
            mangerHierarchyList.add(firstLevelEmployeeObj);
        }
        for (Employee firstLevelEmployeeObj : firstLevelEmployeeArrayList) {
            List<Employee> secondLevelFirstMangerEmployeeList = employeeService.getEmployeesByFirstApprovalManager(firstLevelEmployeeObj.getId());
            List<Employee> secondLevelSecondManagerEmployeeList = employeeService.getEmployeesBySecondApprovalManager(firstLevelEmployeeObj.getId());
            for (Employee secondLevelFirstMangerEmployeeObj : secondLevelFirstMangerEmployeeList) {
                allEmployeeListForSecondLevel.add(secondLevelFirstMangerEmployeeObj);
            }
            for (Employee secondLevelSecondManagerEmployeeObj : secondLevelSecondManagerEmployeeList) {
                allEmployeeListForSecondLevel.add(secondLevelSecondManagerEmployeeObj);
            }
            LinkedHashSet<Employee> secondLevelhashSet = new LinkedHashSet<>(allEmployeeListForSecondLevel);
            ArrayList<Employee> secondLevelEmployeeArrayList = new ArrayList<>(secondLevelhashSet);
            for (Employee secondLevelEmployeeObj : secondLevelEmployeeArrayList) {
                mangerHierarchyList.add(secondLevelEmployeeObj);
            }
            for (Employee secondLevelEmployeeObj : secondLevelEmployeeArrayList) {
                List<Employee> thirdLevelFirstMangerEmployeeList = employeeService.getEmployeesByFirstApprovalManager(secondLevelEmployeeObj.getId());
                List<Employee> thirdLevelSecondManagerEmployeeList = employeeService.getEmployeesBySecondApprovalManager(secondLevelEmployeeObj.getId());
                for (Employee thirdLevelFirstMangerEmployeeObj : thirdLevelFirstMangerEmployeeList) {
                    allEmployeeListForThirdLevel.add(thirdLevelFirstMangerEmployeeObj);
                }
                for (Employee thirdLevelSecondManagerEmployeeObj : thirdLevelSecondManagerEmployeeList) {
                    allEmployeeListForThirdLevel.add(thirdLevelSecondManagerEmployeeObj);
                }
                LinkedHashSet<Employee> ThirdLevelhashSet = new LinkedHashSet<>(allEmployeeListForThirdLevel);
                ArrayList<Employee> thirdLevelEmployeeArrayList = new ArrayList<>(ThirdLevelhashSet);
                for (Employee thirdLevelEmployeeObj : thirdLevelEmployeeArrayList) {
                    mangerHierarchyList.add(thirdLevelEmployeeObj);
                }
                for (Employee thirdLevelEmployeeObj : thirdLevelEmployeeArrayList) {
                    List<Employee> fourthLevelFirstMangerEmployeeList = employeeService.getEmployeesByFirstApprovalManager(thirdLevelEmployeeObj.getId());
                    List<Employee> fourthLevelSecondManagerEmployeeList = employeeService.getEmployeesBySecondApprovalManager(thirdLevelEmployeeObj.getId());
                    for (Employee fourthLevelFirstMangerEmployeeObj : fourthLevelFirstMangerEmployeeList) {
                        allEmployeeListForFourthLevel.add(fourthLevelFirstMangerEmployeeObj);
                    }
                    for (Employee fourthLevelSecondManagerEmployeeObj : fourthLevelSecondManagerEmployeeList) {
                        allEmployeeListForFourthLevel.add(fourthLevelSecondManagerEmployeeObj);
                    }
                    LinkedHashSet<Employee> FourthLevelhashSet = new LinkedHashSet<>(allEmployeeListForFourthLevel);
                    ArrayList<Employee> fourthLevelEmployeeArrayList = new ArrayList<>(FourthLevelhashSet);
                    for (Employee fourthLevelEmployeeObj : fourthLevelEmployeeArrayList) {
                        mangerHierarchyList.add(fourthLevelEmployeeObj);
                    }
                }
            }
        }
        LinkedHashSet<Employee> finalMangerHierarchyhashSet = new LinkedHashSet<>(mangerHierarchyList);
        ArrayList<Employee> finalMangerHierarchy = new ArrayList<>(finalMangerHierarchyhashSet);
        return new ResponseEntity(finalMangerHierarchy, HttpStatus.OK);
    }

    @PostMapping(value = "employee/assign-approval-manager-new/{employeeId}/{firstApproval}/{secondApproval}")
    public ResponseEntity<Employee> assignApprovalManagersNew(@PathVariable("employeeId") Long employeeId, @PathVariable("firstApproval") Long firstApproval, @PathVariable("secondApproval") Long secondApproval, @RequestBody Employee ManagerList) {

        Employee employeeData = null;
        Optional<Employee> employeeFirstApproval = employeeService.getEmployee(firstApproval);
        Optional<Employee> employeeSecondApproval = employeeService.getEmployee(secondApproval);
        Optional<Employee> persistedEmployee = employeeService.getEmployee(employeeId);

        Employee employee = persistedEmployee.get();
        if (employeeFirstApproval.isPresent()) {
            employee.setFirstApprovalManager(employeeFirstApproval.get());
        } else employee.setFirstApprovalManager(employeeData);

        if (employeeSecondApproval.isPresent()) {
            employee.setSecondApprovalManager(employeeSecondApproval.get());
        } else employee.setSecondApprovalManager(employeeData);

        if (ManagerList.getThirdApprovalManager().getId() != null) {
            Optional<Employee> employeeThirdApproval = employeeService.getEmployee(ManagerList.getThirdApprovalManager().getId());
            if (employeeThirdApproval.isPresent()) {
                employee.setThirdApprovalManager(employeeThirdApproval.get());
            }
        } else employee.setThirdApprovalManager(employeeData);

        if (ManagerList.getFourthApprovalManager().getId() != null) {
            Optional<Employee> employeeFourthApproval = employeeService.getEmployee(ManagerList.getFourthApprovalManager().getId());
            if (employeeFourthApproval.isPresent()) {
                employee.setFourthApprovalManager(employeeFourthApproval.get());
            }

        } else employee.setFourthApprovalManager(employeeData);

        if (ManagerList.getFifthApprovalManager().getId() != null) {
            Optional<Employee> employeeFifthApproval = employeeService.getEmployee(ManagerList.getFifthApprovalManager().getId());
            if (employeeFifthApproval.isPresent()) {
                employee.setFifthApprovalManager(employeeFifthApproval.get());
            }
        } else employee.setFifthApprovalManager(employeeData);

        employeeData = employeeService.save(employee);
        return new ResponseEntity<>(employeeData, HttpStatus.OK);
    }

    @PostMapping(value = "employee/check-photo-size")
    public ResponseEntity checkProfilePicture(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new FileNotFoundException();

        } else if (file.getSize() > 2097152) {
            throw new EntityNotFoundException("Sorry! File size contains more than 2MB");
        }
        return new ResponseEntity(file.getSize(), HttpStatus.OK);
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
                openingLeave.setEarnLeave(new BigDecimal(0));
                closingLeave.setEarnLeave(new BigDecimal(0));
            } else {
                openingLeave.setEarnLeave(new BigDecimal(0));
                closingLeave.setEarnLeave(new BigDecimal(0));
            }

            if (earningLeaveRule.getLeaveType().equals(LeaveType.MEDICAL_LEAVE)) {
                if (employee.getEsiNumber().compareTo("0") > 0) {
                    openingLeave.setMedicalLeave(new BigDecimal(0));
                    closingLeave.setMedicalLeave(new BigDecimal(0));
                } else {
                    openingLeave.setMedicalLeave(new BigDecimal(10));
                    closingLeave.setMedicalLeave(new BigDecimal(10));

                }
            } else {
                if (employee.getEsiNumber().compareTo("0") > 0) {
                    openingLeave.setMedicalLeave(new BigDecimal(0));
                    closingLeave.setMedicalLeave(new BigDecimal(0));
                } else {
                    openingLeave.setMedicalLeave(new BigDecimal(10));
                    closingLeave.setMedicalLeave(new BigDecimal(10));

                }
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


    @GetMapping("employee/last-three-ctc/{empId}")
    public ResponseEntity<List<EmployeeCTCData>> lastThreeCtc(@PathVariable("empId") long empId) {
        List<EmployeeCTCData> employeeCTCData = employeeCtcDataRepo.findByEmpId(empId);
        List<EmployeeCTCData> employeeCTCDataList = new ArrayList<>();
        Collections.reverse(employeeCTCData);
        System.out.println(employeeCTCData.size());

        if (employeeCTCData.size() == 1) {
            EmployeeCTCData e0 = employeeCTCData.get(0);
            employeeCTCDataList.add(e0);
        } else if (employeeCTCData.size() == 2) {
            EmployeeCTCData e0 = employeeCTCData.get(0);
            employeeCTCDataList.add(e0);
            EmployeeCTCData e1 = employeeCTCData.get(1);
            employeeCTCDataList.add(e1);
        } else if (employeeCTCData.size() >= 3) {
            EmployeeCTCData e0 = employeeCTCData.get(0);
            employeeCTCDataList.add(e0);
            EmployeeCTCData e1 = employeeCTCData.get(1);
            employeeCTCDataList.add(e1);
            EmployeeCTCData e2 = employeeCTCData.get(2);
            employeeCTCDataList.add(e2);
        }
        if (employeeCTCDataList.isEmpty()) {

        }
        return new ResponseEntity<>(employeeCTCDataList, HttpStatus.OK);
    }

    //comment for stanley
    @GetMapping("sync-contract-with-employee")
    @Scheduled(initialDelay = 9000, fixedRate = 60000)
    public ResponseEntity<List<EmpPermanentContract>> syncContractEmployees() {
        System.out.println("new Emp reg check");
        List<EmpPermanentContract> empPermanentContracts = permanentContractService.getAll(false);
        for (EmpPermanentContract empPermanentContract : empPermanentContracts) {
            Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(empPermanentContract.getEmployeeCode());
            if (!employee.isPresent()) {
                Employee employee1 = new Employee();
                employee1.setEmployeeCode(empPermanentContract.getEmployeeCode());
                employee1.setFirstName(empPermanentContract.getFirstName());
                employee1.setLastName(empPermanentContract.getLastName());
                if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT) && empPermanentContract.isBelongsTo() == false) {
                    employee1.setEmployeeType(EmployeeType.PERMANENT);
                } else {
                    employee1.setEmployeeType(empPermanentContract.getEmployeeType());
                }
                employee1.setCompany(empPermanentContract.getContractCompany());
                employee1.setContractCompany(empPermanentContract.getContractCompany());
                employee1.setDateOfJoining(empPermanentContract.getDateOfJoining());
                employee1.setDepartmentName(empPermanentContract.getDepartmentName());
                employee1.setRfid(empPermanentContract.getCardId());
                Department department = departmentService.getByName(empPermanentContract.getDepartmentName());
                if (department != null) {
                    if (employee1.getDepartment() == null) {
                        employee1.setDepartment(department);
                    }
                }
                employee1.setStatus(true);
                employee1.setEsiNumber("0");
                employeeService.save(employee1);
                allocateLeave(employee1);
            } else {
                Employee employee1 = employee.get();
//                System.out.println("Editing employee");
                employee1.setEmployeeCode(empPermanentContract.getEmployeeCode());
                employee1.setFirstName(empPermanentContract.getFirstName());
                employee1.setLastName(empPermanentContract.getLastName());
                employee1.setRfid(empPermanentContract.getCardId());
//                employee1.setEmployeeType(EmployeeType.CONTRACT);
//                if (empPermanentContract.getEmployeeType().equals(EmployeeType.PERMANENT_CONTRACT) && empPermanentContract.isBelongsTo() == false) {
////                    employee1.setEmployeeType(EmployeeType.PERMANENT);
//                } else {
//                    employee1.setEmployeeType(empPermanentContract.getEmployeeType());
//                }
                employee1.setCompany(empPermanentContract.getContractCompany());
                employee1.setContractCompany(empPermanentContract.getContractCompany());
                employee1.setDateOfJoining(empPermanentContract.getDateOfJoining());
                employee1.setDepartmentName(empPermanentContract.getDepartmentName());
                Department department = departmentService.getByName(empPermanentContract.getDepartmentName());
                if (department != null) {
                    if (employee1.getDepartment() == null) {
                        employee1.setDepartment(department);
                    }
                }
                employee1.setStatus(true);
                employee1.setEsiNumber("0");
                employeeService.save(employee1);
//                System.out.println("ALREADY EXISTS");
            }
        }
        return new ResponseEntity<>(empPermanentContracts, HttpStatus.OK);
    }


    //comment for stanley use only for astra and GE
    @GetMapping("sync-employee-with-headcount")
    @Scheduled(initialDelay = 4001, fixedRate = 70000)
    public ResponseEntity<List<Employee>> syncPermContractEmployeesForHeadcount() {
        List<Employee> empPermanentContracts = employeeService.findAll();

        for (Employee employee : empPermanentContracts) {
            if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                EmpPermanentContract empPermanentContrac = permanentContractService.get(employee.getEmployeeCode());
                if (empPermanentContrac == null) {
                    EmpPermanentContract empPermanentContract1 = new EmpPermanentContract();
                    empPermanentContract1.setEmployeeCode(employee.getEmployeeCode());
                    empPermanentContract1.setFirstName(employee.getFirstName());
                    empPermanentContract1.setLastName(employee.getLastName());
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        empPermanentContract1.setEmployeeType(EmployeeType.PERMANENT_CONTRACT);
                    }/* else {
                    empPermanentContract1.setEmployeeType(EmployeeType.CONTRACT);
                }*/
                    empPermanentContract1.setContractCompany(employee.getContractCompany());
                    empPermanentContract1.setDateOfJoining(employee.getDateOfJoining());
                    if (employee.getDepartment() != null) {
//                        System.out.println("deppppppppppppppppppp : " + employee.getDepartment().getName());
                        employee.setDepartmentName(employee.getDepartment().getName());
                        empPermanentContract1.setDepartmentName(employee.getDepartmentName());
                        empPermanentContract1.setDepartment(employee.getDepartment());
                    } else {
                        empPermanentContract1.setDepartmentName(employee.getDepartmentName());
                    }
                    empPermanentContract1.setStatus(true);
                    empPermanentContract1.setBelongsTo(false);
                    if (employee.getRfid()!=null) {
                    empPermanentContract1.setCardId(employee.getRfid());
                    }
                    permanentContractService.save(empPermanentContract1);
                } else {
                    EmpPermanentContract empPermanentContract1 = empPermanentContrac;
                    empPermanentContract1.setEmployeeCode(employee.getEmployeeCode());
                    empPermanentContract1.setFirstName(employee.getFirstName());
                    empPermanentContract1.setLastName(employee.getLastName());
                    if (employee.getEmployeeType().equals(EmployeeType.PERMANENT) || employee.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                        empPermanentContract1.setEmployeeType(EmployeeType.PERMANENT_CONTRACT);
                    } /*else {
                    empPermanentContract1.setEmployeeType(EmployeeType.CONTRACT);
                }*/
                    empPermanentContract1.setContractCompany(employee.getContractCompany());
                    empPermanentContract1.setDateOfJoining(employee.getDateOfJoining());
                    if (employee.getDepartment() != null) {
//                        System.out.println("deppppppppppppppppppp : " + employee.getDepartment().getName());
                        employee.setDepartmentName(employee.getDepartment().getName());
                        empPermanentContract1.setDepartment(employee.getDepartment());
                        empPermanentContract1.setDepartmentName(employee.getDepartmentName());
                    } else {
                        empPermanentContract1.setDepartmentName(employee.getDepartmentName());
                    }
                    empPermanentContract1.setStatus(true);
                    empPermanentContract1.setBelongsTo(false);
                    if (employee.getRfid()!=null) {
                        empPermanentContract1.setCardId(employee.getRfid());
                    }
                    permanentContractService.save(empPermanentContract1);
                }
            }
        }
        return new ResponseEntity<>(empPermanentContracts, HttpStatus.OK);
    }


    //Use only for astra
    @GetMapping("sync-visitor-with-employee")
    @Scheduled(initialDelay = 3121, fixedRate = 60000)
    public void syncVisitor() {
        System.out.println("new ");
        List<VisitorPass> empPermanentContracts = visitorPassService.getAllVisitors();
        for (VisitorPass empPermanentContract : empPermanentContracts) {
            Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(empPermanentContract.getMobileNumber());
            if (!employee.isPresent()) {
                Employee employee1 = new Employee();
                employee1.setEmployeeCode(empPermanentContract.getMobileNumber());
                employee1.setFirstName(empPermanentContract.getFirstName());
                employee1.setLastName(empPermanentContract.getLastName());
                employee1.setEmployeeType(EmployeeType.VISITOR);
                employee1.setCompany(empPermanentContract.getCompanyName());
                employee1.setContractCompany(empPermanentContract.getCompanyName());
                employee1.setDateOfJoining(empPermanentContract.getDateOfVisit());
                employee1.setDepartmentName(empPermanentContract.getDepartmentName());
                employee1.setRfid(empPermanentContract.getRfid());
                Department department = departmentService.getByName(empPermanentContract.getDepartmentName());
                if (department != null) {
                    if (employee1.getDepartment() == null) {
                        employee1.setDepartment(department);
                    }
                }
                employee1.setStatus(true);
                employee1.setEsiNumber("0");
                employeeService.save(employee1);
                allocateLeave(employee1);
            } else {
                Employee employee1 = employee.get();
                employee1.setEmployeeCode(empPermanentContract.getMobileNumber());
                employee1.setFirstName(empPermanentContract.getFirstName());
                employee1.setLastName(empPermanentContract.getLastName());
                employee1.setEmployeeType(EmployeeType.VISITOR);
                employee1.setCompany(empPermanentContract.getCompanyName());
                employee1.setContractCompany(empPermanentContract.getCompanyName());
                employee1.setDateOfJoining(empPermanentContract.getDateOfVisit());
                employee1.setDepartmentName(empPermanentContract.getDepartmentName());
                employee1.setRfid(empPermanentContract.getRfid());
                Department department = departmentService.getByName(empPermanentContract.getDepartmentName());
                if (department != null) {
                    if (employee1.getDepartment() == null) {
                        employee1.setDepartment(department);
                    }
                }
                employee1.setStatus(true);
                employee1.setEsiNumber("0");
                employeeService.save(employee1);

//                System.out.println("ALREADY EXISTS");
            }
        }
    }

    @GetMapping("update-ctc-for-pf")
    public void updateCtcForAllEmployees() {
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            if (employee.getEmployeeCTCData() != null) {
                EmployeeCTCData employeeCTCData = employee.getEmployeeCTCData();
                double basic = employeeCTCData.getBasicSalary().doubleValue();
                double maxBasic = 15000;
                if (basic > maxBasic) {
                    System.out.println("name " + employee.getFirstName());
                    BigDecimal pf = BigDecimal.valueOf(maxBasic * 0.12);
                    employeeCTCData.setEpfContribution(pf);
                    updateEmployeeSalary(employeeCTCData);
                }

            }
        }
    }

    public ResponseEntity<Employee> updateEmployeeSalary(EmployeeCTCData ctcDataDto) {
        Employee persistedEmployee = null;
        if (ctcDataDto != null) {
            EmployeeCTCData employeeCTCData = ctcDataDto;
            employeeCTCData.setStatus(true);
            employeeCTCData.setBasicSalary(ctcDataDto.getBasicSalary());
            employeeCTCData.setVariableDearnessAllowance(ctcDataDto.getVariableDearnessAllowance());
            employeeCTCData.setConveyanceAllowance(ctcDataDto.getConveyanceAllowance());
            employeeCTCData.setHouseRentAllowance(ctcDataDto.getHouseRentAllowance());
            employeeCTCData.setEducationalAllowance(ctcDataDto.getEducationalAllowance());
            employeeCTCData.setMealsAllowance(ctcDataDto.getMealsAllowance());
            employeeCTCData.setWashingAllowance(ctcDataDto.getWashingAllowance());
            employeeCTCData.setOtherAllowance(ctcDataDto.getOtherAllowance());
            employeeCTCData.setMiscellaneousAllowance(ctcDataDto.getMiscellaneousAllowance());
            employeeCTCData.setMobileAllowance(ctcDataDto.getMobileAllowance());
            employeeCTCData.setEmployeeEsicContribution(ctcDataDto.getEmployeeEsicContribution());
            employeeCTCData.setRla(ctcDataDto.getRla());
            employeeCTCData.setTpt(ctcDataDto.getTpt());
            employeeCTCData.setUniformAllowance(ctcDataDto.getUniformAllowance());
            employeeCTCData.setShoeAllowance(ctcDataDto.getShoeAllowance());
            employeeCTCData.setBonus(ctcDataDto.getBonus());
            employeeCTCData.setEpfContribution(ctcDataDto.getEpfContribution());
            employeeCTCData.setGratuity(ctcDataDto.getGratuity());
            employeeCTCData.setMedicalPolicy(ctcDataDto.getMedicalPolicy());
            employeeCTCData.setMedicalReimbursement(ctcDataDto.getMedicalReimbursement());
            employeeCTCData.setLeaveTravelAllowance(ctcDataDto.getLeaveTravelAllowance());
            employeeCTCData.setRoyalty(ctcDataDto.getRoyalty());
            employeeCTCData.setMonthlyCtc(ctcDataDto.getMonthlyCtc());
            employeeCTCData.setYearlyCtc(ctcDataDto.getYearlyCtc());
            employeeCtcDataRepo.save(employeeCTCData);
        }
        return new ResponseEntity<>(persistedEmployee, HttpStatus.OK);
    }

    @Scheduled(initialDelay = 3000, fixedRate = 3600000)
    public void checkLastNameNull() {
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            if (employee.getLastName() == null) {
                employee.setLastName("");
                employeeService.save(employee);
            }
        }
    }

    @PostMapping("employee/set-ot/{id}/{status}")
    public void setOtForEmployee(@PathVariable long id, @PathVariable boolean status) {
        Optional<Employee> employee = employeeService.getEmployee(id);
        if (employee.isPresent()) {
            employee.get().setOtRequired(status);
            employeeService.save(employee.get());
        }
    }

    @GetMapping("employee/permanent")
    public ResponseEntity<List<Employee>> getOnlyPermanentEmployees() {
        List<Employee> employeeData = employeeService.findAll();
        List<Employee> employeeList = new ArrayList<>();
        for (Employee e : employeeData) {
            Employee emp = new Employee();
            emp.setId(e.getId());
            emp.setFirstName(e.getFirstName());
            emp.setLastName(e.getLastName());
            emp.setEmployeeCode(e.getEmployeeCode());
            emp.setEmployeeAddress(e.getEmployeeAddress());
            emp.setFatherName(e.getFatherName());
            emp.setMotherName(e.getMotherName());
            emp.setGender(e.getGender());
            emp.setMaritalStatus(e.getMaritalStatus());
            emp.setDateOfMarriage(e.getDateOfMarriage());
            emp.setDateOfBirth(e.getDateOfBirth());
            emp.setAge(e.getAge());
            emp.setPhoneNumber(e.getPhoneNumber());
            emp.setEmergencyPhoneNumber(e.getEmergencyPhoneNumber());
            emp.setBloodGroup(e.getBloodGroup());
            emp.setReligion(e.getReligion());
            emp.setCaste(e.getCaste());
            emp.setAdharNumber(e.getAdharNumber());
            emp.setFamilyDependents(e.getFamilyDependents());
            emp.setCurrentAddress(e.getCurrentAddress());
            emp.setPermanentAddress(e.getPermanentAddress());
            emp.setDateOfJoining(e.getDateOfJoining());
            emp.setProbationaryPeriod(e.getProbationaryPeriod());
            emp.setPfNumber(e.getPfNumber());
            emp.setUanNumber(e.getUanNumber());
            emp.setPanNumber(e.getPanNumber());
            emp.setEsiNumber(e.getEsiNumber());
            emp.setLevel(e.getLevel());
            emp.setWifeName(e.getWifeName());
            emp.setOfferedSalary(e.getOfferedSalary());
            emp.setFamilyMember(e.getFamilyMember());
            emp.setQualification(e.getQualification());
            emp.setPreviousEmployement(e.getPreviousEmployement());
            emp.setEmployeeCertifications(e.getEmployeeCertifications());
            emp.setEmployeeType(e.getEmployeeType());
            emp.setDepartment(e.getDepartment());
            emp.setDesignation(e.getDesignation());
            emp.setDepartmentTrackerList(e.getDepartmentTrackerList());
            emp.setOtRequired(e.isOtRequired());
            emp.setDesignationStartDate(e.getDesignationStartDate());
            emp.setDesignationEndDate(e.getDesignationEndDate());
            emp.setFirstWeekOff(e.getFirstWeekOff());
            emp.setSecondWeekOff(e.getSecondWeekOff());
            emp.setFirstWeekOffName(e.getFirstWeekOffName());
            emp.setSecondWeekOffName(e.getSecondWeekOffName());
            emp.setRfid(e.getRfid());
            emp.setReleaved(e.isReleaved());
            emp.setFlexi(e.isFlexi());
            emp.setMins(e.getMins());
            if (e.getDepartmentName() != null && (!e.getDepartmentName().equalsIgnoreCase(""))) {
                Department department = new Department();
                department.setName(e.getDepartmentName());
                emp.setDepartment(department);
                Department departmentCheck = departmentService.getByName(e.getDepartmentName());
                if (departmentCheck == null) {
                    departmentCheck = new Department();
                    departmentCheck.setName(e.getDepartmentName());
                    departmentCheck.setStatus(true);
                    departmentService.save(departmentCheck);
                }
                if (e.getDepartmentTrackerList().isEmpty()) {
                    DepartmentTracker departmentTracker = new DepartmentTracker();
                    departmentTracker.setDepartmentName(e.getDepartmentName());
                    List<DepartmentTracker> departmentTrackers = new ArrayList<>();
                    departmentTrackerRepository.save(departmentTracker);
                    departmentTrackers.add(departmentTracker);
                    e.setDepartmentTrackerList(departmentTrackers);
                    employeeService.save(e);
                }
                if (e.getDepartment() == null) {
                    e.setDepartment(departmentCheck);
                    employeeService.save(e);
                }

            }

            if (emp.getEmployeeType().equals(EmployeeType.PERMANENT) ||
                    emp.getEmployeeType().equals(EmployeeType.PERMANENT_WORKER)) {
                employeeList.add(emp);
            }
        }
        if (CollectionUtils.isEmpty(employeeData)) {
            throw new EntityNotFoundException("employeeData");
        }
        return new ResponseEntity<List<Employee>>(employeeList, HttpStatus.OK);
    }

    @PostMapping("employee/flexi-timing/{employeeId}/{mins}/{status}")
    public void setFelixi(@PathVariable long employeeId, @PathVariable double mins, @PathVariable boolean status) {
        Optional<Employee> employee = employeeService.getEmployee(employeeId);
        Employee employee1 = employee.get();
        employee1.setFlexi(status);
        employee1.setMins(mins);
        employeeService.save(employee1);
    }

    public void sliceVolume(long radius,long thickness, long angle){
        double pie = 3.14;
        if(thickness>0 && thickness<=2 && radius>0 && radius<=20 && angle>0 && angle<=360){
            double area = (angle/360)*(pie *radius*radius);
            double volume = area *thickness;
            double volumeCeil = Math.ceil(volume);
            System.out.printf(""+volumeCeil);
        }
    }


    @PostMapping("employee/releave/{employeeCode}")
    public void releaveEmployee(@PathVariable("employeeCode") String employeeCode){
        Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(employeeCode);
        if (employee.isPresent()){
            Employee emp = employee.get();
            emp.setRfid(null);
            emp.setReleaved(true);
            employeeService.save(emp);
        }
        EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
        if (empPermanentContract!=null){
            empPermanentContract.setCardId(null);
            empPermanentContract.setReleaved(true);
            permanentContractService.save(empPermanentContract);
        }
    }

    @PostMapping("employee/check-rfid-duplicates/{rfid}")
    public void checkRfidDuplicates(@PathVariable("rfid") String rfid){
        Employee employee = employeeService.getByRfid(rfid);
        if (employee!=null){
            throw new EntityNotFoundException("RFID already configured");
        }
    }

}