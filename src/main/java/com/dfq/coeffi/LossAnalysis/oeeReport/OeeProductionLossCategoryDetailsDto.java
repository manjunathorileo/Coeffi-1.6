package com.dfq.coeffi.LossAnalysis.oeeReport;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OeeProductionLossCategoryDetailsDto {
    private long lossCategoryId;
    private String lossCategoryName;
    private long totalLossTime;
}
