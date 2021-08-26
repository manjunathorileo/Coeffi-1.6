package com.dfq.coeffi.SOPDetails.SOPType;

import java.util.List;
import java.util.Optional;

public interface SopTypeService {
    SopType saveSop(SopType sopType);
    List<SopType> getSopList();
    Optional<SopType> getSopTypeById(long id);
    SopType deleteSopType(long id);
}