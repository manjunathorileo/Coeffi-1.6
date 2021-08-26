package com.dfq.coeffi.repository.hr;
/*
 * @author Ashvini B
 */

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.hr.employee.Qualification;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByRefNameAndRefNumber(String refName, Integer refNumber);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    @Query("update Employee e set e.status=false where e.id=:id")
    @Modifying
    void delete(@Param("id") Long id);

    @Query("select e from Employee e where (e.status=true and NOT e.firstName='ADMIN') ORDER BY e.employeeCode ASC")
    List<Employee> getEmployees();

    @Query("select e from Employee e where e.employeeLogin.id=:id")
    Optional<Employee> getEmployeeByLoginId(@Param("id") Long id);

    @Query("select e from Employee e where e.employeeBank.id=:id")
    Optional<Employee> getEmployeeBankDetail(@Param("id") Long id);

    @Query("select e from Employee e where e.status=true and e.employeeType = 'TEACHING'")
    List<Employee> getTeachingStaff();


    @Query("select e.id, e.firstName, e.lastName, e.employeeCode, e.gender ,e.department ,e.designation from Employee e where (e.status=true and NOT e.firstName='ADMIN') ORDER BY e.employeeCode ASC")
    List<Employee> getEmployeeSelectedDeatils();

    @Modifying
    @Query("update Employee e set e.imagePath = :path where e.id = :id")
    void updateProfile(@Param("path") String path, @Param("id") long id);

    @Query("select e from Employee e where e.adharNumber=:aadhaarNumber")
    Optional<Employee> findEmployeeByAadhaarNumber(@Param("aadhaarNumber") String aadhaarNumber);

    @Query("select e from Employee e where e.employeeCTCData.id=:id")
    Optional<Employee> getEmployeeSalaryDetails(@Param("id") Long id);

    @Query("select e from Employee e where e.department.id=:departmentId ORDER BY e.employeeCode ASC")
    List<Employee> findByDepartment(@Param("departmentId") long departmentId);

    @Query("select e from Employee e where e.firstApprovalManager.id=:managerId")
    Optional<Employee> findByFirstApprovalManager(@Param("managerId") long managerId);

    @Query("select e from Employee e where e.secondApprovalManager.id=:managerId")
    Optional<Employee> findBySecondApprovalManager(@Param("managerId") long managerId);

    @Query("select e from Employee e where e.firstApprovalManager.id=:firstApprovalManager ORDER BY e.employeeCode ASC")
    List<Employee> findByFirstApprovalManagerId(@Param("firstApprovalManager") long firstApprovalManager);

    @Query("select e from Employee e where e.secondApprovalManager.id=:secondApprovalManager ORDER BY e.employeeCode ASC")
    List<Employee> findBySecondApprovalManagerId(@Param("secondApprovalManager") long secondApprovalManager);

    @Query("select e from Employee e where (e.id=:employeeId AND e.employeeLogin.password=:password)")
    Optional<Employee> findByIdAndPassword(@Param("employeeId") long employeeId, @Param("password") String password);

    Qualification findByQualificationId(long id);

    List<Employee> findByEmployeeTypeAndStatus(EmployeeType employeeType, boolean status);

    Employee findByRfid(String rfid);
}
