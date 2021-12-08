package com.dfq.coeffi.controller.leave;

import com.dfq.coeffi.CompanySettings.Entity.CompanyName;
import com.dfq.coeffi.EvacuationDashboard.EvacuationDto;
import com.dfq.coeffi.Gate.Entity.EmployeeGateAssignment;
import com.dfq.coeffi.Gate.Entity.Gate;
import com.dfq.coeffi.Gate.Service.EmployeeGateAssignmentService;
import com.dfq.coeffi.Gate.Service.GateService;
import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.GatePassDto;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.leave.EmployeeGatePass;
import com.dfq.coeffi.entity.leave.GatePassStatus;
import com.dfq.coeffi.entity.leave.LeaveStatus;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.evacuationApi.InsideCsvRaw;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.leave.EmployeeGatePassService;
import com.dfq.coeffi.util.DateUtil;
import com.dfq.coeffi.visitor.Entities.VisitorPass;
import jxl.format.BorderLineStyle;
import jxl.write.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static jxl.format.Alignment.*;
import static jxl.format.Alignment.LEFT;

@RestController
@Slf4j
public class EmployeeGatePassController extends BaseController {

    private final AcademicYearService academicYearService;
    private final EmployeeGatePassService employeeGatePassService;
    private final EmployeeService employeeService;


    @Autowired
    public EmployeeGatePassController(EmployeeGatePassService employeeGatePassService, AcademicYearService academicYearService, EmployeeService employeeService) {
        this.employeeGatePassService = employeeGatePassService;
        this.academicYearService = academicYearService;
        this.employeeService = employeeService;
    }

    @Autowired
    GateService gateService;
    @Autowired
    EmployeeGateAssignmentService employeeGateAssignmentService;

    @PostMapping("gate-pass")
    public ResponseEntity<EmployeeGatePass> applyGatePass(@Valid @RequestBody EmployeeGatePass employeeGatePass) {
        Optional<Employee> employee = employeeService.getEmployee(employeeGatePass.getEmployeeId());
        if (!employee.isPresent()) {
            throw new EntityNotFoundException("Emp not found");
        }
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
        if (!academicYear.isPresent()) {
            throw new EntityNotFoundException("AcademicYear not found");
        }

        if (employee.get().getFirstApprovalManager() == null) {
            throw new EntityNotFoundException("First Manager not found");
        }
//        if (employee.get().getSecondApprovalManager() == null) {
//            throw new EntityNotFoundException("Second Manager not found");
//        }
        employeeGatePass.setEmployeeObject(employee.get());
        employeeGatePass.setEmployeeName(employee.get().getFirstName() + " " + employee.get().getLastName());
        employeeGatePass.setAcademicYear(academicYear.get());
        employeeGatePass.setGatePassStatus(GatePassStatus.CREATED);
        employeeGatePass.setFirstApprover(employee.get().getFirstApprovalManager().getId());
        if (employee.get().getSecondApprovalManager() != null) {
            employeeGatePass.setSecondApprover(employee.get().getSecondApprovalManager().getId());
        }
        EmployeeGatePass persistedGatePass = employeeGatePassService.createEmployeeGatePass(employeeGatePass);

        return new ResponseEntity<>(persistedGatePass, HttpStatus.CREATED);
    }

    @GetMapping("gate-pass")
    public ResponseEntity<List<EmployeeGatePass>> approvedTodaysGatePass() {
        List<EmployeeGatePass> approvedEmployeeGatePass = employeeGatePassService.getApprovedEmployeeGatePass(GatePassStatus.APPROVED, DateUtil.getTodayDate());
        ArrayList approvedList = new ArrayList();
        for (EmployeeGatePass employeeGatePass : approvedEmployeeGatePass) {
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeGatePass.getEmployeeId());
            Employee employee = new Employee();
            employee.setFirstName(employeeObj.get().getFirstName());
            employee.setLastName(employeeObj.get().getLastName());
            employee.setId(employeeObj.get().getId());
            employeeGatePass.setEmployeeObject(employee);
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            approvedList.add(employeeGatePass);
        }
        return new ResponseEntity(approvedList, HttpStatus.OK);
    }

    @GetMapping("gate-pass/un-approve")
    public ResponseEntity<EmployeeGatePass> gateUnapprovedGatePass() {
        List<EmployeeGatePass> unapprovedGatePass = employeeGatePassService.getApprovedEmployeeGatePass(GatePassStatus.CREATED, DateUtil.getTodayDate());
        ArrayList unApprovedList = new ArrayList();
        for (EmployeeGatePass employeeGatePass : unapprovedGatePass) {
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeGatePass.getEmployeeId());
            Employee employee = new Employee();
            employee.setFirstName(employeeObj.get().getFirstName());
            employee.setLastName(employeeObj.get().getLastName());
            employee.setId(employeeObj.get().getId());
            employeeGatePass.setEmployeeObject(employee);
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            unApprovedList.add(employeeGatePass);
        }
        return new ResponseEntity(unApprovedList, HttpStatus.OK);
    }

    @PostMapping("gate-pass/approval")
    public ResponseEntity<EmployeeGatePass> gatGatePassApprove(@RequestBody GatePassDto gatePassDto) {
        Optional<EmployeeGatePass> employeeGatePassObj = employeeGatePassService.getGatePassById(gatePassDto.id);
        EmployeeGatePass employeeGatePass = employeeGatePassObj.get();
        employeeGatePass.setGatePassStatus(gatePassDto.gatePassStatus);
        employeeGatePass.setDescription(gatePassDto.description);
        Optional<Employee> employeeObj = employeeService.getEmployee(employeeGatePass.getEmployeeId());
        employeeGatePass.setEmployeeName(employeeObj.get().getFirstName() + " " + employeeObj.get().getLastName());
        employeeGatePassService.createEmployeeGatePass(employeeGatePass);
        return new ResponseEntity(employeeGatePass, HttpStatus.OK);
    }

    @GetMapping("gate-pass/approved-list")
    public ResponseEntity<List<EmployeeGatePass>> approvedList() {
        List<EmployeeGatePass> approvedEmployeeGatePass = employeeGatePassService.getApprovedEmployeeGatePass(GatePassStatus.APPROVED);
        ArrayList approvedList = new ArrayList();
        for (EmployeeGatePass employeeGatePass : approvedEmployeeGatePass) {
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeGatePass.getEmployeeId());
            Employee employee = new Employee();
            employee.setFirstName(employeeObj.get().getFirstName());
            employee.setLastName(employeeObj.get().getLastName());
            employee.setId(employeeObj.get().getId());
            employee.setEmployeeCode(employeeObj.get().getEmployeeCode());
            if (employeeObj.get().getDepartment() != null) {
                employee.setDepartmentName(employeeObj.get().getDepartment().getName());
            }
            employeeGatePass.setEmployeeObject(employee);
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            approvedList.add(employeeGatePass);
        }
        return new ResponseEntity(approvedList, HttpStatus.OK);
    }

    @GetMapping("gate-pass/rejected-list")
    public ResponseEntity<List<EmployeeGatePass>> rejectedList() {
        List<EmployeeGatePass> rejectedGatePass = employeeGatePassService.getApprovedEmployeeGatePass(GatePassStatus.REJECTED);
        ArrayList rejectedList = new ArrayList();
        for (EmployeeGatePass employeeGatePass : rejectedGatePass) {
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeGatePass.getEmployeeId());
            Employee employee = new Employee();
            employee.setFirstName(employeeObj.get().getFirstName());
            employee.setLastName(employeeObj.get().getLastName());
            employee.setId(employeeObj.get().getId());
            employeeGatePass.setEmployeeObject(employee);
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            rejectedList.add(employeeGatePass);
        }
        return new ResponseEntity(rejectedList, HttpStatus.OK);
    }

    @GetMapping("gate-pass/by-employee/{employeeId}")
    public ResponseEntity<List<EmployeeGatePass>> approvedGatePassByEmployeeId(@PathVariable long employeeId) {
        List<EmployeeGatePass> employeeGatePasses = employeeGatePassService.getEmployeeGatePassByEmployeeAndStatus(GatePassStatus.CREATED, employeeId);
        List<EmployeeGatePass> employeeGatePassForward = employeeGatePassService.getEmployeeGatePassByEmployeeAndStatus(GatePassStatus.FORWARD, employeeId);

        for (EmployeeGatePass employeeGatePass : employeeGatePassForward) {
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeGatePass.getEmployeeId());
            Employee employee = new Employee();
            employee.setFirstName(employeeObj.get().getFirstName());
            employee.setLastName(employeeObj.get().getLastName());
            employee.setId(employeeObj.get().getId());
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            employeeGatePass.setEmployeeObject(employee);

            employeeGatePasses.add(employeeGatePass);
        }
        return new ResponseEntity(employeeGatePasses, HttpStatus.OK);
    }

    @GetMapping("gate-pass/approved-list/{employeeId}")
    public ResponseEntity<List<EmployeeGatePass>> approvedListByEmployeeId(@PathVariable long employeeId) {
        List<EmployeeGatePass> approvedEmployeeGatePass = employeeGatePassService.getEmployeeGatePassByEmployeeAndStatus(GatePassStatus.APPROVED, employeeId);
        ArrayList approvedList = new ArrayList();
        for (EmployeeGatePass employeeGatePass : approvedEmployeeGatePass) {
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeGatePass.getEmployeeId());
            Employee employee = new Employee();
            employee.setFirstName(employeeObj.get().getFirstName());
            employee.setLastName(employeeObj.get().getLastName());

            employee.setId(employeeObj.get().getId());
            employeeGatePass.setEmployeeObject(employee);
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            approvedList.add(employeeGatePass);
        }
        return new ResponseEntity(approvedList, HttpStatus.OK);
    }

    @GetMapping("gate-pass/rejected-list/{employeeId}")
    public ResponseEntity<List<EmployeeGatePass>> rejectedListByEmployeeId(@PathVariable long employeeId) {
        List<EmployeeGatePass> rejectedGatePass = employeeGatePassService.getEmployeeGatePassByEmployeeAndStatus(GatePassStatus.REJECTED, employeeId);
        ArrayList rejectedList = new ArrayList();
        for (EmployeeGatePass employeeGatePass : rejectedGatePass) {
            Optional<Employee> employeeObj = employeeService.getEmployee(employeeGatePass.getEmployeeId());
            Employee employee = new Employee();
            employee.setFirstName(employeeObj.get().getFirstName());
            employee.setLastName(employeeObj.get().getLastName());

            employee.setId(employeeObj.get().getId());
            employeeGatePass.setEmployeeObject(employee);
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            rejectedList.add(employeeGatePass);
        }
        return new ResponseEntity(rejectedList, HttpStatus.OK);
    }

    @GetMapping("gate-pass/get-created-list/{firstApprover}")
    public ResponseEntity<List<EmployeeGatePass>> createdListforFirstApprover(@PathVariable long firstApprover) {
        List<EmployeeGatePass> createdGatePass = employeeGatePassService.getEmployeeGatePassByFirstApproverAndStatus(GatePassStatus.CREATED, firstApprover);
        List<EmployeeGatePass> employeeGatePassList = new ArrayList<>();
        for (EmployeeGatePass employeeGatePass : createdGatePass) {
            Employee employee = new Employee();
            employee.setId(employeeGatePass.getEmployeeObject().getId());
            employee.setFirstName(employeeGatePass.getEmployeeObject().getFirstName());
            employee.setLastName(employeeGatePass.getEmployeeObject().getLastName());
            employee.setEmployeeCode(employeeGatePass.getEmployeeObject().getEmployeeCode());
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            employeeGatePass.setEmployeeObject(employee);
            employeeGatePassList.add(employeeGatePass);
        }
        if (!(createdGatePass.size() > 0 && createdGatePass != null)) {
            throw new EntityNotFoundException("No EmployeeGatePass created");
        }
        return new ResponseEntity(employeeGatePassList, HttpStatus.OK);
    }

    @PostMapping("gate-pass/forward-gatepass")
    public ResponseEntity<List<EmployeeGatePass>> forwardPassToSecondManager(@RequestBody GatePassDto gatePassDto) {
        Optional<EmployeeGatePass> forwardEmployeeGatePass = employeeGatePassService.getGatePassById(gatePassDto.id);
        if (!forwardEmployeeGatePass.isPresent()) {
            throw new EntityNotFoundException();
        }
        EmployeeGatePass employeeGatePass = forwardEmployeeGatePass.get();
        employeeGatePass.setGatePassStatus(GatePassStatus.FORWARD);
        employeeGatePass.setId(gatePassDto.id);
        employeeGatePass.setDescription(gatePassDto.description);
        employeeGatePassService.createEmployeeGatePass(employeeGatePass);
        return new ResponseEntity(employeeGatePass, HttpStatus.OK);
    }

    @PostMapping("gate-pass/reject-gatepass")
    public ResponseEntity<List<EmployeeGatePass>> rejectGatepass(@RequestBody GatePassDto gatePassDto) {
        Optional<EmployeeGatePass> rejectGatePass = employeeGatePassService.getGatePassById(gatePassDto.id);
        if (!rejectGatePass.isPresent()) {
            throw new EntityNotFoundException();
        }
        EmployeeGatePass employeeGatePass = rejectGatePass.get();
        employeeGatePass.setGatePassStatus(GatePassStatus.REJECTED);
        employeeGatePass.setDescription(gatePassDto.description);
        employeeGatePass.setId(gatePassDto.id);
        employeeGatePassService.createEmployeeGatePass(employeeGatePass);
        return new ResponseEntity(employeeGatePass, HttpStatus.OK);
    }

    @PostMapping("gate-pass/approve-gatepass")
    public ResponseEntity<List<EmployeeGatePass>> approveGatepass(@RequestBody GatePassDto gatePassDto) {
        Optional<EmployeeGatePass> approveGatePass = employeeGatePassService.getGatePassById(gatePassDto.id);
        if (!approveGatePass.isPresent()) {
            throw new EntityNotFoundException();
        }
        EmployeeGatePass employeeGatePass = approveGatePass.get();
        employeeGatePass.setGatePassStatus(GatePassStatus.APPROVED);
        employeeGatePass.setId(gatePassDto.id);
        employeeGatePassService.createEmployeeGatePass(employeeGatePass);
        return new ResponseEntity(employeeGatePass, HttpStatus.OK);
    }

    @GetMapping("gate-pass/get-forwarded-gatepass/{secondApprover}")
    public ResponseEntity<List<EmployeeGatePass>> forwarderListBySecondManagerId(@PathVariable long secondApprover) {
        List<EmployeeGatePass> forwardedGatePass = employeeGatePassService.getEmployeeGatePassBySecondApproverAndStatus(GatePassStatus.FORWARD, secondApprover);
        List<EmployeeGatePass> lightEmployeeGatePass = new ArrayList<>();
        for (EmployeeGatePass employeeGatePass : forwardedGatePass) {
            Employee employeeOfGatePass = employeeGatePass.getEmployeeObject();
            Employee employee = new Employee();
            employee.setId(employeeOfGatePass.getId());
            employee.setFirstName(employeeOfGatePass.getFirstName());
            employee.setLastName(employeeOfGatePass.getLastName());
            employee.setEmployeeCode(employeeOfGatePass.getEmployeeCode());
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            employeeGatePass.setEmployeeObject(employee);
            lightEmployeeGatePass.add(employeeGatePass);
        }
        if (!(forwardedGatePass.size() > 0 && forwardedGatePass != null)) {
            throw new EntityNotFoundException("No EmployeeGatePass Forwared for Second Approver");
        }
        return new ResponseEntity(lightEmployeeGatePass, HttpStatus.OK);
    }

    @GetMapping("gate-pass/get-all-gatepass-manager/{managerId}")
    public ResponseEntity<List<EmployeeGatePass>> getAllGatepassListForFirstManagerId(@PathVariable long managerId) {
        List<EmployeeGatePass> allGatePassList = employeeGatePassService.getAllGatePassByApprover(managerId, managerId);
        List<EmployeeGatePass> lightEmployeeGatePass = new ArrayList<>();
        for (EmployeeGatePass employeeGatePass : allGatePassList) {
            Employee employeeOfGatePass = employeeGatePass.getEmployeeObject();
            Employee employee = new Employee();
            employee.setId(employeeOfGatePass.getId());
            employee.setFirstName(employeeOfGatePass.getFirstName());
            employee.setLastName(employeeOfGatePass.getLastName());
            employee.setEmployeeCode(employeeOfGatePass.getEmployeeCode());
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            employeeGatePass.setEmployeeObject(employee);
            lightEmployeeGatePass.add(employeeGatePass);
        }
        return new ResponseEntity(lightEmployeeGatePass, HttpStatus.OK);
    }

    @GetMapping("gate-pass/get-all-gatepass-second-manager/{managerId}")
    public ResponseEntity<List<EmployeeGatePass>> getAllGatepassListForSecondManagerId(@PathVariable long managerId) {
        Optional<Employee> employeeObj = employeeService.getEmployeeBySecondManagerId(managerId);
        if (!employeeObj.isPresent()) {
            throw new EntityNotFoundException("Employee Gatepass not found");
        }
        Employee employee1 = employeeObj.get();
        List<EmployeeGatePass> allGatePassList = employeeGatePassService.getGatePassByEmployeeId(employee1.getId());
        List<EmployeeGatePass> lightEmployeeGatePass = new ArrayList<>();
        for (EmployeeGatePass employeeGatePass : allGatePassList) {
            Employee employeeOfGatePass = employeeGatePass.getEmployeeObject();
            Employee employee = new Employee();
            employee.setId(employeeOfGatePass.getId());
            employee.setFirstName(employeeOfGatePass.getFirstName());
            employee.setLastName(employeeOfGatePass.getLastName());
            employee.setEmployeeCode(employeeOfGatePass.getEmployeeCode());
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            employeeGatePass.setEmployeeObject(employee);
            lightEmployeeGatePass.add(employeeGatePass);
        }
        return new ResponseEntity(lightEmployeeGatePass, HttpStatus.OK);
    }

    @GetMapping("gate-pass/get-all-gatepass-by-employee/{employeeId}")
    public ResponseEntity<List<EmployeeGatePass>> getAllGatepassByEmployeeID(@PathVariable("employeeId") long employeeId) {
        List<EmployeeGatePass> allGatePassListByEmployeeId = employeeGatePassService.getGatePassByEmployeeId(employeeId);
        List<EmployeeGatePass> lightEmployeeGatePass = new ArrayList<>();
        for (EmployeeGatePass employeeGatePass : allGatePassListByEmployeeId) {
            Employee employeeOfGatePass = employeeGatePass.getEmployeeObject();
            Employee employee = new Employee();
            employee.setId(employeeOfGatePass.getId());
            employee.setFirstName(employeeOfGatePass.getFirstName());
            employee.setLastName(employeeOfGatePass.getLastName());
            employee.setEmployeeCode(employeeOfGatePass.getEmployeeCode());
            employeeGatePass.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
            employeeGatePass.setEmployeeObject(employee);
            lightEmployeeGatePass.add(employeeGatePass);
        }
        if (allGatePassListByEmployeeId == null && allGatePassListByEmployeeId.size() == 0) {
            throw new EntityNotFoundException("EmployeeGatePass Not found for employeeId : " + employeeId);
        }
        return new ResponseEntity(lightEmployeeGatePass, HttpStatus.OK);
    }


//    @PostMapping("gate-pass/edit-save/{employeeId}")
//    public ResponseEntity<EmployeeGatePass> editSave(@RequestBody GatePassDto gatePassDto, @PathVariable("employeeId") long employeeId) {
//        List<EmployeeGatePass> approvedEmployeeGatePass = employeeGatePassService.getEmployeeGatePassByEmployeeAndStatus(GatePassStatus.APPROVED, employeeId);
//        EmployeeGatePass employeeGatePass=new EmployeeGatePass();
//        for (EmployeeGatePass e:approvedEmployeeGatePass) {
//            if(e.getEmployeeId()==employeeId){
//                if(gatePassDto.getActualExit() != null ){
//                    e.setActualExitTime(gatePassDto.getActualExit());
//                    employeeGatePass=e;
//                    employeeGatePassService.createEmployeeGatePass(e);
//                }
//                else if(gatePassDto.getActualEntry() != null){
//                    e.setActualEntryTime(gatePassDto.getActualEntry()) ;
//                    employeeGatePass=e;
//                    employeeGatePassService.createEmployeeGatePass(e);
//                }
//            }
//        }
//        return new ResponseEntity(employeeGatePass, HttpStatus.OK);
//    }


    @PostMapping("gate-pass/edit-save/{gId}")
    public ResponseEntity<EmployeeGatePass> editSave(@RequestBody GatePassDto gatePassDto, @PathVariable("gId") long gId) {
        Optional<EmployeeGatePass> employeeGatePassOld = employeeGatePassService.getGatePassById(gId);
        EmployeeGatePass e = employeeGatePassOld.get();
        if (gatePassDto.getActualExit() != null) {
            e.setActualExitTime(gatePassDto.getActualExit());
            employeeGatePassService.createEmployeeGatePass(e);
        }
        if (gatePassDto.getActualEntry() != null) {
            e.setActualEntryTime(gatePassDto.getActualEntry());
            employeeGatePassService.createEmployeeGatePass(e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("gate-pass/bulk-assign-gate-pass")
    public void importContract(@RequestParam("file") MultipartFile file) {
        gateAssign(file);
    }


    /**
     * Employee more data import
     *
     * @param file
     * @return
     */
    public void gateAssign(MultipartFile file) {
        List<Employee> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    Row row = sheet.getRow(i);
                    EmployeeGateAssignment employeeGateAssignment = new EmployeeGateAssignment();
                    Optional<Employee> employeeObj = employeeService.getEmployeeByEmployeeCode((row.getCell(1).getStringCellValue()));
                    if (!employeeObj.isPresent()) {
                        throw new EntityNotFoundException("Employee Not Found: " + (long) (row.getCell(1).getNumericCellValue()));
                    }
                    Employee employee = employeeObj.get();

                    Gate inGate = gateService.getGateByNumber((row.getCell(2).getStringCellValue()));
                    Gate outGate = gateService.getGateByNumber((row.getCell(3).getStringCellValue()));
                    if (inGate == null) {
                        throw new EntityNotFoundException("InGate Not Found: ");
                    }
                    if (outGate == null) {
                        throw new EntityNotFoundException("OutGate Not Found: ");
                    }
                    employeeGateAssignment.setEmployee(employee);
                    employeeGateAssignment.setInGate(inGate);
                    employeeGateAssignment.setOutGate(outGate);
                    employeeGateAssignmentService.saveEmployeeGate(employeeGateAssignment);
                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("gate-pass/approved-download")
    public ResponseEntity<List<GatePassDto>> getEmployeeGatePass(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        List<GatePassDto> monthlyEmployeeAttendanceDtos = getGatePassByStatus(GatePassStatus.APPROVED);
        OutputStream out = null;
        String fileName = "Permanent-Contract-Report_";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry(workbook, monthlyEmployeeAttendanceDtos, response, 0);
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("No entries today");
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

    @GetMapping("gate-pass/rejected-download")
    public ResponseEntity<List<GatePassDto>> getEmployeeGatePassRej(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ParseException {

        List<GatePassDto> monthlyEmployeeAttendanceDtos = getGatePassByStatus(GatePassStatus.REJECTED);
        OutputStream out = null;
        String fileName = "Permanent-Contract-Report_";
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(response.getOutputStream());
        try {
            if (monthlyEmployeeAttendanceDtos != null) {
                attendanceEntry(workbook, monthlyEmployeeAttendanceDtos, response, 0);
            }
            if (monthlyEmployeeAttendanceDtos.isEmpty())
                throw new EntityNotFoundException("No entries today");
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

    public List<GatePassDto> getGatePassByStatus(GatePassStatus status) {
        List<EmployeeGatePass> gatePasses = employeeGatePassService.getApprovedEmployeeGatePass(status);
        List<GatePassDto> gatePassDtos = new ArrayList<>();
        for (EmployeeGatePass gatePass:gatePasses){
            GatePassDto gatePassDto = new GatePassDto();
            gatePassDto.setEmployeeCode(gatePass.getEmployeeObject().getEmployeeCode());
            gatePassDto.setEmployeeName(gatePass.getEmployeeName());
            gatePassDto.setRequestedOn(gatePass.getGatePassRequestOn());
            gatePassDto.setFromTime(gatePass.getFromTime());
            gatePassDto.setToTime(gatePass.getToTime());
            gatePassDtos.add(gatePassDto);
        }
        return gatePassDtos;
    }

    private WritableWorkbook attendanceEntry(WritableWorkbook workbook, List<GatePassDto> gatePassDtos, HttpServletResponse response, int index) throws IOException, WriteException {
        WritableSheet s = workbook.createSheet("Gate pass", index);
        s.getSettings().setPrintGridLines(false);
        WritableFont headerFont = new WritableFont(WritableFont.TIMES, 10);
        headerFont.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setAlignment(CENTRE);
        WritableFont headerFontLeft = new WritableFont(WritableFont.TIMES, 7);
        headerFontLeft.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat headerFormatLeft = new WritableCellFormat(headerFontLeft);
        headerFormatLeft.setAlignment(LEFT);
        WritableFont headerFontRight = new WritableFont(WritableFont.TIMES, 7);
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
        cellFormatDate.setBackground(jxl.format.Colour.ICE_BLUE);
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

        WritableFont cf = new WritableFont(WritableFont.ARIAL, 7);
        WritableCellFormat cLeft = new WritableCellFormat(cf);
        cLeft.setAlignment(LEFT);
        cLeft.setBackground(jxl.format.Colour.ICE_BLUE);
        cLeft.setBorder(jxl.format.Border.ALL, BorderLineStyle.THIN);

        s.setColumnView(0, 10);
        s.setColumnView(1, 20);
        s.setColumnView(2, 20);
        s.setColumnView(3, 20);
        s.setColumnView(4, 20);
        s.setColumnView(5, 20);
        s.setColumnView(6, 10);
        s.setColumnView(7, 15);
        s.setColumnView(8, 15);

        s.mergeCells(0, 0, 4, 0);
        Label lable = new Label(0, 0, "Permanent Employees Live Head Count Report - On: " + DateUtil.mySqlFormatDate(), headerFormat);
        s.addCell(lable);

        int rowNum = 2;
        for (GatePassDto employeeAttendanceDto : gatePassDtos) {
            s.addCell(new Label(0, 1, "#", cellFormat));
            s.addCell(new Label(0, rowNum, "" + Integer.valueOf(rowNum - 1), cLeft));
            s.addCell(new Label(1, 1, "Employee Code", cellFormat));
            s.addCell(new Label(1, rowNum, "" + employeeAttendanceDto.getEmployeeCode(), cLeft));
            s.addCell(new Label(2, 1, "Employee Name", cellFormat));
            s.addCell(new Label(2, rowNum, "" + employeeAttendanceDto.getEmployeeName(), cLeft));
            s.addCell(new Label(3, 1, "Date", cellFormat));
            s.addCell(new Label(3, rowNum, "" + employeeAttendanceDto.getRequestedOn(), cLeft));
            s.addCell(new Label(4, 1, "Status", cellFormat));
            s.addCell(new Label(4, rowNum, "" + employeeAttendanceDto.getStatus(), cLeft));
            s.addCell(new Label(5, 1, "From time", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getFromTime(), cLeft));
            s.addCell(new Label(5, 1, "To time", cellFormat));
            s.addCell(new Label(5, rowNum, "" + employeeAttendanceDto.getToTime(), cLeft));
            rowNum = rowNum + 1;
        }
        s.addCell(new Label(5, rowNum, "Total Count = " + Integer.valueOf(rowNum - 2), cellFormat));
        return workbook;
    }

}