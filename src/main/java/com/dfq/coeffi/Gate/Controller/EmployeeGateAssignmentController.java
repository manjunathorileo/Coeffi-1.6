package com.dfq.coeffi.Gate.Controller;

import com.dfq.coeffi.Gate.Entity.*;
import com.dfq.coeffi.Gate.GateAccessMssql;
import com.dfq.coeffi.Gate.GateAccessService;
import com.dfq.coeffi.Gate.Repository.EmployeeGateAssignmentRepository;
import com.dfq.coeffi.Gate.Service.EmployeeGateAssignmentService;
import com.dfq.coeffi.Gate.Service.GateService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.employeePermanentContract.services.PermanentContractService;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.user.User;
import com.dfq.coeffi.repository.hr.EmployeeRepository;
import com.dfq.coeffi.service.UserService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.superadmin.Services.CompanyConfigureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class EmployeeGateAssignmentController extends BaseController {
    @Autowired
    EmployeeGateAssignmentService employeeGateAssignmentService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    GateService gateService;
    @Autowired
    EmployeeGateAssignmentRepository employeeGateAssignmentRepository;
    @Autowired
    UserService userService;
    @Autowired
    CompanyConfigureService companyConfigureService;
    @Autowired
    GateAccessService gateAccessService;

    @PostMapping("employee-gate-assignment")
    public ResponseEntity<List<EmployeeGateAssignment>> assignGate(@Valid @RequestBody EmployeeGateAssignmentDto employeeGateAssignmentDto) {
        List<EmployeeGateAssignment> employeeGateAssignmentList = new ArrayList<>();

        List<Employee> employeesByType = employeeService.getEmployeeByType(employeeGateAssignmentDto.getEmployeeType(), true);
        List<Gate> inGates = new ArrayList<>();
        String ingatesRef = "";
        String outgatesRef = "";
        for (long gateId : employeeGateAssignmentDto.getInGateIds()) {
            Gate gateIn = gateService.getGateById(gateId);
            inGates.add(gateIn);
            ingatesRef = ingatesRef +gateIn.getGateNumber()+"," ;
        }

        List<Gate> outGates = new ArrayList<>();
        for (long gateId : employeeGateAssignmentDto.getOutGateIds()) {
            Gate gateOut = gateService.getGateById(gateId);
            outGates.add(gateOut);
            outgatesRef = outgatesRef + gateOut.getGateNumber()+",";
        }
        for (Employee employee : employeesByType) {
            EmployeeGateAssignment employeeGateAssignment = new EmployeeGateAssignment();
            boolean skip = checkEmployeeGateAlreadyAssigned(employee.getId(),employee.getEmployeeType(),employee.getEmployeeCode());
            if(!skip) {
                employeeGateAssignment.setEmployee(employee);
                employeeGateAssignment.setInGates(inGates);
                employeeGateAssignment.setOutGates(outGates);
                employeeGateAssignment.setInGateNumbersList(ingatesRef);
                employeeGateAssignment.setOutGateNumbersList(outgatesRef);

                for (Gate ingate : inGates){
                    List<GateAccessMssql> gateAccessMssqlOld =
                            gateAccessService.getByEmployeeAndController(employee.getEmployeeCode(),ingate.getGateNumber());
                    if (gateAccessMssqlOld .isEmpty()) {
                        GateAccessMssql gateAccessMssqlDoor1 = new GateAccessMssql();
                        gateAccessMssqlDoor1.setEmpId(employee.getEmployeeCode());
                        gateAccessMssqlDoor1.setControllerCode(ingate.getGateNumber());
                        gateAccessMssqlDoor1.setDoorId("1");
                        gateAccessService.createGateAccess(gateAccessMssqlDoor1);

                        GateAccessMssql gateAccessMssqlDoor2 = new GateAccessMssql();
                        gateAccessMssqlDoor2.setEmpId(employee.getEmployeeCode());
                        gateAccessMssqlDoor2.setControllerCode(ingate.getGateNumber());
                        gateAccessMssqlDoor2.setDoorId("2");
                        gateAccessService.createGateAccess(gateAccessMssqlDoor2);
                    }
                }

                for (Gate outGate : outGates){

                    List<GateAccessMssql> gateAccessMssqlOld =
                            gateAccessService.getByEmployeeAndController(employee.getEmployeeCode(),outGate.getGateNumber());
                    if (gateAccessMssqlOld .isEmpty()) {
                        GateAccessMssql gateAccessMssqlDoor1 = new GateAccessMssql();
                        gateAccessMssqlDoor1.setEmpId(employee.getEmployeeCode());
                        gateAccessMssqlDoor1.setControllerCode(outGate.getGateNumber());
                        gateAccessMssqlDoor1.setDoorId("1");
                        gateAccessService.createGateAccess(gateAccessMssqlDoor1);

                        GateAccessMssql gateAccessMssqlDoor2 = new GateAccessMssql();
                        gateAccessMssqlDoor2.setEmpId(employee.getEmployeeCode());
                        gateAccessMssqlDoor2.setControllerCode(outGate.getGateNumber());
                        gateAccessMssqlDoor2.setDoorId("2");
                        gateAccessService.createGateAccess(gateAccessMssqlDoor2);

                    }
                }
                employeeGateAssignmentService.saveEmployeeGate(employeeGateAssignment);
            }
            employeeGateAssignmentList.add(employeeGateAssignment);
        }
        return new ResponseEntity<>(employeeGateAssignmentList, HttpStatus.CREATED);
    }

//    @PostMapping("employee-gate-assignment")
//    public ResponseEntity<List<EmployeeGateAssignment>> assignGate(@Valid @RequestBody EmployeeGateAssignmentDto employeeGateAssignmentDto) {
//        List<EmployeeGateAssignment> employeeGateAssignmentList = new ArrayList<>();
//        for (long id : employeeGateAssignmentDto.getEmployeeIds()) {
//            EmployeeGateAssignment employeeGateAssignment = new EmployeeGateAssignment();
//            Optional<Employee> employeeObj = employeeService.getEmployee(id);
//            if (!employeeObj.isPresent()) {
//                throw new EntityNotFoundException("Employee Not Found: " + id);
//            }
//            Employee employee = employeeObj.get();
//            Gate inGate = gateService.getGateById(employeeGateAssignmentDto.getInGateId());
//            Gate outGate = gateService.getGateById(employeeGateAssignmentDto.getOutGateId());
//            if (inGate == null) {
//                throw new EntityNotFoundException("InGate Not Found: " + employeeGateAssignmentDto.getInGateId());
//            }
//            if (outGate == null) {
//                throw new EntityNotFoundException("OutGate Not Found: " + employeeGateAssignmentDto.getOutGateId());
//            }
//            checkEmployeeGateAlreadyAssigned(employee.getId());
//            employeeGateAssignment.setEmployee(employee);
//            employeeGateAssignment.setInGate(inGate);
//            employeeGateAssignment.setOutGate(outGate);
//            employeeGateAssignmentService.saveEmployeeGate(employeeGateAssignment);
//            employeeGateAssignmentList.add(employeeGateAssignment);
//
//        }
//        return new ResponseEntity<>(employeeGateAssignmentList, HttpStatus.CREATED);
//    }

    @Autowired
    PermanentContractService permanentContractService;

    private boolean checkEmployeeGateAlreadyAssigned(long employeeId, EmployeeType employeeType,String employeeCode) {
        EmployeeGateAssignment employeeGateAssignments = employeeGateAssignmentRepository.findByEmployeeId(employeeId);
        boolean skip = false;
        if (employeeGateAssignments != null) {
//            if (employeeGateAssignments.getInGate() != null) {
//                throw new EntityNotFoundException("InGate Already Assigned :" + employeeGateAssignments.getEmployee().getFirstName());
//            } else if (employeeGateAssignments.getOutGate() != null) {
//                throw new EntityNotFoundException("OutGate Already Assigned :" + employeeGateAssignments.getEmployee().getFirstName());
//            }
            System.out.printf("assigned already checking next employee");
            skip = true;
        }else if(employeeType.equals(EmployeeType.CONTRACT) || employeeType.equals(EmployeeType.PERMANENT_CONTRACT)){
            EmpPermanentContract empPermanentContract = permanentContractService.get(employeeCode);
            if (empPermanentContract!=null){
                if (empPermanentContract.isStatus()){
                    skip = false;
                }
            }else if (empPermanentContract == null){
                skip = true;
            }
        }
        return skip;
    }

    @GetMapping("employee-gate-assigned-list")
    public ResponseEntity<List<EmployeeGateDto>> getEmployeesGate() {
        List<EmployeeGateDto> employeeGateDtos = new ArrayList<>();
        List<EmployeeGateAssignment> employeeGateAssignmentList = employeeGateAssignmentRepository.findAll();
        for (EmployeeGateAssignment e : employeeGateAssignmentList) {
            EmployeeGateDto employeeGateDto = new EmployeeGateDto();
            Employee employee = e.getEmployee();
            employeeGateDto.setEmployeeNumber(employee.getId());
            employeeGateDto.setEmployeeCode(employee.getEmployeeCode());
            employeeGateDto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            employeeGateDto.setEmployeeType(employee.getEmployeeType());
            if (employee.getDepartment() != null) {
                employeeGateDto.setDepartment(employee.getDepartment().getName());
            }
            employeeGateDto.setInGates(e.getInGates());
            employeeGateDto.setOutGates(e.getOutGates());
            employeeGateDto.setInGatesRef(e.getInGateNumbersList());
            employeeGateDto.setOutGatesRef(e.getOutGateNumbersList());
            employeeGateDtos.add(employeeGateDto);
        }
        return new ResponseEntity<>(employeeGateDtos, HttpStatus.OK);
    }


    @PostMapping("edit-employee-assigned-gate")
    public ResponseEntity<EmployeeGateAssignment> editGate(@RequestBody EmployeeGateAssignmentDto employeeGateAssignmentDto) {
        EmployeeGateAssignment employeeGateAssignment = employeeGateAssignmentRepository.findByEmployeeId(employeeGateAssignmentDto.getEmpId());
        List<Gate> inGates = new ArrayList<>();
        String ingatesRef = "";
        String outgatesRef = "";
        for (long gateId : employeeGateAssignmentDto.getInGateIds()) {
            Gate gateIn = gateService.getGateById(gateId);
            inGates.add(gateIn);
            ingatesRef = ingatesRef + gateIn.getGateNumber()+",";
        }

        List<Gate> outGates = new ArrayList<>();
        for (long gateId : employeeGateAssignmentDto.getOutGateIds()) {
            Gate gateOut = gateService.getGateById(gateId);
            outGates.add(gateOut);
            outgatesRef = outgatesRef +gateOut.getGateNumber()+",";
        }
        employeeGateAssignment.setInGates(inGates);
        employeeGateAssignment.setOutGates(outGates);
        employeeGateAssignment.setInGateNumbersList(ingatesRef);
        employeeGateAssignment.setOutGateNumbersList(outgatesRef);

        for (Gate ingate : inGates){
            List<GateAccessMssql> gateAccessMssqlOld =
                    gateAccessService.getByEmployeeAndController(employeeGateAssignment.getEmployee().getEmployeeCode(),ingate.getGateNumber());
            if (gateAccessMssqlOld .isEmpty()) {
                GateAccessMssql gateAccessMssqlDoor1 = new GateAccessMssql();
                gateAccessMssqlDoor1.setEmpId(employeeGateAssignment.getEmployee().getEmployeeCode());
                gateAccessMssqlDoor1.setControllerCode(ingate.getGateNumber());
                gateAccessMssqlDoor1.setDoorId("1");
                gateAccessService.createGateAccess(gateAccessMssqlDoor1);

                GateAccessMssql gateAccessMssqlDoor2 = new GateAccessMssql();
                gateAccessMssqlDoor2.setEmpId(employeeGateAssignment.getEmployee().getEmployeeCode());
                gateAccessMssqlDoor2.setControllerCode(ingate.getGateNumber());
                gateAccessMssqlDoor2.setDoorId("2");
                gateAccessService.createGateAccess(gateAccessMssqlDoor2);
            }
        }

        for (Gate outGate : outGates){

            List<GateAccessMssql> gateAccessMssqlOld =
                    gateAccessService.getByEmployeeAndController(employeeGateAssignment.getEmployee().getEmployeeCode(),outGate.getGateNumber());
            if (gateAccessMssqlOld .isEmpty()) {
                GateAccessMssql gateAccessMssqlDoor1 = new GateAccessMssql();
                gateAccessMssqlDoor1.setEmpId(employeeGateAssignment.getEmployee().getEmployeeCode());
                gateAccessMssqlDoor1.setControllerCode(outGate.getGateNumber());
                gateAccessMssqlDoor1.setDoorId("1");
                gateAccessService.createGateAccess(gateAccessMssqlDoor1);

                GateAccessMssql gateAccessMssqlDoor2 = new GateAccessMssql();
                gateAccessMssqlDoor2.setEmpId(employeeGateAssignment.getEmployee().getEmployeeCode());
                gateAccessMssqlDoor2.setControllerCode(outGate.getGateNumber());
                gateAccessMssqlDoor2.setDoorId("2");
                gateAccessService.createGateAccess(gateAccessMssqlDoor2);

            }
        }

        employeeGateAssignmentService.saveEmployeeGate(employeeGateAssignment);
        return new ResponseEntity<>(employeeGateAssignment, HttpStatus.OK);
    }

//    @PostMapping("edit-employee-assigned-gate/{eid}/{inGateId}/{outGateId}")
//    public ResponseEntity<EmployeeGateAssignment> editGate(@PathVariable("eid") long eid, @PathVariable long inGateId, @PathVariable long outGateId) {
//        EmployeeGateAssignment employeeGateAssignment = employeeGateAssignmentRepository.findByEmployeeId(eid);
//        Gate inGate = gateService.getGateById(inGateId);
//        Gate outGate = gateService.getGateById(outGateId);
//        employeeGateAssignment.setInGate(inGate);
//        employeeGateAssignment.setOutGate(outGate);
//        employeeGateAssignmentService.saveEmployeeGate(employeeGateAssignment);
//        return new ResponseEntity<>(employeeGateAssignment, HttpStatus.OK);
//    }


    //User Management Details Edit

//    @GetMapping("user-management-details")
//    public ResponseEntity<List<UserManagementDto>> getUsers() {
//        List<Employee> employeeList = employeeService.findAll();
//        List<UserManagementDto> userManagementDtoList = new ArrayList<>();
//
//        for (Employee e : employeeList) {
//            UserManagementDto userManagementDto = new UserManagementDto();
//            userManagementDto.setEmployeeId(e.getId());
//            userManagementDto.setEmployeeCode(e.getEmployeeCode());
//            userManagementDto.setEmployeeName(e.getFirstName() + " " + e.getLastName());
//            userManagementDto.setEmployeeType(e.getEmployeeType());
//            userManagementDto.setDepartment(e.getDepartment().getName());
//            User user = e.getEmployeeLogin();
//            if (user != null) {
//                userManagementDto.setUserName(user.getEmail());
//                userManagementDto.setPassword(user.getPassword());
//                userManagementDtoList.add(userManagementDto);
//            }
//        }
//        return new ResponseEntity<>(userManagementDtoList, HttpStatus.OK);
//    }

    @GetMapping("user-management-details")
    public ResponseEntity<User> getAllUsers() {
        List<User> users = userService.getUsers();
        List<User> userList = new ArrayList<>();

        for (User user : users) {
            if (user.isActive()) {
                Optional<Employee> employee = employeeService.getEmployeeByLogin(user.getId());
                if (employee.isPresent()) {
                    user.setEmpId(employee.get().getId());
                }
                userList.add(user);
            }
        }
        return new ResponseEntity(userList, HttpStatus.OK);
    }

    @PostMapping("user-management-details-edit/{eid}")
    public ResponseEntity<Employee> saveUser(@RequestBody UserManagementDto userManagementDto, @PathVariable("eid") long eid) {
        Employee employee = employeeRepository.findOne(eid);
        User user = employee.getEmployeeLogin();
        user.setEmail(userManagementDto.getUserName());
        user.setPassword(userManagementDto.getPassword());
        employee.setEmployeeLogin(user);
        employeeService.save(employee);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    //hr report based on gender
    @GetMapping("sixteen-twenty")
    public ResponseEntity<List<HrReportDto>> getFifteen() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            long age = Long.valueOf(e.getAge());
            if (age >= 16 && age <= 20) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(age);
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(age);
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("fifteen-twenty-count")
    public ResponseEntity<HrReportDto> getFifteenCount() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) > 16 && Long.valueOf(e.getAge()) < 20) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }

    @GetMapping("twentyone-twentyfive")
    public ResponseEntity<List<HrReportDto>> get2125() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 21 && Long.valueOf(e.getAge()) <= 25) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("twentyone-twentyfive-count")
    public ResponseEntity<HrReportDto> get2125Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 21 && Long.valueOf(e.getAge()) <= 25) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }

    @GetMapping("twentysix-thirty")
    public ResponseEntity<List<HrReportDto>> get2630() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 26 && Long.valueOf(e.getAge()) <= 30) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("twentysix-thirty-count")
    public ResponseEntity<HrReportDto> get2630Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 26 && Long.valueOf(e.getAge()) <= 30) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }

    @GetMapping("thirtyone-thirtyfive")
    public ResponseEntity<List<HrReportDto>> get3135() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 31 && Long.valueOf(e.getAge()) <= 35) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("thirtyone-thirtyfive-count")
    public ResponseEntity<HrReportDto> get3135Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 31 && Long.valueOf(e.getAge()) <= 35) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }

    @GetMapping("thirtysix-fourty")
    public ResponseEntity<List<HrReportDto>> get3540() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 36 && Long.valueOf(e.getAge()) <= 40) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("thirtysix-fourty-count")
    public ResponseEntity<HrReportDto> get3540Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 36 && Long.valueOf(e.getAge()) <= 40) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }

    @GetMapping("fourtyone-fourtyfive")
    public ResponseEntity<List<HrReportDto>> get4145() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 41 && Long.valueOf(e.getAge()) <= 45) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("fourtyone-fourtyfive-count")
    public ResponseEntity<HrReportDto> get4145Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 41 && Long.valueOf(e.getAge()) <= 45) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }

    @GetMapping("fourtysix-fifty")
    public ResponseEntity<List<HrReportDto>> get4650() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 46 && Long.valueOf(e.getAge()) <= 50) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("fourtysix-fifty-count")
    public ResponseEntity<HrReportDto> get4650Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 46 && Long.valueOf(e.getAge()) <= 50) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }

    @GetMapping("fiftyone-fiftyfive")
    public ResponseEntity<List<HrReportDto>> get5155() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 51 && Long.valueOf(e.getAge()) <= 55) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("fiftyone-fiftyfive-count")
    public ResponseEntity<HrReportDto> get5155Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 51 && Long.valueOf(e.getAge()) <= 55) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }


    @GetMapping("fiftysix-sixty")
    public ResponseEntity<List<HrReportDto>> get5660() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 56 && Long.valueOf(e.getAge()) <= 60) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("fiftysix-sixty-count")
    public ResponseEntity<HrReportDto> get5660Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 56 && Long.valueOf(e.getAge()) <= 60) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }

    @GetMapping("sixtyone-sixtyfive")
    public ResponseEntity<List<HrReportDto>> get6165() {
        List<Employee> employeeList = employeeService.findAll();
        List<HrReportDto> hrReportDtoList = new ArrayList<>();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            HrReportDto hrReportDto = new HrReportDto();
            if (Long.valueOf(e.getAge()) >= 61 && Long.valueOf(e.getAge()) <= 65) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                    hrReportDto.setEmployeeId(e.getId());
                    hrReportDto.setEmployeeName(e.getFirstName());
                    hrReportDto.setDepartment(e.getDepartmentName());
                    hrReportDto.setAge(Long.valueOf(e.getAge()));
                    hrReportDto.setGender(e.getGender());
                    hrReportDtoList.add(hrReportDto);
                }
            }

        }
        return new ResponseEntity<>(hrReportDtoList, HttpStatus.OK);
    }

    @GetMapping("sixtyone-sixtyfive-count")
    public ResponseEntity<HrReportDto> get6165Count() {
        List<Employee> employeeList = employeeService.findAll();
        HrReportDto hrReportDto = new HrReportDto();
        long mCount = 0, fCount = 0;
        for (Employee e : employeeList) {
            if (Long.valueOf(e.getAge()) >= 61 && Long.valueOf(e.getAge()) <= 65) {
                if (e.getGender().equalsIgnoreCase("male")) {
                    mCount = mCount + 1;
                }
                if (e.getGender().equalsIgnoreCase("female")) {
                    fCount = fCount + 1;
                }
            }
        }
        hrReportDto.setMCount(mCount);
        hrReportDto.setFcount(fCount);
        return new ResponseEntity<>(hrReportDto, HttpStatus.OK);
    }


    @DeleteMapping("gate-assignment/delete/{empId}")
    public void deleteByEmpId(@PathVariable long empId){
        EmployeeGateAssignment employeeGateAssignment = employeeGateAssignmentRepository.findByEmployeeId(empId);
        employeeGateAssignmentRepository.delete(employeeGateAssignment);
    }
}
