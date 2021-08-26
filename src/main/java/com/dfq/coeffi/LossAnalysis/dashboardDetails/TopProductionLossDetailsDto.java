package com.dfq.coeffi.LossAnalysis.dashboardDetails;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopProductionLossDetailsDto {

    private long productionId;
    private String productionLineName;
    private long totalTimeInMin;
    List<TotalCategoryLossDetailsDto> totalCategoryLossDetailsDtoList;
}
