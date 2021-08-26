package com.dfq.coeffi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MonthlyStudentAttendanceDto {
    private String stuedntName;
    private String employeeName;
    private long studentId;
    private long classId;
    private long sectionId;
    private List<MonthlyStatusDto> monthlyStatus;
}