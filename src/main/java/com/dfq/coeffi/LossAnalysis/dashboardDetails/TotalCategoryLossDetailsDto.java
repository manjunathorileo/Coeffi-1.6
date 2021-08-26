package com.dfq.coeffi.LossAnalysis.dashboardDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalCategoryLossDetailsDto {

    private long productionLineId;
    private long categoryId;
    private String lossCategory;
    private long totalTimeInMin;
}
