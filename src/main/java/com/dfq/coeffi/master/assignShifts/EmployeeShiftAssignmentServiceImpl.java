package com.dfq.coeffi.master.assignShifts;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.master.shift.Shift;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeShiftAssignmentServiceImpl implements  EmployeeShiftAssignmentService{

    private final EmployeeShiftAssignmentRepository employeeShiftAssignmentRepository;

    public EmployeeShiftAssignmentServiceImpl(EmployeeShiftAssignmentRepository employeeShiftAssignmentRepository){
        this.employeeShiftAssignmentRepository = employeeShiftAssignmentRepository;
    }
    @Override
    public EmployeeShiftAssignment saveEmployeeShiftAssignment(EmployeeShiftAssignment employeeShiftAssignment) {
        return employeeShiftAssignmentRepository.save(employeeShiftAssignment);
    }

    @Override
    public List<EmployeeShiftAssignment> getAllEmployeeShiftAssignment() {
        return employeeShiftAssignmentRepository.findAll();
    }

    @Override
    public Optional<EmployeeShiftAssignment> getEmployeeShiftAssignmentById(long id) {
        return Optional.ofNullable(employeeShiftAssignmentRepository.findOne(id));
    }

    @Override
    public Optional<EmployeeShiftAssignment> getEmployeeShiftAssignmentByShift(Shift shift) {
        return employeeShiftAssignmentRepository.findByShift(shift);
    }

    @Override
    public EmployeeShiftAssignment getCurrentShiftByEmployeeId(long employeeId, Date todayDate) {
        return employeeShiftAssignmentRepository.getCurrentShiftByEmployeeId(employeeId,todayDate);
    }

    @Override
    public List<Employee> getEmployeeListByShiftAndDate(Date todayDate, Shift shift) {
        List<Employee> employees = new ArrayList<>();
        List<EmployeeShiftAssignment> employeeShiftAssignments = employeeShiftAssignmentRepository.getEmployeeListByShiftAndDate(todayDate,shift);
        for (EmployeeShiftAssignment employeeShiftAssignment:employeeShiftAssignments) {
            Employee employee = employeeShiftAssignment.getEmployee();
            employees.add(employee);
        }
        return employees;
    }

    @Override
    public List<EmployeeShiftAssignment> getEmployeeShiftAssignmentByEmployeeId(long employeeId) {
        return employeeShiftAssignmentRepository.findByEmployeeId(employeeId);
    }
}
