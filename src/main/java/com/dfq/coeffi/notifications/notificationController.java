package com.dfq.coeffi.notifications;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.DateDto;
import com.dfq.coeffi.entity.hr.employee.AttendanceStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeAttendance;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeAttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class notificationController extends BaseController {

    @Autowired
    EmployeeAttendanceService employeeAttendanceService;
    @Autowired
    EmployeeService employeeService;

    @PostMapping("notify-attendance")
    public List<String> attendanceNotify(@RequestBody DateDto dateDto) throws ParseException {
        List<Employee> employees = employeeService.findAll();
        List<String> strings = new ArrayList<>();
        for (Employee employee : employees) {
            List<EmployeeAttendance> employeeAttendances = employeeAttendanceService.getEmployeeAttendanceByEmployeeIdAndStatus(employee.getId(), AttendanceStatus.PRESENT, dateDto.startDate, dateDto.endDate);
            ArrayList allPresent = new ArrayList();
            List halfDayList = new ArrayList();
            long count = 0;
            for (EmployeeAttendance attendance : employeeAttendances) {
                DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String shiftStartTime = timeFormat.format(attendance.getShift().getStartTime());
                long shiftStart = timeFormat.parse(shiftStartTime).getTime();
                String shiftEndTime = timeFormat.format(attendance.getShift().getEndTime());
                long shiftEnd = timeFormat.parse(shiftEndTime).getTime();
                if (attendance.getInTime() == null) {
                    String s = "No InTime for " + attendance.getEmployee().getFirstName() + " On " + attendance.getMarkedOn();
                    strings.add(s);
                }
                String inTime = timeFormat.format(attendance.getInTime());
                if (attendance.getOutTime() == null) {
                    String s = "No Out-Time for " + attendance.getEmployee().getFirstName() + " On " + attendance.getMarkedOn();
                    strings.add(s);
                }
            }
        }
        return strings;

    }


}
