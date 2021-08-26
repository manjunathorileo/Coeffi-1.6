package com.dfq.coeffi.master.assignShifts;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.employeePermanentContract.entities.EmpPermanentContract;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.util.DateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
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
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
public class EmployeeShiftAssignmentController extends BaseController {

    private final EmployeeShiftAssignmentService employeeShiftAssignmentService;
    private final EmployeeService employeeService;
    private final ShiftService shiftService;

    @Autowired
    public EmployeeShiftAssignmentController(EmployeeShiftAssignmentService employeeShiftAssignmentService, EmployeeService employeeService,
                                             ShiftService shiftService) {
        this.employeeShiftAssignmentService = employeeShiftAssignmentService;
        this.employeeService = employeeService;
        this.shiftService = shiftService;
    }

    @Autowired
    EmployeeShiftAssignmentRepository employeeShiftAssignmentRepository;

    @GetMapping("employee-shift-assignment")
    public ResponseEntity<List<EmployeeShiftAssignment>> getAllEmployeeShiftAssignment() {
        List<EmployeeShiftAssignment> employeeShiftAssignments = employeeShiftAssignmentService.getAllEmployeeShiftAssignment();
        List<EmployeeShiftAssignment> employeeShiftAssignmentList = new ArrayList<>();
        for (EmployeeShiftAssignment employeeShiftAssignment : employeeShiftAssignments) {
            Employee employee = employeeShiftAssignment.getEmployee();
            Employee employee1 = new Employee();
            employee1.setEmployeeCode(employee.getEmployeeCode());
            employee1.setFirstName(employee.getFirstName());
            employee1.setLastName(employee.getLastName());
            employee1.setId(employee.getId());
            employeeShiftAssignment.setEmployee(employee1);
            employeeShiftAssignmentList.add(employeeShiftAssignment);

        }
        return new ResponseEntity<>(employeeShiftAssignmentList, HttpStatus.OK);
    }

    @PostMapping("employee-shift-assignment")
    public ResponseEntity<List<EmployeeShiftAssignment>> assignEmployeeToShifts(@Valid @RequestBody EmployeeShiftAssignmentDto employeeShiftAssignmentDto) {
        if (employeeShiftAssignmentDto.getFromDate().after(employeeShiftAssignmentDto.getToDate())) {
            throw new EntityNotFoundException("FromDate should be lessthan ToDate: ");
        }
        List<EmployeeShiftAssignment> employeeShiftAssignments = new ArrayList<>();
        //int week = employeeShiftAssignment.getWeekNo();
        int year = DateUtil.getCurrentYear();

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);

        Calendar cal = Calendar.getInstance();
        cal.setTime(employeeShiftAssignmentDto.getFromDate());
        int week = cal.get(Calendar.WEEK_OF_YEAR);

        for (long id : employeeShiftAssignmentDto.getEmployeeIds()) {
            EmployeeShiftAssignment employeeShiftAssignment = new EmployeeShiftAssignment();
            Optional<Employee> employeeObj = employeeService.getEmployee(id);
            if (!employeeObj.isPresent()) {
                throw new EntityNotFoundException("Employee Not Found: " + id);
            }
            Employee employee = employeeObj.get();
            Shift shift = shiftService.getShift(employeeShiftAssignmentDto.getShiftId());
            if (shift == null) {
                throw new EntityNotFoundException("Shift Not Found: " + employeeShiftAssignmentDto.getShiftId());
            }
            checkEmployeeAlreadyAssigned(employeeShiftAssignmentDto.getFromDate(), employeeShiftAssignmentDto.getToDate(), employee.getId());

            employeeShiftAssignment.setEmployee(employee);
            employeeShiftAssignment.setShift(shift);
            employeeShiftAssignment.setWeekNo(week);
            employeeShiftAssignment.setFromDate(employeeShiftAssignmentDto.getFromDate());
            employeeShiftAssignment.setToDate(employeeShiftAssignmentDto.getToDate());
            employeeShiftAssignmentService.saveEmployeeShiftAssignment(employeeShiftAssignment);
            employeeShiftAssignments.add(employeeShiftAssignment);
        }
        return new ResponseEntity<>(employeeShiftAssignments, HttpStatus.CREATED);
    }

    @PostMapping("employee-shift-assignment/import-excel")
    public ResponseEntity importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException, ParseException {
        List<EmployeeShiftAssignment> employeeShiftAssignments = uploadExcelFile(file);
        return new ResponseEntity(HttpStatus.OK);
    }

//    private Workbook uploadExcelFile(MultipartFile multipartFile) throws IOException, ParseException {
//        Workbook workbook = new HSSFWorkbook(multipartFile.getInputStream());
//        Sheet sheet = workbook.getSheetAt(0);
//        DataFormatter dataFormatter = new DataFormatter();
//        for (Row row : sheet) {
//            EmployeeShiftAssignment shiftAssignment = new EmployeeShiftAssignment();
//            for (int i = 0; i < row.getLastCellNum(); i++) {
//                String cellValue = dataFormatter.formatCellValue(row.getCell(i));
//                shiftAssignment.setFromDate(row.getCell(1).getDateCellValue());
//                shiftAssignment.setToDate(row.getCell(2).getDateCellValue());
//                Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(row.getCell(3).getStringCellValue());
//                shiftAssignment.setEmployee(employee.get());
//                Shift shift = shiftService.getShift((long) row.getCell(4).getNumericCellValue());
//                shiftAssignment.setShift(shift);
//                checkEmployeeAlreadyAssigned(shiftAssignment.getFromDate(), employee.get().getId());
//            }
//            if (shiftAssignment.getFromDate().after(shiftAssignment.getToDate())) {
//                throw new EntityNotFoundException("FromDate should be lessthan ToDate: ");
//            }
//            employeeShiftAssignmentService.saveEmployeeShiftAssignment(shiftAssignment);
//        }
//        workbook.close();
//        return workbook;
//    }


    private List<EmployeeShiftAssignment> uploadExcelFile(MultipartFile file) throws IOException, ParseException {
        List<EmployeeShiftAssignment> dto = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = sheet.getPhysicalNumberOfRows();
            if (rowNumber > 1) {
                for (int i = 1; i < rowNumber; i++) {
                    EmployeeShiftAssignment shiftAssignment = null;
                    Row row = sheet.getRow(i);
                    long code = (long) row.getCell(0).getNumericCellValue();
                    Optional<Employee> employee = employeeService.getEmployeeByEmployeeCode(String.valueOf(code));
                    if (employee.isPresent()) {
                        List<EmployeeShiftAssignment> employeeShiftAssignment = employeeShiftAssignmentService.getEmployeeShiftAssignmentByEmployeeId(employee.get().getId());
                        if (employeeShiftAssignment != null) {
                            Collections.reverse(employeeShiftAssignment);
                            shiftAssignment = employeeShiftAssignment.get(0);
                            Shift shift = shiftService.getShift((long) row.getCell(1).getNumericCellValue());
                            shiftAssignment.setShift(shift);
                            shiftAssignment.setFromDate(new Date());
                            Date d = new Date();
                            Calendar today_plus_year = Calendar.getInstance();
                            today_plus_year.add(Calendar.YEAR, 10);

                            shiftAssignment.setToDate(today_plus_year.getTime());
                            employeeShiftAssignmentService.saveEmployeeShiftAssignment(shiftAssignment);
                        } else {
                            shiftAssignment = new EmployeeShiftAssignment();
                            Shift shift = shiftService.getShift((long) row.getCell(1).getNumericCellValue());
                            shiftAssignment.setShift(shift);
                            shiftAssignment.setFromDate(new Date());

                            Date d = new Date();
                            Calendar today_plus_year = Calendar.getInstance();
                            today_plus_year.add(Calendar.YEAR, 10);

                            shiftAssignment.setToDate(today_plus_year.getTime());
                            shiftAssignment.setEmployee(employee.get());
                            employeeShiftAssignmentService.saveEmployeeShiftAssignment(shiftAssignment);
                        }
                    } else {
                        System.out.println("No employee: ");
                    }

                    dto.add(shiftAssignment);
                }
            } else {
                throw new NoSuchFieldException("No data in excel sheet\", \"Import");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    private void checkEmployeeAlreadyAssigned(Date fromDate, Date toDate, long employeeId) {
        List<Date> dates = DateUtil.getDaysBetweenDates(fromDate, toDate);
        List<EmployeeShiftAssignment> employeeShiftAssignments = employeeShiftAssignmentService.getEmployeeShiftAssignmentByEmployeeId(employeeId);
        for (Date dateToCheck : dates) {
            for (EmployeeShiftAssignment employeeShiftAssignment : employeeShiftAssignments) {
                if (dateToCheck.after(employeeShiftAssignment.getFromDate()) && dateToCheck.before(employeeShiftAssignment.getToDate())) {
                    throw new EntityNotFoundException("Employee Already Assigned : on" + dateToCheck);
                } else if (dateToCheck.equals(employeeShiftAssignment.getFromDate()) || dateToCheck.equals(employeeShiftAssignment.getToDate())) {
                    throw new EntityNotFoundException("Employee Already Assigned on: " + dateToCheck);
                }
            }
        }
    }


    @DeleteMapping("employee-shift-assignment/{id}")
    public void deleteAssignment(@PathVariable long id) {
        employeeShiftAssignmentRepository.delete(id);
    }
}
