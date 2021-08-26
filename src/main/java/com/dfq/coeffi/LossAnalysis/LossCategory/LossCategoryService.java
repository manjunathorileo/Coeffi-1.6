package com.dfq.coeffi.LossAnalysis.LossCategory;

import java.util.List;
import java.util.Optional;

public interface LossCategoryService {

    LossCategory createLossCategory(LossCategory lossCategory);

    List<LossCategory> getAllLossCategory();

    Optional<LossCategory> getLossCategory(long id);

    LossCategory deleteLossCategory(long id);
}