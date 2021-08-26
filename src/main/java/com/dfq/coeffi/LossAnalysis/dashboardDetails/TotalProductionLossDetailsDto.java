package com.dfq.coeffi.LossAnalysis.dashboardDetails;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TotalProductionLossDetailsDto {

    private long productionLineId;
    private String productionLineName;
    private List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtos;
    private long totalLossTime;
    private long currentTime;
    private long opertationTime;
}
