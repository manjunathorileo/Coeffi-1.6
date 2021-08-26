package com.dfq.coeffi.dto;

import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonthlyLeaveReport {
    public double totalVacationLeaveCount;
    public double totalCasuaLeaveCount;
    public double totalUnPaidLeaveCount;
    public double totalSickLeaveCount;
    public double totalPaidLeaveCount;
    public int refId;
    public Employee employee;
}
