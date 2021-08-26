package com.dfq.coeffi.preventiveMaintenance.durationType;

import java.util.List;
import java.util.Optional;

public interface DurationTypeService {

    DurationType saveDurationType(DurationType durationType);
    List<DurationType> getAllDurationType();
    Optional<DurationType> getDurationById(long id);
    DurationType deleteDurationType(long id);
}
