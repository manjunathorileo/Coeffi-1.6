package com.dfq.coeffi.GeoLocation;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.master.assignShifts.EmployeeShiftAssignmentService;
import com.dfq.coeffi.master.shift.ShiftService;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import com.dfq.coeffi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
public class GeoLocationController extends BaseController {
    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    ShiftService shiftService;
    @Autowired
    EmployeeShiftAssignmentService employeeShiftAssignmentService;
    @Autowired
    EmployeeService employeeService;


    @PostMapping("geo-location/mark-out-attendance")
    public EmployeeAttendance markAttendanceThroughGeoLocation(@RequestBody GeoLocationDto dto) throws Exception {
        Date today = new Date();
        EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(today, dto.getEmpId());
        if(employeeAttendance==null){
            throw new Exception("Mark In First");
        }
       if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
            employeeAttendance.setLatitudeOut(dto.getLatitude());
            employeeAttendance.setLongitudeOut(dto.getLongitude());
            employeeAttendance.setPlaceOut(dto.getPlace());
            employeeAttendance.setOutTime(today);
            employeeAttendanceService.createEmployeeAttendance(employeeAttendance,false);
        } else {
            throw new Exception("Out Time Already Marked");
        }
        return employeeAttendance;
    }


    @PostMapping("todays-attendance-details")
    public ResponseEntity<EmployeeAttendance> getTodaysAttendance(@RequestBody GeoLocationDto dto) {

        EmployeeAttendance e = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(DateUtil.getTodayDate(), dto.getEmpId());
        return new ResponseEntity<>(e, HttpStatus.OK);
    }

    @PostMapping("geo-location/mark-in-attendance")
    public ResponseEntity<EmployeeAttendance> markInAttendace(@RequestBody GeoLocationDto dto) throws Exception {
        Date today = new Date();
        EmployeeAttendance employeeAttendance = employeeAttendanceService.getEmployeeAttendanceByEmployeeId(today, dto.getEmpId());
        if (employeeAttendance == null) {
            EmployeeAttendance newEmployeeAttendance = new EmployeeAttendance();
            newEmployeeAttendance.setAttendanceStatus(AttendanceStatus.PRESENT);
            newEmployeeAttendance.setLatitudeIn(dto.getLatitude());
            newEmployeeAttendance.setLongitudeIn(dto.getLongitude());
            newEmployeeAttendance.setPlaceIn(dto.getPlace());
            Optional<Employee> e = employeeService.getEmployee(dto.getEmpId());
            newEmployeeAttendance.setEmployee(e.get());
            newEmployeeAttendance.setInTime(today);
            newEmployeeAttendance.setMarkedOn(today);
            newEmployeeAttendance.setShift(shiftService.getShift(1));
            employeeAttendanceService.createEmployeeAttendance(newEmployeeAttendance,false);

        } else if (employeeAttendance != null && employeeAttendance.getInTime() != null && employeeAttendance.getOutTime() == null && employeeAttendance.getAttendanceStatus().equals(AttendanceStatus.PRESENT)) {
            throw new Exception("In Time Already Marked");

        } else {
            throw new Exception("In Time Already Marked For Today");
        }
        return new ResponseEntity<>(employeeAttendance, HttpStatus.OK);

    }
}
