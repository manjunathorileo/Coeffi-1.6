package com.dfq.coeffi.servicesimpl.hr;
/*
 * @author Ashvini B
 */

import com.dfq.coeffi.entity.hr.ProbationaryPeriodStatus;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.hr.employee.Qualification;
import com.dfq.coeffi.repository.hr.EmployeeRepository;
import com.dfq.coeffi.service.hr.EmployeeService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        super();
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee save(Employee employee) {
        employee.setStatus(true);
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.getEmployees();
    }

    @Override
    public Employee findOne(Long id) {
        return employeeRepository.getOne(id);
    }

    @Override
    public void delete(Long id) {
        employeeRepository.delete(id);
    }

    @Override
    public Optional<Employee> getEmployee(Long id) {
        return ofNullable(employeeRepository.findOne(id));
    }

    @Override
    public List<Employee> getEmployeeByRefNameRefNumber(String refName, Integer refNumber) {
        return employeeRepository.findByRefNameAndRefNumber(refName, refNumber);
    }

    @Override
    public Optional<Employee> getEmployeeBankDetails(Long employeeBankId) {
        return employeeRepository.getEmployeeBankDetail(employeeBankId);
    }

    @Override
    public Employee merge(Employee employee) {
        return employeeRepository.saveAndFlush(employee);
    }

    @Override
    public void updateProfilePic(Employee employee) {
        employeeRepository.updateProfile(employee.getImagePath(), employee.getId());
    }

    @Override
    public Optional<Employee> getEmployeeByLogin(Long id) {
        return employeeRepository.getEmployeeByLoginId(id);
    }

    @Override
    public List<Employee> getTeachingStaff() {
        return employeeRepository.getEmployees();
    }

    public List<Employee> getEmployeeSelectedDetails() {
        return employeeRepository.getEmployeeSelectedDeatils();
    }

    @Override
    public List<Employee> getEmployeeLightWeight() {
        return employeeRepository.getEmployeeSelectedDeatils();
    }

    @Override
    public Optional<Employee> isAadhaarExists(String adharNumber) {
        return employeeRepository.findEmployeeByAadhaarNumber(adharNumber);
    }

    @Override
    public Optional<Employee> getEmployeeSalaryDetails(Long employeeBankId) {
        return employeeRepository.getEmployeeSalaryDetails(employeeBankId);
    }

    @Override
    public List<Employee> getEmployeesByDepartment(long departmentId) {
        return employeeRepository.findByDepartment(departmentId);
    }

    @Override
    public Optional<Employee> getEmployeeByEmployeeCode(String employeeCode) {
        return employeeRepository.findByEmployeeCode(employeeCode);
    }

    @Override
    public Optional<Employee> getEmployeeByManagerId(long managerId) {
        return employeeRepository.findByFirstApprovalManager(managerId);
    }

    @Override
    public Optional<Employee> getEmployeeBySecondManagerId(long managerId) {
        return employeeRepository.findBySecondApprovalManager(managerId);
    }

    @Override
    public List<Employee> getAllEmployeeUnderProbationaryPeriod() {
        List<Employee> employees = employeeRepository.findAll();
        List<Employee> employeeUnderProbationaryPeriodList = new ArrayList<>();
        for (Employee employee:employees) {
            if (employee.getProbationaryPeriodStatus().equals(ProbationaryPeriodStatus.UNDER_PROBATIONARY)){
                employeeUnderProbationaryPeriodList.add(employee);
            }
        }
        return employeeUnderProbationaryPeriodList;
    }

    @Override
    public List<Employee> getEmployeesByFirstApprovalManager(long firstApprovalManager) {
        return employeeRepository.findByFirstApprovalManagerId(firstApprovalManager);
    }

    @Override
    public List<Employee> getEmployeesBySecondApprovalManager(long secondApprovalManager) {
        return employeeRepository.findBySecondApprovalManagerId(secondApprovalManager);
    }

    @Override
    public Optional<Employee> getEmployeeByIdAndPassword(long employeeId, String password) {
        return employeeRepository.findByIdAndPassword(employeeId,password);
    }

    @Override
    public Optional<Employee> checkEmployeeCode(String employeeCode) {
        return employeeRepository.findByEmployeeCode(employeeCode);
    }

    @Override
    public Qualification getQuaificationById(long id) {
        return employeeRepository.findByQualificationId(id);
    }

    //Arun
    @Override
    public List<Employee> getEmployeeByType(EmployeeType employeeType,boolean status) {
        return employeeRepository.findByEmployeeTypeAndStatus(employeeType,status);
    }

    @Override
    public Employee getByRfid(String cardId) {
        return employeeRepository.findByRfid(cardId);
    }

}