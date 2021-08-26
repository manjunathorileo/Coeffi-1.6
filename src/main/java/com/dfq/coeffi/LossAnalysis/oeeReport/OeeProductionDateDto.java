package com.dfq.coeffi.LossAnalysis.oeeReport;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OeeProductionDateDto {
    private long date;
    private String monthName;
    private long year;
    private List<OeeProductionShiftDto> oeeProductionShiftDtos;
}