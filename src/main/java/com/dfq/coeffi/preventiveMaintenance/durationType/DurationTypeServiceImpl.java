package com.dfq.coeffi.preventiveMaintenance.durationType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DurationTypeServiceImpl implements DurationTypeService {

    @Autowired
    private DurationTypeRepository durationTypeRepository;

    @Override
    public DurationType saveDurationType(DurationType durationType) {
        return durationTypeRepository.save(durationType);
    }

    @Override
    public List<DurationType> getAllDurationType() {
        List<DurationType> durationTypes = new ArrayList<>();
        List<DurationType> durationTypeList = durationTypeRepository.findAll();
        for (DurationType durationType:durationTypeList) {
            if (durationType.getStatus().equals(true)){
                durationTypes.add(durationType);
            }
        }
        return durationTypes;
    }

    @Override
    public Optional<DurationType> getDurationById(long id) {
        return durationTypeRepository.findById(id);
    }

    @Override
    public DurationType deleteDurationType(long id) {
        Optional<DurationType> durationTypeOptional = durationTypeRepository.findById(id);
        DurationType durationType = durationTypeOptional.get();
        durationType.setStatus(false);
        DurationType durationTypeObj = durationTypeRepository.save(durationType);
        return durationTypeObj;
    }
}
