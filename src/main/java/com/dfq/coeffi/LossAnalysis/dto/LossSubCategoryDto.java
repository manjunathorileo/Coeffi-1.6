package com.dfq.coeffi.LossAnalysis.dto;


import com.dfq.coeffi.LossAnalysis.LossSubCategory.LossSubCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LossSubCategoryDto {
    private LossSubCategory lossSubCategory;
    private long totalSubCategoryLossTime;
}
