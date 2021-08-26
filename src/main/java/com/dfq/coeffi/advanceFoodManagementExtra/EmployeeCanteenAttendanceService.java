package com.dfq.coeffi.advanceFoodManagementExtra;


import java.util.Date;
import java.util.List;

public interface EmployeeCanteenAttendanceService {

    void saveEmployeeCanteenAttendance(EmployeeCanteenAttendance employeeCanteenAttendance);

    EmployeeCanteenAttendance getEmployeeCanteenAttendanceByDeviceLogId(String deviceLogId);

    List<EmployeeCanteenAttendance> getPunchedAttendance(Date date);
}
