package com.dfq.coeffi.advanceFoodManagementExtra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EmployeeCanteenAttendanceImplementation implements EmployeeCanteenAttendanceService {
    @Autowired
    EmployeeCanteenAttendanceRepository employeeCanteenAttendanceRepository;

    @Override
    public void saveEmployeeCanteenAttendance(EmployeeCanteenAttendance employeeCanteenAttendance) {
        employeeCanteenAttendanceRepository.save(employeeCanteenAttendance);
    }

    @Override
    public EmployeeCanteenAttendance getEmployeeCanteenAttendanceByDeviceLogId(String deviceLogId) {
        return employeeCanteenAttendanceRepository.findByDeviceLogId(deviceLogId);
    }

    @Override
    public List<EmployeeCanteenAttendance> getPunchedAttendance(Date date) {
        return employeeCanteenAttendanceRepository.findByMarkedOn(date);
    }
}
