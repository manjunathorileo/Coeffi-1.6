package com.dfq.coeffi.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dfq.coeffi.entity.hr.employee.Employee;
import com.dfq.coeffi.entity.payroll.EmployeeSalary;
import com.dfq.coeffi.entity.payroll.payrollmaster.PayHead;
import com.dfq.coeffi.entity.payroll.payrollmaster.PayHeadContract;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SalaryUtil implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3349921882327182531L;

    /**
     * Function to calculate the employee salary from CTC
     */

    public static EmployeeSalary calculateBasicSalary(List<PayHead> payHeadList, Employee employee) {
        BigDecimal basicSalary = new BigDecimal(1);
        BigDecimal houseRentAllowance = new BigDecimal(1);
        BigDecimal employeesPF = new BigDecimal(1);
        BigDecimal medicalAllowance = new BigDecimal(0);
        BigDecimal travelAllowance = new BigDecimal(0);
        BigDecimal professionalTax = new BigDecimal(0);
        BigDecimal specialAllowance = new BigDecimal(0);
        BigDecimal totalEarning = new BigDecimal(0);
        BigDecimal per = new BigDecimal(0);
        BigDecimal totalDeduction = new BigDecimal(0);
        BigDecimal netSalaryPayble = new BigDecimal(0);
        BigDecimal month = new BigDecimal(12);
        BigDecimal tds = new BigDecimal(0);
        BigDecimal tdsSlab1 = new BigDecimal(350000);
        BigDecimal tdsSlab2 = new BigDecimal(500000);

        BigDecimal oFSalary = new BigDecimal(employee.getOfferedSalary());
        System.out.println("============Printing Employee type=====" + employee.getEmployeeType());
        oFSalary = oFSalary.divide(month, 2); //2 decimal
        EmployeeSalary employeeSalary = new EmployeeSalary();
        employeeSalary.setEmployee(employee);
        for (PayHead payHead : payHeadList) {
            if (payHead.getDescription().equalsIgnoreCase("Basic")) {
                if (payHead.getUnitType().equalsIgnoreCase("Percentage")) {
                    per = new BigDecimal(100);
                } else {
                    per = new BigDecimal(1);

                }
                BigDecimal basicUnit = new BigDecimal(payHead.getUnit());
                basicSalary = oFSalary.multiply(basicUnit.divide(per));
                employeeSalary.setBasicSalary(basicSalary);
            } else if (payHead.getDescription().equalsIgnoreCase("HRA")) {

                if (payHead.getUnitType().equalsIgnoreCase("Percentage")) {
                    per = new BigDecimal(100);
                } else {
                    per = new BigDecimal(1);

                }
                BigDecimal houseRentAllowanceUnit = new BigDecimal(payHead.getUnit());
                houseRentAllowance = basicSalary.multiply(houseRentAllowanceUnit.divide(per));
                System.out.println("==============Printing HRA==============\n" + houseRentAllowance);
                employeeSalary.setHouseRentAllowance(houseRentAllowance);
            } else if (payHead.getDescription().equalsIgnoreCase("EPF")) {
                if (payHead.getUnitType().equalsIgnoreCase("Percentage")) {
                    per = new BigDecimal(100);
                } else {
                    per = new BigDecimal(1);
                }
                BigDecimal employeesPFUnit = new BigDecimal(payHead.getUnit());
                employeesPF = basicSalary.multiply(employeesPFUnit.divide(per));
                employeeSalary.setEmployeesPF(employeesPF);
                employeeSalary.setEmployeersPF(employeesPF);
            } else if (payHead.getDescription().equalsIgnoreCase("Medical Allowance")) {
                if (payHead.getUnitType().equalsIgnoreCase("Percentage")) {
                    per = new BigDecimal(100);
                } else {
                    per = new BigDecimal(1);

                }
                BigDecimal medicalAllowancePFUnit = new BigDecimal(payHead.getUnit());
                medicalAllowance = medicalAllowancePFUnit.divide(per);
                employeeSalary.setMedicalAllowance(medicalAllowance);
            } else if (payHead.getDescription().equalsIgnoreCase("Travel Allowance")) {
                if (payHead.getUnitType().equalsIgnoreCase("Percentage")) {
                    per = new BigDecimal(100);
                } else {
                    per = new BigDecimal(1);

                }
                BigDecimal travelAllowanceUnit = new BigDecimal(payHead.getUnit());
                travelAllowance = travelAllowanceUnit.divide(per);
                employeeSalary.setTravelAllowance(travelAllowance);
            } else if (payHead.getDescription().equalsIgnoreCase("Professional Tax")) {
                if (payHead.getUnitType().equalsIgnoreCase("Percentage")) {
                    per = new BigDecimal(100);
                } else {
                    per = new BigDecimal(1);

                }
                BigDecimal professionalTaxUnit = new BigDecimal(payHead.getUnit());
                professionalTax = professionalTaxUnit.divide(per);
                employeeSalary.setProfessionalTax(professionalTax);
            } else if (employee.getOfferedSalary() >= 250000 && employee.getOfferedSalary() <= 500000) {
                if (payHead.getDescription().equalsIgnoreCase("TDS-1")) {
                    if (payHead.getUnitType().equalsIgnoreCase("Percentage")) {
                        per = new BigDecimal(100);
                    } else {
                        per = new BigDecimal(1);

                    }
                    BigDecimal offeredSalary = new BigDecimal(employee.getOfferedSalary());
                    BigDecimal tdsCalc = offeredSalary.subtract(tdsSlab1);
                    tds = new BigDecimal(payHead.getUnit());
                    tds = tdsCalc.multiply(tds.divide(per));
                    employeeSalary.setTds(tds);
                }
            } else if (employee.getOfferedSalary() > 500000 && employee.getOfferedSalary() <= 633000) {
                if (payHead.getDescription().equalsIgnoreCase("TDS-2")) {
                    if (payHead.getUnitType().equalsIgnoreCase("Percentage")) {
                        per = new BigDecimal(100);
                    } else {
                        per = new BigDecimal(1);
                    }
                    BigDecimal offeredSalary = new BigDecimal(employee.getOfferedSalary());
                    BigDecimal tdsCalc = offeredSalary.subtract(tdsSlab2);
                    tds = new BigDecimal(payHead.getUnit());
                    tds = tdsCalc.multiply(tds.divide(per));
                    employeeSalary.setTds(tds);
                }
            }
        }

        totalEarning = totalEarning.add(employeeSalary.getBasicSalary().add(employeeSalary.getHouseRentAllowance().add(
                employeeSalary.getMedicalAllowance().add(employeeSalary.getTravelAllowance()))));
        totalDeduction = totalDeduction.add(employeeSalary.getEmployeersPF().add(employeeSalary.getEmployeesPF().add(
                employeeSalary.getProfessionalTax())));
        specialAllowance = oFSalary.subtract(totalEarning);
        employeeSalary.setSpecialAllowance(specialAllowance);
        totalEarning = totalEarning.add(specialAllowance);
        employeeSalary.setTotalEarning(totalEarning);
        employeeSalary.setTotalDeduction(totalDeduction);
        netSalaryPayble = totalEarning.subtract(employeeSalary.getTotalDeduction());
        employeeSalary.setNetSalaryPayble(netSalaryPayble);
        return employeeSalary;
    }

    public static EmployeeSalary calculateBasicSalaryNonTeaching(List<PayHeadContract> payHeadContractList, Employee employee) {
        System.out.println("================Printing No of Values in Payhead-Non Teaching : Salary Util=============\n" + payHeadContractList.size());
        BigDecimal basicSalary = new BigDecimal(1);
        BigDecimal specialAllowance = new BigDecimal(0);
        BigDecimal totalEarning = new BigDecimal(0);
        BigDecimal month = new BigDecimal(12);
        BigDecimal per = new BigDecimal(0);
        BigDecimal oFSalary = new BigDecimal(employee.getOfferedSalary());
        BigDecimal netSalaryPayble = new BigDecimal(0);
        oFSalary = oFSalary.divide(month, 2);
        EmployeeSalary employeeSalary = new EmployeeSalary();
        employeeSalary.setEmployee(employee);
        for (PayHeadContract payHeadContract : payHeadContractList) {
            if (payHeadContract.getDescription().equalsIgnoreCase("Basic")) {
                if (payHeadContract.getUnitType().equalsIgnoreCase("Percentage")) {
                    per = new BigDecimal(100);
                } else {
                    per = new BigDecimal(1);
                }
                BigDecimal basicUnit = new BigDecimal(payHeadContract.getUnit());
                basicSalary = oFSalary.multiply(basicUnit.divide(per));
                employeeSalary.setBasicSalary(basicSalary);
            }
        }
        totalEarning = totalEarning.add(employeeSalary.getBasicSalary());
        specialAllowance = oFSalary.subtract(totalEarning);
        employeeSalary.setSpecialAllowance(specialAllowance);
        BigDecimal noValue = new BigDecimal(0);
        employeeSalary.setMedicalAllowance(noValue);
        employeeSalary.setTravelAllowance(noValue);
        employeeSalary.setEmployeersPF(noValue);
        employeeSalary.setEmployeesPF(noValue);
        employeeSalary.setHouseRentAllowance(noValue);
        netSalaryPayble = totalEarning.add(specialAllowance);
        employeeSalary.setTotalEarning(netSalaryPayble);
        employeeSalary.setNetSalaryPayble(netSalaryPayble);
        employeeSalary.setTotalDeduction(noValue);
        employeeSalary.setProfessionalTax(noValue);
        employeeSalary.setTds(noValue);
        return employeeSalary;
    }
}
