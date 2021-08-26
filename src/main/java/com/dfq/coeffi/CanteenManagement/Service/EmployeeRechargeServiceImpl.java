package com.dfq.coeffi.CanteenManagement.Service;

import com.dfq.coeffi.CanteenManagement.Entity.EmployeeRecharge;
import com.dfq.coeffi.CanteenManagement.Repository.EmployeeRechargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EmployeeRechargeServiceImpl implements EmployeeRechargeService  {

    @Autowired
    EmployeeRechargeRepository employeeRechargeRepository;

    @Override
    public EmployeeRecharge saveRecharge(EmployeeRecharge employeeRecharge) {
        return employeeRechargeRepository.save(employeeRecharge) ;
    }

    @Override
    public List<EmployeeRecharge> getRecharges() {
        return employeeRechargeRepository.findAll();
    }

    @Override
    public void delete(long id) {
        employeeRechargeRepository.delete(id);
    }

    @Override
    public EmployeeRecharge getEmployeeRecharge(long id) {
        return employeeRechargeRepository.findById(id);
    }

    @Override
    public List<EmployeeRecharge> getEmployeeRechargeByEmpId(long empId) {
        return employeeRechargeRepository.findByEmpId(empId);
    }

    @Override
    public List<EmployeeRecharge> getEmployeeRechargeByDate(Date fromDate, Date toDate) {
        return employeeRechargeRepository.getEmployeeRechargeBetweenDate(fromDate, toDate);
    }


}
