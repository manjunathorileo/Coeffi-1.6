package com.dfq.coeffi.SOPDetails.SOPCategory;


import com.dfq.coeffi.SOPDetails.SOPType.SopType;

import java.util.List;
import java.util.Optional;

public interface SopCategoryService
{
    SopCategory saveDigitalSOP(SopCategory SOPCategory);

    List<SopCategory> getDigitalSOP();

    Optional<SopCategory> getSopCategory(long id);

    SopCategory deleteSopCategory(long id);

    List<SopCategory> getSopCategoryBySopType(SopType sopType);
}