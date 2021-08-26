package com.dfq.coeffi.LossAnalysis.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LossAnalysisDto {
    private long totalTime;
    private long totalRunTime;
    private long totalLossTime;
    private long totalProduction;
    private List<LossCategoryDto> lossCategoryDtos;
}
