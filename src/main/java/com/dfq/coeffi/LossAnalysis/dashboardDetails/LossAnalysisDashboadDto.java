package com.dfq.coeffi.LossAnalysis.dashboardDetails;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LossAnalysisDashboadDto {

    private long totalTime;
    private long totalProductionLossTime;
    private long topProductionLossTime;
    private List<TopProductionLossDetailsDto> topProductionLossDetailDtos;
    private List<TotalProductionLossDetailsDto> totalProductionLossDetailsDtos;

}
