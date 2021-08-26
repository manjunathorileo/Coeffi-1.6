package com.dfq.coeffi.master.assignShifts;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.master.shift.Shift;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeShiftAssignmentService {
    EmployeeShiftAssignment saveEmployeeShiftAssignment(EmployeeShiftAssignment employeeShiftAssignment);
    List<EmployeeShiftAssignment> getAllEmployeeShiftAssignment();
    Optional<EmployeeShiftAssignment> getEmployeeShiftAssignmentById(long id);
    Optional<EmployeeShiftAssignment> getEmployeeShiftAssignmentByShift(Shift shift);

    EmployeeShiftAssignment getCurrentShiftByEmployeeId(long employeeId, Date todayDate);
    List<Employee> getEmployeeListByShiftAndDate(Date todayDate, Shift shift);
    List<EmployeeShiftAssignment> getEmployeeShiftAssignmentByEmployeeId(long employeeId);


}
