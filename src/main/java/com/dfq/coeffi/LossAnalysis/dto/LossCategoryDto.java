package com.dfq.coeffi.LossAnalysis.dto;


import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LossCategoryDto {

    private LossCategory lossCategory;
    private long totalCategoryLossTime;
    private List<LossSubCategoryDto> lossSubCategoryDtos;

}
