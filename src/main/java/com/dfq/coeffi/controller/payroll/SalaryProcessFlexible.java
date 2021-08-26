package com.dfq.coeffi.controller.payroll;

import com.dfq.coeffi.controller.BaseController;
import com.dfq.coeffi.dto.EmployeeSalaryDto;
import com.dfq.coeffi.dto.EmployeeSalaryProcessDto;
import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.hr.employee.EmployeeType;
import com.dfq.coeffi.entity.payroll.EmployeeCTCData;
import com.dfq.coeffi.entity.payroll.EmployeeSalaryProcess;
import com.dfq.coeffi.service.hr.EmployeeService;
import com.dfq.coeffi.service.payroll.EmployeeSalaryProcessService;
import com.dfq.coeffi.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class SalaryProcessFlexible extends BaseController {

    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeeSalaryProcessService employeeSalaryProcessService;
    @Autowired
    EmployeeSalaryProcessController employeeSalaryProcessController;

    @PostMapping("employee-salary/generate-by-month-flexible")
    public ResponseEntity<List<EmployeeSalaryProcess>> createEmployeeSalaryProcess(@RequestBody EmployeeSalaryProcessDto employeeSalaryProcessDto) throws Exception {
        List<EmployeeSalaryProcess> employeeSalaryProcessList = new ArrayList<>();
        int year = (employeeSalaryProcessDto.startDate.getYear())+1900;
        int month = employeeSalaryProcessDto.startDate.getMonth()+1;
        System.out.println("Stard: "+ employeeSalaryProcessDto.startDate +" month :" +month);
        List<Employee> employeeList = employeeService.getEmployeeByType(EmployeeType.PERMANENT, true);
        List<Employee> employeeListW = employeeService.getEmployeeByType(EmployeeType.PERMANENT_WORKER, true);
        List<Employee> employeeListCont = employeeService.getEmployeeByType(EmployeeType.CONTRACT, true);
        employeeList.addAll(employeeListW);
        employeeList.addAll(employeeListCont);

        if (employeeList != null && employeeList.size() > 0) {
            for (Employee employee : employeeList) {
                Optional<EmployeeSalaryProcess> employeeSalaryObj = employeeSalaryProcessService.getEmployeeSalaryCreatedByMonth(employee.getId(), Month.of(month).name(), String.valueOf(year));
                if (employeeSalaryObj.isPresent()) {
//                    throw new EntityNotFoundException("Employee Salary Already Generated");
                    employeeSalaryProcessService.delete(employeeSalaryObj.get().getId());
                }
                if (employee.getEmployeeCTCData() != null) {
                    EmployeeCTCData employeeCTCData = employee.getEmployeeCTCData();
                    EmployeeSalaryProcess employeeSalaryProcess = employeeSalaryProcessController.createSalaryProcess(employeeCTCData, year, month);
                    employeeSalaryProcess.setRefId(employee.getId());
                    //---------------------------------------------
                    Employee employee1 = new Employee();
                    employee1.setId(employee.getId());
                    employee1.setFirstName(employee.getFirstName());
                    employee1.setLastName(employee.getLastName());
                    employee1.setEmployeeCode(employee.getEmployeeCode());
                    employee1.setDateOfJoining(employee.getDateOfJoining());
                    employee1.setEmployeeCTCData(employee.getEmployeeCTCData());
                    employee1.setEmployeeType(employee.getEmployeeType());
                    employee1.setOtRequired(employee.isOtRequired());
                    //---------------------------------------------
                    employeeSalaryProcess.setEmployee(employee1);
                    System.out.println("Employee /in id: " + employee.getId());
                    employeeSalaryProcess = employeeSalaryProcessController.checkLateEntryCount(employeeSalaryProcess, employeeSalaryProcessDto.startDate, employeeSalaryProcessDto.endDate);
                    employeeSalaryProcess = employeeSalaryProcessController.checkLossOfPay(employeeSalaryProcess,employeeSalaryProcessDto.startDate, employeeSalaryProcessDto.endDate);
                    employeeSalaryProcess = employeeSalaryProcessController.getCurrentMonthEmployeeLeaveBalanceByEmployeeId(employeeSalaryProcess);
//                    BigDecimal arrears = checkArrears(employeeSalaryProcess);
                    employeeSalaryProcess.setCurrentBasic(employeeSalaryProcess.getCurrentBasic());
                    employeeSalaryProcessService.createEmployeeSalaryProcess(employeeSalaryProcess);
                    employeeSalaryProcessList.add(employeeSalaryProcess);
                }
            }
        }
        System.out.println("Total employees " + employeeList.size() + " salary list " + employeeSalaryProcessList.size());
        return new ResponseEntity(employeeSalaryProcessList, HttpStatus.OK);
    }
}
