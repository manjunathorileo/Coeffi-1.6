package com.dfq.coeffi.LossAnalysis.oeeReport;

import com.dfq.coeffi.master.shift.Shift;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OeeProductionShiftDto {
    private Shift shift;
    private List<OeeProductionDetailsDto> oeeProductionDetailsDtos;

    private long totalTime;
    private long totalLossTime;
    private long totalOperationTime;
    private List<OeeProductionLossCategoryDetailsDto> oeeProductionLossCategoryDetailsDtos;

    private Double availability;
    private Double performance;
    private Double quality;
    private Double overallEquipmentEfficiency;
}
