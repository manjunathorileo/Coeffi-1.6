package com.dfq.coeffi.LossAnalysis.oeeReport;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OeeProductionLineDto {
    private String productionLineName;
    private long totalNoOfDays;
    private long totalShifts;
    private List<OeeProductionDateDto> oeeProductionDateDtos;
}
