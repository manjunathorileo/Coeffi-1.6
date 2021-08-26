package com.dfq.coeffi.LossAnalysis.LossSubCategory;

import com.dfq.coeffi.LossAnalysis.LossCategory.LossCategory;

import java.util.List;
import java.util.Optional;

public interface LossSubCategoryService {
    LossSubCategory createLossSubCategory(LossSubCategory lossSubCategory);

    List<LossSubCategory> getAllLossSubCategory();

    Optional<LossSubCategory> getLossSubCategory(long id);

    List<LossSubCategory> getLossSubCategoryByLosscategory(LossCategory lossCategory);

    LossSubCategory deleteLossSubCategory(long id);

}
