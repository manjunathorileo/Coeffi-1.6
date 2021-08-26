package com.dfq.coeffi.controller.holiday;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.dto.HolidayUpdateDto;
import com.dfq.coeffi.entity.holiday.Holiday;
import com.dfq.coeffi.entity.holiday.HolidayDefination;
import com.dfq.coeffi.entity.holiday.HolidayType;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.master.AcademicYear;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.service.AcademicYearService;
import com.dfq.coeffi.service.holiday.HolidayDefinationService;
import com.dfq.coeffi.service.holiday.HolidayService;
import com.dfq.coeffi.service.holiday.HolidayTypeService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.*;

/*
 * @author Azhar razvi
 */

@RestController
public class HolidayController extends BaseController {
    private final HolidayService holidayService;
    private final HolidayDefinationService holidayDefinationService;
    private final HolidayTypeService holidayTypeService;
    private final AcademicYearService academicYearService;
    private final EmployeeService employeeService;
    private final EmployeeAttendanceService employeeAttendanceService;

    @Autowired
    public HolidayController(HolidayService holidayService, HolidayDefinationService holidayDefinationService,
                             AcademicYearService academicYearService, HolidayTypeService holidayTypeService,
                             EmployeeService employeeService, EmployeeAttendanceService employeeAttendanceService) {
        this.holidayService = holidayService;
        this.holidayDefinationService = holidayDefinationService;
        this.academicYearService = academicYearService;
        this.holidayTypeService = holidayTypeService;
        this.employeeService = employeeService;
        this.employeeAttendanceService = employeeAttendanceService;
    }

    @GetMapping("holiday")
    public ResponseEntity<List<Holiday>> listAll() {
        List<Holiday> holiday = holidayService.findAllHoliday();
        long y = DateUtil.getCurrentYear();
        List<Holiday> currentYearHoliday = new ArrayList<>();
        for (Holiday h : holiday) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(h.getCreatedOn());
            cal.getWeekYear();
            System.out.println("Calyear "+cal.getWeekYear() + " and "+ y);

            if (cal.getWeekYear() == y) {
                currentYearHoliday.add(h);
            }
            if (currentYearHoliday == null) {
                throw new EntityNotFoundException("No Holidays For This Year");
            }
        }
        if (CollectionUtils.isEmpty(holiday)) {
            throw new EntityNotFoundException("holiday");
        }
        return new ResponseEntity<>(currentYearHoliday, HttpStatus.OK);
    }

    @PostMapping("holiday/{id}")
    public ResponseEntity<Holiday> update(@PathVariable long id, @Valid @RequestBody Holiday holiday) {
        Optional<Holiday> persistedHoliday = holidayService.getHolidya(id);
        if (!persistedHoliday.isPresent()) {
            throw new EntityNotFoundException(Holiday.class.getSimpleName());
        }
        holiday.setId(id);
        holiday.setCreatedOn(holiday.getCreatedOn());

        holidayService.createHoliday(holiday);
        return new ResponseEntity<>(holiday, HttpStatus.OK);
    }

    @DeleteMapping("holiday/{id}")
    public ResponseEntity<Holiday> deleteHoliday(@PathVariable Long id) {
        Optional<Holiday> holiday = holidayService.getHolidya(id);
        if (!holiday.isPresent()) {
            throw new EntityNotFoundException(Holiday.class.getName());
        }
        holidayService.deleteHoliday(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("holiday")
    public ResponseEntity<Holiday> saveHoliday(@Valid @RequestBody Holiday holidayList) {
        Optional<AcademicYear> academicYear = academicYearService.getActiveAcademicYear();
        holidayList.setAcademicYear(academicYear.get());
        holidayList.setCreatedOn(new Date());
        Holiday holiday = holidayService.createHoliday(holidayList);
        if (holiday != null) {
            updateAttendanceData(holiday);
        }
        return new ResponseEntity<>(holiday, HttpStatus.OK);
    }

    void updateAttendanceData(Holiday holiday) {
        List<Employee> employees = employeeService.findAll();
        for (Employee employee : employees) {
            EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(holiday.getStartDate(), employee.getId());
            if (employeeAttendance != null) {
                employeeAttendance.setMarkedOn(holiday.getStartDate());
                employeeAttendance.setAttendanceStatus(AttendanceStatus.HOLIDAY);
                employeeAttendance.setRecordedTime(holiday.getStartDate());
                employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
            } else {
                EmployeeAttendance attendance = new EmployeeAttendance();
                attendance.setMarkedOn(holiday.getStartDate());
                attendance.setAttendanceStatus(AttendanceStatus.HOLIDAY);
                attendance.setEmployee(employee);
                employeeAttendanceService.createEmployeeAttendance(attendance,false);
            }
        }
    }


    @PostMapping("holiday/update-holiday")
    public ResponseEntity<Holiday> updateHoliday(@RequestBody HolidayUpdateDto holidayUpdateDto) {
        Optional<Holiday> holidayObj = holidayService.getHolidya(holidayUpdateDto.getId());
        if (!holidayObj.isPresent()) {
            throw new EntityNotFoundException("Holiday");
        }
        Holiday holiday = holidayObj.get();

        Optional<HolidayDefination> holidayDefinationObj = holidayDefinationService.getHolidayDefination(holidayUpdateDto.getHolidayDefinationId());
        HolidayDefination holidayDefination = holidayDefinationObj.get();

        Optional<HolidayType> holidayTypeObj = holidayTypeService.getHolidayType(holidayUpdateDto.getHolidayTypeId());
        HolidayType holidayType = holidayTypeObj.get();

        holiday.setStartDate(holidayUpdateDto.getStartDate());
        holiday.setEndDate(holidayUpdateDto.getEndDate());

        //holiday.setHolidayDefination(holidayDefination);
        //holiday.setHolidayType(holidayType);

        holidayService.createHoliday(holiday);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("holiday/between-to-dates")
    public ResponseEntity<List<Holiday>> getHolidayBetweenStartDateAndEndDate(@RequestBody DateDto dateDto) {
        List<Holiday> holidays = holidayService.getHolidayBetweenStartDateAndEndDate(DateUtil.convertDateToFormat(dateDto.getStartDate()), DateUtil.convertDateToFormat(dateDto.getEndDate()));

        if (CollectionUtils.isEmpty(holidays)) {
            throw new EntityNotFoundException("Holiday");
        }
        return new ResponseEntity<>(holidays, HttpStatus.OK);
    }
}