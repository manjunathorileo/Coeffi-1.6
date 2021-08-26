package com.dfq.coeffi.service.hr;
/*
 * @author Ashvini B
 */

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.hr.employee.Qualification;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    Employee save(Employee employee);

    Employee merge(Employee employee);

    void updateProfilePic(Employee employee);

    List<Employee> findAll();

    Optional<Employee> getEmployee(Long id);

    Optional<Employee> getEmployeeByLogin(Long userId);

    Employee findOne(Long id);

    void delete(Long id);

    List<Employee> getEmployeeByRefNameRefNumber(String refName, Integer refNumber);

    Optional<Employee> getEmployeeBankDetails(Long employeeBankId);

    List<Employee> getTeachingStaff();

    List<Employee> getEmployeeSelectedDetails();

    List<Employee> getEmployeeLightWeight();

    Optional<Employee> isAadhaarExists(String adharNumber);

    Optional<Employee> getEmployeeSalaryDetails(Long employeeSalaryId);

    List<Employee> getEmployeesByDepartment(long departmentId);

    Optional<Employee> getEmployeeByEmployeeCode(String employeeCode);

    Optional<Employee> getEmployeeByManagerId(long managerId);

    Optional<Employee> getEmployeeBySecondManagerId(long managerId);

    List<Employee> getAllEmployeeUnderProbationaryPeriod();

    List<Employee> getEmployeesByFirstApprovalManager(long firstApprovalManager);

    List<Employee> getEmployeesBySecondApprovalManager(long secondApprovalManager);

    Optional<Employee> getEmployeeByIdAndPassword(long employeeId, String password);

    Optional<Employee> checkEmployeeCode(String employeeCode);
    Qualification getQuaificationById(long id);

    List<Employee> getEmployeeByType(EmployeeType permanent,boolean status);

    Employee getByRfid(String cardId);
}