package com.dfq.coeffi.foodManagement;

import com.dfq.coeffi.dto.MonthlyStatusDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.LifecycleState;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class FoodReportDto {

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date markedOn;
    private String company;
    private String location;
    private String department;
    private String employeeType;
    private long bfAvailed;
    private long bfRate;
    private long lunchAvailed;
    private long lunchRate;
    private long snacksAvailed;
    private long snacksRate;
    private long dinnerAvailed;
    private long dinnerRate;
    private long midnightSnackAvailed;
    private long midnightSnackRate;
    private long totalToday;
    private String foodType;

    private long foodRate;
    private long availed;
    private long total;
    private String caterer;

    private long employeeId;
    private String employeeName;
    private String designation;
    private String employeeCode;
    List<MonthlyStatusDto> monthlyStatusDtos;

    private long openingBalance;
    private long closingBalance;
    private long debited;
    private long credited;



}
