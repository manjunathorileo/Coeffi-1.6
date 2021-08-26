package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.EmployeeRecharge;

import java.util.Date;
import java.util.List;

public interface EmployeeRechargeService {

    EmployeeRecharge saveRecharge(EmployeeRecharge employeeRecharge);

    List<EmployeeRecharge> getRecharges();

    void delete(long id);

    EmployeeRecharge getEmployeeRecharge(long id);

    List<EmployeeRecharge> getEmployeeRechargeByEmpId(long empId);

    List<EmployeeRecharge> getEmployeeRechargeByDate(Date fromDate, Date toDate);
}
