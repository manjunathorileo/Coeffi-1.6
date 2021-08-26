package com.dfq.coeffi.dto;

import com.dfq.coeffi.controller.leave.leavebalance.AvailLeave;
import com.dfq.coeffi.controller.leave.leavebalance.ClosingLeave;
import com.dfq.coeffi.controller.leave.leavebalance.OpeningLeave;
import com.dfq.coeffi.entity.hr.employee.Employee;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@Getter
@Setter
public class EmployeeLeaveBalanceDto {

    private Employee employee;
    private OpeningLeave openingLeave;
    private AvailLeave availLeave;
    private ClosingLeave closingLeave;

    private long employeeId;
    private BigDecimal openingEarnLeave;
    private BigDecimal openingCasualLeave;
    private BigDecimal openingMedicalLeave;

}
